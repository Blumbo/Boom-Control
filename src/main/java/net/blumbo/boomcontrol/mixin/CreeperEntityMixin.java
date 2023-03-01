package net.blumbo.boomcontrol.mixin;

import net.blumbo.boomcontrol.custom.ExplosionValues;
import net.minecraft.entity.mob.CreeperEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin {

    @Shadow public abstract boolean shouldRenderOverlay();

    @ModifyArg(method = "explode", index = 4, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/world/explosion/Explosion$DestructionType;)Lnet/minecraft/world/explosion/Explosion;"))
    private float setPower(float power) {
        if (shouldRenderOverlay()) {
            return ExplosionValues.CHARGED_CREEPER.getPower(power);
        }
        return (ExplosionValues.CREEPER.getPower(power));
    }


}
