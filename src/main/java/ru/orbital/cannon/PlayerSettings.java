package ru.orbital.cannon;

import org.bukkit.entity.EntityType;

import java.util.UUID;

public final class PlayerSettings {

    public static final int UNLIMITED = -1;

    private EntityType projectile;
    private int count;
    private UUID target;
    private SpawnMode mode;

    public PlayerSettings(EntityType projectile, int count, UUID target, SpawnMode mode) {
        this.projectile = projectile;
        this.count = count;
        this.target = target;
        this.mode = mode;
    }

    public EntityType getProjectile() {
        return projectile;
    }

    public void setProjectile(EntityType projectile) {
        this.projectile = projectile;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public UUID getTarget() {
        return target;
    }

    public void setTarget(UUID target) {
        this.target = target;
    }

    public SpawnMode getMode() {
        return mode == null ? SpawnMode.NORMAL : mode;
    }

    public void setMode(SpawnMode mode) {
        this.mode = mode;
    }
}
