package me.hammerle.kp.snuviscript.commands;

import me.hammerle.kp.KajetansPlugin;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.EulerAngle;

public class ArmorStandCommands {
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerFunction("as.getBodyPose",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).getBodyPose());
        KajetansPlugin.scriptManager.registerFunction("as.getHeadPose",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).getHeadPose());
        KajetansPlugin.scriptManager.registerFunction("as.getleftarmpose",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).getLeftArmPose());
        KajetansPlugin.scriptManager.registerFunction("as.getleftlegpose",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).getLeftLegPose());
        KajetansPlugin.scriptManager.registerFunction("as.getrightarmpose",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).getRightArmPose());
        KajetansPlugin.scriptManager.registerFunction("as.getrightlegpose",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).getRightLegPose());
        KajetansPlugin.scriptManager.registerFunction("as.hasarms",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).hasArms());
        KajetansPlugin.scriptManager.registerFunction("as.hasbaseplate",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).hasArms());
        KajetansPlugin.scriptManager.registerFunction("as.ismarker",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).isMarker());
        KajetansPlugin.scriptManager.registerFunction("as.issmall",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).isSmall());
        KajetansPlugin.scriptManager.registerConsumer("as.setarms",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).setArms(in[1].getBoolean(sc)));
        KajetansPlugin.scriptManager.registerConsumer("as.setbaseplate",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).setBasePlate(in[1].getBoolean(sc)));
        KajetansPlugin.scriptManager.registerConsumer("as.setmarker",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).setMarker(in[1].getBoolean(sc)));
        KajetansPlugin.scriptManager.registerConsumer("as.setsmall",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).setSmall(in[1].getBoolean(sc)));
        KajetansPlugin.scriptManager.registerConsumer("as.setbodypose",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).setBodyPose((EulerAngle) in[1].get(sc)));
        KajetansPlugin.scriptManager.registerConsumer("as.canmove",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).setCanMove(in[1].getBoolean(sc)));
        KajetansPlugin.scriptManager.registerConsumer("as.cantick",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).setCanTick(in[1].getBoolean(sc)));
        KajetansPlugin.scriptManager.registerConsumer("as.setheadpose",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).setHeadPose((EulerAngle) in[1].get(sc)));
        KajetansPlugin.scriptManager.registerConsumer("as.setleftarmpose",
                (sc, in) -> ((ArmorStand) in[0].get(sc))
                        .setLeftArmPose((EulerAngle) in[1].get(sc)));
        KajetansPlugin.scriptManager.registerConsumer("as.setleftlegpose",
                (sc, in) -> ((ArmorStand) in[0].get(sc))
                        .setLeftLegPose((EulerAngle) in[1].get(sc)));
        KajetansPlugin.scriptManager.registerConsumer("as.setrightarmpose",
                (sc, in) -> ((ArmorStand) in[0].get(sc))
                        .setRightArmPose((EulerAngle) in[1].get(sc)));
        KajetansPlugin.scriptManager.registerConsumer("as.setrightlegpose",
                (sc, in) -> ((ArmorStand) in[0].get(sc))
                        .setRightLegPose((EulerAngle) in[1].get(sc)));

        KajetansPlugin.scriptManager.registerConsumer("euler.new",
                (sc, in) -> new EulerAngle(in[0].getDouble(sc), in[1].getDouble(sc),
                        in[2].getDouble(sc)));
        KajetansPlugin.scriptManager.registerConsumer("euler.getx",
                (sc, in) -> ((EulerAngle) in[0].get(sc)).getX());
        KajetansPlugin.scriptManager.registerConsumer("euler.gety",
                (sc, in) -> ((EulerAngle) in[0].get(sc)).getY());
        KajetansPlugin.scriptManager.registerConsumer("euler.getz",
                (sc, in) -> ((EulerAngle) in[0].get(sc)).getZ());
        KajetansPlugin.scriptManager.registerConsumer("euler.setx",
                (sc, in) -> ((EulerAngle) in[0].get(sc)).setX(in[1].getDouble(sc)));
        KajetansPlugin.scriptManager.registerConsumer("euler.sety",
                (sc, in) -> ((EulerAngle) in[0].get(sc)).setY(in[1].getDouble(sc)));
        KajetansPlugin.scriptManager.registerConsumer("euler.setz",
                (sc, in) -> ((EulerAngle) in[0].get(sc)).setZ(in[1].getDouble(sc)));
    }
}
