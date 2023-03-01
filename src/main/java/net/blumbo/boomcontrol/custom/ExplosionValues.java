package net.blumbo.boomcontrol.custom;

import net.blumbo.boomcontrol.commands.BoomControlCmd;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.world.explosion.Explosion;

import java.util.HashMap;

public class ExplosionValues {

    public static HashMap<String, ExplosionValues> valuesMap = new HashMap<>();

    public static final ExplosionValues TNT = new ExplosionValues("tnt", "TNT");
    public static final ExplosionValues CRYSTAL = new ExplosionValues("crystal");
    public static final ExplosionValues ANCHOR = new ExplosionValues("anchor").hasFire();
    public static final ExplosionValues BED = new ExplosionValues("bed").hasFire();

    public static final ExplosionValues CREEPER = new ExplosionValues("creeper");
    public static final ExplosionValues CHARGED_CREEPER = new ExplosionValues("chargedCreeper");
    public static final ExplosionValues GHAST_FIREBALL = new ExplosionValues("ghastFireball").hasFire();
    public static final ExplosionValues WITHER_SKULL = new ExplosionValues("witherSkull");

    public String name;
    public float powerPercentage;
    public float firePercentage;
    public boolean destroyItems;

    public ExplosionValues(String id, String name) {
        this.name = name;
        this.powerPercentage = 100F;
        this.firePercentage = 0F;
        this.destroyItems = true;
        valuesMap.put(id, this);
    }

    public ExplosionValues(String id) {
        this(id, BoomControlCmd.camelToLookGood(id));
    }

    public float getPower(float sourcePower) {
        return powerPercentage * sourcePower / 100F;
    }

    public ExplosionValues hasFire() {
        this.firePercentage = (100F / 3F);
        return this;
    }

    public static ExplosionValues get(Explosion explosion) {
        Entity e = explosion.entity;
        if (e != null) {
            if (e instanceof FireballEntity fireball && fireball.getOwner() instanceof GhastEntity) return GHAST_FIREBALL;
            if (e instanceof EndCrystalEntity) return CRYSTAL;
            if (e instanceof TntEntity) return TNT;
            if (e instanceof WitherSkullEntity) return WITHER_SKULL;
            if (e instanceof CreeperEntity creeper) {
                if (creeper.shouldRenderOverlay()) return CHARGED_CREEPER;
                else return CREEPER;
            }
        }

        DamageSource source = explosion.getDamageSource();
        if (source instanceof AnchorDamageSource) return ANCHOR;
        if (source instanceof BedDamageSource) return BED;

        return null;
    }

}
