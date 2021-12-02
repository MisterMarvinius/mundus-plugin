/*package me.km.inventory;

import me.hammerle.snuviscript.code.Script;
import me.km.networking.ModPacketHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class CustomContainer extends Container {
    private final ModInventory inv;
    private final int numRows;

    public CustomContainer(int id, PlayerInventory pInv, ModInventory inv) {
        super(null, id);
        // basic stuff
        this.inv = inv;
        this.numRows = inv.getRows();
        inv.openInventory(pInv.player);
        int i = (this.numRows - 4) * 18;

        // inventory slots
        int counter = 0;
        for(int y = 0; y < this.numRows; y++) {
            for(int x = 0; x < 9; x++) {
                if(inv.isSlotValid(x, y)) {
                    addSlot(new Slot(inv, counter, 8 + x * 18, 18 + y * 18));
                    counter++;
                }
            }
        }

        // plaver inventory slots
        for(int y = 0; y < 3; y++) {
            for(int x = 0; x < 9; x++) {
                addSlot(new Slot(pInv, x + y * 9 + 9, 8 + x * 18, 103 + y * 18 + i));
            }
        }

        for(int x = 0; x < 9; x++) {
            addSlot(new Slot(pInv, x, 8 + x * 18, 161 + i));
        }
    }

    public ModInventory getInventoryBase() {
        return inv;
    }

    public boolean onButtonClick(int slot, int dragType, ClickType click, PlayerEntity p) {
        return false;
    }

    public static void openForPlayer(ServerPlayerEntity p, ModInventory inv, String title, Script sc) {
        // taken from ServerPlayerEntity.openContainer
        if(p.isSpectator()) {
            p.sendStatusMessage((new TranslationTextComponent("container.spectatorCantOpen")).mergeStyle(TextFormatting.RED), true);
            return;
        }
        if(p.openContainer != p.container) {
            p.closeScreen();
        }

        p.getNextWindowId();
        Container container = new ServerCustomContainer(p.currentWindowId, p.inventory, inv, title, sc);
        ModPacketHandler.sendCustomInventory(p, p.currentWindowId, title, inv);
        container.addListener(p);
        p.openContainer = container;
    }

    @Override
    public boolean canInteractWith(PlayerEntity p) {
        return this.inv.isUsableByPlayer(p);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity p, int index) {
        // taken from ChestContainer, changed size
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        int size = inv.getSizeInventory();
        if(slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();
            if(index < size) {
                if(!this.mergeItemStack(slotStack, size, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if(!this.mergeItemStack(slotStack, 0, size, false)) {
                return ItemStack.EMPTY;
            }

            if(slotStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        return stack;
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slot) {
        return inv.getSlotStatus(slot.slotNumber) == 1;
    }

    @Override
    public boolean canDragIntoSlot(Slot slot) {
        return inv.getSlotStatus(slot.slotNumber) == 1;
    }

    @Override
    protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        boolean flag = false;
        int i = startIndex;
        if(reverseDirection) {
            i = endIndex - 1;
        }

        if(stack.isStackable()) {
            while(!stack.isEmpty()) {
                if(reverseDirection) {
                    if(i < startIndex) {
                        break;
                    }
                } else if(i >= endIndex) {
                    break;
                }

                if(inv.getSlotStatus(i) == 1) {
                    Slot slot = this.inventorySlots.get(i);
                    ItemStack itemstack = slot.getStack();
                    if(!itemstack.isEmpty() && areItemsAndTagsEqual(stack, itemstack)) {
                        int j = itemstack.getCount() + stack.getCount();
                        int maxSize = Math.min(slot.getSlotStackLimit(), stack.getMaxStackSize());
                        if(j <= maxSize) {
                            stack.setCount(0);
                            itemstack.setCount(j);
                            slot.onSlotChanged();
                            flag = true;
                        } else if(itemstack.getCount() < maxSize) {
                            stack.shrink(maxSize - itemstack.getCount());
                            itemstack.setCount(maxSize);
                            slot.onSlotChanged();
                            flag = true;
                        }
                    }
                }

                if(reverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        if(!stack.isEmpty()) {
            if(reverseDirection) {
                i = endIndex - 1;
            } else {
                i = startIndex;
            }

            while(true) {
                if(reverseDirection) {
                    if(i < startIndex) {
                        break;
                    }
                } else if(i >= endIndex) {
                    break;
                }

                if(inv.getSlotStatus(i) == 1) {
                    Slot slot1 = this.inventorySlots.get(i);
                    ItemStack itemstack1 = slot1.getStack();
                    if(itemstack1.isEmpty() && slot1.isItemValid(stack)) {
                        if(stack.getCount() > slot1.getSlotStackLimit()) {
                            slot1.putStack(stack.split(slot1.getSlotStackLimit()));
                        } else {
                            slot1.putStack(stack.split(stack.getCount()));
                        }

                        slot1.onSlotChanged();
                        flag = true;
                        break;
                    }
                }

                if(reverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        return flag;
    }

    @Override
    public final ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity p) {
        if(slotId < 0 || slotId >= inv.getSizeInventory()) {
            return super.slotClick(slotId, dragType, clickTypeIn, p);
        }
        switch(inv.getSlotStatus(slotId)) {
            case 0:
                return ItemStack.EMPTY;
            case 1:
                return super.slotClick(slotId, dragType, clickTypeIn, p);
            case 2:
            case 3:
                onButtonClick(slotId, dragType, clickTypeIn, p);
                return ItemStack.EMPTY;
        }
        return ItemStack.EMPTY;
    }
}
*/
