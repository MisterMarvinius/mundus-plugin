package me.hammerle.mp.snuviscript.commands;

import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import me.hammerle.mp.MundusPlugin;

public class SoundCommands {
    public static void registerFunctions() {
        MundusPlugin.scriptManager.registerFunction("sound.getall", (sc, in) -> {
            return Registry.SOUNDS.stream()
                    .map(f -> "minecraft:" + f.toString().toLowerCase())
                    .sorted()
                    .collect(Collectors.toList());
        }, "object");
        MundusPlugin.scriptManager.registerFunction("sound.get",
                (sc, in) -> Registry.SOUNDS.get(NamespacedKey.fromString(in[0].getString(sc))), "object");
        MundusPlugin.scriptManager.registerFunction("sound.getcategory",
                (sc, in) -> SoundCategory.valueOf(in[0].getString(sc)), "object");
        MundusPlugin.scriptManager.registerConsumer("sound.spawn", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            float volume = in.length >= 4 ? in[3].getFloat(sc) : 1.0f;
            float pitch =
                    in.length >= 5 ? in[4].getFloat(sc) : ((float) Math.random() * 0.1f + 0.9f);
            l.getWorld().playSound(l, (Sound) in[1].get(sc), (SoundCategory) in[2].get(sc), volume,
                    pitch);
        });
        MundusPlugin.scriptManager.registerConsumer("sound.spawnforplayer", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            float volume = in.length >= 4 ? in[3].getFloat(sc) : 1.0f;
            float pitch =
                    in.length >= 5 ? in[4].getFloat(sc) : ((float) Math.random() * 0.1f + 0.9f);
            p.playSound(p.getLocation(), (Sound) in[1].get(sc), (SoundCategory) in[2].get(sc),
                    volume, pitch);
        });
    }
}
