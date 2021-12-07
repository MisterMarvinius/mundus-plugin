package me.hammerle.kp.snuviscript.commands;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import me.hammerle.kp.KajetansPlugin;
import net.kyori.adventure.text.Component;

public class ShopCommands {
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerFunction("shop.new",
                (sc, in) -> Bukkit.createMerchant((Component) in[0].get(sc)));
        KajetansPlugin.scriptManager.registerConsumer("shop.addoffer", (sc, in) -> {
            Merchant npc = (Merchant) in[0].get(sc);
            ItemStack buy = (ItemStack) in[1].get(sc);
            ItemStack sell = (ItemStack) in[2].get(sc);
            int maxUses = (in.length >= 4) ? in[3].getInt(sc) : Integer.MAX_VALUE;
            MerchantRecipe recipe = new MerchantRecipe(sell, maxUses);
            recipe.addIngredient(buy);
            ArrayList<MerchantRecipe> recipes = new ArrayList<>(npc.getRecipes());
            recipes.add(recipe);
            npc.setRecipes(recipes);
        });
        KajetansPlugin.scriptManager.registerConsumer("shop.adddoubleoffer", (sc, in) -> {
            Merchant npc = (Merchant) in[0].get(sc);
            ItemStack buyA = (ItemStack) in[1].get(sc);
            ItemStack buyB = (ItemStack) in[2].get(sc);
            ItemStack sell = (ItemStack) in[3].get(sc);
            int maxUses = (in.length >= 5) ? in[4].getInt(sc) : Integer.MAX_VALUE;
            MerchantRecipe recipe = new MerchantRecipe(sell, maxUses);
            recipe.addIngredient(buyA);
            recipe.addIngredient(buyB);
            ArrayList<MerchantRecipe> recipes = new ArrayList<>(npc.getRecipes());
            recipes.add(recipe);
            npc.setRecipes(recipes);
        });
        KajetansPlugin.scriptManager.registerConsumer("shop.open", (sc, in) -> {
            Merchant npc = (Merchant) in[0].get(sc);
            Player p = (Player) in[1].get(sc);
            p.openMerchant(npc, false);
        });
    }
}
