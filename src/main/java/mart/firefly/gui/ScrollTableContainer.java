package mart.firefly.gui;

import mart.firefly.Firefly;
import mart.firefly.gui.button.ContainerButton;
import mart.firefly.network.ScrollTablePacket;
import mart.firefly.setup.ModBlocks;
import mart.firefly.setup.ModTileEntities;
import mart.firefly.tile.ScrollTableTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ScrollTableContainer extends BaseContainer {

    public ScrollTableContainer(int id, BlockPos pos, PlayerInventory inventory) {
        super(ModTileEntities.SCROLL_TABLE_CONTAINER, id, pos, inventory);

        tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
            addSlot(new SlotItemHandler(h, 0, 31, 23));
            addSlot(new SlotItemHandler(h, 1, 31, 43));
            addSlot(new SlotItemHandler(h, 2, 130, 34));
        });

        addButton(new ContainerButton(79, 33){
            @Override
            public void activate(BaseContainer container) {
                if(tileEntity instanceof ScrollTableTile){
                    ScrollTablePacket message = new ScrollTablePacket(tileEntity.getPos());
                    Firefly.channel.sendToServer(message);
                }
            }
        });

        fillPlayerInv(inventory);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            itemstack = stack.copy();
            if (index < 3) {
                if (!this.mergeItemStack(stack, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(stack, itemstack);
            } else {
                if (!this.mergeItemStack(stack, 0, 2, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, stack);
        }

        return itemstack;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos()), playerIn, ModBlocks.SCROLL_TABLE.get());
    }
}
