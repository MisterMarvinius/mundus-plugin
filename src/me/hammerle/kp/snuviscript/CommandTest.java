package me.hammerle.kp.snuviscript;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.hammerle.kp.NMS;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.Blocks;

public class CommandTest extends KajetanCommand {
    public static boolean noEvents = false;
    public static boolean noTick = false;

    @Override
    public void sendMessage(CommandSender cs, String message) {
        cs.sendMessage(String.format("[§dTest§r] %s", message));
    }

    private void printHelp(CommandSender cs) {
        sendMessage(cs, "/test ...");
        sendListMessage(cs, "event", "toggle the usage of events");
        sendListMessage(cs, "tick", "toggle the usage of the tick");
        sendListMessage(cs, "status", "see the status");
        sendListMessage(cs, "block", "tests block sets");
    }

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public Iterable<String> getAliases() {
        return List.of("t");
    }

    @Override
    public void execute(CommandSender cs, String[] args) {
        if(args.length == 0) {
            printHelp(cs);
            return;
        }
        switch(args[0].toLowerCase()) {
            case "events":
            case "event": {
                noEvents = !noEvents;
                if(noEvents) {
                    sendMessage(cs, "Events are now off");
                } else {
                    sendMessage(cs, "Events are now on");
                }
                return;
            }
            case "tick": {
                noTick = !noTick;
                if(noTick) {
                    sendMessage(cs, "Tick is now off");
                } else {
                    sendMessage(cs, "Tick is now on");
                }
                return;
            }
            case "status": {
                sendMessage(cs, "Events: " + !noEvents);
                sendMessage(cs, "Tick: " + !noTick);
                return;
            }
            case "block": {
                if(!(cs instanceof Player)) {
                    sendMessage(cs, "Must be performed by a player.");
                    return;
                }
                Player p = (Player) cs;
                Location l = p.getLocation();
                Location l2 = l.clone().add(1, 0, 0);
                Location l3 = l.clone().add(-1, 0, 0);

                long time = -System.nanoTime();
                l.getBlock().setType(Material.STONE);
                l2.getBlock().setType(Material.STONE);
                l3.getBlock().setType(Material.STONE);
                time += System.nanoTime();

                sendMessage(cs, String.format("Paper: %.4f ms per set", time / 3_000_000.0));



                time = -System.nanoTime();
                var w = NMS.map(p.getWorld());
                int x = l.getBlockX();
                int y = l.getBlockY();
                int z = l.getBlockZ();
                w.a(new BlockPosition(x + 1, y, z + 2), Blocks.b.n(), 3);
                w.a(new BlockPosition(x, y, z + 2), Blocks.b.n(), 3);
                w.a(new BlockPosition(x - 1, y, z + 2), Blocks.b.n(), 3);
                time += System.nanoTime();

                sendMessage(cs, String.format("NMS: %.4f ms per set", time / 3_000_000.0));
                return;
            }
        }
        printHelp(cs);
    }
}
