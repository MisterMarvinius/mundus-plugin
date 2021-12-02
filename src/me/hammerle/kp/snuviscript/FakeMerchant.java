/*package me.km.snuviscript;

import me.km.entities.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FakeMerchant extends Entity implements IMerchant {
    private PlayerEntity customer = null;
    private final MerchantOffers offers = new MerchantOffers();

    public FakeMerchant() {
        super(ModEntities.NOBODY, null);
    }

    @Override
    public PlayerEntity getCustomer() {
        return this.customer;
    }

    @Override
    public void setCustomer(PlayerEntity p) {
        if (p != null) {
            world = p.world;
            setPosition(p.getPosX(), p.getPosY(), p.getPosZ());
        }
        this.customer = p;
    }

    @Override
    public MerchantOffers getOffers() {
        return this.offers;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void setClientSideOffers(MerchantOffers offers) {
    }

    @Override
    public void onTrade(MerchantOffer offer) {
        offer.increaseUses();
    }

    @Override
    public void verifySellingItem(ItemStack stack) {
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public int getXp() {
        return 0;
    }

    @Override
    public void setXP(int xp) {
    }

    @Override
    public SoundEvent getYesSound() {
        return SoundEvents.ENTITY_VILLAGER_YES;
    }

    @Override
    protected void registerData() {
    }

    @Override
    protected void readAdditional(CompoundNBT cnbt) {
    }

    @Override
    protected void writeAdditional(CompoundNBT cnbt) {
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return null;
    }

    @Override
    public boolean hasXPBar() {
        return false;
    }
}
*/
