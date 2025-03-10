package me.floppa.chestloot.Modules;

import me.floppa.chestloot.Chestloot;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ChestlootBlock extends Block {
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
        openGUI(player,pos);
        return InteractionResult.SUCCESS;
    }

    private void openGUI(Player player, BlockPos pos) {
        NetworkHooks.openScreen((ServerPlayer) player, new MenuProvider() {
            @Override
            public @NotNull Component getDisplayName() {
                return Component.translatable("container.chestloot");
            }

            @Override
            public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player player) {
                return new ChestlootContainer(windowId, playerInventory, player);
            }
        }, pos);
    }

}
