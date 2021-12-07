package me.hammerle.kp.snuviscript.commands;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import me.hammerle.kp.KajetansPlugin;
import me.hammerle.kp.snuviscript.SnuviInventoryHolder;
import me.hammerle.kp.snuviscript.SnuviInventoryHolder.SnuviSlotType;
import net.kyori.adventure.text.Component;

public class InventoryCommands {
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerFunction("inv.new", (sc, in) -> SnuviInventoryHolder
                .create(in[0].getString(sc), (Component) in[1].get(sc)));
        KajetansPlugin.scriptManager.registerFunction("inv.getid", (sc, in) -> {
            Inventory inv = (Inventory) in[0].get(sc);
            if(inv.getHolder() instanceof SnuviInventoryHolder) {
                return (double) ((SnuviInventoryHolder) inv.getHolder()).getId();
            }
            return -1;
        });
        KajetansPlugin.scriptManager.registerFunction("inv.loadchest", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            Block b = l.getBlock();
            if(b instanceof Chest) {
                Chest chest = (Chest) b;
                return chest.getBlockInventory();
            }
            return null;
        });
        KajetansPlugin.scriptManager.registerConsumer("inv.setitem", (sc, in) -> {
            Inventory inv = (Inventory) in[0].get(sc);
            int index = in[1].getInt(sc);
            ItemStack stack = (ItemStack) in[2].get(sc);
            if(inv.getHolder() instanceof SnuviInventoryHolder
                    && ((SnuviInventoryHolder) inv.getHolder())
                            .getSlotType(index) != SnuviSlotType.NORMAL) {
                stack = stack.clone();
                stack.addItemFlags(ItemFlag.HIDE_DESTROYS);
            }
            inv.setItem(index, stack);
        });
        KajetansPlugin.scriptManager.registerFunction("inv.getitem",
                (sc, in) -> ((Inventory) in[0].get(sc)).getItem(in[1].getInt(sc)));
        KajetansPlugin.scriptManager.registerFunction("inv.getsize",
                (sc, in) -> (double) ((Inventory) in[0].get(sc)).getSize());
        KajetansPlugin.scriptManager.registerConsumer("inv.open", (sc, in) -> {
            ((Player) in[1].get(sc)).openInventory((Inventory) in[0].get(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("inv.close", (sc, in) -> {
            ((Player) in[0].get(sc)).closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        });
    }
}
