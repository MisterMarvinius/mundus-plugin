package me.hammerle.kp.snuviscript.commands;

import me.hammerle.kp.KajetansPlugin;
import me.hammerle.snuviscript.code.SnuviUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import java.time.Duration;
import org.bukkit.entity.Player;

public class TitleCommands {
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerConsumer("title.settime", (sc, in) -> {
            ((Player) in[0].get(sc)).sendTitlePart(TitlePart.TIMES,
                    Title.Times.of(Duration.ofMillis(in[1].getInt(sc) * 50),
                            Duration.ofMillis(in[2].getInt(sc) * 50),
                            Duration.ofMillis(in[3].getInt(sc) * 50)));
        });
        KajetansPlugin.scriptManager.registerConsumer("title.clear", (sc, in) -> {
            ((Player) in[0].get(sc)).clearTitle();
        });
        KajetansPlugin.scriptManager.registerConsumer("title.reset", (sc, in) -> {
            ((Player) in[0].get(sc)).resetTitle();
        });
        KajetansPlugin.scriptManager.registerConsumer("title.send", (sc, in) -> {
            ((Player) in[0].get(sc)).sendTitlePart(TitlePart.TITLE,
                    Component.text(SnuviUtils.connect(sc, in, 1)));
        });
        KajetansPlugin.scriptManager.registerConsumer("title.setsub", (sc, in) -> {
            ((Player) in[0].get(sc)).sendTitlePart(TitlePart.SUBTITLE,
                    Component.text(SnuviUtils.connect(sc, in, 1)));
        });
    }
}
