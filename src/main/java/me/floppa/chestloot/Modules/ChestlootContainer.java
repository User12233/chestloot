package me.floppa.chestloot.Modules;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import me.floppa.chestloot.Chestloot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static me.floppa.chestloot.Modules.ChestLootConfig.rand;

public class ChestlootContainer extends AbstractContainerMenu {

    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, Chestloot.MODID);
    public static final RegistryObject<MenuType<ChestlootContainer>> CHESTLOOT_CONTAINER =
            CONTAINERS.register("chestloot_container", () -> new MenuType<>(ChestlootContainer::new,FeatureFlagSet.of()));

    private ContainerLevelAccess containerlevel;
    private final IItemHandler chestcontainer;
    //client
    public ChestlootContainer(int id, Inventory inv) {
        this(id,inv,inv.player,BlockPos.ZERO);
    }
    // Server
    public ChestlootContainer(int id, Inventory inv, Player player, BlockPos pos) {
        super(CHESTLOOT_CONTAINER.get(), id);
        this.containerlevel = ContainerLevelAccess.create(player.level(), pos);
        chestcontainer = new ItemStackHandler(27);
        int i = (3 - 4) * 18;

        for(int j = 0; j < 3; ++j) {
            for(int k = 0; k < 9; ++k) {
                this.addSlot(new SlotItemHandler(chestcontainer, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }

        for(int l = 0; l < 3; ++l) {
            for(int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(inv, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
            }
        }

        for(int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(inv, i1, 8 + i1 * 18, 161 + i));
        }
        for(int ia = 0; ia<rand.nextInt(ChestLootConfig.amountToGiveMin.get(), ChestLootConfig.amountToGiveMax.get()); ia++) {
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
                        chestcontainer.insertItem(ia,item,false);
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
                        chestcontainer.insertItem(ia,item,false);
                    }
                    case "attachment" -> {
                        LogUtils.getLogger().info(part2[2]);
                        JsonObject jsonObject = JsonParser.parseString(part2[2]).getAsJsonObject();
                        CompoundTag tag = new CompoundTag();
                        tag.putString("AttachmentId", jsonObject.get("AttachmentId").getAsString());
                        ItemStack item = new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(part2[0], part2[1]))));
                        item.setTag(tag);
                        chestcontainer.insertItem(ia,item,false);
                    }
                    case "potion" -> {
                        CompoundTag tag = new CompoundTag();
                        tag.putString("Potion", part2[2].replace("{}", "").split(":")[1].replace("}",""));
                        ItemStack item = new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(part2[0], part2[1]))));
                        item.setTag(tag);
                        LogUtils.getLogger().info("Test {}", part2[2].replace("{}", "").split(":")[1] + tag.getAsString());
                        chestcontainer.insertItem(ia,item,false);
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
                        chestcontainer.insertItem(ia,new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(idandname[0], idandname[1]))), count),false);
                    }
                }
                LogUtils.getLogger().info(parts);
            } catch (Exception e) {
                LogUtils.getLogger().info("Error occurred with chestloot - " + e.getMessage());
            }
        }
    }

    public static void register(IEventBus bus) {
        CONTAINERS.register(bus);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (pIndex < 3 * 9) {
                if (!this.moveItemStackTo(itemstack1, 3 * 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 3 * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
        }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
