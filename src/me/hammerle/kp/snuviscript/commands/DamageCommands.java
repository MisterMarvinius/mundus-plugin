package me.hammerle.kp.snuviscript.commands;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import me.hammerle.kp.KajetansPlugin;
import me.hammerle.kp.NMS;

public class DamageCommands {
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerFunction("damage.getimmediatesource",
                (sc, in) -> NMS.getImmediateSource(NMS.toDamageSource(in[0].get(sc))));
        KajetansPlugin.scriptManager.registerFunction("damage.gettruesource",
                (sc, in) -> NMS.getTrueSource(NMS.toDamageSource(in[0].get(sc))));
        KajetansPlugin.scriptManager.registerFunction("damage.iscreativeplayer",
                (sc, in) -> NMS.toDamageSource(in[0].get(sc)).i());
        KajetansPlugin.scriptManager.registerFunction("damage.isabsolute",
                (sc, in) -> NMS.toDamageSource(in[0].get(sc)).j());
        KajetansPlugin.scriptManager.registerFunction("damage.isdifficultyscaled",
                (sc, in) -> NMS.toDamageSource(in[0].get(sc)).w());
        KajetansPlugin.scriptManager.registerFunction("damage.isexplosion",
                (sc, in) -> NMS.toDamageSource(in[0].get(sc)).d());
        KajetansPlugin.scriptManager.registerFunction("damage.isfire",
                (sc, in) -> NMS.toDamageSource(in[0].get(sc)).s());
        KajetansPlugin.scriptManager.registerFunction("damage.ismagic",
                (sc, in) -> NMS.toDamageSource(in[0].get(sc)).x());
        KajetansPlugin.scriptManager.registerFunction("damage.isprojectile",
                (sc, in) -> NMS.toDamageSource(in[0].get(sc)).b());
        KajetansPlugin.scriptManager.registerFunction("damage.isunblockable",
                (sc, in) -> NMS.toDamageSource(in[0].get(sc)).f());
        KajetansPlugin.scriptManager.registerFunction("damage.isfall",
                (sc, in) -> NMS.toDamageSource(in[0].get(sc)).z());
        KajetansPlugin.scriptManager.registerFunction("damage.isdamaginghelmet",
                (sc, in) -> NMS.toDamageSource(in[0].get(sc)).g());
        KajetansPlugin.scriptManager.registerFunction("damage.issweep",
                (sc, in) -> NMS.toDamageSource(in[0].get(sc)).isSweep());
        KajetansPlugin.scriptManager.registerFunction("damage.gettype",
                (sc, in) -> NMS.toDamageSource(in[0].get(sc)).u());
        KajetansPlugin.scriptManager.registerFunction("damage.get",
                (sc, in) -> NMS.parseDamageSource(in[0].getString(sc)));
        KajetansPlugin.scriptManager.registerFunction("damage.sting",
                (sc, in) -> NMS.sting((LivingEntity) in[0].get(sc)));
        KajetansPlugin.scriptManager.registerFunction("damage.mobattack",
                (sc, in) -> NMS.mobAttack((LivingEntity) in[0].get(sc)));
        KajetansPlugin.scriptManager.registerFunction("damage.mobindirect",
                (sc, in) -> NMS.mobIndirect((Entity) in[0].get(sc), (LivingEntity) in[1].get(sc)));
        KajetansPlugin.scriptManager.registerFunction("damage.playerattack",
                (sc, in) -> NMS.playerAttack((Player) in[0].get(sc)));
        KajetansPlugin.scriptManager.registerFunction("damage.arrow",
                (sc, in) -> NMS.arrow((Arrow) in[0].get(sc), (Entity) in[1].get(sc)));
        KajetansPlugin.scriptManager.registerFunction("damage.trident",
                (sc, in) -> NMS.trident((Entity) in[0].get(sc), (Entity) in[1].get(sc)));
        KajetansPlugin.scriptManager.registerFunction("damage.firework",
                (sc, in) -> NMS.firework((Firework) in[0].get(sc), (Entity) in[1].get(sc)));
        KajetansPlugin.scriptManager.registerFunction("damage.fireball",
                (sc, in) -> NMS.fireball((LargeFireball) in[0].get(sc), (Entity) in[1].get(sc)));
        KajetansPlugin.scriptManager.registerFunction("damage.witherskull",
                (sc, in) -> NMS.witherSkull((WitherSkull) in[0].get(sc), (Entity) in[1].get(sc)));
        KajetansPlugin.scriptManager.registerFunction("damage.projectile",
                (sc, in) -> NMS.projectile((Entity) in[0].get(sc), (Entity) in[1].get(sc)));
        KajetansPlugin.scriptManager.registerFunction("damage.indirectmagic",
                (sc, in) -> NMS.indirectMagic((Entity) in[0].get(sc), (Entity) in[1].get(sc)));
        KajetansPlugin.scriptManager.registerFunction("damage.thorns",
                (sc, in) -> NMS.thorns((Entity) in[0].get(sc)));
        KajetansPlugin.scriptManager.registerFunction("damage.explosion",
                (sc, in) -> NMS.explosion((LivingEntity) in[0].get(sc)));
        KajetansPlugin.scriptManager.registerFunction("damage.netherbed",
                (sc, in) -> NMS.netherBed());
    }
}
