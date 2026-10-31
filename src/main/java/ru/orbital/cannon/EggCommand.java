package ru.orbital.cannon;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class EggCommand implements TabExecutor {

    private static final int MODE_UNKNOWN = 0;
    private static final int MODE_ROD = 1;
    private static final int MODE_FIRE = 2;

    private final OrbitalCannonPlugin plugin;
    private final CannonConfig config;
    private final SettingsManager settings;
    private final CannonService service;
    private final CannonItems items;

    public EggCommand(OrbitalCannonPlugin plugin, CannonConfig config,
                      SettingsManager settings, CannonService service, CannonItems items) {
        this.plugin = plugin;
        this.config = config;
        this.settings = settings;
        this.service = service;
        this.items = items;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload") && canReload(sender)) {
            plugin.reloadAll();
            Msg.ok(sender, "Готово.");
            return true;
        }

        if (!(sender instanceof Player player)) {
            usage(sender, label);
            return true;
        }

        if (args.length != 4) {
            usage(player, label);
            return true;
        }

        ParsedProjectile parsed = parseProjectile(args[0], config);
        if (parsed == null) {
            Msg.error(player, "Недоступно.");
            return true;
        }
        EntityType type = parsed.type();
        SpawnMode spawnMode = parsed.mode();
        String entityArgs = parsed.args();
        if (!EntityTypes.isAllowed(type, config)) {
            Msg.error(player, "Недоступно.");
            return true;
        }

        Integer count = parseCount(args[1]);
        if (count == null) {
            Msg.error(player, "Недоступно.");
            return true;
        }
        if (count == PlayerSettings.UNLIMITED && !player.hasPermission("egg.unlimited")) {
            Msg.error(player, "Недоступно.");
            return true;
        }

        Player target = resolveTarget(player, args[2]);
        if (target == null) {
            Msg.error(player, "Цель не в сети.");
            return true;
        }

        int mode = parseMode(args[3]);
        if (mode == MODE_UNKNOWN) {
            usage(player, label);
            return true;
        }

        PlayerSettings current = settings.get(player);
        current.setProjectile(type);
        current.setCount(count);
        current.setTarget(target.getUniqueId());
        current.setMode(spawnMode);
        current.setArgs(entityArgs);

        if (mode == MODE_ROD) {
            if (!player.hasPermission("egg.rod")) {
                Msg.error(player, "Недоступно.");
                return true;
            }
            ItemStack rod = items.createRod(spawnMode, type, count, target.getUniqueId(), target.getName(), entityArgs);
            var leftovers = player.getInventory().addItem(rod);
            for (ItemStack left : leftovers.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), left);
            }
            Msg.ok(player, "Готово.");
            return true;
        }

        service.fire(player, target, type, count, spawnMode, entityArgs);
        return true;
    }

    private static boolean canReload(CommandSender sender) {
        return sender instanceof Player player
                ? player.hasPermission("egg.admin")
                : sender.hasPermission("egg.admin");
    }

    private static Player resolveTarget(Player player, String raw) {
        String value = raw.toLowerCase(Locale.ROOT);
        if (value.equals("me") || value.equals("self") || value.equals("я") || value.equals("себя")) {
            return player;
        }
        Player exact = Bukkit.getPlayerExact(raw);
        return exact != null ? exact : Bukkit.getPlayer(raw);
    }

    private static int parseMode(String raw) {
        return switch (raw.toLowerCase(Locale.ROOT)) {
            case "rod", "fishing", "удочка", "уд", "палка" -> MODE_ROD;
            case "fire", "command", "cmd", "запуск", "команда", "сразу" -> MODE_FIRE;
            default -> MODE_UNKNOWN;
        };
    }

    private static Integer parseCount(String raw) {
        String value = raw.trim().toLowerCase(Locale.ROOT);
        if (value.equals("unlimited") || value.equals("inf") || value.equals("infinity")
                || value.equals("all") || value.equals("∞") || value.equals("*") || value.equals("-1")) {
            return PlayerSettings.UNLIMITED;
        }
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static void usage(CommandSender sender, String label) {
        Msg.info(sender, "Использование: /" + label
                + " <снаряд|orbital|stab> <кол-во> <цель> <fire|rod>");
    }

    private static ParsedProjectile parseProjectile(String raw, CannonConfig config) {
        String value = raw == null ? "" : raw.trim();
        if (value.isEmpty()) {
            return null;
        }

        String args = null;
        String withoutArgs = value;
        Matcher matcher = Pattern.compile("\\{(.*)\\}$", Pattern.DOTALL).matcher(value);
        if (matcher.find()) {
            args = matcher.group(1).trim();
            withoutArgs = value.substring(0, matcher.start()).trim();
        }

        String lowerNoArgs = withoutArgs.toLowerCase(Locale.ROOT);

        SpawnMode mode = SpawnMode.NORMAL;
        String typeToken = withoutArgs;

        int split = lowerNoArgs.indexOf(':');
        if (split > 0) {
            SpawnMode prefixed = SpawnMode.parse(lowerNoArgs.substring(0, split));
            if (prefixed != SpawnMode.NORMAL) {
                mode = prefixed;
                typeToken = withoutArgs.substring(split + 1).trim();
            }
        } else {
            SpawnMode bare = SpawnMode.parse(lowerNoArgs);
            if (bare != SpawnMode.NORMAL) {
                mode = bare;
                typeToken = "";
            }
        }

        EntityType type;
        if (typeToken.isEmpty()) {
            type = config.projectileFor(mode);
        } else {
            type = CannonConfig.parseType(typeToken);
            if (type == null) {
                return null;
            }
        }
        if (type == null) {
            return null;
        }
        if (!EntityTypes.isAllowed(type, config)) {
            return null;
        }
        return new ParsedProjectile(mode, type, args);
    }

    private record ParsedProjectile(SpawnMode mode, EntityType type, String args) {
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
