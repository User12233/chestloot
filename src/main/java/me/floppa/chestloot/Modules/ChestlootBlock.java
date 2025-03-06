package me.floppa.chestloot.Modules;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import me.floppa.chestloot.Chestloot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ChestlootBlock extends Block {
    Random rand = new Random();
    public ChestlootBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if(player.getCooldowns().isOnCooldown(Chestloot.chestcopy.get().asItem()) || level.isClientSide) {
            return InteractionResult.PASS;
        }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        server.getGameRules().getRule(GameRules.RULE_ANNOUNCE_ADVANCEMENTS).set(false,server);
        player.getCooldowns().addCooldown(Chestloot.chestcopy.get().asItem(), 5);
        Objects.requireNonNull(server.getLevel(Level.OVERWORLD)).setBlock(pos, Blocks.AIR.defaultBlockState(),3);
        List<ItemStack> drops = new ArrayList<>();
        // Open container menu for player
        for(int i = 0; i<rand.nextInt(ChestLootConfig.amountToGiveMin.get(), ChestLootConfig.amountToGiveMax.get()); i++) {
            String givenitem = ChestLootConfig.getRandomItem();
            String parts = givenitem.replaceFirst(":", " ");
            String[] part2 = parts.split(" ");
            LogUtils.getLogger().info(parts);
            try {
                switch (part2[1]) {
                    case "ammo" -> {
                        LogUtils.getLogger().info(part2[2]);
                        JsonObject jsonObject = JsonParser.parseString(part2[2]).getAsJsonObject();
                        CompoundTag tag = new CompoundTag();
                        tag.putString("AmmoId", jsonObject.get("AmmoId").getAsString());
                        ItemStack item = new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(part2[0], part2[1]))), Integer.parseInt(part2[3]));
                        item.setTag(tag);
                        drops.add(item);
                    }
                    case "modern_kinetic_gun" -> {
                        LogUtils.getLogger().info(part2[2]);
                        JsonObject jsonObject = JsonParser.parseString(part2[2]).getAsJsonObject();
                        CompoundTag tag = new CompoundTag();
                        tag.putString("GunId", jsonObject.get("GunId").getAsString());
                        tag.putString("GunCurrentAmmoCount", jsonObject.get("GunCurrentAmmoCount").getAsString());
                        tag.putString("HasBulletInBarrel", jsonObject.get("HasBulletInBarrel").getAsString());
                        tag.putString("GunFireMode", jsonObject.get("GunFireMode").getAsString());
                        ItemStack item = new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(part2[0], part2[1]))));
                        item.setTag(tag);
                        drops.add(item);
                    }
                    case "attachment" -> {
                        LogUtils.getLogger().info(part2[2]);
                        JsonObject jsonObject = JsonParser.parseString(part2[2]).getAsJsonObject();
                        CompoundTag tag = new CompoundTag();
                        tag.putString("AttachmentId", jsonObject.get("AttachmentId").getAsString());
                        ItemStack item = new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(part2[0], part2[1]))));
                        item.setTag(tag);
                        drops.add(item);
                    }
                    default -> {
                        String[] idandname = parts.split(" ");
                        int count = 1;
                        if (idandname.length >= 3) {
                            try {
                                count = Integer.parseInt(idandname[2]);
                            } catch (NumberFormatException e) {
                                LogUtils.getLogger().info(e.getMessage());
                            }
                        }
                        drops.add(new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(idandname[0], idandname[1]))), count));
                    }
                }
                LogUtils.getLogger().info(parts);
            } catch(Exception e) {
                LogUtils.getLogger().info("Error occurred with chestloot - "+e.getMessage());
            }
            openGUI(player,pos,drops);
        }
        return InteractionResult.SUCCESS;
    }

    private void openGUI(Player player, BlockPos pos,List<ItemStack> items) {
        NetworkHooks.openScreen((ServerPlayer) player, new MenuProvider() {
            @Override
            public @NotNull Component getDisplayName() {
                return Component.translatable("container.chestloot");
            }

            @Override
            public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player player) {
                return new ChestlootContainer(windowId, playerInventory, player,pos,items);
            }
        }, pos);
    }

}
