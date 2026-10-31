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
}
