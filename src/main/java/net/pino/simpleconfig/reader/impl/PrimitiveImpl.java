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

    public static Object primitiveToObj(String value, Class<?> clazz){
        if(clazz == int.class){
            return Integer.parseInt(value);
        }else if(clazz == boolean.class){
            return Boolean.parseBoolean(value);
        }else if(clazz == long.class){
            return Long.parseLong(value);
        }
        return null;
    }

}
