package me.hammerle.kp.snuviscript.commands;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import me.hammerle.kp.KajetansPlugin;
import me.hammerle.kp.NMS;
import me.hammerle.snuviscript.exceptions.StackTrace;

public class LivingCommands {
    private static String getName(Attribute a) {
        String name = a.toString();
        name = name.toLowerCase();
        name = name.replace("_", "");
        if(name.startsWith("generic")) {
            return name.substring(7);
        }
        return name;
    }

    @SuppressWarnings("unchecked")
    public static void registerFunctions() {
        for(Attribute a : Attribute.values()) {
            String name = getName(a);
            KajetansPlugin.scriptManager.registerConsumer("living.set" + name, (sc, in) -> {
                LivingEntity liv = (LivingEntity) in[0].get(sc);
                AttributeInstance instance = liv.getAttribute(a);
                if(instance == null) {
                    return;
                }
                instance.setBaseValue(in[1].getDouble(sc));
            });
            KajetansPlugin.scriptManager.registerFunction("living.get" + name, (sc, in) -> {
                LivingEntity liv = (LivingEntity) in[0].get(sc);
                AttributeInstance instance = liv.getAttribute(a);
                if(instance == null) {
                    return 0.0;
                }
                return instance.getBaseValue();
            });
            KajetansPlugin.scriptManager.registerConsumer("living.reset" + name, (sc, in) -> {
                LivingEntity liv = (LivingEntity) in[0].get(sc);
                AttributeInstance instance = liv.getAttribute(a);
                if(instance == null) {
                    return;
                }
                instance.setBaseValue(instance.getDefaultValue());
            });
        }
        KajetansPlugin.scriptManager.registerConsumer("living.setai", (sc, in) -> {
            LivingEntity ent = (LivingEntity) in[0].get(sc);
            ent.setAI(in[1].getBoolean(sc));
        });
        KajetansPlugin.scriptManager.registerFunction("living.near", (sc, in) -> {
            Object o = in[0].get(sc);
            Location l;
            if(o instanceof Location) {
                l = (Location) o;
            } else {
                l = ((LivingEntity) o).getLocation();
            }
            return l.getWorld().getNearbyLivingEntities(l, in[1].getDouble(sc));
        });
        KajetansPlugin.scriptManager.registerFunction("living.gethealth",
                (sc, in) -> (double) ((LivingEntity) in[0].get(sc)).getHealth());
        KajetansPlugin.scriptManager.registerConsumer("living.sethealth",
                (sc, in) -> ((LivingEntity) in[0].get(sc)).setHealth(in[1].getDouble(sc)));
        KajetansPlugin.scriptManager.registerConsumer("living.damage", (sc, in) -> {
            LivingEntity liv = (LivingEntity) in[0].get(sc);
            float damage = in[1].getFloat(sc);
            DamageSource damageSource = (DamageSource) in[2].get(sc);

            StackTrace trace = sc.getStackTrace();
            KajetansPlugin.scheduleTask(() -> {
                try {
                    liv.damage(damage, damageSource);
                } catch(Exception ex) {
                    sc.getScriptManager().getLogger().print(null, ex, "living.damage", sc.getName(),
                            sc, trace);
                }
            });
        });
        KajetansPlugin.scriptManager.registerConsumer("living.heal", (sc, in) -> {
            LivingEntity liv = (LivingEntity) in[0].get(sc);
            float heal = in[1].getFloat(sc);
            StackTrace trace = sc.getStackTrace();
            KajetansPlugin.scheduleTask(() -> {
                try {
                    NMS.map(liv).heal(heal, EntityRegainHealthEvent.RegainReason.CUSTOM);
                } catch(Exception ex) {
                    sc.getScriptManager().getLogger().print(null, ex, "living.heal", sc.getName(),
                            sc, trace);
                }
            });
        });
        KajetansPlugin.scriptManager.registerFunction("living.shootprojectile", (sc, in) -> {
            LivingEntity liv = (LivingEntity) in[0].get(sc);
            return liv.launchProjectile(
                    (Class<? extends Projectile>) Class.forName(in[1].getString(sc)));
        });
        KajetansPlugin.scriptManager.registerConsumer("living.setequip", (sc, in) -> {
            LivingEntity liv = (LivingEntity) in[0].get(sc);
            liv.getEquipment().setItem((EquipmentSlot) in[1].get(sc), (ItemStack) in[2].get(sc));
        });
        KajetansPlugin.scriptManager.registerFunction("living.getequip", (sc, in) -> {
            LivingEntity liv = (LivingEntity) in[0].get(sc);
            return liv.getEquipment().getItem((EquipmentSlot) in[1].get(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("living.setinvisible", (sc, in) -> {
            ((LivingEntity) in[0].get(sc)).setInvisible(in[1].getBoolean(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("living.addeffect", (sc, in) -> {
            LivingEntity base = (LivingEntity) in[0].get(sc);
            boolean showParticles = in.length >= 5 ? in[4].getBoolean(sc) : true;
            String potionEffectName = in[1].getString(sc).toLowerCase();
            NamespacedKey key = NamespacedKey.minecraft(potionEffectName);
            PotionEffectType potionEffectType = Registry.POTION_EFFECT_TYPE.get(key);
            base.addPotionEffect(new PotionEffect(potionEffectType,
                    in[2].getInt(sc), in[3].getInt(sc), showParticles, showParticles));
        });
        KajetansPlugin.scriptManager.registerConsumer("living.cleareffects", (sc, in) -> {
            LivingEntity liv = (LivingEntity) in[0].get(sc);
            for(PotionEffect effect : liv.getActivePotionEffects()) {
                liv.removePotionEffect(effect.getType());
            }
        });
        KajetansPlugin.scriptManager.registerFunction("living.geteffectamplifier", (sc, in) -> {
            NamespacedKey key = NamespacedKey.minecraft(in[1].getString(sc));
            PotionEffectType effectType = Registry.POTION_EFFECT_TYPE.get(key);
            PotionEffect effect = ((LivingEntity) in[0].get(sc)).getPotionEffect(effectType);
            return (double) (effect == null ? -1 : effect.getAmplifier());
        });
    }
}
