package net.pino.simpleconfig.impl;

import net.pino.simpleconfig.utils.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public interface QuickConfig {

    File getConfigFile();
    void setConfigFile(File configFile);

    FileConfiguration getFileConfiguration();
    void setFileConfiguration(FileConfiguration fileConfiguration);

    /***
     * Register a basic Config that does not auto update and has no fields
     * @param plugin Your Plugin Instance
     */
    default void registerQuickConfig(Plugin plugin, String fileName){
        File configFile = new File(plugin.getDataFolder(), fileName);
        setConfigFile(configFile);

        if(!configFile.exists()){
            FileUtils.saveResource(fileName, plugin);
        }
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
        setFileConfiguration(fileConfiguration);
    }

    /***
     * Save the in Memory configuration in the @File and Reload it
     */
    default void saveAndReload(){
        try{
            getFileConfiguration().save(getConfigFile());
            setFileConfiguration(YamlConfiguration.loadConfiguration(getConfigFile()));
        }catch (IOException exception){
            exception.printStackTrace();
        }

    }

    /***
     * Simply reload the in Memory configuration reading @File
     */
    default void reload(){
        setFileConfiguration(YamlConfiguration.loadConfiguration(getConfigFile()));
    }

}
