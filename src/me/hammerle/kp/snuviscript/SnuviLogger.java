package me.hammerle.kp.snuviscript;

import org.bukkit.Bukkit;
import me.hammerle.kp.KajetansPlugin;
import me.hammerle.snuviscript.code.ISnuviLogger;
import me.hammerle.snuviscript.code.Script;
import me.hammerle.snuviscript.exceptions.StackTrace;

public class SnuviLogger implements ISnuviLogger {
    private boolean printErrorToConsole = true;
    private boolean printDebugToConsole = true;

    public static class Message {
        public final String message;
        public final long timestamp;

        public Message(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }
    }

    private final RingArray<Message> debugHistory = new RingArray<>(100);
    private final RingArray<Message> errorHistory = new RingArray<>(100);

    public void setConsoleErrorLogging(boolean b) {
        printErrorToConsole = b;
    }

    public void setConsoleDebugLogging(boolean b) {
        printDebugToConsole = b;
    }

    public RingArray<Message> getDebugHistory() {
        return debugHistory;
    }

    public RingArray<Message> getErrorHistory() {
        return errorHistory;
    }

    private void sendToPlayers(String msg, String perm) {
        Bukkit.getOnlinePlayers().forEach(p -> {
            if(p.hasPermission(perm)) {
                p.sendMessage(msg);
            }
        });
    }

    @Override
    public void print(String message, Exception ex, String function, String scriptname, Script sc,
            StackTrace lines) {
        StringBuilder sb = new StringBuilder();
        sb.append("[§cLogger§r] ");

        String color;
        if(ex == null) {
            color = "§e";
            sb.append(color).append(message).append("§r");
        } else {
            color = "§c";
            sb.append(color).append(ex.getClass().getSimpleName()).append("§r: '").append(color)
                    .append(ex.getMessage());
            if(message != null && !message.isEmpty()) {
                sb.append(" - ").append(message);
            }
            sb.append("§r'");
        }
        if(scriptname != null && !scriptname.isEmpty()) {
            sb.append(" in script '").append(color).append(scriptname).append("§r'");
        }
        if(sc != null) {
            sb.append(" id '").append(color).append(sc.getId()).append("§r'");
        }
        if(function != null && !function.isEmpty()) {
            sb.append(" in function '").append(color).append(function).append("§r'");
        }
        if(lines != null) {
            sb.append(" in line '").append(color).append(lines).append("§r'");
        }

        String msg = sb.toString();
        if(ex == null) {
            debugHistory.add(new Message(msg));
            if(printDebugToConsole) {
                KajetansPlugin.log(msg);
            }
            sendToPlayers(msg, "script.debug");
        } else {
            errorHistory.add(new Message(msg));
            if(printErrorToConsole) {
                KajetansPlugin.log(msg);
            }
            sendToPlayers(msg, "script.error");
        }
    }
}
