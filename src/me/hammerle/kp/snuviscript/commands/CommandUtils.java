package me.hammerle.kp.snuviscript.commands;

import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.hammerle.snuviscript.code.Script;
import net.kyori.adventure.text.Component;

public class CommandUtils {
    private final static UUID SERVER_UUID = new UUID(0, 0);

    public static UUID getUUID(Object o) {
        if(o instanceof Player) {
            return ((Player) o).getUniqueId();
        } else if(o instanceof UUID) {
            return (UUID) o;
        } else if("SERVER".equals(o)) {
            return SERVER_UUID;
        }
        return UUID.fromString(o.toString());
    }

    public static void sendMessageToGroup(Object group, Script sc, Component text) {
        doForGroup(group, sc, p -> p.sendMessage(text));
    }

    public static void doForGroup(Object group, Script sc, Consumer<CommandSender> c) {
        if(!(group instanceof String)) {
            c.accept((CommandSender) group);
            return;
        }
        switch(group.toString().toLowerCase()) {
            case "online":
                Bukkit.getOnlinePlayers().forEach(p -> c.accept(p));
                return;
            case "dev":
                Bukkit.getOnlinePlayers().forEach(p -> {
                    if(p.hasPermission("script.debug")) {
                        c.accept(p);
                    }
                });
                return;
            case "server":
                c.accept(Bukkit.getServer().getConsoleSender());
                return;
        }
    }
}
