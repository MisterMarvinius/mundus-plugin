package me.hammerle.mp.snuviscript.commands;

import org.bukkit.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.google.common.collect.HashMultimap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import me.hammerle.mp.MundusPlugin;
import net.kyori.adventure.text.Component;

public class ItemCommands {
    @SuppressWarnings("unchecked")
    public static void registerFunctions() {
        MundusPlugin.scriptManager.registerFunction("material.get",
                (sc, in) -> Material.matchMaterial(in[0].getString(sc)), "material");
        MundusPlugin.scriptManager.registerFunction("material.getall",
                (sc, in) -> Material.values());
        MundusPlugin.scriptManager.registerConsumer("material.setcooldown", (sc, in) -> {
            Player p = (Player) in[1].get(sc);
            p.setCooldown((Material) in[0].get(sc), in[2].getInt(sc));
        });
        MundusPlugin.scriptManager.registerFunction("material.getslot",
                (sc, in) -> ((Material) in[0].get(sc)).getEquipmentSlot());
        MundusPlugin.scriptManager.registerFunction("material.isitem",
                (sc, in) -> ((Material) in[0].get(sc)).isItem());
        MundusPlugin.scriptManager.registerFunction("material.issolid",
                (sc, in) -> ((Material) in[0].get(sc)).isSolid());
        MundusPlugin.scriptManager.registerFunction("material.isblock",
                (sc, in) -> ((Material) in[0].get(sc)).isBlock());
        MundusPlugin.scriptManager.registerFunction("material.isedible",
                (sc, in) -> ((Material) in[0].get(sc)).isEdible());

        MundusPlugin.scriptManager.registerFunction("item.new",
                (sc, in) -> new ItemStack((Material) in[0].get(sc),
                        in.length >= 2 ? in[1].getInt(sc) : 1));
        MundusPlugin.scriptManager.registerFunction("item.drop", (sc, in) -> {
            Location l = (Location) in[1].get(sc);
            return l.getWorld().dropItem(l, (ItemStack) in[0].get(sc));
        });
        MundusPlugin.scriptManager.registerFunction("item.gettag",
                (sc, in) -> Bukkit.getTag(Tag.REGISTRY_ITEMS,
                        NamespacedKey.fromString(in[0].getString(sc)), Material.class));
        MundusPlugin.scriptManager.registerFunction("item.hastag",
                (sc, in) -> ((Tag<Material>) in[1].get(sc))
                        .isTagged(((ItemStack) in[0].get(sc)).getType()));
        MundusPlugin.scriptManager.registerFunction("item.gettype",
                (sc, in) -> ((ItemStack) in[0].get(sc)).getType());
        MundusPlugin.scriptManager.registerFunction("item.getmaxamount",
                (sc, in) -> (double) ((ItemStack) in[0].get(sc)).getMaxStackSize());
        MundusPlugin.scriptManager.registerFunction("item.getamount",
                (sc, in) -> (double) ((ItemStack) in[0].get(sc)).getAmount());
        MundusPlugin.scriptManager.registerConsumer("item.setamount",
                (sc, in) -> ((ItemStack) in[0].get(sc)).setAmount(in[1].getInt(sc)));
        MundusPlugin.scriptManager.registerFunction("item.hasname", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            if(stack.getItemMeta() == null) {
                return false;
            }
            return stack.getItemMeta().hasDisplayName();
        });
        MundusPlugin.scriptManager.registerFunction("item.getname", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            if(stack.getItemMeta() == null) {
                return null;
            }
            return stack.getItemMeta().displayName();
        });
        MundusPlugin.scriptManager.registerConsumer("item.setname", (sc, in) -> {
            ((ItemStack) in[0].get(sc))
                    .editMeta(meta -> meta.displayName((Component) in[1].get(sc)));
        });
        MundusPlugin.scriptManager.registerFunction("item.getlore", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            if(stack.getItemMeta() == null) {
                return new ArrayList<Component>();
            }
            return stack.getItemMeta().lore();
        });
        MundusPlugin.scriptManager.registerConsumer("item.setlore", (sc, in) -> {
            ((ItemStack) in[0].get(sc))
                    .editMeta(meta -> meta.lore((List<Component>) in[1].get(sc)));
        });
        MundusPlugin.scriptManager.registerConsumer("item.addpotion", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            PotionMeta meta = (PotionMeta) stack.getItemMeta();
            meta.addCustomEffect(new PotionEffect(
                    Registry.POTION_EFFECT_TYPE.get(NamespacedKey.fromString(in[1].getString(sc))),
                    in[2].getInt(sc), in[3].getInt(sc)), false);
            stack.setItemMeta(meta);
        });
        MundusPlugin.scriptManager.registerConsumer("item.addflag", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            stack.addItemFlags(ItemFlag.valueOf(in[1].getString(sc)));
        });
        MundusPlugin.scriptManager.registerConsumer("item.removeflag", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            stack.removeItemFlags(ItemFlag.valueOf(in[1].getString(sc)));
        });
        MundusPlugin.scriptManager.registerFunction("item.getallattributes", (sc, in) -> {
            return Registry.ATTRIBUTE.stream()
                    .map(attribute -> {
                        NamespacedKey key = Registry.ATTRIBUTE.getKey(attribute);
                        if(key != null) {
                            return key.toString();
                        }
                        return "minecraft:" + attribute.toString().toLowerCase();
                    })
                    .sorted()
                    .collect(Collectors.toList());
        });
        MundusPlugin.scriptManager.registerConsumer("item.addattribute", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            stack.editMeta(meta -> {
                Attribute a = Registry.ATTRIBUTE.get(NamespacedKey.fromString(in[1].getString(sc)));
                double d = in[3].getDouble(sc);
                Operation o = Operation.valueOf(in[4].getString(sc));
                EquipmentSlot slot = (EquipmentSlot) in[2].get(sc);
                EquipmentSlotGroup slotGroup = slot.getGroup();

                HashMultimap<Attribute, AttributeModifier> map = HashMultimap.create();
                boolean merged = false;
                var oldMap = meta.getAttributeModifiers();
                if(oldMap != null) {
                    for(var entry : oldMap.entries()) {
                        AttributeModifier m = entry.getValue();
                        if(entry.getKey() == a && m.getOperation() == o
                                && m.getSlotGroup() == slotGroup) {
                            NamespacedKey key =
                                    new NamespacedKey(MundusPlugin.instance, "custom");
                            AttributeModifier attributeModifier =
                                    new AttributeModifier(key, d + m.getAmount(), o, slotGroup);
                            map.put(a, attributeModifier);
                            merged = true;
                        } else {
                            map.put(entry.getKey(), entry.getValue());
                        }
                    }
                }
                if(!merged) {
                    NamespacedKey key =
                            new NamespacedKey(MundusPlugin.instance, "custom");
                    AttributeModifier attributeModifier =
                            new AttributeModifier(key, d, o, slotGroup);
                    map.put(a, attributeModifier);
                }
                meta.setAttributeModifiers(map);
            });
        });
        MundusPlugin.scriptManager.registerConsumer("item.clearattributes", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            stack.editMeta(meta -> {
                for(var entry : meta.getAttributeModifiers().entries()) {
                    meta.removeAttributeModifier(entry.getKey(), entry.getValue());
                }
            });
        });
        MundusPlugin.scriptManager.registerFunction("item.hasattributes", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            if(stack.getItemMeta() == null) {
                return false;
            }
            return stack.getItemMeta().hasAttributeModifiers();
        });
        MundusPlugin.scriptManager.registerConsumer("item.adddefaulttags", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            stack.editMeta(meta -> {
                HashMultimap<Attribute, AttributeModifier> full = HashMultimap.create();
                for(EquipmentSlot slot : EquipmentSlot.values()) {
                    var map = stack.getType().getDefaultAttributeModifiers(slot);
                    full.putAll(map);
                }
                meta.setAttributeModifiers(full);
            });
        });
        MundusPlugin.scriptManager.registerFunction("item.clone",
                (sc, in) -> ((ItemStack) in[0].get(sc)).clone());
        MundusPlugin.scriptManager.registerFunction("item.getmaxdamage", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            return (double) stack.getType().getMaxDurability();
        });
        MundusPlugin.scriptManager.registerFunction("item.isdamageable", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            return stack.getItemMeta() instanceof Damageable;
        });
        MundusPlugin.scriptManager.registerFunction("item.getdamage", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            Damageable damage = (Damageable) stack.getItemMeta();
            return (double) damage.getDamage();
        });
        MundusPlugin.scriptManager.registerConsumer("item.setdamage", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            Damageable damage = (Damageable) stack.getItemMeta();
            damage.setDamage(in[1].getInt(sc));
            stack.setItemMeta(damage);
        });
    }
}
