package me.hammerle.kp.snuviscript.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import me.hammerle.kp.CustomItems;
import me.hammerle.kp.KajetansPlugin;
import me.hammerle.kp.NMS;
import me.hammerle.kp.CustomItems.CustomItem;
import net.kyori.adventure.text.Component;

public class ItemCommands {
    @SuppressWarnings("unchecked")
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerFunction("material.get",
                (sc, in) -> Material.matchMaterial(in[0].getString(sc)));
        KajetansPlugin.scriptManager.registerConsumer("material.setcooldown", (sc, in) -> {
            Player p = (Player) in[1].get(sc);
            p.setCooldown((Material) in[0].get(sc), in[2].getInt(sc));
        });

        KajetansPlugin.scriptManager.registerFunction("item.custom.getall",
                (sc, in) -> CustomItem.values());
        KajetansPlugin.scriptManager.registerFunction("item.custom.get", (sc, in) -> {
            try {
                return CustomItem.valueOf(in[0].getString(sc));
            } catch(IllegalArgumentException ex) {
                return null;
            }
        });
        KajetansPlugin.scriptManager.registerFunction("item.custom.new", (sc, in) -> CustomItems
                .build((CustomItem) in[0].get(sc), in.length >= 2 ? in[1].getInt(sc) : 1));
        KajetansPlugin.scriptManager.registerFunction("item.getcustom",
                (sc, in) -> CustomItems.getCustomItem((ItemStack) in[0].get(sc)));

        KajetansPlugin.scriptManager.registerFunction("item.new",
                (sc, in) -> new ItemStack((Material) in[0].get(sc),
                        in.length >= 2 ? in[1].getInt(sc) : 1));
        KajetansPlugin.scriptManager.registerFunction("item.drop", (sc, in) -> {
            Location l = (Location) in[1].get(sc);
            return l.getWorld().dropItem(l, (ItemStack) in[0].get(sc));
        });
        KajetansPlugin.scriptManager.registerFunction("item.gettag",
                (sc, in) -> Bukkit.getTag(Tag.REGISTRY_ITEMS,
                        NamespacedKey.fromString(in[0].getString(sc)), Material.class));
        KajetansPlugin.scriptManager.registerFunction("item.hastag",
                (sc, in) -> ((Tag<Material>) in[1].get(sc))
                        .isTagged(((ItemStack) in[0].get(sc)).getType()));
        KajetansPlugin.scriptManager.registerFunction("item.gettype",
                (sc, in) -> ((ItemStack) in[0].get(sc)).getType());
        KajetansPlugin.scriptManager.registerFunction("item.getmaxamount",
                (sc, in) -> (double) NMS.map((ItemStack) in[0].get(sc)).d());
        KajetansPlugin.scriptManager.registerFunction("item.getamount",
                (sc, in) -> (double) ((ItemStack) in[0].get(sc)).getAmount());
        KajetansPlugin.scriptManager.registerConsumer("item.setamount",
                (sc, in) -> ((ItemStack) in[0].get(sc)).setAmount(in[1].getInt(sc)));
        KajetansPlugin.scriptManager.registerFunction("item.hasname", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            if(stack.getItemMeta() == null) {
                return false;
            }
            return stack.getItemMeta().hasDisplayName();
        });
        KajetansPlugin.scriptManager.registerFunction("item.getname", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            if(stack.getItemMeta() == null) {
                return null;
            }
            return stack.getItemMeta().displayName();
        });
        KajetansPlugin.scriptManager.registerConsumer("item.setname", (sc, in) -> {
            ((ItemStack) in[0].get(sc))
                    .editMeta(meta -> meta.displayName((Component) in[1].get(sc)));
        });
        KajetansPlugin.scriptManager.registerFunction("item.getlore", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            if(stack.getItemMeta() == null) {
                return new ArrayList<Component>();
            }
            return stack.getItemMeta().lore();
        });
        KajetansPlugin.scriptManager.registerConsumer("item.setlore", (sc, in) -> {
            ((ItemStack) in[0].get(sc))
                    .editMeta(meta -> meta.lore((List<Component>) in[1].get(sc)));
        });
        KajetansPlugin.scriptManager.registerConsumer("item.addpotion", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            PotionMeta meta = (PotionMeta) stack.getItemMeta();
            meta.addCustomEffect(new PotionEffect(PotionEffectType.getByName(in[1].getString(sc)),
                    in[2].getInt(sc), in[3].getInt(sc)), false);
            stack.setItemMeta(meta);
        });
        KajetansPlugin.scriptManager.registerConsumer("item.addflag", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            stack.addItemFlags(ItemFlag.valueOf(in[1].getString(sc)));
        });
        KajetansPlugin.scriptManager.registerConsumer("item.removeflag", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            stack.removeItemFlags(ItemFlag.valueOf(in[1].getString(sc)));
        });
        KajetansPlugin.scriptManager.registerConsumer("item.addattribute", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            stack.editMeta(meta -> {
                Attribute a = Attribute.valueOf(in[1].getString(sc));
                double d = in[3].getDouble(sc);
                Operation o = Operation.valueOf(in[4].getString(sc));
                EquipmentSlot slot = (EquipmentSlot) in[2].get(sc);

                HashMultimap<Attribute, AttributeModifier> map = HashMultimap.create();
                boolean merged = false;
                var oldMap = meta.getAttributeModifiers();
                if(oldMap != null) {
                    for(var entry : oldMap.entries()) {
                        AttributeModifier m = entry.getValue();
                        if(entry.getKey() == a && m.getOperation() == o && m.getSlot() == slot) {
                            UUID uuid = UUID.randomUUID();
                            map.put(a, new AttributeModifier(uuid, uuid.toString(),
                                    d + m.getAmount(), o, slot));
                            merged = true;
                        } else {
                            map.put(entry.getKey(), entry.getValue());
                        }
                    }
                }
                if(!merged) {
                    UUID uuid = UUID.randomUUID();
                    map.put(a, new AttributeModifier(uuid, uuid.toString(), d, o, slot));
                }
                meta.setAttributeModifiers(map);
            });
        });
        KajetansPlugin.scriptManager.registerConsumer("item.clearattributes", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            stack.editMeta(meta -> {
                for(var entry : meta.getAttributeModifiers().entries()) {
                    meta.removeAttributeModifier(entry.getKey(), entry.getValue());
                }
            });
        });
        KajetansPlugin.scriptManager.registerFunction("item.hasattributes", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            if(stack.getItemMeta() == null) {
                return false;
            }
            return stack.getItemMeta().hasAttributeModifiers();
        });
        KajetansPlugin.scriptManager.registerConsumer("item.adddefaulttags", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            stack.editMeta(meta -> {
                for(EquipmentSlot slot : EquipmentSlot.values()) {
                    var map = stack.getType().getItemAttributes(slot);
                    for(var entry : map.entries()) {
                        meta.addAttributeModifier(entry.getKey(), entry.getValue());
                    }
                }
            });
        });
        KajetansPlugin.scriptManager.registerFunction("item.clone",
                (sc, in) -> ((ItemStack) in[0].get(sc)).clone());
        KajetansPlugin.scriptManager.registerFunction("item.getmaxdamage", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            return (double) stack.getType().getMaxDurability();
        });
        KajetansPlugin.scriptManager.registerFunction("item.isdamageable", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            return stack.getItemMeta() instanceof Damageable;
        });
        KajetansPlugin.scriptManager.registerFunction("item.getdamage", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            Damageable damage = (Damageable) stack.getItemMeta();
            return (double) damage.getDamage();
        });
        KajetansPlugin.scriptManager.registerConsumer("item.setdamage", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            Damageable damage = (Damageable) stack.getItemMeta();
            damage.setDamage(in[1].getInt(sc));
            stack.setItemMeta(damage);
        });
    }
}
