package me.hammerle.kp.snuviscript.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.RayTraceResult;
import me.hammerle.kp.KajetansPlugin;
import me.hammerle.kp.NMS;
import me.hammerle.snuviscript.code.SnuviUtils;
import net.kyori.adventure.text.Component;

public class PlayerCommands {
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerFunction("player.getitemamount", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            boolean useData = in[1].getBoolean(sc);
            ItemStack stack = (ItemStack) in[2].get(sc);
            if(stack.getAmount() == 0) {
                return 0;
            }
            int counter = 0;
            ItemStack[] stacks = p.getInventory().getContents();
            if(useData) {
                for(int i = 0; i < stacks.length; i++) {
                    if(stacks[i] != null && stacks[i].isSimilar(stack)) {
                        counter += stacks[i].getAmount();
                    }
                }
            } else {
                for(int i = 0; i < stacks.length; i++) {
                    if(stacks[i] != null && stacks[i].getType() == stack.getType()) {
                        counter += stacks[i].getAmount();
                    }
                }
            }
            return (double) counter;
        });
        KajetansPlugin.scriptManager.registerFunction("player.removeitem", (sc, in) -> {
            ItemStack stack = ((ItemStack) in[1].get(sc)).clone();
            Player p = (Player) in[0].get(sc);
            HashMap<Integer, ItemStack> left = p.getInventory().removeItemAnySlot(stack);
            int count = 0;
            for(ItemStack lStack : left.values()) {
                count += lStack.getAmount();
            }
            stack.setAmount(count);
            return stack;
        });
        KajetansPlugin.scriptManager.registerFunction("player.giveitem", (sc, in) -> {
            ItemStack stack = ((ItemStack) in[1].get(sc)).clone();
            Player p = (Player) in[0].get(sc);
            HashMap<Integer, ItemStack> left = p.getInventory().addItem(stack);
            int count = 0;
            for(ItemStack lStack : left.values()) {
                count += lStack.getAmount();
            }
            stack.setAmount(count);
            return stack;
        });
        KajetansPlugin.scriptManager.registerConsumer("player.respawn", (sc, in) -> {
            Player p = ((Player) in[0].get(sc));
            p.spigot().respawn();
        });
        KajetansPlugin.scriptManager.registerConsumer("player.clearinventory",
                (sc, in) -> ((Player) in[0].get(sc)).getInventory().clear());
        KajetansPlugin.scriptManager.registerConsumer("player.say", (sc, in) -> {
            Player p = ((Player) in[0].get(sc));
            p.chat(SnuviUtils.connect(sc, in, 1));
        });
        KajetansPlugin.scriptManager.registerConsumer("player.setcompass", (sc, in) -> {
            Player p = ((Player) in[0].get(sc));
            p.setCompassTarget((Location) in[1].get(sc));
        });
        KajetansPlugin.scriptManager.registerFunction("player.gethunger",
                (sc, in) -> (double) ((Player) in[0].get(sc)).getFoodLevel());
        KajetansPlugin.scriptManager.registerConsumer("player.sethunger", (sc, in) -> {
            ((Player) in[0].get(sc)).setFoodLevel(in[1].getInt(sc));
        });
        KajetansPlugin.scriptManager.registerFunction("player.getsaturation",
                (sc, in) -> (double) ((Player) in[0].get(sc)).getSaturation());
        KajetansPlugin.scriptManager.registerConsumer("player.setsaturation",
                (sc, in) -> ((Player) in[0].get(sc)).setSaturation(in[1].getFloat(sc)));
        KajetansPlugin.scriptManager.registerFunction("player.getname", (sc, in) -> {
            Object o = in[0].get(sc);
            if(o instanceof Player) {
                return ((Player) o).getName();
            }
            OfflinePlayer op = Bukkit.getOfflinePlayer(CommandUtils.getUUID(o));
            if(op == null) {
                return null;
            }
            return op.getName();
        });
        KajetansPlugin.scriptManager.registerFunction("player.getuuid", (sc, in) -> {
            Object o = in[0].get(sc);
            if(o instanceof Player) {
                return ((Player) o).getUniqueId();
            }
            OfflinePlayer op = Bukkit.getOfflinePlayerIfCached(o.toString());
            if(op == null) {
                return null;
            }
            return op.getUniqueId();
        });
        KajetansPlugin.scriptManager.registerFunction("player.getid",
                (sc, in) -> (double) CommandUtils.getUUID(in[0].get(sc)).hashCode());
        KajetansPlugin.scriptManager.registerFunction("player.get",
                (sc, in) -> Bukkit.getPlayer(CommandUtils.getUUID(in[0].get(sc))));
        KajetansPlugin.scriptManager.registerFunction("player.getuuidfromid", (sc, in) -> {
            int id = in[0].getInt(sc);
            for(OfflinePlayer op : Bukkit.getOfflinePlayers()) {
                if(op.getUniqueId().hashCode() == id) {
                    return op.getUniqueId();
                }
            }
            return null;
        });
        KajetansPlugin.scriptManager.registerFunction("player.getnamefromid", (sc, in) -> {
            int id = in[0].getInt(sc);
            for(OfflinePlayer op : Bukkit.getOfflinePlayers()) {
                if(op.getUniqueId().hashCode() == id) {
                    return op.getName();
                }
            }
            return null;
        });
        KajetansPlugin.scriptManager.registerFunction("player.getip",
                (sc, in) -> ((Player) in[0].get(sc)).spigot().getRawAddress().toString());
        KajetansPlugin.scriptManager.registerFunction("player.iscreative",
                (sc, in) -> ((Player) in[0].get(sc)).getGameMode() == GameMode.CREATIVE);
        KajetansPlugin.scriptManager.registerFunction("player.isspectator",
                (sc, in) -> ((Player) in[0].get(sc)).getGameMode() == GameMode.SPECTATOR);
        KajetansPlugin.scriptManager.registerFunction("player.issurvival",
                (sc, in) -> ((Player) in[0].get(sc)).getGameMode() == GameMode.SURVIVAL);
        KajetansPlugin.scriptManager.registerFunction("player.isadventure",
                (sc, in) -> ((Player) in[0].get(sc)).getGameMode() == GameMode.ADVENTURE);
        KajetansPlugin.scriptManager.registerConsumer("player.setfly", (sc, in) -> {
            Player p = ((Player) in[0].get(sc));
            boolean b = in[1].getBoolean(sc);
            p.setAllowFlight(b);
        });
        KajetansPlugin.scriptManager.registerFunction("player.hasfly",
                (sc, in) -> ((Player) in[0].get(sc)).getAllowFlight());
        KajetansPlugin.scriptManager.registerFunction("player.isflying",
                (sc, in) -> ((Player) in[0].get(sc)).isFlying());
        KajetansPlugin.scriptManager.registerConsumer("player.setgamemode", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            switch(in[1].get(sc).toString()) {
                case "survival":
                case "s":
                case "0":
                    p.setGameMode(GameMode.SURVIVAL);
                    return;
                case "creative":
                case "c":
                case "1":
                    p.setGameMode(GameMode.CREATIVE);
                    return;
                case "adventure":
                case "a":
                case "2":
                    p.setGameMode(GameMode.ADVENTURE);
                    return;
                case "spectator":
                case "w":
                case "3":
                    p.setGameMode(GameMode.SPECTATOR);
                    return;
            }
            p.setGameMode(GameMode.CREATIVE);
        });
        KajetansPlugin.scriptManager.registerFunction("player.getlastdamager", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            EntityDamageEvent e = p.getLastDamageCause();
            if(!(e instanceof EntityDamageByEntityEvent)) {
                return null;
            }
            EntityDamageByEntityEvent damage = (EntityDamageByEntityEvent) e;
            return damage.getDamager();
        });
        KajetansPlugin.scriptManager.registerConsumer("player.dropinventory", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            PlayerInventory inv = p.getInventory();
            for(int i = 0; i < inv.getSize(); i++) {
                ItemStack stack = inv.getItem(i);
                if(stack != null) {
                    p.getWorld().dropItemNaturally(p.getLocation(), stack);
                }
            }
            inv.clear();
        });
        KajetansPlugin.scriptManager.registerFunction("player.gettarget", (sc, in) -> {
            Player p = (Player) in[0].get(sc);

            double radius = in[1].getDouble(sc);
            if(radius > 128.0) {
                radius = 128.0;
            }

            FluidCollisionMode mode = FluidCollisionMode.NEVER;
            if(in.length >= 3 && in[2].getBoolean(sc)) {
                mode = FluidCollisionMode.ALWAYS;
            }

            RayTraceResult result = p.rayTraceBlocks(radius, mode);
            if(result == null) {
                return null;
            }
            return result.getHitBlock().getLocation();
        });
        KajetansPlugin.scriptManager.registerFunction("player.gettargetentity", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            return p.getTargetEntity(in[1].getInt(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("player.action", (sc, in) -> {
            Component text = (Component) in[0].get(sc);
            CommandUtils.doForGroup(in[0].get(sc), sc, p -> ((Player) p).sendActionBar(text));
        });
        KajetansPlugin.scriptManager.registerFunction("player.getspawn", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            return p.getBedSpawnLocation();
        });
        KajetansPlugin.scriptManager.registerAlias("player.getspawn", "player.getbedspawn");
        KajetansPlugin.scriptManager.registerConsumer("player.setspawn", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            Location l = (Location) in[1].get(sc);
            p.setBedSpawnLocation(l, true);
        });
        KajetansPlugin.scriptManager.registerAlias("player.setspawn", "player.setbedspawn");
        KajetansPlugin.scriptManager.registerConsumer("player.damageitem", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            ItemStack stack = p.getEquipment().getItemInMainHand();
            NMS.map(stack).a(in[1].getInt(sc), NMS.map(p), c -> {
            });
        });
        KajetansPlugin.scriptManager.registerConsumer("player.damagearmor", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            NMS.map(p).fq().a(NMS.toDamageSource(in[2].get(sc)), in[1].getFloat(sc),
                    new int[] {0, 1, 2, 3});
        });
        KajetansPlugin.scriptManager.registerConsumer("player.openenderchest", (sc, in) -> {
            Player p1 = (Player) in[0].get(sc);
            Player p2 = (Player) in[1].get(sc);
            p1.openInventory(p2.getEnderChest());
        });
        KajetansPlugin.scriptManager.registerConsumer("player.addtotalexp", (sc, in) -> {
            ((Player) in[0].get(sc)).giveExp(in[1].getInt(sc));
        });
        KajetansPlugin.scriptManager.registerFunction("player.getlevel",
                (sc, in) -> (double) ((Player) in[0].get(sc)).getLevel());
        KajetansPlugin.scriptManager.registerConsumer("player.setlevel", (sc, in) -> {
            ((Player) in[0].get(sc)).setLevel(in[1].getInt(sc));
        });
        KajetansPlugin.scriptManager.registerFunction("player.getexp",
                (sc, in) -> (double) ((Player) in[0].get(sc)).getExp());
        KajetansPlugin.scriptManager.registerConsumer("player.setexp",
                (sc, in) -> ((Player) in[0].get(sc)).setExp(in[1].getFloat(sc)));
        KajetansPlugin.scriptManager.registerFunction("player.gethead", (sc, in) -> {
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setPlayerProfile(
                    Bukkit.createProfile(CommandUtils.getUUID(in[0].get(sc)), in[1].getString(sc)));
            skull.setItemMeta(meta);
            return skull;
        });
        KajetansPlugin.scriptManager.registerFunction("player.getinv",
                (sc, in) -> ((Player) in[0].get(sc)).getInventory());
        KajetansPlugin.scriptManager.registerFunction("player.getenderinv",
                (sc, in) -> ((Player) in[0].get(sc)).getEnderChest());
        KajetansPlugin.scriptManager.registerConsumer("player.setdisplayname", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            p.playerListName((Component) in[1].get(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("player.hide", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            p.setInvisible(true);
        });
        KajetansPlugin.scriptManager.registerConsumer("player.show", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            p.setInvisible(false);
        });
        KajetansPlugin.scriptManager.registerFunction("players.getamount",
                (sc, in) -> (double) Bukkit.getOnlinePlayers().size());
        KajetansPlugin.scriptManager.registerFunction("players.tolist",
                (sc, in) -> new ArrayList<>(Bukkit.getOnlinePlayers()));
        KajetansPlugin.scriptManager.registerFunction("players.toworldlist",
                (sc, in) -> new ArrayList<>(((World) in[0].get(sc)).getPlayers()));
        KajetansPlugin.scriptManager.registerFunction("players.near", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            return l.getWorld().getNearbyPlayers(l, in[1].getDouble(sc));
        });
        KajetansPlugin.scriptManager.registerFunction("player.getnearest", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            double distance = 10.0;
            Collection<Player> players = l.getWorld().getNearbyPlayers(l, distance);
            distance *= distance;
            Player closest = null;
            for(Player p : players) {
                double d = l.distanceSquared(p.getLocation());
                if(d < distance) {
                    closest = p;
                    distance = d;
                }
            }
            return closest;
        });
        KajetansPlugin.scriptManager.registerConsumer("player.setflyspeed", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            p.setFlySpeed(in[1].getFloat(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("player.setwalkspeed", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            p.setWalkSpeed(in[1].getFloat(sc));
        });
        KajetansPlugin.scriptManager.registerFunction("player.issneaking", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            return p.isSneaking();
        });
        KajetansPlugin.scriptManager.registerFunction("player.isblocking", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            return p.isBlocking();
        });
        KajetansPlugin.scriptManager.registerConsumer("player.setslot", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            p.getInventory().setHeldItemSlot(in[1].getInt(sc));
        });
    }
}
