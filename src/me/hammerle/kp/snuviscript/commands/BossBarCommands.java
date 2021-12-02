/*package me.km.snuviscript.commands;

import me.hammerle.snuviscript.code.ScriptManager;
import me.km.permissions.Permissions;
import me.km.snuviscript.Scripts;
import static me.km.snuviscript.commands.CommandUtils.doForGroup;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.BossInfo;

public class BossBarCommands {

    private static class CustomBossInfo extends BossInfo {
        public CustomBossInfo() {
            super(MathHelper.getRandomUUID(), new StringTextComponent("nothing"), BossInfo.Color.PINK, BossInfo.Overlay.PROGRESS);
        }
    }

    private static final CustomBossInfo INFO = new CustomBossInfo();

    public static void registerFunctions(ScriptManager sm, Scripts scripts, Permissions perms, MinecraftServer server) {
        sm.registerConsumer("boss.setcolor", (sc, in) -> {
            INFO.setColor(BossInfo.Color.valueOf(in[0].getString(sc)));
        });
        sm.registerConsumer("boss.setcreatefog", (sc, in) -> {
            INFO.setCreateFog(in[0].getBoolean(sc));
        });
        sm.registerConsumer("boss.setdarkensky", (sc, in) -> {
            INFO.setDarkenSky(in[0].getBoolean(sc));
        });
        sm.registerConsumer("boss.setname", (sc, in) -> {
            INFO.setName(new StringTextComponent(in[0].getString(sc)));
        });
        sm.registerConsumer("boss.setoverlay", (sc, in) -> {
            INFO.setOverlay(BossInfo.Overlay.valueOf(in[0].getString(sc)));
        });
        sm.registerConsumer("boss.setpercent", (sc, in) -> {
            INFO.setPercent(in[0].getFloat(sc));
        });
        sm.registerConsumer("boss.setplayendbossmusic", (sc, in) -> {
            INFO.setPlayEndBossMusic(in[0].getBoolean(sc));
        });
        sm.registerConsumer("boss.send", (sc, in) -> {
            SUpdateBossInfoPacket packet = new SUpdateBossInfoPacket(SUpdateBossInfoPacket.Operation.valueOf(in[1].getString(sc)), INFO);
            doForGroup(server, scripts, perms, in[0].get(sc), sc, p -> ((ServerPlayerEntity) p).connection.sendPacket(packet));
        });
    }
}
*/
