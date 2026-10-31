package ru.orbital.cannon;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class CannonItems {

    private final CannonConfig config;

    private final NamespacedKey typeKey;
    private final NamespacedKey countKey;
    private final NamespacedKey targetKey;
    private final NamespacedKey modeKey;
    private final NamespacedKey argsKey;

    public CannonItems(OrbitalCannonPlugin plugin, CannonConfig config) {
        this.config = config;
        this.typeKey = new NamespacedKey(plugin, "rod_type");
        this.countKey = new NamespacedKey(plugin, "rod_count");
        this.targetKey = new NamespacedKey(plugin, "rod_target");
        this.modeKey = new NamespacedKey(plugin, "rod_mode");
        this.argsKey = new NamespacedKey(plugin, "rod_args");
    }

    public ItemStack createRod(SpawnMode mode, EntityType type, int count, UUID targetId, String targetName, String args) {
        ItemStack item = new ItemStack(Material.FISHING_ROD);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        if (!config.rodName().isBlank()) {
            meta.displayName(Component.text(config.rodName(), NamedTextColor.WHITE)
                    .decoration(TextDecoration.ITALIC, false));
        }

        if (config.rodLore()) {
            List<Component> lore = new ArrayList<>();
            if (mode != SpawnMode.NORMAL) {
                lore.add(Component.text("Режим: ", NamedTextColor.DARK_GRAY)
                        .append(Component.text(mode.name().toLowerCase(), NamedTextColor.WHITE))
                        .decoration(TextDecoration.ITALIC, false));
            }
            lore.add(Component.text("Снаряд: ", NamedTextColor.DARK_GRAY)
                    .append(Component.text(type.name().toLowerCase(), NamedTextColor.WHITE))
                    .decoration(TextDecoration.ITALIC, false));
            lore.add(Component.text("Количество: ", NamedTextColor.DARK_GRAY)
                    .append(Component.text(count == PlayerSettings.UNLIMITED ? "безлимит" : String.valueOf(count),
                            NamedTextColor.WHITE))
                    .decoration(TextDecoration.ITALIC, false));
            lore.add(Component.text("Цель: ", NamedTextColor.DARK_GRAY)
                    .append(Component.text(targetName == null ? "?" : targetName, NamedTextColor.WHITE))
                    .decoration(TextDecoration.ITALIC, false));
            meta.lore(lore);
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(typeKey, PersistentDataType.STRING, type.name());
        pdc.set(countKey, PersistentDataType.INTEGER, count);
        pdc.set(modeKey, PersistentDataType.STRING, mode.name());
        if (args != null && !args.isBlank()) {
            pdc.set(argsKey, PersistentDataType.STRING, args);
        }
        if (targetId != null) {
            pdc.set(targetKey, PersistentDataType.STRING, targetId.toString());
        }

        item.setItemMeta(meta);
        return item;
    }

    public boolean isCannonRod(ItemStack item) {
        if (item == null || item.getType() != Material.FISHING_ROD) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        return meta.getPersistentDataContainer().has(typeKey, PersistentDataType.STRING);
    }

    public EntityType readType(ItemStack item) {
        ItemMeta meta = item == null ? null : item.getItemMeta();
        if (meta == null) {
            return null;
        }
        String raw = meta.getPersistentDataContainer().get(typeKey, PersistentDataType.STRING);
        return CannonConfig.parseType(raw);
    }

    public Integer readCount(ItemStack item) {
        ItemMeta meta = item == null ? null : item.getItemMeta();
        if (meta == null) {
            return null;
        }
        return meta.getPersistentDataContainer().get(countKey, PersistentDataType.INTEGER);
    }

    public SpawnMode readMode(ItemStack item) {
        ItemMeta meta = item == null ? null : item.getItemMeta();
        if (meta == null) {
            return SpawnMode.NORMAL;
        }
        String raw = meta.getPersistentDataContainer().get(modeKey, PersistentDataType.STRING);
        return SpawnMode.parse(raw);
    }

    public String readArgs(ItemStack item) {
        ItemMeta meta = item == null ? null : item.getItemMeta();
        if (meta == null) {
            return null;
        }
        return meta.getPersistentDataContainer().get(argsKey, PersistentDataType.STRING);
    }

    public boolean hasMode(ItemStack item) {
        ItemMeta meta = item == null ? null : item.getItemMeta();
        if (meta == null) {
            return false;
        }
        return meta.getPersistentDataContainer().has(modeKey, PersistentDataType.STRING);
    }

    public UUID readTarget(ItemStack item) {
        ItemMeta meta = item == null ? null : item.getItemMeta();
        if (meta == null) {
            return null;
        }
        String raw = meta.getPersistentDataContainer().get(targetKey, PersistentDataType.STRING);
        if (raw == null) {
            return null;
        }
        try {
            return UUID.fromString(raw);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
