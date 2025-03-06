package me.floppa.chestloot.Modules;

import me.floppa.chestloot.Chestloot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.*;
public class ChestlootContainer extends AbstractContainerMenu {

    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, Chestloot.MODID);
    public static final RegistryObject<MenuType<ChestlootContainer>> CHESTLOOT_CONTAINER =
            CONTAINERS.register("chestloot_container", () -> new MenuType<>(ChestlootContainer::new,FeatureFlagSet.of()));

    private ContainerLevelAccess containerlevel;
    private final IItemHandler chestcontainer;
    //client
    public ChestlootContainer(int id, Inventory inv) {
        this(id,inv,inv.player,BlockPos.ZERO,new ArrayList<>());
    }
    // Server
    public ChestlootContainer(int id, Inventory inv, Player player, BlockPos pos, List<ItemStack> items) {
        super(CHESTLOOT_CONTAINER.get(), id);
        this.containerlevel = ContainerLevelAccess.create(player.level(), pos);
        chestcontainer = new ItemStackHandler(27);
        final int slotSizeP1us2 = 18, startX = 8, startY = 86, hotbarY = 142;
        for(int column = 0; column < 9; ++column) {
            for(int row = 0; row < 3; ++row) {
                addSlot(new Slot(inv,8 + row * 8 + column,startX + column * slotSizeP1us2,startY + row * slotSizeP1us2 - 2)); // add slots in chest
            }
            addSlot(new Slot(inv,column,8 + column * slotSizeP1us2,hotbarY)); // adding hotbar of player's items
        }


        // chest's container
        final int slotSizeP1us22 = 18, startXX = 8, startYY = 18;
        for(int column = 0; column < 9; ++column) {
            for(int row = 0; row < 3; ++row) {
                addSlot(new SlotItemHandler(chestcontainer,row * 8 + column,startXX + column * slotSizeP1us22,startYY + row * slotSizeP1us22));
            }
        }
        for(ItemStack itemstack : items) {
            for (int i = 0; i < items.size(); i++) {
                ItemStack stack = items.get(i);
                if (stack != null && stack.isEmpty()) {
                    chestcontainer.insertItem(i, itemstack, false);
                }
            }
        }
    }

    public static void register(IEventBus bus) {
        CONTAINERS.register(bus);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player pPlayer, int pIndex) {
            var retStack = ItemStack.EMPTY;
            final Slot slot = this.slots.get(pIndex);
            if (slot.hasItem()) {
                final ItemStack stack = slot.getItem();
                retStack = stack.copy();

                final int size = this.slots.size() - pPlayer.getInventory().getContainerSize();
                if (pIndex < size) {
                    if (!moveItemStackTo(stack, 0, this.slots.size(), false))
                        return ItemStack.EMPTY;
                } else if (!moveItemStackTo(stack, 0, size, false))
                    return ItemStack.EMPTY;

                if (stack.isEmpty() || stack.getCount() == 0) {
                    slot.set(ItemStack.EMPTY);
                } else {
                    slot.setChanged();
                }

                if (stack.getCount() == retStack.getCount())
                    return ItemStack.EMPTY;

                slot.onTake(pPlayer, stack);
            }

            return retStack;
        }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
