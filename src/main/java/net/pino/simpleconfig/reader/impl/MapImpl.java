package net.pino.simpleconfig.reader.impl;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MapImpl {

    public static Map<?, ?> getMapFromConfiguration(FileConfiguration configuration, String path, Field field) {
        Map<Object, Object> resultMap = new HashMap<>();
        ConfigurationSection section = configuration.getConfigurationSection(path);
        if (section != null) {
            for (String key : section.getKeys(false)) {
                Object value = section.get(key);
                resultMap.put(key, value);
            }
        }
        return resultMap;
    }
}
