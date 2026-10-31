package ru.orbital.cannon;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public final class RodListener implements Listener {

    private final CannonItems items;
    private final CannonService service;
    private final SettingsManager settings;

    public RodListener(CannonItems items, CannonService service, SettingsManager settings) {
        this.items = items;
        this.service = service;
        this.settings = settings;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (!items.isCannonRod(item)) {
            return;
        }

        EquipmentSlot hand = event.getHand();
        if (hand != null) {
            Player player = event.getPlayer();
            ItemStack handItem = hand == EquipmentSlot.OFF_HAND
                    ? player.getInventory().getItemInOffHand()
                    : player.getInventory().getItemInMainHand();
            if (!items.isCannonRod(handItem)) {
                return;
            }
            item = handItem;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();
        PlayerSettings current = settings.get(player);

        EntityType type = items.readType(item);
        Integer count = items.readCount(item);
        UUID targetId = items.readTarget(item);
        SpawnMode mode = items.readMode(item);
        String args = items.readArgs(item);

        if (type == null) {
            Msg.error(player, "Недоступно.");
            return;
        }
        if (count == null) {
            count = current.getCount();
        }
        if (targetId == null) {
            targetId = current.getTarget();
        }
        if (mode == SpawnMode.NORMAL && !items.hasMode(item)) {
            mode = current.getMode();
        }
        if (args == null) {
            args = current.getArgs();
        }

        Player target = targetId == null ? null : Bukkit.getPlayer(targetId);
        if (target == null) {
            Msg.error(player, "Цель не в сети.");
            return;
        }

        service.fire(player, target, type, count, mode, args);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if (items.isCannonRod(player.getInventory().getItemInMainHand())
                || items.isCannonRod(player.getInventory().getItemInOffHand())) {
            event.setCancelled(true);
        }
    }
}
