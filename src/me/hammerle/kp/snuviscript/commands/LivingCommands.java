package me.hammerle.kp.snuviscript.commands;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityRegainHealthEvent;
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
            var damageSource = NMS.toDamageSource(in[2].get(sc));
            StackTrace trace = sc.getStackTrace();
            KajetansPlugin.scheduleTask(() -> {
                try {
                    NMS.map(liv).a(damageSource, damage);
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
            ItemStack stack = ((ItemStack) in[2].get(sc));
            switch(in[1].getString(sc)) {
                case "hand":
                    liv.getEquipment().setItemInMainHand(stack);
                    return;
                case "head":
                    liv.getEquipment().setHelmet(stack);
                    return;
                case "chest":
                    liv.getEquipment().setChestplate(stack);
                    return;
                case "legs":
                    liv.getEquipment().setLeggings(stack);
                    return;
                case "feet":
                    liv.getEquipment().setBoots(stack);
                    return;
                case "offhand":
                    liv.getEquipment().setItemInOffHand(stack);
                    return;
            }
        });
        KajetansPlugin.scriptManager.registerFunction("living.getequip", (sc, in) -> {
            LivingEntity liv = (LivingEntity) in[0].get(sc);
            switch(in[1].getString(sc)) {
                case "hand":
                    return liv.getEquipment().getItemInMainHand();
                case "head":
                    return liv.getEquipment().getHelmet();
                case "chest":
                    return liv.getEquipment().getChestplate();
                case "legs":
                    return liv.getEquipment().getLeggings();
                case "feet":
                    return liv.getEquipment().getBoots();
                case "offhand":
                    return liv.getEquipment().getItemInOffHand();
            }
            return null;
        });
        KajetansPlugin.scriptManager.registerConsumer("living.setinvisible", (sc, in) -> {
            ((LivingEntity) in[0].get(sc)).setInvisible(in[1].getBoolean(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("living.addeffect", (sc, in) -> {
            LivingEntity base = (LivingEntity) in[0].get(sc);
            boolean showParticles = in.length >= 5 ? in[4].getBoolean(sc) : true;
            base.addPotionEffect(new PotionEffect(PotionEffectType.getByName(in[1].getString(sc)),
                    in[2].getInt(sc), in[3].getInt(sc), showParticles, showParticles));
        });
        KajetansPlugin.scriptManager.registerConsumer("living.cleareffects", (sc, in) -> {
            LivingEntity liv = (LivingEntity) in[0].get(sc);
            for(PotionEffect effect : liv.getActivePotionEffects()) {
                liv.removePotionEffect(effect.getType());
            }
        });
        KajetansPlugin.scriptManager.registerFunction("living.geteffectamplifier", (sc, in) -> {
            PotionEffect effect = ((LivingEntity) in[0].get(sc))
                    .getPotionEffect(PotionEffectType.getByName(in[1].getString(sc)));
            return effect == null ? 0 : effect.getAmplifier() + 1;
        });
    }
}
