package me.hammerle.kp;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import me.hammerle.kp.snuviscript.CommandManager;
import me.hammerle.kp.snuviscript.MoveEvents;
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
}
