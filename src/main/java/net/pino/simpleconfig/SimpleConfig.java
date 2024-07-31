package net.pino.simpleconfig;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleConfig extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        FileConfiguration test = YamlConfiguration.loadConfiguration(null);
        test.set
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
