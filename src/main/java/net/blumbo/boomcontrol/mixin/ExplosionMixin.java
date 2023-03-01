package net.blumbo.boomcontrol.mixin;

import net.blumbo.boomcontrol.custom.ExplosionValues;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Explosion.class)
public class ExplosionMixin {

    @Shadow @Final private World world;

    @Shadow @Final @Nullable public Entity entity;
    private ExplosionValues explosionValues;

    @Inject(method = "collectBlocksAndDamageEntities", at = @At("HEAD"))
    private void setExplosionValues(CallbackInfo ci) {
        this.explosionValues = ExplosionValues.get((Explosion)(Object)this);
    }

    @Inject(method = "affectWorld", at = @At("HEAD"))
    private void setExplosionValuesOnWorld(CallbackInfo ci) {
        this.explosionValues = ExplosionValues.get((Explosion)(Object)this);
    }

    @Redirect(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isImmuneToExplosion()Z"))
    private boolean isImmuneToExplosion(Entity instance) {
        if (instance.isImmuneToExplosion()) return true;

        if (instance instanceof ItemEntity && explosionValues != null && !explosionValues.destroyItems) {
            return true;
        }

        return false;
    }

    @Redirect(method = "affectWorld", at = @At(value = "FIELD", target = "Lnet/minecraft/world/explosion/Explosion;createFire:Z"))
    private boolean createFire(Explosion instance) {
        if (!world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            if (entity instanceof CreeperEntity || entity instanceof WitherSkullEntity || entity instanceof FireballEntity) {
                return false;
            }
        }
        if (explosionValues == null) return instance.createFire;
        if (explosionValues.firePercentage <= 0) return false;
        return true;
    }

    @Redirect(method = "affectWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I"))
    private int setFireOdds(Random instance, int vanillaBound) {
        // Has to return 0 to create fire

        if (explosionValues == null) return instance.nextInt(vanillaBound);

        int bound = 1000000;
        int cap = Math.round(explosionValues.firePercentage * bound / 100F);
        if (instance.nextInt(bound) < cap) return 0;
        return 1;
    }

}
