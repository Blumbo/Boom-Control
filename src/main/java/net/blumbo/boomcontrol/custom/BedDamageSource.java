package net.blumbo.boomcontrol.custom;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Vec3d;

public class BedDamageSource extends DamageSource {

    private BedDamageSource(RegistryEntry<DamageType> type, Vec3d position) {
        super(type, position);
    }

    public static BedDamageSource bedExplode(DamageSources sources, Vec3d position) {
        return new BedDamageSource(sources.registry.entryOf(DamageTypes.BAD_RESPAWN_POINT), position);
    }

}
