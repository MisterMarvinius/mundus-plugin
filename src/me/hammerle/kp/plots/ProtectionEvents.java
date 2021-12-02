/*package me.hammerle.kp.plots;

import me.km.overrides.ModEntityPlayerMP;
import me.km.permissions.Permissions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ProtectionEvents {
    private final WorldPlotMap plots;
    private final Permissions perms;

    public ProtectionEvents(WorldPlotMap plotMap, Permissions perms) {
        this.plots = plotMap;
        this.perms = perms;
    }

    private boolean canBypass(PlayerEntity p) {
        return perms.has(p, "plot.bypass");
    }

    private boolean shouldBeProtected(Entity ent) {
        EntityType<?> type = ent.getType();
        return type == EntityType.ITEM_FRAME || type == EntityType.PAINTING
                || type == EntityType.ARMOR_STAND;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockEvent.EntityPlaceEvent e) {
        if(e.getEntity() instanceof PlayerEntity) {
            PlayerEntity p = (PlayerEntity) e.getEntity();
            if(!canBypass(p) && e.getWorld() instanceof World
                    && !plots.canPlaceBlock((World) e.getWorld(), e.getPos(), p)) {
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockEvent.BreakEvent e) {
        PlayerEntity p = e.getPlayer();
        if(!canBypass(p) && e.getWorld() instanceof World
                && !plots.canBreakBlock((World) e.getWorld(), e.getPos(), p)) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBossSpawn(EntityJoinWorldEvent e) {
        EntityType<?> type = e.getEntity().getType();
        if(type == EntityType.WITHER && !e.getWorld().getDimensionType().isUltrawarm()) {
            e.setCanceled(true);
        } else if(type == EntityType.ENDER_DRAGON
                && !e.getWorld().getDimensionType().doesHasDragonFight()) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBucketFill(FillBucketEvent e) {
        RayTraceResult ray = e.getTarget();
        if(ray == null || ray.getType() != RayTraceResult.Type.BLOCK || canBypass(e.getPlayer())) {
            return;
        }
        if(!plots.canUseBucket(e.getWorld(), ((BlockRayTraceResult) ray).getPos(), e.getPlayer())) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityHit(AttackEntityEvent e) {
        if(shouldBeProtected(e.getTarget())) {
            PlayerEntity p = e.getPlayer();
            if(!canBypass(p)
                    && !plots.canHitAmbientEntity(p.world, e.getTarget().getPosition(), p)) {
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onThrowableImpact(ProjectileImpactEvent.Throwable e) {
        if(e.getRayTraceResult().getType() == RayTraceResult.Type.ENTITY) {
            EntityRayTraceResult result = (EntityRayTraceResult) e.getRayTraceResult();
            if(shouldBeProtected(result.getEntity())) {
                Entity thrower = e.getThrowable().func_234616_v_();
                if(thrower != null && (thrower instanceof PlayerEntity)) {
                    PlayerEntity p = (PlayerEntity) thrower;
                    if(!canBypass(p) && !plots.canHitAmbientEntity(p.world,
                            e.getThrowable().getPosition(), p)) {
                        e.setCanceled(true);
                    }
                } else {
                    e.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onThrowableImpact(ProjectileImpactEvent.Arrow e) {
        if(e.getRayTraceResult().getType() == RayTraceResult.Type.ENTITY) {
            EntityRayTraceResult result = (EntityRayTraceResult) e.getRayTraceResult();
            if(shouldBeProtected(result.getEntity())) {
                Entity shooter = e.getArrow().func_234616_v_();
                if(shooter != null && (shooter instanceof ModEntityPlayerMP)) {
                    PlayerEntity p = (PlayerEntity) shooter;
                    if(!canBypass(p)
                            && !plots.canHitAmbientEntity(p.world, e.getArrow().getPosition(), p)) {
                        e.setCanceled(true);
                    }
                } else {
                    e.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void EntityProtectionPotion(EntityStruckByLightningEvent e) {
        e.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent.LeftClickBlock e) {
        PlayerEntity p = e.getPlayer();
        if(!canBypass(p) && !plots.canInteractWithBlock(e.getWorld(), e.getPos(), p)) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent.RightClickBlock e) {
        PlayerEntity p = e.getPlayer();
        if(!canBypass(p) && !plots.canInteractWithBlock(e.getWorld(), e.getPos(), p)) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerEntityInteract(PlayerInteractEvent.EntityInteract e) {
        PlayerEntity p = e.getPlayer();
        if(!canBypass(p)
                && !plots.canInteractWithEntity(e.getWorld(), e.getTarget().getPosition(), p)) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onFarmlandTrample(BlockEvent.FarmlandTrampleEvent e) {
        e.setCanceled(true);
    }
}
*/
