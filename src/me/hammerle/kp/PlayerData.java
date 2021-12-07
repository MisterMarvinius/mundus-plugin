package me.hammerle.kp;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import me.hammerle.kp.snuviscript.ScriptEvents;
import net.kyori.adventure.text.Component;

public class PlayerData {
    private final static String TAG = "kajetansplugin";
    private final static int MAX_LENGTH = 40;

    private Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    private Objective objective;
    private HashMap<String, Object> data = new HashMap<>();
    private ConcurrentHashMap<String, Integer> timers = new ConcurrentHashMap<>();

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

    public void clearTexts() {
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

    public void setData(String name, Object o) {
        if(o == null) {
            data.remove(name);
            return;
        }
        data.put(name, o);
    }

    public Object getData(String name) {
        return data.get(name);
    }

    public void setTimer(String name, int time) {
        if(time <= 0) {
            timers.remove(name);
            return;
        }
        timers.put(name, time);
    }

    public int getTimer(String name) {
        return timers.getOrDefault(name, -1);
    }

    public void clearData() {
        data.clear();
        timers.clear();
    }

    public void tick(Player p) {
        timers.entrySet().removeIf(entry -> {
            int time = entry.getValue() - 1;
            if(time <= 0) {
                if(time == 0) {
                    ScriptEvents.onPlayerDataTick(p, entry.getKey());
                }
                return true;
            }
            entry.setValue(time);
            return false;
        });
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
