package me.hammerle.kp.snuviscript.commands;

import me.hammerle.kp.KajetansPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import java.time.Duration;
import org.bukkit.entity.Player;

public class TitleCommands {
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerConsumer("title.remove", (sc, in) -> {
            ((Player) in[0].get(sc)).clearTitle();
        });
        KajetansPlugin.scriptManager.registerConsumer("title.send", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            p.sendTitlePart(TitlePart.TITLE, (Component) in[1].get(sc));
            Component sub = in.length > 2 ? (Component) in[2].get(sc) : Component.text("");
            p.sendTitlePart(TitlePart.SUBTITLE, sub);
            int fadeIn = in.length > 3 ? in[3].getInt(sc) : 20;
            int stay = in.length > 4 ? in[4].getInt(sc) : 60;
            int fadeOut = in.length > 5 ? in[5].getInt(sc) : 20;
            p.sendTitlePart(TitlePart.TIMES, Title.Times.of(Duration.ofMillis(fadeIn * 50),
                    Duration.ofMillis(stay * 50), Duration.ofMillis(fadeOut * 50)));
        });
    }
}
