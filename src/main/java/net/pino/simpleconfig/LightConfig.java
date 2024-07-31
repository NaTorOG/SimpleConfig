package net.pino.simpleconfig;

import net.pino.simpleconfig.annotations.Config;
import net.pino.simpleconfig.annotations.ConfigFile;
import net.pino.simpleconfig.utils.ResourceSaver;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public abstract class LightConfig {

    /***
     * Represents the Physical File
     */
    protected File configFile;

    /***
     * Represents the in Memory Configuration
     */
    public FileConfiguration fileConfiguration;

    /***
     * Register a basic Config that does not auto update and has no fields
     * @param plugin Your Plugin Instance
     * @param config Your Class extending LightConfig
     */
    public void registerLightConfig(Plugin plugin, Object config) throws Exception{
        Class<?> clazz = config.getClass();

        if(BaseConfig.class.isAssignableFrom(clazz)) throw new IllegalArgumentException("The class must extends only one type!");

        if(!LightConfig.class.isAssignableFrom(clazz)) throw new IllegalArgumentException("The class must extends LightConfig!");

        if(!clazz.isAnnotationPresent(Config.class)) throw new IllegalArgumentException("The class must be annotated with @Config!");

        if(!clazz.isAnnotationPresent(ConfigFile.class)) throw new IllegalArgumentException("The class must be annotated with @ConfigFile!");

        String fileName = clazz.getAnnotation(ConfigFile.class).value();
        configFile = new File(plugin.getDataFolder(), fileName);

        if(!configFile.exists()){
            ResourceSaver.saveResource(fileName, plugin);
        }

        fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
    }

    /***
     * Save the in Memory configuration in the @File and Reload it
     * @throws IOException IO exception that may occur
     */
    public void saveAndReload() throws IOException {
        fileConfiguration.save(configFile);
        fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
    }

    /***
     * Simply reload the in Memory configuration reading @File
     */
    public void reload(){
        fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
    }
}
