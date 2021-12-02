/*package me.km.snuviscript.commands;

import me.hammerle.snuviscript.code.ScriptManager;
import static me.km.snuviscript.commands.CommandUtils.getNamedClass;
import me.km.utils.Location;
import me.km.utils.Mapper;
import me.km.utils.Utils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.*;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class EntityCommands {
    @SuppressWarnings("unchecked")
    public static void registerFunctions(ScriptManager sm) {
        sm.registerConsumer("entity.setnopickup", (sc, in) -> {
            ((AbstractArrowEntity) in[0].get(sc)).pickupStatus =
                    AbstractArrowEntity.PickupStatus.DISALLOWED;
        });
        sm.registerFunction("entity.getenchantmentmodifier",
                (sc, in) -> EnchantmentHelper.getEnchantmentModifierDamage(
                        ((LivingEntity) in[0].get(sc)).getArmorInventoryList(),
                        (DamageSource) in[1].get(sc)));
        sm.registerConsumer("entity.setburning",
                (sc, in) -> ((Entity) in[0].get(sc)).forceFireTicks(in[1].getInt(sc)));
        sm.registerFunction("entity.isburning", (sc, in) -> ((Entity) in[0].get(sc)).isBurning());
        sm.registerFunction("entity.getlook", (sc, in) -> {
            Object[] o = new Object[3];
            Vector3d v = ((Entity) in[0].get(sc)).getLookVec();
            o[0] = v.x;
            o[1] = v.y;
            o[2] = v.z;
            return o;
        });
        sm.registerFunction("entity.getmotion", (sc, in) -> {
            Object[] o = new Object[3];
            Vector3d v = ((Entity) in[0].get(sc)).getMotion();
            o[0] = v.x;
            o[1] = v.y;
            o[2] = v.z;
            return o;
        });
        sm.registerFunction("entity.getlocation", (sc, in) -> new Location((Entity) in[0].get(sc)));
        sm.registerConsumer("entity.setname", (sc, in) -> {
            Entity ent = (Entity) in[0].get(sc);
            ent.setCustomName(new StringTextComponent(in[1].getString(sc)));
            if(in.length >= 3) {
                ent.setCustomNameVisible(in[2].getBoolean(sc));
                return;
            }
            ent.setCustomNameVisible(false);
        });
        sm.registerFunction("entity.getname",
                (sc, in) -> ((Entity) in[0].get(sc)).getDisplayName().getString());
        sm.registerConsumer("entity.throw", (sc, in) -> {
            Entity ent = (Entity) in[0].get(sc);
            ent.setMotion(in[1].getDouble(sc), in[2].getDouble(sc), in[3].getDouble(sc));
            ent.velocityChanged = true;
        });
        sm.registerConsumer("entity.teleport", (sc, in) -> {
            Entity ent = (Entity) in[0].get(sc);
            Location l = (Location) in[1].get(sc);
            if(l.getWorld() == null) {
                throw new IllegalArgumentException("world must not be null");
            }
            if(ent instanceof ServerPlayerEntity) {
                ServerPlayerEntity p = (ServerPlayerEntity) ent;

                p.stopRiding();
                if(p.isSleeping()) {
                    p.stopSleepInBed(true, true);
                }

                float yaw = l.getYaw() != 0.0f ? l.getYaw() : ent.rotationYaw;
                float pitch = l.getPitch() != 0.0f ? l.getPitch() : ent.rotationPitch;
                p.teleport((ServerWorld) l.getWorld(), l.getX(), l.getY(), l.getZ(), yaw, pitch);
            } else {
                if(ent.world != l.getWorld()) {
                    ServerWorld ws = (ServerWorld) l.getWorld();
                    ent.changeDimension(ws);
                }
                float yaw = l.getYaw() != 0.0f ? l.getYaw() : ent.rotationYaw;
                float pitch = l.getPitch() != 0.0f ? l.getPitch() : ent.rotationPitch;
                ent.setLocationAndAngles(l.getX(), l.getY(), l.getZ(), yaw, pitch);
            }
        });

        sm.registerConsumer("entity.removeall", (sc, in) -> {
            Class<? extends Entity> c =
                    (Class<? extends Entity>) getNamedClass(in[0].getString(sc));
            if(c == Entity.class) {
                return;
            }
            Location l = (Location) in[1].get(sc);
            Utils.getEntities(l.getWorld(), l.getX(), l.getY(), l.getZ(), in[2].getDouble(sc), c)
                    .stream().forEach(ent -> {
                        ent.remove();
                    });
        });
        sm.registerConsumer("entity.remove", (sc, in) -> ((Entity) in[0].get(sc)).remove());
        sm.registerConsumer("entity.setinvulnerable", (sc, in) -> {
            ((Entity) in[0].get(sc)).setInvulnerable(in[1].getBoolean(sc));
        });
        sm.registerConsumer("entity.setsilent", (sc, in) -> {
            ((Entity) in[0].get(sc)).setSilent(in[1].getBoolean(sc));
        });
        sm.registerConsumer("entity.setinvisible", (sc, in) -> {
            ((Entity) in[0].get(sc)).setInvisible(in[1].getBoolean(sc));
        });
        sm.registerConsumer("entity.mount", (sc, in) -> {
            ((Entity) in[0].get(sc)).startRiding(((Entity) in[1].get(sc)));
        });
        sm.registerConsumer("entity.unmount", (sc, in) -> {
            ((Entity) in[0].get(sc)).stopRiding();
        });
        sm.registerConsumer("entity.addeffect", (sc, in) -> {
            LivingEntity base = (LivingEntity) in[0].get(sc);
            Effect potion = Mapper.getPotion(in[1].getString(sc));
            if(potion == null) { // doing this only to prevent EffectInstance doing shit
                throw new IllegalArgumentException("potion does not exist");
            }
            if(base.isPotionActive(potion)) {
                base.removePotionEffect(potion);
            }
            boolean showParticles = in.length >= 5 ? in[4].getBoolean(sc) : true;
            base.addPotionEffect(new EffectInstance(potion, in[2].getInt(sc), in[3].getInt(sc),
                    false, showParticles));
        });
        sm.registerConsumer("entity.cleareffects", (sc, in) -> {
            ((LivingEntity) in[0].get(sc)).clearActivePotions();
        });
        sm.registerFunction("entity.geteffectamplifier", (sc, in) -> {
            EffectInstance effect = ((LivingEntity) in[0].get(sc))
                    .getActivePotionEffect(Mapper.getPotion(in[1].getString(sc)));
            return effect == null ? 0 : effect.getAmplifier() + 1;
        });
        sm.registerConsumer("entity.spawnitemframe", (sc, in) -> {
            Location l = ((Location) in[0].get(sc));
            ItemFrameEntity frame = new ItemFrameEntity(l.getWorld(), l.getBlockPos(),
                    Direction.byName(in[1].getString(sc)));
            frame.setDisplayedItem(((ItemStack) in[2].get(sc))); // copy happens in internals
            l.getWorld().addEntity(frame);
        });
        sm.registerFunction("entity.getitemfromframe",
                (sc, in) -> ((ItemFrameEntity) in[0].get(sc)).getDisplayedItem());
        sm.registerFunction("entity.get", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            return Utils.getEntity(l.getWorld(), l.getX(), l.getY(), l.getZ(), in[1].getDouble(sc),
                    (Class<? extends Entity>) getNamedClass(in[2].getString(sc)));
        });
        sm.registerFunction("entity.getpotiontype",
                (sc, in) -> PotionUtils.getPotionFromItem(((PotionEntity) in[0].get(sc)).getItem())
                        .getRegistryName().toString());
        sm.registerConsumer("entity.setgravity", (sc, in) -> {
            ((Entity) in[0].get(sc)).setNoGravity(!in[1].getBoolean(sc));
        });
        sm.registerFunction("entity.iswet", (sc, in) -> ((Entity) in[0].get(sc)).isWet());
        sm.registerConsumer("entity.setpickupdelay", (sc, in) -> {
            ((ItemEntity) in[0].get(sc)).setPickupDelay(in[1].getInt(sc));
        });
        sm.registerFunction("entity.spawn", (sc, in) -> {
            ResourceLocation type = new ResourceLocation(in[0].getString(sc));
            Location l = (Location) in[1].get(sc);
            if(!World.isInvalidPosition(l.getBlockPos())) {
                return null;
            }
            CompoundNBT nbt = in.length >= 3 ? JsonToNBT.getTagFromJson(in[2].getString(sc))
                    : new CompoundNBT();
            nbt.putString("id", type.toString());
            ServerWorld sw = (ServerWorld) l.getWorld();
            Entity ent = EntityType.loadEntityAndExecute(nbt, sw, e -> {
                e.addTag("mod_spawned");
                e.setLocationAndAngles(l.getX(), l.getY(), l.getZ(), e.rotationYaw,
                        e.rotationPitch);
                return e;
            });
            if(ent == null) {
                return ent;
            }
            if(ent instanceof MobEntity) {
                ((MobEntity) ent).onInitialSpawn(sw, sw.getDifficultyForLocation(ent.getPosition()),
                        SpawnReason.COMMAND, null, null);
            }
            if(!sw.func_242106_g(ent)) {
                return null;
            }
            return ent;
        });
        sm.registerFunction("entity.near", (sc, in) -> {
            Object o = in[0].get(sc);
            if(o instanceof Location) {
                return Utils.getEntities((Location) o, in[1].getDouble(sc));
            }
            return Utils.getEntities((Entity) o, in[1].getDouble(sc));
        });
        sm.registerConsumer("entity.setspeed", (sc, in) -> {
            ((LivingEntity) in[0].get(sc)).getAttribute(Attributes.MOVEMENT_SPEED)
                    .setBaseValue(in[1].getDouble(sc));
        });
        sm.registerConsumer("entity.setgrowingage", (sc, in) -> {
            ((AgeableEntity) in[0].get(sc)).setGrowingAge(in[1].getInt(sc));
        });
        sm.registerFunction("entity.gettype",
                (sc, in) -> ((Entity) in[0].get(sc)).getType().getRegistryName().getPath());
        sm.registerFunction("entity.issneaking",
                (sc, in) -> ((Entity) in[0].get(sc)).isCrouching());
        sm.registerFunction("entity.issneaking",
                (sc, in) -> ((Entity) in[0].get(sc)).isCrouching());
        sm.registerFunction("sheep.issheared",
                (sc, in) -> ((SheepEntity) in[0].get(sc)).getSheared());
        sm.registerFunction("sheep.getcolor",
                (sc, in) -> ((SheepEntity) in[0].get(sc)).getFleeceColor().toString());
        sm.registerConsumer("creeper.explode",
                (sc, in) -> ((CreeperEntity) in[0].get(sc)).ignite());
        sm.registerFunction("pet.istamed", (sc, in) -> {
            Object o = in[0].get(sc);
            if(o instanceof AbstractHorseEntity) {
                return ((AbstractHorseEntity) o).isTame();
            }
            return ((TameableEntity) o).isTamed();
        });
        sm.registerConsumer("pet.settamed", (sc, in) -> {
            Object o = in[0].get(sc);
            boolean b = in[1].getBoolean(sc);
            if(o instanceof AbstractHorseEntity) {
                AbstractHorseEntity h = (AbstractHorseEntity) o;
                if(in.length >= 3) {
                    h.setTamedBy((PlayerEntity) in[2].get(sc));
                }
                h.setHorseTamed(b);
                return;
            }
            TameableEntity t = (TameableEntity) o;
            if(in.length >= 3) {
                t.setTamedBy((PlayerEntity) in[2].get(sc));
            }
            t.setTamed(b);
        });
        sm.registerFunction("pet.getowner", (sc, in) -> {
            Object o = in[0].get(sc);
            if(o instanceof AbstractHorseEntity) {
                return ((AbstractHorseEntity) o).getOwnerUniqueId();
            }
            return ((TameableEntity) in[0].get(sc)).getOwnerId();
        });
        sm.registerFunction("entity.addtag",
                (sc, in) -> ((Entity) in[0].get(sc)).addTag(in[1].getString(sc)));
        sm.registerFunction("entity.removetag",
                (sc, in) -> ((Entity) in[0].get(sc)).removeTag(in[1].getString(sc)));
        sm.registerFunction("entity.hastag",
                (sc, in) -> ((Entity) in[0].get(sc)).getTags().contains(in[1].getString(sc)));
        sm.registerConsumer("entity.cleartags",
                (sc, in) -> ((Entity) in[0].get(sc)).getTags().clear());
    }
}
*/
