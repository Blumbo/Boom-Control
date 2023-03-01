package net.blumbo.boomcontrol.mixin;

import net.blumbo.boomcontrol.custom.ExplosionValues;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FireballEntity.class)
public class FireballEntityMixin extends AbstractFireballEntity {

    public FireballEntityMixin(EntityType<? extends AbstractFireballEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyArg(method = "onCollision", index = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFZLnet/minecraft/world/explosion/Explosion$DestructionType;)Lnet/minecraft/world/explosion/Explosion;"))
    private Entity setDamageEntity(Entity entity) {
        return (FireballEntity)(Object)this;
    }

    @ModifyArg(method = "onCollision", index = 4, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFZLnet/minecraft/world/explosion/Explosion$DestructionType;)Lnet/minecraft/world/explosion/Explosion;"))
    private float setPower(float power) {
        if (getOwner() instanceof GhastEntity) {
            return ExplosionValues.GHAST_FIREBALL.getPower(power);
        }
        return power;
    }

}
