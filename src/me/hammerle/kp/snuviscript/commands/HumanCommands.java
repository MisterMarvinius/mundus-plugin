package me.hammerle.kp.snuviscript.commands;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import me.hammerle.kp.KajetansPlugin;
import me.hammerle.kp.NMS;
import me.hammerle.kp.NMS.Human;

public class HumanCommands {
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerFunction("human.spawn", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            return NMS.createHuman(in[1].getString(sc), l);
        });
        KajetansPlugin.scriptManager.registerConsumer("human.setskin", (sc, in) -> {
            Human h = (Human) in[0].get(sc);
            h.setSkin(in[1].getString(sc), in[2].getString(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("human.setskinuuid", (sc, in) -> {
            Human h = (Human) in[0].get(sc);
            PlayerProfile profile = Bukkit.createProfile(CommandUtils.getUUID(in[1].get(sc)));
            KajetansPlugin.scheduleAsyncTask(() -> {
                profile.complete();
                KajetansPlugin.scheduleTask(() -> h.setSkin(profile));
            });
        });
        KajetansPlugin.scriptManager.registerConsumer("human.setname", (sc, in) -> {
            Human h = (Human) in[0].get(sc);
            h.setName(in[1].getString(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("human.canmove", (sc, in) -> {
            Human h = (Human) in[0].get(sc);
            h.canMove(in[1].getBoolean(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("human.setai", (sc, in) -> {
            Human h = (Human) in[0].get(sc);
            h.setAI(in[1].getInt(sc));
        });
        KajetansPlugin.scriptManager.registerFunction("human.getai", (sc, in) -> {
            Human h = (Human) in[0].get(sc);
            return (double) h.getAI();
        });
        KajetansPlugin.scriptManager.registerFunction("human.moveto", (sc, in) -> {
            Human h = (Human) in[0].get(sc);
            return (double) h.moveTo(in[1].getDouble(sc), in[2].getDouble(sc), in[3].getDouble(sc));
        });
    }
}
