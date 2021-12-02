package me.hammerle.kp.snuviscript.commands;

public class DamageCommands {
    public static void registerFunctions() {
        /*KajetansPlugin.scriptManager.registerFunction("damage.getimmediatesource",
                (sc, in) -> ((DamageSource) in[0].get(sc)).getImmediateSource());
        KajetansPlugin.scriptManager.registerFunction("damage.gettruesource",
                (sc, in) -> ((DamageSource) in[0].get(sc)).getTrueSource());
        KajetansPlugin.scriptManager.registerFunction("damage.iscreativeplayer",
                (sc, in) -> ((DamageSource) in[0].get(sc)).isCreativePlayer());
        KajetansPlugin.scriptManager.registerFunction("damage.isabsolute",
                (sc, in) -> ((DamageSource) in[0].get(sc)).isDamageAbsolute());
        KajetansPlugin.scriptManager.registerFunction("damage.isdifficultyscaled",
                (sc, in) -> ((DamageSource) in[0].get(sc)).isDifficultyScaled());
        KajetansPlugin.scriptManager.registerFunction("damage.isexplosion",
                (sc, in) -> ((DamageSource) in[0].get(sc)).isExplosion());
        KajetansPlugin.scriptManager.registerFunction("damage.isfire",
                (sc, in) -> ((DamageSource) in[0].get(sc)).isFireDamage());
        KajetansPlugin.scriptManager.registerFunction("damage.ismagic",
                (sc, in) -> ((DamageSource) in[0].get(sc)).isMagicDamage());
        KajetansPlugin.scriptManager.registerFunction("damage.isprojectile",
                (sc, in) -> ((DamageSource) in[0].get(sc)).isProjectile());
        KajetansPlugin.scriptManager.registerFunction("damage.isunblockable",
                (sc, in) -> ((DamageSource) in[0].get(sc)).isUnblockable());
        KajetansPlugin.scriptManager.registerFunction("damage.gettype",
                (sc, in) -> ((DamageSource) in[0].get(sc)).getDamageType());
        KajetansPlugin.scriptManager.registerFunction("damage.get", (sc, in) -> {
            Object o = in[0].get(sc);
            if(o instanceof LivingEntity) {
                LivingEntity ent = (LivingEntity) o;
                DamageSource ds;
                if(ent instanceof PlayerEntity) {
                    ds = DamageSource.causePlayerDamage((PlayerEntity) ent);
                } else {
                    ds = DamageSource.causeMobDamage(ent);
                }
        
                if(in[1].getBoolean(sc)) {
                    ds.setDamageAllowedInCreativeMode();
                }
                if(in[2].getBoolean(sc)) {
                    ds.setDamageBypassesArmor();
                }
                if(in[3].getBoolean(sc)) {
                    ds.setDamageIsAbsolute();
                }
                if(in[4].getBoolean(sc)) {
                    ds.setExplosion();
                }
                if(in[5].getBoolean(sc)) {
                    ds.setFireDamage();
                }
                if(in[6].getBoolean(sc)) {
                    ds.setMagicDamage();
                }
                if(in[7].getBoolean(sc)) {
                    ds.setProjectile();
                }
                return ds;
            }
        
            switch(o.toString()) {
                case "inFire":
                    return DamageSource.IN_FIRE;
                case "lightningBolt":
                    return DamageSource.LIGHTNING_BOLT;
                case "onFire":
                    return DamageSource.ON_FIRE;
                case "lava":
                    return DamageSource.LAVA;
                case "hotFloor":
                    return DamageSource.HOT_FLOOR;
                case "inWall":
                    return DamageSource.IN_WALL;
                case "cramming":
                    return DamageSource.CRAMMING;
                case "drown":
                    return DamageSource.DROWN;
                case "starve":
                    return DamageSource.STARVE;
                case "cactus":
                    return DamageSource.CACTUS;
                case "fall":
                    return DamageSource.FALL;
                case "flyIntoWall":
                    return DamageSource.FLY_INTO_WALL;
                case "outOfWorld":
                    return DamageSource.OUT_OF_WORLD;
                case "generic":
                    return DamageSource.GENERIC;
                case "magic":
                    return DamageSource.MAGIC;
                case "wither":
                    return DamageSource.WITHER;
                case "anvil":
                    return DamageSource.ANVIL;
                case "fallingBlock":
                    return DamageSource.FALLING_BLOCK;
                case "dragonBreath":
                    return DamageSource.DRAGON_BREATH;
                case "dryout":
                    return DamageSource.DRYOUT;
                case "sweetBerryBush":
                    return DamageSource.SWEET_BERRY_BUSH;
                case "thorns":
                    return DamageSource.causeThornsDamage((Entity) in[1].get(sc));
            }
            return DamageSource.GENERIC;
        });*/
    }
}
