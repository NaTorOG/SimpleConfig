package net.pino.simpleconfig;

import net.pino.simpleconfig.annotations.QuickConfiguration;
import net.pino.simpleconfig.utils.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

public abstract class QuickConfig {

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
     */
    public void registerQuickConfig(Plugin plugin, Field field){

        if(!field.isAnnotationPresent(QuickConfiguration.class)) throw new IllegalArgumentException("The field must be annotated with @QuickConfiguration!");

        String fileName = field.getAnnotation(QuickConfiguration.class).value();
        configFile = new File(plugin.getDataFolder(), fileName);

        if(!configFile.exists()){
            FileUtils.saveResource(fileName, plugin);
        }
        fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
    }

    /***
     * Save the in Memory configuration in the @File and Reload it
     */
    public void saveAndReload(){
        try{
            fileConfiguration.save(configFile);
            fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
        }catch (IOException exception){
            exception.printStackTrace();
        }

    }

    /***
     * Simply reload the in Memory configuration reading @File
     */
    public void reload(){
        fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
    }
}
