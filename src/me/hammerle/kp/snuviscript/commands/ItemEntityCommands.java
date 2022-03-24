package me.hammerle.kp.snuviscript.commands;

import org.bukkit.entity.Item;
import org.bukkit.entity.SizedFireball;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.inventory.ItemStack;
import me.hammerle.kp.KajetansPlugin;

public class ItemEntityCommands {
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerFunction("item.entity.get", (sc, in) -> {
            Object o = in[0].get(sc);
            if(o instanceof SizedFireball) {
                return ((SizedFireball) o).getDisplayItem();
            } else if(o instanceof ThrowableProjectile) {
                return ((ThrowableProjectile) o).getItem();
            }
            return ((Item) o).getItemStack();
        });
        KajetansPlugin.scriptManager.registerConsumer("item.entity.set", (sc, in) -> {
            Object o = in[0].get(sc);
            ItemStack stack = (ItemStack) in[1].get(sc);
            if(o instanceof SizedFireball) {
                ((SizedFireball) o).setDisplayItem(stack);
            } else if(o instanceof ThrowableProjectile) {
                ((ThrowableProjectile) o).setItem(stack);
            } else {
                ((Item) in[0].get(sc)).setItemStack(stack);
            }
        });
        KajetansPlugin.scriptManager.registerConsumer("item.entity.setpickupdelay", (sc, in) -> {
            ((Item) in[0].get(sc)).setPickupDelay(in[1].getInt(sc));
        });
    }
}
