package me.hammerle.kp;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.ServerCommandEvent;
import me.hammerle.kp.snuviscript.CommandManager;
import me.hammerle.kp.snuviscript.MoveEvents;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;

public class Events implements Listener {
    @EventHandler
    public void onServerTick(ServerTickEndEvent e) {
        MoveEvents.tick();
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent e) {
        CommandManager.execute(e.getPlayer(), e.getMessage());
        e.setCancelled(true);
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent e) {
        CommandManager.execute(e.getSender(), e.getCommand());
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        CommandManager.clearPermissions(p);
        KajetansPlugin.scheduleTask(() -> PlayerData.get(p).login(p));
    }

    @EventHandler
    public void onPlayerSendCommandInfo(PlayerCommandSendEvent e) {
        e.getCommands().clear();
        KajetansPlugin.scheduleTask(() -> {
            CommandManager.send(e.getPlayer());
        });
    }
}
