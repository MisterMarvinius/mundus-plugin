/*package me.km.permissions;

import java.util.Collections;
import net.minecraft.command.ICommandSource;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;

public abstract class Command {
    public abstract String getName();

    public Iterable<String> getAliases() {
        return Collections.<String>emptyList();
    }

    public abstract void execute(ICommandSource cs, String[] arg);

    public void sendMessage(ICommandSource cs, String message) {
        cs.sendMessage(new StringTextComponent(String.format("[§dScript§r] %s", message)),
                Util.DUMMY_UUID);
    }

    public void sendListMessage(ICommandSource cs, String message1, String message2) {
        cs.sendMessage(new StringTextComponent(String.format("§d - %s§r %s", message1, message2)),
                Util.DUMMY_UUID);
    }
}
*/
