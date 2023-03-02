package net.blumbo.boomcontrol.custom;

import net.minecraft.entity.damage.BadRespawnPointDamageSource;
import net.minecraft.util.math.Vec3d;

public class BedDamageSource extends BadRespawnPointDamageSource {

    public BedDamageSource(Vec3d vec3d) {
        super(vec3d);
    }

}
