package me.hammerle.mp.snuviscript;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

public class CommandTest extends MundusCommand {
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
        sendListMessage(cs, "kill", "kill the server");
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
            case "kill": {
                sendMessage(cs, "Freezing for 10 seconds ...");
                Bukkit.getWorlds().get(0).setType(1_000_000_000, 100, 1_000_000_000,
                        Material.GLOWSTONE);
                return;
            }
        }
        printHelp(cs);
    }
}
