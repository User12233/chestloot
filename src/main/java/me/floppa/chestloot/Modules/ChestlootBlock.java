package me.floppa.chestloot.Modules;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import me.floppa.chestloot.Chestloot;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
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
        server.getGameRules().getRule(GameRules.RULE_SENDCOMMANDFEEDBACK).set(false,server);
        player.getCooldowns().addCooldown(Chestloot.chestcopy.get().asItem(), 5);
        Objects.requireNonNull(server.getLevel(Level.OVERWORLD)).setBlock(pos, Blocks.AIR.defaultBlockState(),3);
        try {
            server.getCommands().getDispatcher().execute(server.getCommands().getDispatcher().parse("give " + player.getName().getString() + " " + ChestLootConfig.getRandomItem(), server.createCommandSourceStack()));
        } catch (CommandSyntaxException e) {
            LogUtils.getLogger().error("Failed to execute command ", e);
        }
        server.getGameRules().getRule(GameRules.RULE_SENDCOMMANDFEEDBACK).set(true,server);
        return InteractionResult.SUCCESS;
    }

}
