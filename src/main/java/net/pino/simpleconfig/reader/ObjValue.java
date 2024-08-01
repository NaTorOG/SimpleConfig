package net.pino.simpleconfig.reader;

import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;
import java.util.Map;

import static net.pino.simpleconfig.reader.impl.MapImpl.getMapFromConfiguration;
import static net.pino.simpleconfig.reader.impl.PrimitiveImpl.primitiveToObj;

public class ObjValue {

    public static Object toObjValue(FileConfiguration configuration, Field field, String path){
        if(field.getType().isPrimitive()){
            return primitiveToObj(field, configuration, path);
        }else if (Map.class.isAssignableFrom(field.getType())){
            return getMapFromConfiguration(configuration, path, field);
        }else{
            return configuration.getObject(path, field.getType());
        }
    }
}
