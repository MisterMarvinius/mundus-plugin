/*package me.km.snuviscript.commands;

import me.hammerle.snuviscript.code.ScriptManager;
import me.km.snuviscript.FakeMerchant;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.util.text.StringTextComponent;

public class ShopCommands {
    public static void registerFunctions(ScriptManager sm) {
        sm.registerFunction("shop.new", (sc, in) -> new FakeMerchant());
        sm.registerConsumer("shop.addoffer", (sc, in) -> {
            FakeMerchant npc = (FakeMerchant) in[0].get(sc);
            ItemStack buy = (ItemStack) in[1].get(sc);
            ItemStack sell = (ItemStack) in[2].get(sc);
            int maxUses = (in.length >= 4) ? in[3].getInt(sc) : Integer.MAX_VALUE;
            npc.getOffers().add(new MerchantOffer(buy, sell, maxUses, 0, 1.0f));
        });
        sm.registerConsumer("shop.adddoubleoffer", (sc, in) -> {
            FakeMerchant npc = (FakeMerchant) in[0].get(sc);
            ItemStack buyA = (ItemStack) in[1].get(sc);
            ItemStack buyB = (ItemStack) in[2].get(sc);
            ItemStack sell = (ItemStack) in[3].get(sc);
            int maxUses = (in.length >= 5) ? in[4].getInt(sc) : Integer.MAX_VALUE;
            npc.getOffers().add(new MerchantOffer(buyA, buyB, sell, maxUses, 0, 1.0f));
        });
        sm.registerConsumer("shop.open", (sc, in) -> {
            FakeMerchant npc = (FakeMerchant) in[0].get(sc);
            ServerPlayerEntity p = (ServerPlayerEntity) in[1].get(sc);
            String name = in[2].getString(sc);
            npc.setCustomer(p);
            npc.openMerchantContainer(p, new StringTextComponent(name), 1);
        });
    }
}*/
