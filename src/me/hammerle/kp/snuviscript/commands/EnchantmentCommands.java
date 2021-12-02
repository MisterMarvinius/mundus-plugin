/*package me.km.snuviscript.commands;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import me.hammerle.snuviscript.code.ScriptManager;
import me.km.utils.Mapper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class EnchantmentCommands {
    @SuppressWarnings("unchecked")
    public static void registerFunctions(ScriptManager sm) {
        sm.registerFunction("enchantment.get",
                (sc, in) -> Mapper.getEnchantment(in[0].getString(sc)));
        sm.registerFunction("enchantment.add", (sc, in) -> {
            ((ItemStack) in[1].get(sc)).addEnchantment((Enchantment) in[0].get(sc),
                    in[2].getInt(sc));
            return true;
        });
        sm.registerFunction("enchantment.getlevel", (sc, in) -> {
            return (double) EnchantmentHelper.getEnchantmentLevel((Enchantment) in[0].get(sc),
                    (ItemStack) in[1].get(sc));
        });
        sm.registerFunction("enchantment.readfromitem", (sc, in) -> {
            return getEnchantments((ItemStack) in[0].get(sc));
        });
        sm.registerConsumer("enchantment.writetoitem", (sc, in) -> {
            setEnchantments((Map<Enchantment, Double>) in[0].get(sc), (ItemStack) in[1].get(sc));
        });
    }

    private static Map<Enchantment, Double> getEnchantments(ItemStack stack) {
        ListNBT listnbt =
                stack.getItem() == Items.ENCHANTED_BOOK ? EnchantedBookItem.getEnchantments(stack)
                        : stack.getEnchantmentTagList();
        return listToMap(listnbt);
    }

    private static Map<Enchantment, Double> listToMap(ListNBT list) {
        Map<Enchantment, Double> map = new LinkedHashMap<>();
        for(int i = 0; i < list.size(); ++i) {
            CompoundNBT c = list.getCompound(i);
            Enchantment ench = ForgeRegistries.ENCHANTMENTS
                    .getValue(ResourceLocation.tryCreate(c.getString("id")));
            if(ench != null) {
                map.put(ench, (double) c.getInt("lvl"));
            }
        }
        return map;
    }

    private static void setEnchantments(Map<Enchantment, Double> enchMap, ItemStack stack) {
        ListNBT list = new ListNBT();
        for(Entry<Enchantment, Double> entry : enchMap.entrySet()) {
            Enchantment ench = entry.getKey();
            if(ench != null) {
                int i = entry.getValue().intValue();
                CompoundNBT c = new CompoundNBT();
                c.putString("id", String.valueOf(ForgeRegistries.ENCHANTMENTS.getKey(ench)));
                c.putShort("lvl", (short) i);
                list.add(c);
                if(stack.getItem() == Items.ENCHANTED_BOOK) {
                    EnchantedBookItem.addEnchantment(stack, new EnchantmentData(ench, i));
                }
            }
        }
        if(list.isEmpty()) {
            stack.removeChildTag("Enchantments");
        } else if(stack.getItem() != Items.ENCHANTED_BOOK) {
            stack.setTagInfo("Enchantments", list);
        }
    }
}*/
