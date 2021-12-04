package me.hammerle.kp.snuviscript.commands;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import me.hammerle.kp.KajetansPlugin;

public class ParticleCommands {
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerFunction("particle.getall",
                (sc, in) -> Particle.values());
        KajetansPlugin.scriptManager.registerFunction("particle.get",
                (sc, in) -> Particle.valueOf(in[0].getString(sc)));
        KajetansPlugin.scriptManager.registerConsumer("particle.spawn", (sc, in) -> {
            Location l = ((Location) in[0].get(sc));
            Particle data = ((Particle) in[1].get(sc));
            int count = in.length >= 3 ? in[2].getInt(sc) : 1;
            double speed = in.length >= 4 ? in[3].getDouble(sc) : 0.0;
            double offX = in.length >= 5 ? in[4].getDouble(sc) : 0.0;
            double offY = in.length >= 6 ? in[5].getDouble(sc) : 0.0;
            double offZ = in.length >= 7 ? in[6].getDouble(sc) : 0.0;
            l.getWorld().spawnParticle(data, l.getX(), l.getY(), l.getZ(), count, offX, offY, offZ,
                    speed);
        });
        KajetansPlugin.scriptManager.registerConsumer("particle.spawnplayer", (sc, in) -> {
            Location l = ((Location) in[0].get(sc));
            Particle data = ((Particle) in[1].get(sc));
            Player p = (Player) in[2].get(sc);
            int count = in.length >= 4 ? in[3].getInt(sc) : 1;
            double speed = in.length >= 5 ? in[4].getDouble(sc) : 0.0;
            double offX = in.length >= 6 ? in[5].getDouble(sc) : 0.0;
            double offY = in.length >= 7 ? in[6].getDouble(sc) : 0.0;
            double offZ = in.length >= 8 ? in[7].getDouble(sc) : 0.0;
            p.spawnParticle(data, l, count, offX, offY, offZ, speed);
        });
    }
}
