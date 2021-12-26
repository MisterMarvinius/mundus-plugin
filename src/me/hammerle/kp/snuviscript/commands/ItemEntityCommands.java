package me.hammerle.kp.snuviscript.commands;

import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import me.hammerle.kp.KajetansPlugin;

public class ItemEntityCommands {
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerFunction("item.entity.get",
                (sc, in) -> ((Item) in[0].get(sc)).getItemStack());
        KajetansPlugin.scriptManager.registerConsumer("item.entity.set", (sc, in) -> {
            ((Item) in[0].get(sc)).setItemStack((ItemStack) in[1].get(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("item.entity.setpickupdelay", (sc, in) -> {
            ((Item) in[0].get(sc)).setPickupDelay(in[1].getInt(sc));
        });
    }
}
