package me.hammerle.kp.snuviscript;

import java.util.Collections;
import org.bukkit.command.CommandSender;

public abstract class KajetanCommand {
    public abstract String getName();

    public Iterable<String> getAliases() {
        return Collections.<String>emptyList();
    }

    public abstract void execute(CommandSender cs, String[] arg);

    public void sendMessage(CommandSender cs, String message) {
        cs.sendMessage(String.format("[§dScript§r] %s", message));
    }

    public void sendListMessage(CommandSender cs, String message1, String message2) {
        cs.sendMessage(String.format("§d - %s§r %s", message1, message2));
    }
}
