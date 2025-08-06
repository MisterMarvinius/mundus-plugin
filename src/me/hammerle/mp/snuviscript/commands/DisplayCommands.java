package me.hammerle.mp.snuviscript.commands;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import me.hammerle.mp.MundusPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class DisplayCommands {
    public static void registerFunctions() {

        MundusPlugin.scriptManager.registerConsumer("display.pivot", (sc, in) -> {
            Display d = (Display) in[0].get(sc);
            Billboard b = Billboard.valueOf(in[1].getString(sc));
            d.setBillboard(b);
        });

        MundusPlugin.scriptManager.registerFunction("display.spawn", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            Component text = (Component) in[1].get(sc);
            World w = l.getWorld();
            TextDisplay d = w.spawn(l, TextDisplay.class, entity -> {
                entity.text(text);
            });
            return d;
        });
        MundusPlugin.scriptManager.registerConsumer("display.backgroundcolor", (sc, in) -> {
            TextDisplay d = (TextDisplay) in[0].get(sc);
            Color c = Color.fromARGB(in[1].getInt(sc), in[2].getInt(sc), in[3].getInt(sc),
                    in[4].getInt(sc));
            d.setBackgroundColor(c);
        });
        MundusPlugin.scriptManager.registerConsumer("display.settext", (sc, in) -> {
            TextDisplay d = (TextDisplay) in[0].get(sc);
            Component text = (Component) in[1].get(sc);
            d.text(text);
        });
        MundusPlugin.scriptManager.registerConsumer("display.linewidth", (sc, in) -> {
            TextDisplay d = (TextDisplay) in[0].get(sc);
            d.setLineWidth(in[1].getInt(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("display.alignment", (sc, in) -> {
            TextDisplay d = (TextDisplay) in[0].get(sc);
            d.setAlignment(TextAlignment.valueOf(in[1].getString(sc)));
        });
        MundusPlugin.scriptManager.registerConsumer("display.seethrough", (sc, in) -> {
            TextDisplay d = (TextDisplay) in[0].get(sc);
            d.setSeeThrough(in[1].getBoolean(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("display.shadowed", (sc, in) -> {
            TextDisplay d = (TextDisplay) in[0].get(sc);
            d.setShadowed(in[1].getBoolean(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("display.opacity", (sc, in) -> {
            TextDisplay d = (TextDisplay) in[0].get(sc);
            d.setTextOpacity((byte) in[1].getInt(sc));
        });


        MundusPlugin.scriptManager.registerFunction("display.spawnblock", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            Material m = (Material) in[1].get(sc);
            World w = l.getWorld();
            BlockDisplay d = w.spawn(l, BlockDisplay.class, entity -> {
                entity.setBlock(m.createBlockData());
            });
            return d;
        });
        MundusPlugin.scriptManager.registerConsumer("display.setblock", (sc, in) -> {
            BlockDisplay b = (BlockDisplay) in[0].get(sc);
            Material m = (Material) in[1].get(sc);
            b.setBlock(m.createBlockData());
        });


        MundusPlugin.scriptManager.registerFunction("display.spawnitem", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            ItemStack stack = (ItemStack) in[1].get(sc);
            World w = l.getWorld();
            ItemDisplay d = w.spawn(l, ItemDisplay.class, entity -> {
                entity.setItemStack(stack);
            });
            return d;
        });
        MundusPlugin.scriptManager.registerConsumer("display.setitem", (sc, in) -> {
            ItemDisplay d = (ItemDisplay) in[0].get(sc);
            ItemStack stack = (ItemStack) in[1].get(sc);
            d.setItemStack(stack);
        });

        MundusPlugin.scriptManager.registerConsumer("display.transform", (sc, in) -> {
            Display d = (Display) in[0].get(sc);
            Vector3f translation = (Vector3f) in[1].get(sc);
            Quaternionf leftRotation = (Quaternionf) in[2].get(sc);
            Vector3f scale = (Vector3f) in[3].get(sc);
            Quaternionf rightRotation = (Quaternionf) in[4].get(sc);
            d.setTransformation(new Transformation(
                    translation,
                    leftRotation,
                    scale,
                    rightRotation));
        });
        MundusPlugin.scriptManager.registerFunction("vector.new",
                (sc, in) -> new Vector3f(in[0].getFloat(sc), in[1].getFloat(sc),
                        in[2].getFloat(sc)));
        MundusPlugin.scriptManager.registerFunction("rotation.new",
                (sc, in) -> new Quaternionf(in[0].getFloat(sc), in[1].getFloat(sc),
                        in[2].getFloat(sc), in[3].getFloat(sc)));
    }
}
