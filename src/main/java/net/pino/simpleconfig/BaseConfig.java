package net.pino.simpleconfig;

import net.pino.simpleconfig.annotations.Config;
import net.pino.simpleconfig.annotations.ConfigFile;
import net.pino.simpleconfig.utils.FieldsReader;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public abstract class BaseConfig {

    /***
     * Represents the Physical File
     */
    protected File configFile;

    /***
     * Represents the in Memory Configuration
     */
    protected FileConfiguration fileConfiguration;

    /***
     * Register and update your configuration and will also add non-existing values into @File
     * @param plugin Your Plugin Instance
     */
    public void registerConfig(Plugin plugin){
        Class<?> clazz = this.getClass();

        if(LightConfig.class.isAssignableFrom(clazz)) throw new IllegalArgumentException("The class must extends only one type!");

        if(!BaseConfig.class.isAssignableFrom(clazz)) throw new IllegalArgumentException("The class must extends BaseConfig!");

        if(!clazz.isAnnotationPresent(Config.class)) throw new IllegalArgumentException("The class must be annotated with @Config!");

        if(!clazz.isAnnotationPresent(ConfigFile.class)) throw new IllegalArgumentException("The class must be annotated with @ConfigFile!");

        String fileName = clazz.getAnnotation(ConfigFile.class).value();
        configFile = new File(plugin.getDataFolder(), fileName);
        fileConfiguration = YamlConfiguration.loadConfiguration(configFile);

        try{
            FieldsReader.readFields(fileConfiguration, this);
            fileConfiguration.options().parseComments(true);
            fileConfiguration.save(configFile);
        }catch (IllegalAccessException | IOException exception){
            exception.printStackTrace();
        }
    }

    /***
     * Save the in Memory configuration in the @File and Reload it
     * @param plugin Your Plugin Instance
     */
    public void saveAndReload(Plugin plugin){
        registerConfig(plugin);
        fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
    }

    /***
     * Simply reload the in Memory configuration reading @File
     */
    public void reload(){
        fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
        try{
            FieldsReader.readFields(fileConfiguration, this);
        }catch (IllegalAccessException exception){
            exception.printStackTrace();
        }
    }

}
