package ru.orbital.cannon;

import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class EntityTypes {

    private EntityTypes() {
    }

    public static boolean isAllowed(EntityType type) {
        return isAllowed(type, null);
    }

    public static boolean isAllowed(EntityType type, CannonConfig config) {
        if (type == null || type == EntityType.UNKNOWN) {
            return false;
        }
        if (type == EntityType.PLAYER || type == EntityType.ITEM || type == EntityType.EXPERIENCE_ORB) {
            return false;
        }
        if (config != null && config.blockedTypes().contains(type)) {
            return false;
        }
        if (type == EntityType.TNT || type == EntityType.FALLING_BLOCK) {
            return true;
        }
        try {
            return type.isSpawnable();
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static List<String> suggestions() {
        List<String> names = new ArrayList<>();
        for (EntityType type : EntityType.values()) {
            if (isAllowed(type)) {
                names.add(type.name().toLowerCase());
            }
        }
        names.sort(Comparator.naturalOrder());
        return names;
    }
}
