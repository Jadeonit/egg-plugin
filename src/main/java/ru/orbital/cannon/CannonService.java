package ru.orbital.cannon;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class CannonService {

    public static final String TAG = "orbital_cannon";

    private static final int HARD_LIMIT = 100_000;

    private final JavaPlugin plugin;
    private final CannonConfig config;

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Map<UUID, Long> spawnedUntil = new HashMap<>();

    public CannonService(JavaPlugin plugin, CannonConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    public boolean fire(Player shooter, Player target, EntityType type, int rawCount) {
        if (shooter == null) {
            return false;
        }
        if (target == null || !target.isOnline()) {
            Msg.error(shooter, "Цель не выбрана или не в сети. /egg <снаряд> <кол-во> <цель> fire");
            return false;
        }
        if (!EntityTypes.isAllowed(type, config)) {
            Msg.error(shooter, "Недоступно.");
            return false;
        }

        long now = System.currentTimeMillis();
        Long until = cooldowns.get(shooter.getUniqueId());
        if (until != null && until > now && !shooter.hasPermission("egg.bypasscooldown")) {
            long left = Math.max(1, (until - now + 999) / 1000);
            Msg.warn(shooter, "Кулдаун: ещё " + left + " с.");
            return false;
        }

        if (rawCount == PlayerSettings.UNLIMITED && !shooter.hasPermission("egg.unlimited")) {
            Msg.error(shooter, "Недоступно.");
            return false;
        }

        int total = resolveCount(shooter, rawCount);
        if (total <= 0) {
            Msg.error(shooter, "Количество должно быть больше нуля.");
            return false;
        }

        cooldowns.put(shooter.getUniqueId(), now + config.cooldownSeconds() * 1000L);

        Location center = target.getLocation().clone();
        World world = center.getWorld();
        if (world == null) {
            Msg.error(shooter, "Мир цели недоступен.");
            return false;
        }

        new SalvoTask(world, center, type, total).runTaskTimer(plugin, 0L, 1L);

        Msg.ok(shooter, "Залп: " + total + " × " + type.name().toLowerCase() + " по " + target.getName());
        if (config.launchSound()) {
            world.playSound(center, "entity.firework_rocket.launch", 1.0f, 0.6f);
        }
        return true;
    }

    private int resolveCount(Player shooter, int rawCount) {
        int requested;
        if (rawCount == PlayerSettings.UNLIMITED) {
            if (config.unlimitedCap() > 0) {
                requested = config.unlimitedCap();
            } else if (config.maxPerFire() > 0) {
                requested = config.maxPerFire();
            } else {
                requested = HARD_LIMIT;
            }
        } else {
            requested = Math.max(1, rawCount);
        }

        if (config.maxPerFire() > 0 && requested > config.maxPerFire()
                && !shooter.hasPermission("egg.bypasscap")) {
            Msg.warn(shooter, "Количество уменьшено до " + config.maxPerFire()
                    + " (safety.max-entities-per-fire).");
            requested = config.maxPerFire();
        }
        if (requested > HARD_LIMIT) {
            Msg.warn(shooter, "Слишком много сущностей, уменьшено до " + HARD_LIMIT + ".");
            requested = HARD_LIMIT;
        }
        return requested;
    }

    public void cleanupExpired() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<UUID, Long>> it = spawnedUntil.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Long> entry = it.next();
            if (entry.getValue() > now) {
                continue;
            }
            Entity entity = Bukkit.getEntity(entry.getKey());
            if (entity != null && entity.isValid()) {
                entity.remove();
            }
            it.remove();
        }
    }

    public void removeAll() {
        for (UUID uuid : spawnedUntil.keySet()) {
            Entity entity = Bukkit.getEntity(uuid);
            if (entity != null && entity.isValid()) {
                entity.remove();
            }
        }
        spawnedUntil.clear();
    }

    private final class SalvoTask extends BukkitRunnable {

        private final World world;
        private final Location center;
        private final EntityType type;
        private final int total;
        private final ThreadLocalRandom random = ThreadLocalRandom.current();

        private int spawned;

        private SalvoTask(World world, Location center, EntityType type, int total) {
            this.world = world;
            this.center = center;
            this.type = type;
            this.total = total;
        }

        @Override
        public void run() {
            if (!plugin.isEnabled()) {
                cancel();
                return;
            }
            int batch = Math.min(config.perTick(), total - spawned);
            for (int i = 0; i < batch; i++) {
                spawnOne();
            }
            if (spawned >= total) {
                cancel();
            }
        }

        private void spawnOne() {
            double angle = random.nextDouble() * Math.PI * 2.0;
            double distance = Math.sqrt(random.nextDouble()) * config.radius();
            double x = center.getX() + Math.cos(angle) * distance;
            double z = center.getZ() + Math.sin(angle) * distance;
            double y = center.getY() + config.height() + random.nextDouble() * config.heightJitter();

            Location location = new Location(world, x, y, z);
            Entity entity;
            try {
                entity = spawnEntity(location);
            } catch (Throwable throwable) {
                plugin.getLogger().warning("Не удалось заспавнить " + type.name() + ": " + throwable.getMessage());
                spawned++;
                return;
            }

            if (entity == null) {
                spawned++;
                return;
            }

            if (entity instanceof TNTPrimed tnt) {
                double fallDistance = Math.max(1.0, y - center.getY());
                double speed = Math.abs(config.velocity());
                int fallTicks = speed > 0.05 ? (int) Math.ceil(fallDistance / speed) : 100;
                tnt.setFuseTicks(Math.max(10, fallTicks + config.tntFuseExtra()));
            }

            if (config.disableAi() && entity instanceof Mob mob) {
                mob.setAI(false);
            }

            entity.setPersistent(false);
            entity.addScoreboardTag(TAG);
            entity.setVelocity(new Vector(0.0, config.velocity(), 0.0));

            if (config.cleanupSeconds() > 0) {
                spawnedUntil.put(entity.getUniqueId(), System.currentTimeMillis() + config.cleanupSeconds() * 1000L);
            }
            spawned++;
        }

        private Entity spawnEntity(Location location) {
            if (type == EntityType.FALLING_BLOCK) {
                var blockData = config.fallingBlockMaterial().createBlockData();
                FallingBlock falling = world.spawn(location, FallingBlock.class,
                        block -> block.setBlockData(blockData));
                falling.setDropItem(false);
                falling.setHurtEntities(false);
                return falling;
            }
            return world.spawnEntity(location, type);
        }
    }
}
