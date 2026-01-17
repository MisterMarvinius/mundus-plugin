package me.hammerle.mp.snuviscript.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import me.hammerle.mp.MundusPlugin;
import me.hammerle.mp.utils.LocationIterator;

public class LocationCommands {
    public static void registerFunctions() {
        MundusPlugin.scriptManager.registerFunction("loc.new", (sc, in) -> {
            if(in.length >= 6) {
                return new Location((World) in[0].get(sc), in[1].getDouble(sc), in[2].getDouble(sc),
                        in[3].getDouble(sc), in[4].getFloat(sc), in[5].getFloat(sc));
            }
            return new Location((World) in[0].get(sc), in[1].getDouble(sc), in[2].getDouble(sc),
                    in[3].getDouble(sc), 0, 0);
        });
        MundusPlugin.scriptManager.registerFunction("loc.getx",
                (sc, in) -> ((Location) in[0].get(sc)).getX());
        MundusPlugin.scriptManager.registerFunction("loc.gety",
                (sc, in) -> ((Location) in[0].get(sc)).getY());
        MundusPlugin.scriptManager.registerFunction("loc.getz",
                (sc, in) -> ((Location) in[0].get(sc)).getZ());
        MundusPlugin.scriptManager.registerFunction("loc.getblockx",
                (sc, in) -> (double) ((Location) in[0].get(sc)).getBlockX());
        MundusPlugin.scriptManager.registerFunction("loc.getblocky",
                (sc, in) -> (double) ((Location) in[0].get(sc)).getBlockY());
        MundusPlugin.scriptManager.registerFunction("loc.getblockz",
                (sc, in) -> (double) ((Location) in[0].get(sc)).getBlockZ());
        MundusPlugin.scriptManager.registerConsumer("loc.set",
                (sc, in) -> ((Location) in[0].get(sc)).set(in[1].getDouble(sc), in[2].getDouble(sc),
                        in[3].getDouble(sc)));
        MundusPlugin.scriptManager.registerConsumer("loc.setx",
                (sc, in) -> ((Location) in[0].get(sc)).setX(in[1].getDouble(sc)));
        MundusPlugin.scriptManager.registerConsumer("loc.sety",
                (sc, in) -> ((Location) in[0].get(sc)).setY(in[1].getDouble(sc)));
        MundusPlugin.scriptManager.registerConsumer("loc.setz",
                (sc, in) -> ((Location) in[0].get(sc)).setZ(in[1].getDouble(sc)));
        MundusPlugin.scriptManager.registerConsumer("loc.add",
                (sc, in) -> ((Location) in[0].get(sc)).add(in[1].getDouble(sc), in[2].getDouble(sc),
                        in[3].getDouble(sc)));
        MundusPlugin.scriptManager.registerConsumer("loc.addx",
                (sc, in) -> ((Location) in[0].get(sc)).add(in[1].getDouble(sc), 0.0, 0.0));
        MundusPlugin.scriptManager.registerConsumer("loc.addy",
                (sc, in) -> ((Location) in[0].get(sc)).add(0.0, in[1].getDouble(sc), 0.0));
        MundusPlugin.scriptManager.registerConsumer("loc.addz",
                (sc, in) -> ((Location) in[0].get(sc)).add(0.0, 0.0, in[1].getDouble(sc)));
        MundusPlugin.scriptManager.registerConsumer("loc.setyaw",
                (sc, in) -> ((Location) in[0].get(sc)).setYaw(in[1].getFloat(sc)));
        MundusPlugin.scriptManager.registerFunction("loc.getyaw",
                (sc, in) -> (double) ((Location) in[0].get(sc)).getYaw());
        MundusPlugin.scriptManager.registerConsumer("loc.setpitch",
                (sc, in) -> ((Location) in[0].get(sc)).setPitch(in[1].getFloat(sc)));
        MundusPlugin.scriptManager.registerFunction("loc.getpitch",
                (sc, in) -> (double) ((Location) in[0].get(sc)).getPitch());
        MundusPlugin.scriptManager.registerFunction("loc.getworld",
                (sc, in) -> ((Location) in[0].get(sc)).getWorld());
        MundusPlugin.scriptManager.registerFunction("loc.ischunkloaded",
                (sc, in) -> ((Location) in[0].get(sc)).isChunkLoaded());
        MundusPlugin.scriptManager.registerFunction("loc.distance",
                (sc, in) -> ((Location) in[0].get(sc)).distance((Location) in[1].get(sc)));
        MundusPlugin.scriptManager.registerFunction("loc.mod",
                (sc, in) -> ((Location) in[0].get(sc)).clone().add(in[1].getDouble(sc),
                        in[2].getDouble(sc), in[3].getDouble(sc)));
        MundusPlugin.scriptManager.registerFunction("loc.isbetween", (sc, in) -> {
            Location l1 = (Location) in[0].get(sc);
            Location l2 = (Location) in[1].get(sc);
            Location l3 = (Location) in[2].get(sc);
            return l1.getX() >= Math.min(l2.getX(), l3.getX())
                    && l1.getX() <= Math.max(l2.getX(), l3.getX())
                    && l1.getY() >= Math.min(l2.getY(), l3.getY())
                    && l1.getY() <= Math.max(l2.getY(), l3.getY())
                    && l1.getZ() >= Math.min(l2.getZ(), l3.getZ())
                    && l1.getZ() <= Math.max(l2.getZ(), l3.getZ());
        });
        MundusPlugin.scriptManager.registerConsumer("loc.sort", (sc, in) -> {
            Location l1 = (Location) in[0].get(sc);
            Location l2 = (Location) in[1].get(sc);
            if(l1.getX() > l2.getX()) {
                double tmp = l1.getX();
                l1.setX(l2.getX());
                l2.setX(tmp);
            }
            if(l1.getY() > l2.getY()) {
                double tmp = l1.getY();
                l1.setY(l2.getY());
                l2.setY(tmp);
            }
            if(l1.getZ() > l2.getZ()) {
                double tmp = l1.getZ();
                l1.setZ(l2.getZ());
                l2.setZ(tmp);
            }
        });
        MundusPlugin.scriptManager.registerFunction("loc.iterator", (sc, in) -> {
            Object o = in[0].get(sc);
            if(o instanceof Location) {
                Location l1 = (Location) o;
                Location l2 = (Location) in[1].get(sc);
                return new LocationIterator(l1.getWorld(), l1.getBlockX(), l1.getBlockY(),
                        l1.getBlockZ(), l2.getBlockX(), l2.getBlockY(), l2.getBlockZ());
            }
            return new LocationIterator((World) in[0].get(sc), in[1].getInt(sc), in[2].getInt(sc),
                    in[3].getInt(sc), in[4].getInt(sc), in[5].getInt(sc), in[6].getInt(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("loc.explode", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            Entity ent = (Entity) in[1].get(sc);
            float power = in[2].getFloat(sc);
            boolean fire = in[3].getBoolean(sc);
            boolean destroys = in[4].getBoolean(sc);
            l.getWorld().createExplosion(l, power, fire, destroys, ent);
        });

    }
}
