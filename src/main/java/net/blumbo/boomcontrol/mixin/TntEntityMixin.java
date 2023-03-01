package net.blumbo.boomcontrol.mixin;

import net.blumbo.boomcontrol.custom.ExplosionValues;
import net.minecraft.entity.TntEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TntEntity.class)
public class TntEntityMixin {

    @ModifyArg(method = "explode", index = 4, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/world/explosion/Explosion$DestructionType;)Lnet/minecraft/world/explosion/Explosion;"))
    private float setPower(float power) {
        return (ExplosionValues.TNT.getPower(power));
    }

}
