package me.hammerle.kp.snuviscript;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import me.hammerle.snuviscript.code.Script;

public class MoveEvents {
    public static class Data {
        private static int idCounter = 0;

        private final Script script;
        private final int coolDown;
        private int ticks;
        private int livingTime;

        private final World world;
        private final double minX;
        private final double minY;
        private final double minZ;
        private final double maxX;
        private final double maxY;
        private final double maxZ;

        private final int id = idCounter++;
        private final UUID uuid;

        public Data(Script sc, Location l1, Location l2, int cooldown, int livingTime, UUID uuid) {
            this.script = sc;
            this.coolDown = cooldown;
            this.ticks = cooldown;
            this.livingTime = livingTime;
            this.world = l1.getWorld();
            this.minX = Math.min(l1.getX(), l2.getX());
            this.minY = Math.min(l1.getY(), l2.getY());
            this.minZ = Math.min(l1.getZ(), l2.getZ());
            this.maxX = Math.max(l1.getX(), l2.getX());
            this.maxY = Math.max(l1.getY(), l2.getY());
            this.maxZ = Math.max(l1.getZ(), l2.getZ());
            this.uuid = uuid;
        }

        private int getId() {
            return id;
        }

        private boolean isSameScript(Script sc) {
            return script == sc;
        }

        private boolean tickLiving() {
            if(livingTime == -1) {
                return false;
            }
            livingTime--;
            return livingTime < 0;
        }

        private boolean tick() {
            ticks--;
            if(ticks > 0) {
                return true;
            }
            ticks = coolDown;
            return false;
        }

        private boolean check(Player p) {
            Location l = p.getLocation();
            double posX = l.getX();
            double posY = l.getY();
            double posZ = l.getZ();
            if(p.getWorld() != world || posX < minX || posX > maxX || posZ < minZ || posZ > maxZ
                    || posY < minY || posY > maxY) {
                return false;
            }
            boolean b = (uuid == null || p.getUniqueId().equals(uuid));
            if(b) {
                ScriptEvents.onPlayerMove(p, id);
            }
            return coolDown == -1 && b;
        }
    }

    private final static ConcurrentHashMap<Integer, Data> DATA = new ConcurrentHashMap<>();

    public static int add(Data d) {
        if(d.isSameScript(null)) {
            return -1;
        }
        DATA.put(d.getId(), d);
        return d.getId();
    }

    public static void remove(Script sc) {
        DATA.entrySet().removeIf(e -> e.getValue().isSameScript(sc));
    }

    public static void remove(int id) {
        DATA.remove(id);
    }

    public static void tick() {
        DATA.values().removeIf(data -> {
            if(data.tickLiving()) {
                return true;
            }
            if(data.tick()) {
                return false;
            }
            return Bukkit.getServer().getOnlinePlayers().stream().anyMatch(p -> data.check(p));
        });
    }
}
