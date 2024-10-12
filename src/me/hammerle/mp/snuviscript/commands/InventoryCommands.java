package me.hammerle.mp.snuviscript.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import me.hammerle.mp.MundusPlugin;
import me.hammerle.mp.snuviscript.SnuviInventoryHolder;
import me.hammerle.mp.snuviscript.SnuviInventoryHolder.SnuviSlotType;
import net.kyori.adventure.text.Component;

public class InventoryCommands {
    public static void registerFunctions() {
        MundusPlugin.scriptManager.registerFunction("inv.new", (sc, in) -> SnuviInventoryHolder
                .create(in[0].getString(sc), (Component) in[1].get(sc)));
        MundusPlugin.scriptManager.registerFunction("inv.getid", (sc, in) -> {
            Inventory inv = (Inventory) in[0].get(sc);
            if(inv.getHolder() instanceof SnuviInventoryHolder) {
                return (double) ((SnuviInventoryHolder) inv.getHolder()).getId();
            }
            return -1;
        });
        MundusPlugin.scriptManager.registerConsumer("inv.setitem", (sc, in) -> {
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
        MundusPlugin.scriptManager.registerFunction("inv.getitem",
                (sc, in) -> ((Inventory) in[0].get(sc)).getItem(in[1].getInt(sc)));
        MundusPlugin.scriptManager.registerFunction("inv.getsize",
                (sc, in) -> (double) ((Inventory) in[0].get(sc)).getSize());
        MundusPlugin.scriptManager.registerConsumer("inv.open", (sc, in) -> {
            ((Player) in[1].get(sc)).openInventory((Inventory) in[0].get(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("inv.close", (sc, in) -> {
            ((Player) in[0].get(sc)).closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        });
        MundusPlugin.scriptManager.registerConsumer("inv.clear",
                (sc, in) -> ((Inventory) in[0].get(sc)).clear());
        MundusPlugin.scriptManager.registerConsumer("inv.closeall", (sc, in) -> {
            for(Player p : Bukkit.getOnlinePlayers()) {
                Inventory inv = p.getOpenInventory().getTopInventory();
                if(inv.getHolder() instanceof SnuviInventoryHolder) {
                    p.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                }
            }
        });
    }
}
