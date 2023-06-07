package net.blumbo.boomcontrol.custom;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Vec3d;

public class AnchorDamageSource extends DamageSource {

    private AnchorDamageSource(RegistryEntry<DamageType> type, Vec3d position) {
        super(type, position);
    }

    public static AnchorDamageSource anchorExplode(DamageSources sources, Vec3d position) {
        return new AnchorDamageSource(sources.registry.entryOf(DamageTypes.BAD_RESPAWN_POINT), position);
    }

}
