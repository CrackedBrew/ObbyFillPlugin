package io.github.crackedbrew.obbyfillplugin;

import java.util.logging.Logger;

import io.github.crackedbrew.obbyfillplugin.commands.ObbyFillCommand;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class ObbyFillPlugin extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;
    FileConfiguration config = getConfig();

    @Override
    public void onDisable() {
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    @Override
    public void onEnable() {
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        //configuration file
        config.addDefault("enabled", true);
        config.options().copyDefaults(true);
        saveConfig();

        config.addDefault("server_tag", "Default_Server_Tag");
        config.options().copyDefaults(true);
        saveConfig();

        config.addDefault("obsidian_stack_price", 1000);
        config.options().copyDefaults(true);
        saveConfig();

        //prompt messages configuration
        //transaction success message
        config.addDefault("transaction_success_message", "serverTag $$$ was spent on obsidian at $ps");
        config.options().copyDefaults(true);
        saveConfig();

        config.addDefault("not_enough_space_message", "serverTag You don't have any free space in your inventory");
        config.options().copyDefaults(true);
        saveConfig();

        config.addDefault("not_enough_money_message", "serverTag You don't have enough money to use this command");
        config.options().copyDefaults(true);
        saveConfig();


        //config check if the plugin is enabled or not, if not, then disable the plugin
        if(!config.getBoolean("enabled")) {

            getServer().getPluginManager().disablePlugin(this);

        }


        //commands
        this.getCommand("obbyfill").setExecutor(new ObbyFillCommand(econ, config));

    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

}
