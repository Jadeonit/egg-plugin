package ru.orbital.cannon;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class OrbitalCannonPlugin extends JavaPlugin {

    private CannonConfig cannonConfig;
    private SettingsManager settings;
    private CannonService service;
    private CannonItems items;
    private BukkitTask cleanupTask;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Msg.init(getConfig().getString("messages.prefix", ""));

        this.cannonConfig = new CannonConfig();
        this.cannonConfig.reload(getConfig());

        this.settings = new SettingsManager(cannonConfig);
        this.service = new CannonService(this, cannonConfig);
        this.items = new CannonItems(this, cannonConfig);

        EggCommand command = new EggCommand(this, cannonConfig, settings, service, items);
        PluginCommand pluginCommand = getCommand("egg");
        if (pluginCommand != null) {
            pluginCommand.setExecutor(command);
            pluginCommand.setTabCompleter(command);
        } else {
            getLogger().severe("Command 'egg' is missing in plugin.yml!");
        }

        getServer().getPluginManager().registerEvents(new RodListener(items, service, settings), this);
        getServer().getPluginManager().registerEvents(new FallDamageListener(), this);

        this.cleanupTask = getServer().getScheduler().runTaskTimer(this, service::cleanupExpired, 100L, 100L);

        getLogger().info("Egg enabled.");
    }

    @Override
    public void onDisable() {
        if (cleanupTask != null) {
            cleanupTask.cancel();
            cleanupTask = null;
        }
        if (service != null) {
            service.removeAll();
        }
        getLogger().info("Egg disabled.");
    }

    public void reloadAll() {
        reloadConfig();
        cannonConfig.reload(getConfig());
        Msg.init(getConfig().getString("messages.prefix", ""));
    }

    public CannonConfig cannonConfig() {
        return cannonConfig;
    }
}
