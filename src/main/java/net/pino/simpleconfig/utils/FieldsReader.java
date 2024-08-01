package net.pino.simpleconfig.utils;

import net.pino.simpleconfig.annotations.impl.ConfigEntry;
import net.pino.simpleconfig.annotations.inside.Comment;
import net.pino.simpleconfig.annotations.inside.ConfigSection;
import net.pino.simpleconfig.annotations.inside.Path;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;
import java.util.Arrays;

import static net.pino.simpleconfig.reader.ObjValue.toObjValue;

public class FieldsReader {

    public static void readFields(FileConfiguration configuration, Object config) throws IllegalAccessException {
        Class<?> clazz = config.getClass();
        for(Field field : clazz.getDeclaredFields()){
            field.setAccessible(true);
            if(field.isAnnotationPresent(Path.class)) {
                String path = field.getAnnotation(Path.class).value();
                if(!field.isAnnotationPresent(ConfigSection.class)) {
                    if (configuration.contains(path)) {
                        Object value = toObjValue(configuration, field, path);
                        if (value != null) {
                            field.set(config, value);
                        }

                    } else {
                        configuration.set(path, field.get(config));
                        if (field.isAnnotationPresent(Comment.class)) {
                            configuration.setComments(path, Arrays.asList(field.getAnnotation(Comment.class).value()));
                        }
                    }

                }else{
                    if (field.isAnnotationPresent(Comment.class)) {
                        configuration.setComments(path, Arrays.asList(field.getAnnotation(Comment.class).value()));
                    }
                    ConfigSection section = field.getAnnotation(ConfigSection.class);
                    configuration.createSection(section.name());
                    for(ConfigEntry entry : section.entries()){
                        configuration.getConfigurationSection(section.name()).set(entry.key(), entry.value());
                    }
                }

            }
        }
    }
}
