/*package me.km.snuviscript.commands;

import me.hammerle.snuviscript.code.ScriptManager;
import me.km.utils.Location;
import me.km.utils.Mapper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.server.ServerWorld;

public class SoundCommands {
    public static void registerFunctions(ScriptManager sm) {
        sm.registerFunction("sound.get", (sc, in) -> Mapper.getSound(in[0].getString(sc)));
        sm.registerFunction("sound.getcategory", (sc, in) -> Mapper.getSoundCategory(in[0].getString(sc)));
        sm.registerConsumer("sound.spawn", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            ServerWorld sw = (ServerWorld) l.getWorld();
            float volume = in.length >= 4 ? in[3].getFloat(sc) : 1.0f;
            float pitch = in.length >= 5 ? in[4].getFloat(sc) : (sw.rand.nextFloat() * 0.1f + 0.9f);
            sw.playSound(null, l.getX(), l.getY(), l.getZ(), (SoundEvent) in[1].get(sc), (SoundCategory) in[2].get(sc), volume, pitch);
        });
        sm.registerConsumer("sound.spawnforplayer", (sc, in) -> {
            ServerPlayerEntity p = (ServerPlayerEntity) in[0].get(sc);
            float volume = in.length >= 4 ? in[3].getFloat(sc) : 1.0f;
            float pitch = in.length >= 5 ? in[4].getFloat(sc) : (p.world.rand.nextFloat() * 0.1f + 0.9f);
            p.connection.sendPacket(new SPlaySoundEffectPacket((SoundEvent) in[1].get(sc),
                    (SoundCategory) in[2].get(sc), p.getPosX(), p.getPosY(), p.getPosZ(), volume, pitch));
        });
    }
}
*/
