package me.floppa.chestloot;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import me.floppa.chestloot.Modules.ChestLootConfig;
import me.floppa.chestloot.Modules.ChestlootBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.joml.Matrix4f;
import org.slf4j.Logger;

import java.util.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Chestloot.MODID)
public class Chestloot {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "chestloot";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static RegistryObject<Block> chestcopy = REGISTER.register("chestcopy_block",() -> new ChestlootBlock(BlockBehaviour.Properties.copy(Blocks.CHEST)));;
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> CHESTCOPY_ITEM = ITEMS.register("chestcopy", () ->
            new BlockItem(chestcopy.get(), new Item.Properties().stacksTo(64)));

    public Chestloot() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ITEMS.register(modEventBus);
        REGISTER.register(modEventBus);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(EventsHandler.class);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER,ChestLootConfig.COMMON_CONFIG,"chestloot-server.toml");
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("placeloot").requires(predicate -> ChestLootConfig.Admins.get().stream().anyMatch(nick -> Objects.requireNonNull(predicate.getPlayer()).getName().getString().equals(nick)))
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayer();

                    Vec3 eyePos = player.getEyePosition(1.0F);

                    Vec3 lookVec = player.getLookAngle();

                    Vec3 reachVec = eyePos.add(lookVec.scale(3));

                    // Создаем ClipContext для определения блока (используем OUTLINE для учета краев блоков и игнорируем жидкости)
                    ClipContext context1 = new ClipContext(eyePos, reachVec, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player);
                    // Выполняем лучевое прослеживание
                    BlockHitResult result = player.level().clip(context1);
                    // Если луч попал в блок, возвращаем его координаты
                    if (result.getType() == HitResult.Type.BLOCK) {
                        BlockPos pos = result.getBlockPos().atY(result.getBlockPos().getY()+1);
                        Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD)).setBlock(pos,chestcopy.get().defaultBlockState(),3);
                        addPosChestToConfig(context.getSource().getPlayer(),pos);
                        return 1;
                    }
                    return 0;
                }));
    }

    public static void addPosChestToConfig(ServerPlayer player, BlockPos pos) {
        List<String> modifiableList = new ArrayList<>(ChestLootConfig.chestsPositions.get());
        String cords = pos.getX() + "," + pos.getY() + "," + pos.getZ();
        if(!modifiableList.contains(cords)) {
            modifiableList.add(cords);
            ChestLootConfig.chestsPositions.set(modifiableList);
        } else {
            player.sendSystemMessage(Component.literal("§4Сундук уже существует"));
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Checking chest positions in config");
        if(!ChestLootConfig.chestsPositions.get().isEmpty() && ChestLootConfig.delayOnRespawn.get() != null) {
            for (String pos : ChestLootConfig.chestsPositions.get()) {
                String[] parts = pos.split(",");
                BlockPos result = new BlockPos(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()), Integer.parseInt(parts[2].trim()));
                EventsHandler.posChests.add(result);
                Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD)).setBlock(result, chestcopy.get().defaultBlockState(),3);
            }
            EventsHandler.delay = ChestLootConfig.delayOnRespawn.get();
            LOGGER.info("Delay on spawn, chest positions are set!");
        } else {
            LOGGER.warn("Chest positions are empty or delay on spawn is null");
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD,modid = MODID,value = Dist.DEDICATED_SERVER)
    public static class EventsHandler {
        private static ArrayList<BlockPos> posChests = new ArrayList<>() {};

        @SubscribeEvent
        @OnlyIn(Dist.CLIENT)
        public static void onRenderGui(RenderGuiEvent.Post event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen instanceof TitleScreen) {
                PoseStack poseStack = event.getGuiGraphics().pose();

                String text = "[CL] Fine, check desc";

                int screenWidth = event.getWindow().getScreenWidth();
                int screenHeight = event.getWindow().getScreenHeight();
                int textWidth = mc.font.width(text);
                float x = screenWidth - textWidth - 10;
                float y = screenHeight - mc.font.lineHeight - 10;

                int color = 0xFFFFFF;
                Matrix4f matrix = poseStack.last().pose();
                MultiBufferSource buffer = mc.renderBuffers().bufferSource();
                Font.DisplayMode displayMode = Font.DisplayMode.NORMAL;
                int packedLight = 15728880;
                int packedOverlay = 0;

                int renderedWidth = mc.font.drawInBatch(text,x,y,color,true,matrix,buffer,displayMode,packedLight,packedOverlay);
            }
        }

        private static int tickhavecompleted = 0;
        private static int delay = 0;
        @SubscribeEvent
        public static void onTick(TickEvent.ServerTickEvent e) {
            tickhavecompleted++;
            if(tickhavecompleted >= delay ) {
                tickhavecompleted = 0;
                if(posChests != null && !posChests.isEmpty()) {
                    for(BlockPos pos : posChests) {
                        Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD)).setBlock(pos, chestcopy.get().defaultBlockState(),3);
                    }
                    posChests.clear();
                }
            }
        }

        @SubscribeEvent
        public static void onBlockBreak(BlockEvent.BreakEvent e) {
            if (Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD)).getBlockState(e.getPos()).getBlock() == chestcopy.get()) {
                e.setCanceled(true);
            }
        }
    }
}
