package me.hammerle.mp.snuviscript.commands;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.hammerle.mp.MundusPlugin;

public class EnchantmentCommands {
    @SuppressWarnings("unchecked")
    public static void registerFunctions() {
        MundusPlugin.scriptManager.registerFunction("enchantment.get",
                (sc, in) -> RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
                        .get(NamespacedKey.fromString(in[0].getString(sc))), "object");
        MundusPlugin.scriptManager.registerFunction("enchantment.getmaxlevel",
                (sc, in) -> (double) ((Enchantment) in[0].get(sc)).getMaxLevel(), "object");
        MundusPlugin.scriptManager.registerConsumer("enchantment.add", (sc, in) -> {
            ((ItemStack) in[1].get(sc)).addUnsafeEnchantment((Enchantment) in[0].get(sc),
                    in[2].getInt(sc));
        });
        MundusPlugin.scriptManager.registerFunction("enchantment.getlevel",
                (sc, in) -> (double) ((ItemStack) in[1].get(sc))
                        .getEnchantmentLevel((Enchantment) in[0].get(sc)), "object");
        MundusPlugin.scriptManager.registerFunction("enchantment.readfromitem", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            HashMap<Enchantment, Double> map = new HashMap<>();
            for(var e : stack.getEnchantments().entrySet()) {
                map.put(e.getKey(), (double) e.getValue());
            }
            return map;
        }, "object");
        MundusPlugin.scriptManager.registerConsumer("enchantment.writetoitem", (sc, in) -> {
            ItemStack stack = (ItemStack) in[1].get(sc);
            for(Enchantment e : stack.getEnchantments().keySet()) {
                stack.removeEnchantment(e);
            }
            Map<Enchantment, Double> map = (Map<Enchantment, Double>) in[0].get(sc);
            for(var e : map.entrySet()) {
                stack.addEnchantment(e.getKey(), e.getValue().intValue());
            }
        });
    }
}
