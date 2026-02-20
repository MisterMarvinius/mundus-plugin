package me.hammerle.mp.snuviscript.commands;

import me.hammerle.mp.MundusPlugin;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.EulerAngle;

public class ArmorStandCommands {
    public static void registerFunctions() {
        MundusPlugin.scriptManager.registerFunction("as.getbodypose",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).getBodyPose(), "object");
        MundusPlugin.scriptManager.registerFunction("as.getheadpose",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).getHeadPose(), "object");
        MundusPlugin.scriptManager.registerFunction("as.getleftarmpose",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).getLeftArmPose(), "object");
        MundusPlugin.scriptManager.registerFunction("as.getleftlegpose",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).getLeftLegPose(), "object");
        MundusPlugin.scriptManager.registerFunction("as.getrightarmpose",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).getRightArmPose(), "object");
        MundusPlugin.scriptManager.registerFunction("as.getrightlegpose",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).getRightLegPose(), "object");
        MundusPlugin.scriptManager.registerFunction("as.hasarms",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).hasArms(), "object");
        MundusPlugin.scriptManager.registerFunction("as.hasbaseplate",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).hasArms(), "object");
        MundusPlugin.scriptManager.registerFunction("as.ismarker",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).isMarker(), "object");
        MundusPlugin.scriptManager.registerFunction("as.issmall",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).isSmall(), "object");
        MundusPlugin.scriptManager.registerConsumer("as.setarms",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).setArms(in[1].getBoolean(sc)));
        MundusPlugin.scriptManager.registerConsumer("as.setbaseplate",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).setBasePlate(in[1].getBoolean(sc)));
        MundusPlugin.scriptManager.registerConsumer("as.setmarker",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).setMarker(in[1].getBoolean(sc)));
        MundusPlugin.scriptManager.registerConsumer("as.setsmall",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).setSmall(in[1].getBoolean(sc)));
        MundusPlugin.scriptManager.registerConsumer("as.setbodypose",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).setBodyPose((EulerAngle) in[1].get(sc)));
        MundusPlugin.scriptManager.registerConsumer("as.canmove",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).setCanMove(in[1].getBoolean(sc)));
        MundusPlugin.scriptManager.registerConsumer("as.cantick",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).setCanTick(in[1].getBoolean(sc)));
        MundusPlugin.scriptManager.registerConsumer("as.setheadpose",
                (sc, in) -> ((ArmorStand) in[0].get(sc)).setHeadPose((EulerAngle) in[1].get(sc)));
        MundusPlugin.scriptManager.registerConsumer("as.setleftarmpose",
                (sc, in) -> ((ArmorStand) in[0].get(sc))
                        .setLeftArmPose((EulerAngle) in[1].get(sc)));
        MundusPlugin.scriptManager.registerConsumer("as.setleftlegpose",
                (sc, in) -> ((ArmorStand) in[0].get(sc))
                        .setLeftLegPose((EulerAngle) in[1].get(sc)));
        MundusPlugin.scriptManager.registerConsumer("as.setrightarmpose",
                (sc, in) -> ((ArmorStand) in[0].get(sc))
                        .setRightArmPose((EulerAngle) in[1].get(sc)));
        MundusPlugin.scriptManager.registerConsumer("as.setrightlegpose",
                (sc, in) -> ((ArmorStand) in[0].get(sc))
                        .setRightLegPose((EulerAngle) in[1].get(sc)));

        MundusPlugin.scriptManager.registerFunction("euler.new",
                (sc, in) -> new EulerAngle(in[0].getDouble(sc), in[1].getDouble(sc),
                        in[2].getDouble(sc)), "object");
        MundusPlugin.scriptManager.registerFunction("euler.getx",
                (sc, in) -> ((EulerAngle) in[0].get(sc)).getX(), "object");
        MundusPlugin.scriptManager.registerFunction("euler.gety",
                (sc, in) -> ((EulerAngle) in[0].get(sc)).getY(), "object");
        MundusPlugin.scriptManager.registerFunction("euler.getz",
                (sc, in) -> ((EulerAngle) in[0].get(sc)).getZ(), "object");
        MundusPlugin.scriptManager.registerFunction("euler.setx",
                (sc, in) -> ((EulerAngle) in[0].get(sc)).setX(in[1].getDouble(sc)), "object");
        MundusPlugin.scriptManager.registerFunction("euler.sety",
                (sc, in) -> ((EulerAngle) in[0].get(sc)).setY(in[1].getDouble(sc)), "object");
        MundusPlugin.scriptManager.registerFunction("euler.setz",
                (sc, in) -> ((EulerAngle) in[0].get(sc)).setZ(in[1].getDouble(sc)), "object");
    }
}
