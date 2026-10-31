package ru.orbital.cannon;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class CannonConfig {

    private EntityType defaultProjectile = EntityType.TNT;
    private int defaultCount = 50;

    private double height = 60.0;
    private double heightJitter = 10.0;
    private double radius = 8.0;
    private double velocity = -2.5;
    private int perTick = 20;
    private int tntFuseExtra = 5;
    private Material fallingBlockMaterial = Material.SAND;
    private boolean disableAi = false;

    private int maxPerFire = 2000;
    private int unlimitedCap = 500;
    private int cleanupSeconds = 600;
    private final Set<EntityType> blockedTypes = EnumSet.noneOf(EntityType.class);

    private int cooldownSeconds = 3;
    private boolean launchSound = true;

    private String rodName = "";
    private boolean rodLore = false;

    private boolean mobSlowFalling = true;
    private int mobSlowFallingSeconds = 30;
    private double mobFallSpeed = -0.3;

    private EntityType orbitalProjectile = EntityType.TNT;
    private double orbitalHeight = 300.0;
    private double orbitalJitter = 6.0;
    private double orbitalRadius = 25.0;
    private double orbitalVelocity = -2.5;
    private int orbitalFuseExtra = 10;

    private EntityType megaorbitalProjectile = EntityType.TNT;
    private double megaorbitalHeight = 300.0;
    private double megaorbitalJitter = 10.0;
    private double megaorbitalRadius = 1000.0;
    private double megaorbitalVelocity = -2.5;
    private int megaorbitalFuseExtra = 10;

    private EntityType stabProjectile = EntityType.TNT;
    private double stabHeight = 300.0;
    private double stabJitter = 4.0;
    private double stabRadius = 1.5;
    private double stabVelocity = -8.0;
    private double stabDepth = 8.0;
    private double stabSpeed = 2.5;
    private float stabYield = 6.0f;

    private EntityType witherProjectile = EntityType.WITHER_SKULL;
    private double witherHeight = 80.0;
    private double witherJitter = 10.0;
    private double witherRadius = 6.0;
    private double witherVelocity = -3.0;

    public void reload(FileConfiguration c) {
        this.defaultProjectile = parseType(c.getString("defaults.projectile", "TNT"));
        if (this.defaultProjectile == null) {
            this.defaultProjectile = EntityType.TNT;
        }
        this.defaultCount = Math.max(1, c.getInt("defaults.count", 50));

        this.height = c.getDouble("spawn.height", 60.0);
        this.heightJitter = Math.max(0.0, c.getDouble("spawn.height-jitter", 10.0));
        this.radius = Math.max(0.0, c.getDouble("spawn.radius", 8.0));
        this.velocity = c.getDouble("spawn.velocity", -2.5);
        this.perTick = Math.max(1, c.getInt("spawn.per-tick", 20));
        this.tntFuseExtra = Math.max(0, c.getInt("spawn.tnt-fuse-extra", 5));
        this.disableAi = c.getBoolean("spawn.disable-ai", false);

        Material mat = Material.matchMaterial(c.getString("spawn.falling-block-material", "SAND"));
        this.fallingBlockMaterial = mat != null ? mat : Material.SAND;

        this.maxPerFire = c.getInt("safety.max-entities-per-fire", 2000);
        this.unlimitedCap = c.getInt("safety.unlimited-cap", 500);
        this.cleanupSeconds = Math.max(0, c.getInt("safety.cleanup-seconds", 600));

        this.blockedTypes.clear();
        List<String> blocked = c.getStringList("safety.blocked-types");
        for (String raw : blocked) {
            EntityType type = parseType(raw);
            if (type != null) {
                this.blockedTypes.add(type);
            }
        }

        this.cooldownSeconds = Math.max(0, c.getInt("cooldown-seconds", 3));
        this.launchSound = c.getBoolean("effects.launch-sound", true);

        this.rodName = c.getString("disguise.rod-name", "");
        this.rodLore = c.getBoolean("disguise.rod-lore", false);

        this.mobSlowFalling = c.getBoolean("mobs.slow-falling", true);
        this.mobSlowFallingSeconds = Math.max(1, c.getInt("mobs.slow-falling-seconds", 30));
        this.mobFallSpeed = c.getDouble("mobs.fall-speed", -0.3);

        this.orbitalProjectile = parseType(c.getString("modes.orbital.projectile", "TNT"));
        if (this.orbitalProjectile == null) {
            this.orbitalProjectile = EntityType.TNT;
        }
        this.orbitalHeight = c.getDouble("modes.orbital.height", 300.0);
        this.orbitalJitter = Math.max(0.0, c.getDouble("modes.orbital.height-jitter", 6.0));
        this.orbitalRadius = Math.max(0.0, c.getDouble("modes.orbital.radius", 25.0));
        this.orbitalVelocity = c.getDouble("modes.orbital.velocity", -2.5);
        this.orbitalFuseExtra = Math.max(0, c.getInt("modes.orbital.tnt-fuse-extra", 10));

        this.megaorbitalProjectile = parseType(c.getString("modes.megaorbital.projectile", "TNT"));
        if (this.megaorbitalProjectile == null) {
            this.megaorbitalProjectile = EntityType.TNT;
        }
        this.megaorbitalHeight = c.getDouble("modes.megaorbital.height", 300.0);
        this.megaorbitalJitter = Math.max(0.0, c.getDouble("modes.megaorbital.height-jitter", 10.0));
        this.megaorbitalRadius = Math.max(0.0, c.getDouble("modes.megaorbital.radius", 1000.0));
        this.megaorbitalVelocity = c.getDouble("modes.megaorbital.velocity", -2.5);
        this.megaorbitalFuseExtra = Math.max(0, c.getInt("modes.megaorbital.tnt-fuse-extra", 10));

        this.stabProjectile = parseType(c.getString("modes.stab.projectile", "TNT"));
        if (this.stabProjectile == null) {
            this.stabProjectile = EntityType.TNT;
        }
        this.stabHeight = c.getDouble("modes.stab.height", 300.0);
        this.stabJitter = Math.max(0.0, c.getDouble("modes.stab.height-jitter", 4.0));
        this.stabRadius = Math.max(0.0, c.getDouble("modes.stab.radius", 1.5));
        this.stabVelocity = c.getDouble("modes.stab.velocity", -8.0);
        this.stabDepth = Math.max(0.0, c.getDouble("modes.stab.depth", 8.0));
        this.stabSpeed = Math.max(0.25, c.getDouble("modes.stab.speed", 2.5));
        this.stabYield = (float) Math.max(0.0, c.getDouble("modes.stab.yield", 6.0));

        this.witherProjectile = parseType(c.getString("modes.wither.projectile", "WITHER_SKULL"));
        if (this.witherProjectile == null) {
            this.witherProjectile = EntityType.WITHER_SKULL;
        }
        this.witherHeight = c.getDouble("modes.wither.height", 80.0);
        this.witherJitter = Math.max(0.0, c.getDouble("modes.wither.height-jitter", 10.0));
        this.witherRadius = Math.max(0.0, c.getDouble("modes.wither.radius", 6.0));
        this.witherVelocity = c.getDouble("modes.wither.velocity", -3.0);
    }

    public static EntityType parseType(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String normalized = raw.trim().toUpperCase(Locale.ROOT).replace(' ', '_').replace('-', '_');
        try {
            return EntityType.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public EntityType defaultProjectile() {
        return defaultProjectile;
    }

    public int defaultCount() {
        return defaultCount;
    }

    public double height() {
        return height;
    }

    public double heightJitter() {
        return heightJitter;
    }

    public double radius() {
        return radius;
    }

    public double velocity() {
        return velocity;
    }

    public int perTick() {
        return perTick;
    }

    public int tntFuseExtra() {
        return tntFuseExtra;
    }

    public Material fallingBlockMaterial() {
        return fallingBlockMaterial;
    }

    public boolean disableAi() {
        return disableAi;
    }

    public int maxPerFire() {
        return maxPerFire;
    }

    public int unlimitedCap() {
        return unlimitedCap;
    }

    public int cleanupSeconds() {
        return cleanupSeconds;
    }

    public Set<EntityType> blockedTypes() {
        return blockedTypes;
    }

    public int cooldownSeconds() {
        return cooldownSeconds;
    }

    public boolean launchSound() {
        return launchSound;
    }

    public String rodName() {
        return rodName;
    }

    public boolean rodLore() {
        return rodLore;
    }

    public boolean mobSlowFalling() {
        return mobSlowFalling;
    }

    public int mobSlowFallingSeconds() {
        return mobSlowFallingSeconds;
    }

    public double mobFallSpeed() {
        return mobFallSpeed;
    }

    public EntityType projectileFor(SpawnMode mode) {
        return switch (mode) {
            case ORBITAL -> orbitalProjectile;
            case MEGAORBITAL -> megaorbitalProjectile;
            case STAB -> stabProjectile;
            case WITHER -> witherProjectile;
            default -> defaultProjectile;
        };
    }

    public double modeHeight(SpawnMode mode) {
        return switch (mode) {
            case ORBITAL -> orbitalHeight;
            case MEGAORBITAL -> megaorbitalHeight;
            case STAB -> stabHeight;
            case WITHER -> witherHeight;
            default -> height;
        };
    }

    public double modeJitter(SpawnMode mode) {
        return switch (mode) {
            case ORBITAL -> orbitalJitter;
            case MEGAORBITAL -> megaorbitalJitter;
            case STAB -> stabJitter;
            case WITHER -> witherJitter;
            default -> heightJitter;
        };
    }

    public double modeRadius(SpawnMode mode) {
        return switch (mode) {
            case ORBITAL -> orbitalRadius;
            case MEGAORBITAL -> megaorbitalRadius;
            case STAB -> stabRadius;
            case WITHER -> witherRadius;
            default -> radius;
        };
    }

    public double modeVelocity(SpawnMode mode) {
        return switch (mode) {
            case ORBITAL -> orbitalVelocity;
            case MEGAORBITAL -> megaorbitalVelocity;
            case STAB -> stabVelocity;
            case WITHER -> witherVelocity;
            default -> velocity;
        };
    }

    public int modeFuseExtra(SpawnMode mode) {
        return switch (mode) {
            case ORBITAL -> orbitalFuseExtra;
            case MEGAORBITAL -> megaorbitalFuseExtra;
            case STAB -> 0;
            case WITHER -> 0;
            default -> tntFuseExtra;
        };
    }

    public double stabDepth() {
        return stabDepth;
    }

    public double stabSpeed() {
        return stabSpeed;
    }

    public float stabYield() {
        return stabYield;
    }
}
