package net.blumbo.boomcontrol.mixin;

import net.blumbo.boomcontrol.custom.ExplosionValues;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EndCrystalEntity.class)
public class EndCrystalEntityMixin {

    @ModifyArg(method = "damage", index = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/world/explosion/Explosion$DestructionType;)Lnet/minecraft/world/explosion/Explosion;"))
    private Entity setDamageEntity(Entity entity) {
        return (EndCrystalEntity)(Object)this;
    }

    @ModifyArg(method = "damage", index = 4, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/world/explosion/Explosion$DestructionType;)Lnet/minecraft/world/explosion/Explosion;"))
    private float setPower(float power) {
        return (ExplosionValues.CRYSTAL.getPower(power));
    }

}
