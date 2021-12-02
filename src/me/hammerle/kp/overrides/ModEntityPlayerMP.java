/*package me.km.overrides;

import com.mojang.authlib.GameProfile;
import java.util.HashMap;
import me.hammerle.snuviscript.code.ISnuviScheduler;
import me.km.Server;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlayerListItemPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;

public class ModEntityPlayerMP extends ServerPlayerEntity {

    private final PlayerScoreboard board;
    private int id = -1;
    private final HashMap<String, Integer> timedData;
    private final HashMap<String, Object> data;
    private StringTextComponent tabDisplayName = null;
    private boolean elytra = false;

    private boolean isIterating = false;

    public ModEntityPlayerMP(MinecraftServer ms, ServerWorld w, GameProfile gp,
            PlayerInteractionManager pim) {
        super(ms, w, gp, pim);
        this.board = new PlayerScoreboard();
        this.timedData = new HashMap<>();
        this.data = new HashMap<>();
    }

    public PlayerScoreboard getScoreboard() {
        return board;
    }

    public ModEntityPlayerMP(MinecraftServer ms, ServerWorld w, GameProfile gp,
            PlayerInteractionManager pim, ModEntityPlayerMP old) {
        super(ms, w, gp, pim);
        this.board = old.board;
        this.timedData = old.timedData;
        this.data = old.data;
        this.id = old.id;
    }

    @Override
    public void tick() {
        super.tick();
        board.update(this);
        tickData();
        if(ticksElytraFlying == 1) {
            elytra = true;
            Server.scriptEvents.onPlayerStartElytra(this);
        }
        if(ticksElytraFlying == 0 && elytra) {
            elytra = false;
            Server.scriptEvents.onPlayerStopElytra(this);
        }
    }

    public void setTabListDisplayName(String name, ISnuviScheduler scheduler) {
        tabDisplayName = new StringTextComponent(name);
        scheduler.scheduleTask("setTabListDisplayName", () -> {
            server.getPlayerList().sendPacketToAllPlayers(new SPlayerListItemPacket(
                    SPlayerListItemPacket.Action.UPDATE_DISPLAY_NAME, this));
        }, 5);
    }

    @Override
    public ITextComponent getTabListDisplayName() {
        return tabDisplayName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    private void tickData() {
        isIterating = true;
        timedData.entrySet().removeIf(entry -> {
            int time = entry.getValue() - 1;
            if(time <= 0) {
                if(time == 0) {
                    Server.scriptEvents.onPlayerDataTick(this, entry.getKey());
                }
                return true;
            }
            entry.setValue(time);
            return false;
        });
        isIterating = false;
    }

    public void setVar(String varname, Object value) {
        data.put(varname, value);
    }

    public Object getVar(String varname) {
        return data.get(varname);
    }

    public void setTimer(String varname, int time, ISnuviScheduler scheduler) {
        if(isIterating) {
            scheduler.scheduleTask("setTimer", () -> timedData.put(varname, time));
        } else {
            timedData.put(varname, time);
        }
    }

    public int getTimer(String varname) {
        return timedData.getOrDefault(varname, -1);
    }

    public void clearData(ISnuviScheduler scheduler) {
        if(isIterating) {
            scheduler.scheduleTask("clearData", () -> timedData.clear());
        } else {
            timedData.clear();
        }
        data.clear();
    }

    @Override
    protected int getPermissionLevel() {
        return 999;
    }

    @Override
    public void setSneaking(boolean keyDownIn) {
        Server.scriptEvents.onSneak(attackingPlayer, keyDownIn);
        super.setSneaking(keyDownIn);
    }

    @Override
    protected int getExperiencePoints(PlayerEntity player) {
        if(!this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) && !this.isSpectator()) {
            int level = experienceLevel;
            int total = 0;
            if(level >= 32) {
                level -= 31;
                total += level * 112 + 9 * (level + 1) * level / 2;
                level = 31;
            }
            if(level >= 17) {
                level -= 16;
                total += level * 37 + 5 * (level + 1) * level / 2;
                level = 16;
            }
            total += level * 7 + 2 * level * (level - 1) / 2;
            total += Math.round(experience * xpBarCap());
            return total;
        } else {
            return 0;
        }
    }
}
*/
