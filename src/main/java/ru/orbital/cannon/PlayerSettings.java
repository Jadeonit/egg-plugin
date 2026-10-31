package ru.orbital.cannon;

import org.bukkit.entity.EntityType;

import java.util.UUID;

public final class PlayerSettings {

    public static final int UNLIMITED = -1;

    private EntityType projectile;
    private int count;
    private UUID target;
    private SpawnMode mode;
    private String args;

    public PlayerSettings(EntityType projectile, int count, UUID target, SpawnMode mode, String args) {
        this.projectile = projectile;
        this.count = count;
        this.target = target;
        this.mode = mode;
        this.args = args;
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

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }
}
