package com.divinity.hlspells.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

public class PiercingBoltEntity extends ArrowEntity
{
    public PiercingBoltEntity(EntityType<? extends PiercingBoltEntity> type, World world)
    {
        super(type, world);
    }

    @Override
    protected void onHit(RayTraceResult result)
    {
        RayTraceResult.Type raytraceresult$type = result.getType();
        if (raytraceresult$type == RayTraceResult.Type.ENTITY)
        {
            this.onHitEntity((EntityRayTraceResult) result);
        }

        else if (raytraceresult$type == RayTraceResult.Type.BLOCK)
        {
            this.onHitBlock((BlockRayTraceResult) result);
        }
    }

    @Override
    public IPacket<?> getAddEntityPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void tick()
    {
        super.tick();
        if (this.getOwner() != null && this.distanceTo(this.getOwner()) > 40)
        {
            this.remove();
        }
        Vector3d vector3d1 = this.getDeltaMovement();
        if (this.level.isClientSide)
        {
            this.level.addParticle(ParticleTypes.ENCHANTED_HIT, this.getX() - vector3d1.x, this.getY() - vector3d1.y + 0.15D, this.getZ() - vector3d1.z, 0.0D, 0.0D, 0.0D);
            this.level.addParticle(ParticleTypes.ENCHANTED_HIT, this.getX() - vector3d1.x, this.getY() - vector3d1.y + 0.16D, this.getZ() - vector3d1.z, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected float getWaterInertia()
    {
        return 1F;
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult result)
    {
        Entity entity = result.getEntity();
        Entity entity1 = this.getOwner();
        LivingEntity livingentity = entity1 instanceof LivingEntity ? (LivingEntity) entity1 : null;

        if (result.getEntity() == this.getOwner())
        {
            return;
        }

        boolean flag = entity.hurt(DamageSource.indirectMobAttack(this, livingentity).setProjectile().bypassArmor(), 8.0F);
        if (flag && this.level instanceof ServerWorld)
        {
            ((ServerWorld)this.level).sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 2, 0.2D, 0.2D, 0.2D, 0.0D);
            this.doEnchantDamageEffects(livingentity, entity);
            this.remove();
        }
    }

    @Override
    protected void onHitBlock(BlockRayTraceResult p_230299_1_)
    {
        if (this.level instanceof ServerWorld)
        {
            ((ServerWorld) this.level).sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 2, 0.2D, 0.2D, 0.2D, 0.0D);
            this.playSound(SoundEvents.SHULKER_BULLET_HIT, 1.0F, 1.0F);
            this.remove();
        }
    }

    @Override
    public void checkDespawn()
    {
        if (this.level.getDifficulty() == Difficulty.PEACEFUL)
        {
            this.remove();
        }
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent()
    {
        return SoundEvents.SHULKER_BULLET_HIT;
    }

    @Override
    public boolean isPickable()
    {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        if (!this.level.isClientSide && source.isProjectile())
        {
            this.playSound(SoundEvents.SHULKER_BULLET_HURT, 1.0F, 1.0F);
            ((ServerWorld)this.level).sendParticles(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 15, 0.2D, 0.2D, 0.2D, 0.0D);
            this.remove();
            return true;
        }
        return false;
    }

    @Override
    protected boolean canHitEntity(Entity p_230298_1_)
    {
        return super.canHitEntity(p_230298_1_) && !p_230298_1_.noPhysics;
    }

    @Override
    public boolean isOnFire()
    {
        return false;
    }
}
