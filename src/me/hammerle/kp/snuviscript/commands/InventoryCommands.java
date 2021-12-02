/*package me.km.snuviscript.commands;

import me.hammerle.snuviscript.code.ScriptManager;
import me.km.inventory.CustomContainer;
import me.km.inventory.ModInventory;
import me.km.utils.Location;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ChestTileEntity;

public class InventoryCommands {
    public static void registerFunctions(ScriptManager sm) {
        sm.registerFunction("inv.new", (sc, in) -> new ModInventory(in[0].getString(sc)));
        sm.registerFunction("inv.getid",
                (sc, in) -> (double) ((ModInventory) in[0].get(sc)).getModId());
        sm.registerFunction("inv.loadchest", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            ChestTileEntity chest = (ChestTileEntity) l.getWorld().getTileEntity(l.getBlockPos());
            int size = chest.getSizeInventory();
            if(size % 9 != 0) {
                size /= 9;
                size++;
                size *= 9;
            }
            ModInventory inv = new ModInventory(size);
            for(int i = 0; i < chest.getSizeInventory(); i++) {
                inv.setInventorySlotContents(i, chest.getStackInSlot(i).copy());
            }
            return inv;
        });
        sm.registerConsumer("inv.setitem", (sc, in) -> {
            ((IInventory) in[0].get(sc)).setInventorySlotContents(in[1].getInt(sc),
                    (ItemStack) in[2].get(sc));
        });
        sm.registerFunction("inv.getitem",
                (sc, in) -> ((IInventory) in[0].get(sc)).getStackInSlot(in[1].getInt(sc)));
        sm.registerFunction("inv.getsize",
                (sc, in) -> (double) ((IInventory) in[0].get(sc)).getSizeInventory());
        sm.registerConsumer("inv.open", (sc, in) -> {
            CustomContainer.openForPlayer((ServerPlayerEntity) in[1].get(sc),
                    (ModInventory) in[0].get(sc), in[2].getString(sc), sc);
        });
        sm.registerConsumer("inv.close", (sc, in) -> {
            ((PlayerEntity) in[0].get(sc)).closeScreen();
        });
        sm.registerConsumer("inv.update", (sc, in) -> {
            ServerPlayerEntity p = (ServerPlayerEntity) in[0].get(sc);
            p.sendAllContents(p.openContainer, p.openContainer.getInventory());
        });
    }
}
*/
