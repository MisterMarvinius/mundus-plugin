package me.hammerle.kp;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import net.kyori.adventure.text.Component;

public class PlayerData {
    private final static String TAG = "kajetansplugin";
    private final static int MAX_LENGTH = 40;

    private Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    private Objective objective;

    private PlayerData(Player p) {
        objective = scoreboard.registerNewObjective("kajetansplugin", "dummy",
                Component.text("Default"), RenderType.INTEGER);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        p.setScoreboard(scoreboard);
    }

    public void login(Player p) {
        p.setScoreboard(scoreboard);
    }

    public void setTitle(Component c) {
        objective.displayName(c);
    }

    private boolean isNotUnique(String text) {
        return objective.getScore(text).isScoreSet();
    }

    public void setText(int id, String text) {
        if(text.length() > MAX_LENGTH - 2) {
            text = text.substring(0, MAX_LENGTH - 2);
        }
        String unique = text;
        char c = 0;
        while(isNotUnique(unique) && c < 30000) {
            unique = text + "§" + c;
            c++;
        }

        removeText(id);
        objective.getScore(unique).setScore(id);
    }

    public void removeText(int id) {
        for(String name : scoreboard.getEntries()) {
            Score score = objective.getScore(name);
            if(score.getScore() == id) {
                score.resetScore();
            }
        }
    }

    public void clear() {
        for(String name : scoreboard.getEntries()) {
            objective.getScore(name).resetScore();
        }
    }

    public void setRaw(int id, String text) {
        objective.getScore(text).setScore(id);
    }

    public void removeRaw(String text) {
        objective.getScore(text).resetScore();
    }

    public static PlayerData get(Player p) {
        List<MetadataValue> list = p.getMetadata(TAG);
        if(list == null || list.isEmpty()) {
            PlayerData data = new PlayerData(p);
            p.setMetadata(TAG, new FixedMetadataValue(KajetansPlugin.instance, data));
            return data;
        }
        return (PlayerData) list.get(0).value();
    }
}
