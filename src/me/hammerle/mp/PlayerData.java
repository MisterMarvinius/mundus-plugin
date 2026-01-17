package me.hammerle.mp;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import me.hammerle.mp.snuviscript.ScriptEvents;
import net.kyori.adventure.text.Component;

public class PlayerData {
    private final static int MAX_LENGTH = 40;
    private static final Map<UUID, PlayerData> PLAYER_DATA = new ConcurrentHashMap<>();

    private Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    private Objective objective;
    private HashMap<String, Object> data = new HashMap<>();
    private HashMap<String, Integer> timers = new HashMap<>();
    private HashMap<String, Integer> timerQueue = new HashMap<>();
    private boolean iterating = false;

    private PlayerData(Player p) {
        objective = scoreboard.registerNewObjective("mundusplugin", Criteria.create("DUMMY"),
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
        if(iterating) {
            timerQueue.put(name, time);
            return;
        }
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
        iterating = true;
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
        iterating = false;
        timers.putAll(timerQueue);
        timerQueue.clear();
    }

    public static PlayerData get(Player p) {
        return PLAYER_DATA.computeIfAbsent(p.getUniqueId(), id -> new PlayerData(p));
    }
}
