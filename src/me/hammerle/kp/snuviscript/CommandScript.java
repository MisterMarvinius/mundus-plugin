package me.hammerle.kp.snuviscript;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.bukkit.command.CommandSender;
import me.hammerle.kp.KajetansPlugin;
import me.hammerle.snuviscript.code.Script;

public class CommandScript extends KajetanCommand {
    private void printHelp(CommandSender cs) {
        sendMessage(cs, "/script ...");
        sendListMessage(cs, "start <scripts...>", "starts a script");
        sendListMessage(cs, "see", "shows active scripts");
        sendListMessage(cs, "term <id/all>", "terminates a script");
    }

    @Override
    public String getName() {
        return "script";
    }

    @Override
    public Iterable<String> getAliases() {
        return List.of("s");
    }

    @Override
    public void execute(CommandSender cs, String[] args) {
        if(args.length == 0) {
            printHelp(cs);
            return;
        }

        switch(args[0].toLowerCase()) {
            case "s":
            case "start": {
                if(args.length >= 2) {
                    String[] pars = Arrays.copyOfRange(args, 1, args.length);
                    KajetansPlugin.startScript(null, pars);
                    return;
                }
                break;
            }
            case "see": {
                Collection<Script> scripts = KajetansPlugin.scriptManager.getScripts();
                if(scripts.isEmpty()) {
                    sendMessage(cs, "No scripts are active.");
                    return;
                }
                sendMessage(cs, "Active scripts:");
                scripts.forEach(
                        sc -> sendListMessage(cs, String.valueOf(sc.getId()), sc.getName()));
                return;
            }
            case "t":
            case "term": {
                if(args.length < 2) {
                    break;
                }
                try {
                    if(args[1].equals("all")) {
                        KajetansPlugin.scriptManager.removeScripts();
                        CommandManager.clearCustom();
                        CommandManager.clearIgnored();
                        CommandManager.clearCustomNodes();
                        sendMessage(cs, "All active scripts were terminated.");
                        return;
                    }
                    int id = Integer.parseInt(args[1]);
                    Script sc = KajetansPlugin.scriptManager.getScript(id);
                    if(sc != null) {
                        KajetansPlugin.scriptManager.removeScript(sc);
                        sendMessage(cs, String.format("Script '%s' was terminated.", sc.getName()));
                    } else {
                        sendMessage(cs, String.format("Script id '%d' is not valid.", id));
                    }
                } catch(NumberFormatException ex) {
                    sendMessage(cs, String.format("'%s' is not a valid id.", args[1]));
                } catch(Exception ex) {
                    sendMessage(cs, "An exception on script termination was thrown.");
                    cs.sendMessage(ex.getMessage());
                }
                return;
            }
        }
        printHelp(cs);
    }
}
