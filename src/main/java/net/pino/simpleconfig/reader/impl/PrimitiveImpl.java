package net.pino.simpleconfig.reader.impl;

import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;

public class PrimitiveImpl {

    public static Object primitiveToObj(Field field, FileConfiguration config, String path){
        if(field.getType() == int.class){
            return config.getInt(path);
        }else if(field.getType() == boolean.class){
            return config.getBoolean(path);
        }else if(field.getType() == long.class){
            return config.getLong(path);
        }
        return null;
    }

}
