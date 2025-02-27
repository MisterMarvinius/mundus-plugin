package me.hammerle.mp.snuviscript.commands;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import me.hammerle.mp.MundusPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.TextDisplay.TextAlignment;

public class DisplayCommands {
    public static void registerFunctions() {

        MundusPlugin.scriptManager.registerFunction("display.spawn", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            Component text = (Component) in[1].get(sc);
            World w = l.getWorld();
            TextDisplay t = w.spawn(l, TextDisplay.class, entity -> {
                entity.text(text);
            });
            return t;
        });
        MundusPlugin.scriptManager.registerConsumer("display.pivot", (sc, in) -> {
            TextDisplay t = (TextDisplay) in[0].get(sc);
            Billboard b = Billboard.valueOf(in[1].getString(sc));
            t.setBillboard(b);
        });
        MundusPlugin.scriptManager.registerConsumer("display.backgroundcolor", (sc, in) -> {
            TextDisplay t = (TextDisplay) in[0].get(sc);
            Color c = Color.fromARGB(in[1].getInt(sc), in[2].getInt(sc), in[3].getInt(sc),
                    in[4].getInt(sc));
            t.setBackgroundColor(c);
        });
        MundusPlugin.scriptManager.registerConsumer("display.linewidth", (sc, in) -> {
            TextDisplay t = (TextDisplay) in[0].get(sc);
            t.setLineWidth(in[1].getInt(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("display.alignment", (sc, in) -> {
            TextDisplay t = (TextDisplay) in[0].get(sc);
            t.setAlignment(TextAlignment.valueOf(in[1].getString(sc)));
        });
        MundusPlugin.scriptManager.registerConsumer("display.seethrough", (sc, in) -> {
            TextDisplay t = (TextDisplay) in[0].get(sc);
            t.setSeeThrough(in[1].getBoolean(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("display.seethrough", (sc, in) -> {
            TextDisplay t = (TextDisplay) in[0].get(sc);
            t.setShadowed(in[1].getBoolean(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("display.textopacity", (sc, in) -> {
            TextDisplay t = (TextDisplay) in[0].get(sc);
            t.setTextOpacity((byte) in[1].getInt(sc));
        });
    }
}
