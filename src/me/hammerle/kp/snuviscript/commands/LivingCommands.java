/*package me.km.snuviscript.commands;

import java.util.UUID;
import me.hammerle.snuviscript.code.ScriptManager;
import me.hammerle.snuviscript.exceptions.StackTrace;
import me.km.scheduler.SnuviScheduler;
import me.km.utils.Location;
import me.km.utils.ReflectionUtils;
import me.km.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.util.DamageSource;
import net.minecraftforge.registries.ForgeRegistries;
import me.km.entities.EntityItemProjectile;
import static me.km.snuviscript.commands.CommandUtils.getNamedClass;
import net.minecraft.entity.item.*;
import net.minecraft.entity.projectile.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.inventory.EquipmentSlotType;

public class LivingCommands {
    private static String getName(Attribute attribute) {
        String name = attribute.getAttributeName();
        int index = name.lastIndexOf(".");
        if(index != -1) {
            name = name.substring(index + 1);
        }
        name = name.replace("_", "");
        name = name.toLowerCase();
        return name;
    }

    public static void registerFunctions(ScriptManager sm, SnuviScheduler scheduler) {
        for(Attribute attribute : ForgeRegistries.ATTRIBUTES.getValues()) {
            String name = getName(attribute);
            sm.registerConsumer("living.set" + name, (sc, in) -> {
                ModifiableAttributeInstance a =
                        ((LivingEntity) in[0].get(sc)).getAttribute(attribute);
                if(a == null) {
                    return;
                }
                double amount = in[1].getDouble(sc) - a.getBaseValue();
                UUID uuid = new UUID(name.length(), name.hashCode());
                a.removeModifier(uuid);
                a.applyNonPersistentModifier(new AttributeModifier(uuid, name, amount,
                        AttributeModifier.Operation.ADDITION));
            });
            sm.registerConsumer("living.setpersistent" + name, (sc, in) -> {
                ModifiableAttributeInstance a =
                        ((LivingEntity) in[0].get(sc)).getAttribute(attribute);
                if(a == null) {
                    return;
                }
                double amount = in[1].getDouble(sc) - a.getBaseValue();
                UUID uuid = new UUID(name.length(), name.hashCode());
                a.removeModifier(uuid);
                a.applyPersistentModifier(new AttributeModifier(uuid, name, amount,
                        AttributeModifier.Operation.ADDITION));
            });
            sm.registerFunction("living.get" + name, (sc, in) -> {
                ModifiableAttributeInstance a =
                        ((LivingEntity) in[0].get(sc)).getAttribute(attribute);
                return a == null ? null : a.getValue();
            });
            sm.registerFunction("living.getbase" + name, (sc, in) -> {
                ModifiableAttributeInstance a =
                        ((LivingEntity) in[0].get(sc)).getAttribute(attribute);
                return a == null ? null : a.getBaseValue();
            });
        }
        sm.registerConsumer("living.removeai", (sc, in) -> {
            LivingEntity ent = (LivingEntity) in[0].get(sc);
            if(ent instanceof MobEntity) {
                MobEntity mob = (MobEntity) in[0].get(sc);
                ReflectionUtils.getGoals(mob.goalSelector).clear();
                ReflectionUtils.getGoals(mob.targetSelector).clear();
            }
        });
        sm.registerFunction("living.near", (sc, in) -> {
            Object o = in[0].get(sc);
            if(o instanceof Location) {
                return Utils.getLiving((Location) o, in[1].getDouble(sc));
            }
            return Utils.getLiving((Entity) o, in[1].getDouble(sc));
        });
        sm.registerFunction("living.gethealth",
                (sc, in) -> (double) ((LivingEntity) in[0].get(sc)).getHealth());
        sm.registerConsumer("living.sethealth",
                (sc, in) -> ((LivingEntity) in[0].get(sc)).setHealth(in[1].getFloat(sc)));
        sm.registerConsumer("living.damage", (sc, in) -> {
            LivingEntity liv = (LivingEntity) in[0].get(sc);
            float damage = in[1].getFloat(sc);
            DamageSource damageSource =
                    (in.length >= 3) ? (DamageSource) in[2].get(sc) : DamageSource.GENERIC;
            StackTrace trace = sc.getStackTrace();
            scheduler.scheduleTask("living.damage", () -> {
                try {
                    liv.attackEntityFrom(damageSource, damage);
                } catch(Exception ex) {
                    sc.getScriptManager().getLogger().print(null, ex, "living.damage", sc.getName(),
                            sc, trace);
                }
            });
        });
        sm.registerConsumer("living.heal", (sc, in) -> {
            LivingEntity liv = (LivingEntity) in[0].get(sc);
            float heal = in[1].getFloat(sc);
            StackTrace trace = sc.getStackTrace();
            scheduler.scheduleTask("living.heal", () -> {
                try {
                    liv.heal(heal);
                } catch(Exception ex) {
                    sc.getScriptManager().getLogger().print(null, ex, "living.heal", sc.getName(),
                            sc, trace);
                }
            });
        });
        sm.registerFunction("living.shootprojectile",
                (sc, in) -> launchProjectile((LivingEntity) in[0].get(sc),
                        getNamedClass(in[1].getString(sc)), in[2].getDouble(sc),
                        in.length >= 4 ? in[3].get(sc) : null));
        sm.registerFunction("living.isblocking",
                (sc, in) -> ((LivingEntity) in[0].get(sc)).isActiveItemStackBlocking());
        sm.registerConsumer("living.setequip", (sc, in) -> {
            LivingEntity liv = (LivingEntity) in[0].get(sc);
            ItemStack stack = ((ItemStack) in[2].get(sc)).copy();
            switch(in[1].getString(sc)) {
                case "hand":
                    liv.setItemStackToSlot(EquipmentSlotType.MAINHAND, stack);
                    return;
                case "head":
                    liv.setItemStackToSlot(EquipmentSlotType.HEAD, stack);
                    return;
                case "chest":
                    liv.setItemStackToSlot(EquipmentSlotType.CHEST, stack);
                    return;
                case "legs":
                    liv.setItemStackToSlot(EquipmentSlotType.LEGS, stack);
                    return;
                case "feet":
                    liv.setItemStackToSlot(EquipmentSlotType.FEET, stack);
                    return;
                case "offhand":
                    liv.setItemStackToSlot(EquipmentSlotType.OFFHAND, stack);
            }
        });
        sm.registerFunction("living.getequip", (sc, in) -> {
            LivingEntity liv = (LivingEntity) in[0].get(sc);
            switch(in[1].getString(sc)) {
                case "hand":
                    return liv.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
                case "head":
                    return liv.getItemStackFromSlot(EquipmentSlotType.HEAD);
                case "chest":
                    return liv.getItemStackFromSlot(EquipmentSlotType.CHEST);
                case "legs":
                    return liv.getItemStackFromSlot(EquipmentSlotType.LEGS);
                case "feet":
                    return liv.getItemStackFromSlot(EquipmentSlotType.FEET);
                case "offhand":
                    return liv.getItemStackFromSlot(EquipmentSlotType.OFFHAND);
            }
            return ItemStack.EMPTY;
        });
    }

    @SuppressWarnings("unchecked")
    private static <T> T launchProjectile(LivingEntity liv, Class<? extends T> projectile,
            double scale, Object data) {
        World w = liv.world;
        Entity launch = null;

        if(EntityItemProjectile.class == projectile) {
            if(data == null) {
                throw new NullPointerException("Data musn't be null for EntityItemProjectile");
            }
            ItemStack stack = (ItemStack) data;
            if(stack.isEmpty()) {
                throw new IllegalArgumentException("Empty ItemStack not allowed here");
            }
            launch = new EntityItemProjectile(liv, stack.copy());
            ((EntityItemProjectile) launch).setHeadingFromThrower(liv, liv.rotationPitch,
                    liv.rotationYaw, 0.0f, 1.5f, 1.0f);
        } else if(SnowballEntity.class == projectile) {
            launch = new SnowballEntity(w, liv);
            ((SnowballEntity) launch).shoot(liv.rotationPitch, liv.rotationYaw, 0.0f, 1.5f, 1.0f);
        } else if(EggEntity.class == projectile) {
            launch = new EggEntity(w, liv);
            ((EggEntity) launch).shoot(liv.rotationPitch, liv.rotationYaw, 0.0f, 1.5f, 1.0f);
        } else if(EnderPearlEntity.class == projectile) {
            launch = new EnderPearlEntity(w, liv);
            ((EnderPearlEntity) launch).shoot(liv.rotationPitch, liv.rotationYaw, 0.0f, 1.5f, 1.0f);
        } else if(PotionEntity.class == projectile) {
            launch = new PotionEntity(w, liv);
            ((PotionEntity) launch).setItem((ItemStack) data);
            ((PotionEntity) launch).shoot(liv.rotationPitch, liv.rotationYaw, -20.0f, 0.5f, 1.0f);
        } else if(ExperienceBottleEntity.class == projectile) {
            launch = new ExperienceBottleEntity(w, liv);
            ((ExperienceBottleEntity) launch).shoot(liv.rotationPitch, liv.rotationYaw, -20.0f,
                    0.7f, 1.0f);
        } else if(AbstractArrowEntity.class.isAssignableFrom(projectile)) {
            if(SpectralArrowEntity.class == projectile) {
                launch = new SpectralArrowEntity(w, liv);
            } else {
                launch = new ArrowEntity(w, liv);
                if(data != null) {
                    ((ArrowEntity) launch).setPotionEffect((ItemStack) data);
                }
            }
            ((AbstractArrowEntity) launch).shoot(liv.rotationPitch, liv.rotationYaw, 0.0F, 3.0F,
                    1.0F);
        } else if(DamagingProjectileEntity.class.isAssignableFrom(projectile)) {
            Vector3d v = liv.getLookVec().scale(10);
            if(SmallFireballEntity.class == projectile) {
                launch = new SmallFireballEntity(w, liv, v.x, v.y, v.z);
            } else if(WitherSkullEntity.class == projectile) {
                launch = new WitherSkullEntity(w, liv, v.x, v.y, v.z);
            } else if(DragonFireballEntity.class == projectile) {
                launch = new DragonFireballEntity(w, liv, v.x, v.y, v.z);
            } else {
                launch = new FireballEntity(w, liv, v.x, v.y, v.z);
            }
        } else {
            return null;
        }

        launch.setMotion(launch.getMotion().scale(scale));
        w.addEntity(launch);
        return (T) launch;
    }
}
*/
