/*package me.km.overrides;

import java.util.HashMap;
import java.util.HashSet;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SDisplayObjectivePacket;
import net.minecraft.network.play.server.SScoreboardObjectivePacket;
import net.minecraft.network.play.server.SUpdateScorePacket;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreCriteria.RenderType;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ServerScoreboard.Action;
import net.minecraft.util.text.StringTextComponent;

public class PlayerScoreboard {
    private final HashMap<Integer, String> elements = new HashMap<>();
    private final HashSet<Integer> toRemove = new HashSet<>();
    private final HashMap<Integer, String> toAdd = new HashMap<>();

    private final Scoreboard board = new Scoreboard();
    private final ScoreObjective o;
    private boolean changeNeeded = true;

    private boolean init = true;

    public PlayerScoreboard() {
        o = new ScoreObjective(board, "ScoreBoardAPI", ScoreCriteria.DUMMY, new StringTextComponent("§6---------------"), RenderType.INTEGER);
    }

    public void addText(int id, String text) {
        String s = elements.get(id);
        if(s != null && !s.equals(text)) {
            toRemove.add(id);
            toAdd.put(id, text);
            changeNeeded = true;
        } else if(s == null) {
            toAdd.put(id, text);
            changeNeeded = true;
        }
    }

    public void removeText(int id) {
        if(elements.containsKey(id)) {
            toRemove.add(id);
            changeNeeded = true;
        }
    }

    public void clear(ServerPlayerEntity p) {
        elements.keySet().forEach(i -> toRemove.add(i));
        changeNeeded = true;
        update(p);
    }

    public boolean update(ServerPlayerEntity p) {
        if(!changeNeeded) {
            return false;
        }
        if(init) {
            init = false;
            p.connection.sendPacket(new SScoreboardObjectivePacket(o, 0));
        }

        if(!toRemove.isEmpty()) {
            toRemove.forEach(i -> {
                String s = elements.remove(i);
                if(s != null) {
                    p.connection.sendPacket(new SUpdateScorePacket(Action.REMOVE, o.getName(), s, -1));
                }
            });
            toRemove.clear();
        }

        if(!toAdd.isEmpty()) {
            toAdd.entrySet().stream().forEach((e) -> {
                elements.put(e.getKey(), e.getValue());
                p.connection.sendPacket(new SUpdateScorePacket(Action.CHANGE, o.getName(), e.getValue(), e.getKey()));
            });
            toAdd.clear();
        }

        changeNeeded = false;
        // displaying objective in sidebar
        p.connection.sendPacket(new SDisplayObjectivePacket(1, o));
        return false;
    }
}
*/
