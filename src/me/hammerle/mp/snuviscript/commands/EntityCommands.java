package me.hammerle.mp.snuviscript.commands;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import me.hammerle.mp.MundusPlugin;
import net.kyori.adventure.text.Component;

public class EntityCommands {
    @SuppressWarnings("unchecked")
    private static Class<? extends Entity> getEntityClass(String name) throws Exception {
        return (Class<? extends Entity>) Class.forName(name);
    }

    public static void registerFunctions() {
        MundusPlugin.scriptManager.registerConsumer("entity.setnopickup", (sc, in) -> {
            ((Arrow) in[0].get(sc)).setPickupStatus(PickupStatus.DISALLOWED);
        });
        MundusPlugin.scriptManager.registerConsumer("entity.setburning",
                (sc, in) -> ((Entity) in[0].get(sc)).setFireTicks(in[1].getInt(sc)));
        MundusPlugin.scriptManager.registerFunction("entity.isburning",
                (sc, in) -> ((Entity) in[0].get(sc)).getFireTicks() > 0);
        MundusPlugin.scriptManager.registerFunction("entity.getlook", (sc, in) -> {
            Object[] o = new Object[3];
            Vector v = ((Entity) in[0].get(sc)).getLocation().getDirection();
            o[0] = v.getX();
            o[1] = v.getY();
            o[2] = v.getZ();
            return o;
        });
        MundusPlugin.scriptManager.registerConsumer("entity.setlook", (sc, in) -> {
            Entity e = (Entity) in[0].get(sc);
            Location l = e.getLocation();
            l.setDirection(
                    new Vector(in[1].getDouble(sc), in[2].getDouble(sc), in[3].getDouble(sc)));
            e.teleport(l);
        });
        MundusPlugin.scriptManager.registerFunction("entity.getmotion", (sc, in) -> {
            Object[] o = new Object[3];
            Vector v = ((Entity) in[0].get(sc)).getVelocity();
            o[0] = v.getX();
            o[1] = v.getY();
            o[2] = v.getZ();
            return o;
        });
        MundusPlugin.scriptManager.registerFunction("entity.getlocation",
                (sc, in) -> ((Entity) in[0].get(sc)).getLocation());
        MundusPlugin.scriptManager.registerConsumer("entity.setname", (sc, in) -> {
            Entity ent = (Entity) in[0].get(sc);
            ent.customName((Component) in[1].get(sc));
            if(in.length >= 3) {
                ent.setCustomNameVisible(in[2].getBoolean(sc));
                return;
            }
            ent.setCustomNameVisible(false);
        });
        MundusPlugin.scriptManager.registerFunction("entity.getname",
                (sc, in) -> ((Entity) in[0].get(sc)).customName());
        MundusPlugin.scriptManager.registerConsumer("entity.setmotion", (sc, in) -> {
            Entity ent = (Entity) in[0].get(sc);
            ent.setVelocity(
                    new Vector(in[1].getDouble(sc), in[2].getDouble(sc), in[3].getDouble(sc)));
        });
        MundusPlugin.scriptManager.registerConsumer("entity.teleport", (sc, in) -> {
            Entity ent = (Entity) in[0].get(sc);
            Location l = (Location) in[1].get(sc);
            ent.teleport(l);
        });
        MundusPlugin.scriptManager.registerConsumer("entity.removeall", (sc, in) -> {
            Location l = (Location) in[1].get(sc);
            for(Entity ent : l.getWorld().getNearbyEntitiesByType(
                    getEntityClass(in[0].getString(sc)), l, in[2].getDouble(sc))) {
                ent.remove();
            }
        });
        MundusPlugin.scriptManager.registerConsumer("entity.remove",
                (sc, in) -> ((Entity) in[0].get(sc)).remove());
        MundusPlugin.scriptManager.registerConsumer("entity.setinvulnerable", (sc, in) -> {
            ((Entity) in[0].get(sc)).setInvulnerable(in[1].getBoolean(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("entity.setsilent", (sc, in) -> {
            ((Entity) in[0].get(sc)).setSilent(in[1].getBoolean(sc));
        });
        MundusPlugin.scriptManager.registerFunction("entity.getmount",
                (sc, in) -> ((Entity) in[0].get(sc)).getVehicle());
        MundusPlugin.scriptManager.registerFunction("entity.getpassengers",
                (sc, in) -> ((Entity) in[0].get(sc)).getPassengers());
        MundusPlugin.scriptManager.registerConsumer("entity.mount", (sc, in) -> {
            ((Entity) in[1].get(sc)).addPassenger(((Entity) in[0].get(sc)));
        });
        MundusPlugin.scriptManager.registerConsumer("entity.unmount", (sc, in) -> {
            ((Entity) in[0].get(sc)).leaveVehicle();
        });
        MundusPlugin.scriptManager.registerFunction("entity.get", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            double min = Double.MAX_VALUE;
            Entity minE = null;
            for(Entity e : l.getWorld().getNearbyEntitiesByType(getEntityClass(in[2].getString(sc)),
                    l, in[1].getDouble(sc))) {
                double distance = e.getLocation().distanceSquared(l);
                if(distance < min) {
                    min = distance;
                    minE = e;
                }
            }
            return minE;
        });
        MundusPlugin.scriptManager.registerFunction("entity.getpotiontype",
                (sc, in) -> ((ThrownPotion) in[0].get(sc)).getItem());
        MundusPlugin.scriptManager.registerConsumer("entity.setgravity", (sc, in) -> {
            ((Entity) in[0].get(sc)).setGravity(in[1].getBoolean(sc));
        });
        MundusPlugin.scriptManager.registerFunction("entity.iswet",
                (sc, in) -> ((Entity) in[0].get(sc)).isInWater());
        MundusPlugin.scriptManager.registerFunction("entity.spawn", (sc, in) -> {
            Location l = ((Location) in[0].get(sc));
            return l.getWorld().spawnEntity(l, EntityType.valueOf(in[1].getString(sc)));
        });
        MundusPlugin.scriptManager.registerFunction("entity.near", (sc, in) -> {
            Object o = in[0].get(sc);
            if(o instanceof Location) {
                Location l = (Location) o;
                return l.getWorld().getNearbyEntitiesByType(Entity.class, l, in[1].getDouble(sc));
            }
            Entity ent = (Entity) o;
            double radius = in[1].getDouble(sc);
            return ent.getNearbyEntities(radius, radius, radius);
        });
        MundusPlugin.scriptManager.registerFunction("entity.getuuid", (sc, in) -> {
            Entity ent = (Entity) in[0].get(sc);
            return ent.getUniqueId();
        });
        MundusPlugin.scriptManager.registerConsumer("entity.setgrowingage", (sc, in) -> {
            ((Ageable) in[0].get(sc)).setAge(in[1].getInt(sc));
        });
        MundusPlugin.scriptManager.registerFunction("entity.gettype",
                (sc, in) -> ((Entity) in[0].get(sc)).getType().toString().toLowerCase());
        MundusPlugin.scriptManager.registerFunction("sheep.issheared",
                (sc, in) -> ((Sheep) in[0].get(sc)).isSheared());
        MundusPlugin.scriptManager.registerFunction("sheep.getcolor",
                (sc, in) -> ((Sheep) in[0].get(sc)).getColor().toString());
        MundusPlugin.scriptManager.registerConsumer("creeper.explode",
                (sc, in) -> ((Creeper) in[0].get(sc)).ignite());
        MundusPlugin.scriptManager.registerFunction("pet.istamed", (sc, in) -> {
            return ((Tameable) in[0].get(sc)).isTamed();
        });
        MundusPlugin.scriptManager.registerConsumer("pet.settamed", (sc, in) -> {
            Tameable t = (Tameable) in[0].get(sc);
            t.setTamed(in[1].getBoolean(sc));
            if(in.length >= 3) {
                t.setOwner((Player) in[2].get(sc));
            }
        });
        MundusPlugin.scriptManager.registerFunction("pet.getowner", (sc, in) -> {
            Tameable t = (Tameable) in[0].get(sc);
            return t.getOwner();
        });
        MundusPlugin.scriptManager.registerConsumer("entity.frame.hide", (sc, in) -> {
            ItemFrame frame = (ItemFrame) in[0].get(sc);
            frame.setVisible(false);
        });
        MundusPlugin.scriptManager.registerConsumer("entity.frame.show", (sc, in) -> {
            ItemFrame frame = (ItemFrame) in[0].get(sc);
            frame.setVisible(true);
        });
        MundusPlugin.scriptManager.registerConsumer("entity.frame.setfixed", (sc, in) -> {
            ItemFrame frame = (ItemFrame) in[0].get(sc);
            frame.setFixed(in[1].getBoolean(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("entity.frame.spawn", (sc, in) -> {
            Location l = ((Location) in[0].get(sc));
            ItemFrame frame = l.getWorld().spawn(l, ItemFrame.class);
            frame.setFacingDirection(BlockFace.valueOf(in[1].getString(sc)));
            frame.setItem((ItemStack) in[2].get(sc));
        });
        MundusPlugin.scriptManager.registerFunction("entity.frame.getitem",
                (sc, in) -> ((ItemFrame) in[0].get(sc)).getItem());
        MundusPlugin.scriptManager.registerFunction("slime.getsize", (sc, in) -> {
            Slime s = (Slime) in[0].get(sc);
            return (double) s.getSize();
        });
        MundusPlugin.scriptManager.registerConsumer("slime.setsize", (sc, in) -> {
            Slime s = (Slime) in[0].get(sc);
            s.setSize(in[1].getInt(sc));
        });
    }
}
