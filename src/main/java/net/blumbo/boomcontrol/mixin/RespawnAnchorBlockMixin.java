package net.blumbo.boomcontrol.mixin;

import net.blumbo.boomcontrol.custom.AnchorDamageSource;
import net.blumbo.boomcontrol.custom.ExplosionValues;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(RespawnAnchorBlock.class)
public class RespawnAnchorBlockMixin {

    @ModifyArg(method = "explode", index = 1, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/world/explosion/ExplosionBehavior;DDDFZLnet/minecraft/world/explosion/Explosion$DestructionType;)Lnet/minecraft/world/explosion/Explosion;"))
    private DamageSource setDamageSource(DamageSource damageSource) {
        return new AnchorDamageSource();
    }

    @ModifyArg(method = "explode", index = 6, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/world/explosion/ExplosionBehavior;DDDFZLnet/minecraft/world/explosion/Explosion$DestructionType;)Lnet/minecraft/world/explosion/Explosion;"))
    private float setPower(float power) {
        return (ExplosionValues.ANCHOR.getPower(power));
    }

}
