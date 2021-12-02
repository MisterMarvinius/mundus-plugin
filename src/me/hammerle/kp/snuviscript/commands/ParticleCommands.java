/*package me.km.snuviscript.commands;

import java.util.ArrayList;
import me.hammerle.snuviscript.code.ScriptManager;
import me.km.utils.Location;
import me.km.utils.Mapper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

public class ParticleCommands {
    @SuppressWarnings("unchecked")
    public static void registerFunctions(ScriptManager sm) {
        sm.registerFunction("particle.getall",
                (sc, in) -> new ArrayList<>(ForgeRegistries.PARTICLE_TYPES.getValues()));
        sm.registerFunction("particle.get", (sc, in) -> {
            ParticleType<?> data = Mapper.getParticle(in[0].getString(sc));
            if(data == ParticleTypes.BLOCK || data == ParticleTypes.FALLING_DUST) {
                return new BlockParticleData((ParticleType<BlockParticleData>) data,
                        Mapper.getBlock(in[1].getString(sc)).getDefaultState());
            } else if(data == ParticleTypes.DUST) {
                return new RedstoneParticleData(in[1].getFloat(sc), in[2].getFloat(sc),
                        in[3].getFloat(sc), in[4].getFloat(sc));
            } else if(data == ParticleTypes.ITEM) {
                return new ItemParticleData((ParticleType<ItemParticleData>) data,
                        new ItemStack(Mapper.getItem(in[1].getString(sc))));
            }
            return data;
        });
        sm.registerConsumer("particle.spawn", (sc, in) -> {
            Location l = ((Location) in[0].get(sc));
            IParticleData data = ((IParticleData) in[1].get(sc));
            int count = in.length >= 3 ? in[2].getInt(sc) : 1;
            double speed = in.length >= 4 ? in[3].getDouble(sc) : 0.0;
            double offX = in.length >= 5 ? in[4].getDouble(sc) : 0.0;
            double offY = in.length >= 6 ? in[5].getDouble(sc) : 0.0;
            double offZ = in.length >= 7 ? in[6].getDouble(sc) : 0.0;
            ((ServerWorld) l.getWorld()).spawnParticle(data, l.getX(), l.getY(), l.getZ(), count,
                    offX, offY, offZ, speed);
        });
        sm.registerConsumer("particle.spawnplayer", (sc, in) -> {
            Location l = ((Location) in[0].get(sc));
            IParticleData data = ((IParticleData) in[1].get(sc));
            ServerPlayerEntity p = (ServerPlayerEntity) in[2].get(sc);
            int count = in.length >= 4 ? in[3].getInt(sc) : 1;
            double speed = in.length >= 5 ? in[4].getDouble(sc) : 0.0;
            double offX = in.length >= 6 ? in[5].getDouble(sc) : 0.0;
            double offY = in.length >= 7 ? in[6].getDouble(sc) : 0.0;
            double offZ = in.length >= 8 ? in[7].getDouble(sc) : 0.0;
            ((ServerWorld) l.getWorld()).spawnParticle(p, data, true, l.getX(), l.getY(), l.getZ(),
                    count, offX, offY, offZ, speed);
        });
        sm.registerConsumer("particle.spawncircle", (sc, in) -> {
            Location l = ((Location) in[0].get(sc));
            IParticleData data = ((IParticleData) in[1].get(sc));
            int instances = in[2].getInt(sc);
            double radius = in[3].getDouble(sc);
            int count = in.length >= 5 ? in[4].getInt(sc) : 1;
            double speed = in.length >= 6 ? in[5].getDouble(sc) : 0.0;
            double offX = in.length >= 7 ? in[6].getDouble(sc) : 0.0;
            double offY = in.length >= 8 ? in[7].getDouble(sc) : 0.0;
            double offZ = in.length >= 9 ? in[8].getDouble(sc) : 0.0;
            double x = l.getX();
            double y = l.getY();
            double z = l.getZ();
            ServerWorld sw = (ServerWorld) l.getWorld();
            double angle = 2 * Math.PI / instances;
            for(int i = 0; i < instances; i++) {
                sw.spawnParticle(data, x + Math.cos(i * angle) * radius, y,
                        z + Math.sin(i * angle) * radius, count, offX, offY, offZ, speed);
            }
        });
        sm.registerConsumer("particle.spawnline", (sc, in) -> {
            Location l = ((Location) in[0].get(sc));
            IParticleData data = ((IParticleData) in[1].get(sc));
            int instances = in[2].getInt(sc);
            double stepX = in[3].getDouble(sc);
            double stepY = in[4].getDouble(sc);
            double stepZ = in[5].getDouble(sc);

            int count = in.length >= 7 ? in[6].getInt(sc) : 1;
            double speed = in.length >= 8 ? in[7].getDouble(sc) : 0.0;
            double offX = in.length >= 9 ? in[8].getDouble(sc) : 0.0;
            double offY = in.length >= 10 ? in[9].getDouble(sc) : 0.0;
            double offZ = in.length >= 11 ? in[10].getDouble(sc) : 0.0;
            double x = l.getX();
            double y = l.getY();
            double z = l.getZ();
            ServerWorld sw = (ServerWorld) l.getWorld();
            for(int i = 0; i < instances; i++) {
                sw.spawnParticle(data, x + i * stepX, y + i * stepY, z + i * stepZ, count, offX,
                        offY, offZ, speed);
            }
        });
    }
}
*/
