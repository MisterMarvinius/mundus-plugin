package me.hammerle.mp.snuviscript.commands;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftLeash;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LeashHitch;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import me.hammerle.mp.MundusPlugin;
import me.hammerle.mp.NMS;
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
        for(Attribute a : Registry.ATTRIBUTE) {
            String name = getName(a);
            MundusPlugin.scriptManager.registerConsumer("living.set" + name, (sc, in) -> {
                LivingEntity liv = (LivingEntity) in[0].get(sc);
                AttributeInstance instance = liv.getAttribute(a);
                if(instance == null) {
                    return;
                }
                instance.setBaseValue(in[1].getDouble(sc));
            });
            MundusPlugin.scriptManager.registerFunction("living.get" + name, (sc, in) -> {
                LivingEntity liv = (LivingEntity) in[0].get(sc);
                AttributeInstance instance = liv.getAttribute(a);
                if(instance == null) {
                    return 0.0;
                }
                return instance.getBaseValue();
            });
            MundusPlugin.scriptManager.registerConsumer("living.reset" + name, (sc, in) -> {
                LivingEntity liv = (LivingEntity) in[0].get(sc);
                AttributeInstance instance = liv.getAttribute(a);
                if(instance == null) {
                    return;
                }
                instance.setBaseValue(instance.getDefaultValue());
            });
        }
        MundusPlugin.scriptManager.registerConsumer("living.setai", (sc, in) -> {
            LivingEntity ent = (LivingEntity) in[0].get(sc);
            ent.setAI(in[1].getBoolean(sc));
        });
        MundusPlugin.scriptManager.registerFunction("living.near", (sc, in) -> {
            Object o = in[0].get(sc);
            Location l;
            if(o instanceof Location) {
                l = (Location) o;
            } else {
                l = ((LivingEntity) o).getLocation();
            }
            return l.getWorld().getNearbyLivingEntities(l, in[1].getDouble(sc));
        });
        MundusPlugin.scriptManager.registerFunction("living.gethealth",
                (sc, in) -> (double) ((LivingEntity) in[0].get(sc)).getHealth());
        MundusPlugin.scriptManager.registerConsumer("living.sethealth",
                (sc, in) -> ((LivingEntity) in[0].get(sc)).setHealth(in[1].getDouble(sc)));
        MundusPlugin.scriptManager.registerConsumer("living.damage", (sc, in) -> {
            LivingEntity liv = (LivingEntity) in[0].get(sc);
            float damage = in[1].getFloat(sc);
            DamageSource damageSource = (DamageSource) in[2].get(sc);

            StackTrace trace = sc.getStackTrace();
            MundusPlugin.scheduleTask(() -> {
                try {
                    liv.damage(damage, damageSource);
                } catch(Exception ex) {
                    sc.getScriptManager().getLogger().print(null, ex, "living.damage", sc.getName(),
                            sc, trace);
                }
            });
        });
        MundusPlugin.scriptManager.registerConsumer("living.heal", (sc, in) -> {
            LivingEntity liv = (LivingEntity) in[0].get(sc);
            float heal = in[1].getFloat(sc);
            StackTrace trace = sc.getStackTrace();
            MundusPlugin.scheduleTask(() -> {
                try {
                    NMS.map(liv).heal(heal, EntityRegainHealthEvent.RegainReason.CUSTOM);
                } catch(Exception ex) {
                    sc.getScriptManager().getLogger().print(null, ex, "living.heal", sc.getName(),
                            sc, trace);
                }
            });
        });
        MundusPlugin.scriptManager.registerFunction("living.shootprojectile", (sc, in) -> {
            LivingEntity liv = (LivingEntity) in[0].get(sc);
            return liv.launchProjectile(
                    (Class<? extends Projectile>) Class.forName(in[1].getString(sc)));
        });
        MundusPlugin.scriptManager.registerConsumer("living.setequip", (sc, in) -> {
            LivingEntity liv = (LivingEntity) in[0].get(sc);
            liv.getEquipment().setItem((EquipmentSlot) in[1].get(sc), (ItemStack) in[2].get(sc));
        });
        MundusPlugin.scriptManager.registerFunction("living.getequip", (sc, in) -> {
            LivingEntity liv = (LivingEntity) in[0].get(sc);
            return liv.getEquipment().getItem((EquipmentSlot) in[1].get(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("living.setinvisible", (sc, in) -> {
            ((LivingEntity) in[0].get(sc)).setInvisible(in[1].getBoolean(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("living.addeffect", (sc, in) -> {
            LivingEntity base = (LivingEntity) in[0].get(sc);
            boolean showParticles = in.length >= 5 ? in[4].getBoolean(sc) : true;
            String potionEffectName = in[1].getString(sc).toLowerCase();
            NamespacedKey key = NamespacedKey.minecraft(potionEffectName);
            PotionEffectType potionEffectType = Registry.POTION_EFFECT_TYPE.get(key);
            base.addPotionEffect(new PotionEffect(potionEffectType,
                    in[2].getInt(sc), in[3].getInt(sc), showParticles, showParticles));
        });
        MundusPlugin.scriptManager.registerConsumer("living.cleareffects", (sc, in) -> {
            LivingEntity liv = (LivingEntity) in[0].get(sc);
            for(PotionEffect effect : liv.getActivePotionEffects()) {
                liv.removePotionEffect(effect.getType());
            }
        });
        MundusPlugin.scriptManager.registerFunction("living.geteffectamplifier", (sc, in) -> {
            NamespacedKey key = NamespacedKey.minecraft(in[1].getString(sc));
            PotionEffectType effectType = Registry.POTION_EFFECT_TYPE.get(key);
            PotionEffect effect = ((LivingEntity) in[0].get(sc)).getPotionEffect(effectType);
            return (double) (effect == null ? -1 : effect.getAmplifier());
        });
        MundusPlugin.scriptManager.registerFunction("living.isgliding", (sc, in) -> {
            LivingEntity liv = (LivingEntity) in[0].get(sc);
            return liv.isGliding();
        });
        MundusPlugin.scriptManager.registerFunction("living.isswimming", (sc, in) -> {
            LivingEntity liv = (LivingEntity) in[0].get(sc);
            return liv.isSwimming();
        });
        MundusPlugin.scriptManager.registerFunction("living.isglowing", (sc, in) -> {
            LivingEntity liv = (LivingEntity) in[0].get(sc);
            return liv.isGlowing();
        });
        MundusPlugin.scriptManager.registerFunction("living.issleeping", (sc, in) -> {
            LivingEntity liv = (LivingEntity) in[0].get(sc);
            return liv.isSleeping();
        });
        MundusPlugin.scriptManager.registerFunction("living.isleashed", (sc, in) -> {
            LivingEntity liv = (LivingEntity) in[0].get(sc);
            return liv.isLeashed();
        });
        MundusPlugin.scriptManager.registerFunction("living.getleashholder", (sc, in) -> {
            try {
                LivingEntity liv = (LivingEntity) in[0].get(sc);
                Entity e = liv.getLeashHolder();
                if(liv instanceof CraftLeash) {
                    return liv.getLocation();
                } else {
                    return e;
                }
            } catch(Exception e) {
                return null;
            }
        });
        MundusPlugin.scriptManager.registerFunction("living.setleashholder", (sc, in) -> {
            LivingEntity liv = (LivingEntity) in[0].get(sc);
            Object o = in[1].get(sc);

            if(o instanceof Entity) {
                return liv.setLeashHolder((Entity) o);
            } else if(o instanceof Location) {
                Location loc = (Location) o;
                Block block = loc.getBlock();
                if(!block.getType().name().endsWith("_FENCE")) {
                    throw new IllegalArgumentException("Location must be a fence to leash to it.");
                }
                Location hitchLocation = loc.add(0.5, 0.5, 0.5);
                LeashHitch hitch = (LeashHitch) block.getWorld().spawnEntity(hitchLocation,
                        EntityType.LEASH_KNOT);
                return liv.setLeashHolder(hitch);
            } else {
                throw new IllegalArgumentException(
                        "Second argument must be an Entity or a Location.");
            }
        });
        MundusPlugin.scriptManager.registerConsumer("living.unleash", (sc, in) -> {
            LivingEntity liv = (LivingEntity) in[0].get(sc);
            liv.setLeashHolder(null);
        });
        MundusPlugin.scriptManager.registerConsumer("living.swingmainhand", (sc, in) -> {
            LivingEntity liv = (LivingEntity) in[0].get(sc);
            liv.swingMainHand();
        });
        MundusPlugin.scriptManager.registerConsumer("living.swingoffhand", (sc, in) -> {
            LivingEntity liv = (LivingEntity) in[0].get(sc);
            liv.swingOffHand();
        });
    }
}
