package net.pino.simpleconfig.utils;

import net.pino.simpleconfig.annotations.impl.ConfigEntry;
import net.pino.simpleconfig.annotations.inside.Comment;
import net.pino.simpleconfig.annotations.inside.ConfigSection;
import net.pino.simpleconfig.annotations.inside.Path;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.pino.simpleconfig.reader.ObjValue.toObjValue;

public class FieldUtils {

    public static void loadFields(FileConfiguration configuration, Object config) throws IllegalAccessException {
        Class<?> clazz = config.getClass();
        for(Field field : clazz.getDeclaredFields()){
            field.setAccessible(true);
            if(field.isAnnotationPresent(Path.class)) {
                String path = field.getAnnotation(Path.class).value();
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
            }else if(field.isAnnotationPresent(ConfigSection.class)){
                ConfigSection section = field.getAnnotation(ConfigSection.class);
                configuration.createSection(section.name());
                if (field.isAnnotationPresent(Comment.class)) {
                    configuration.setComments(section.name(), Arrays.asList(field.getAnnotation(Comment.class).value()));
                }
                for(ConfigEntry entry : section.entries()){
                    configuration.getConfigurationSection(section.name()).set(entry.key(), entry.value());
                }
                field.set(config, configuration.getConfigurationSection(section.name()));
            }
        }
    }

    public static void reloadFields(FileConfiguration configuration, Object config){
        Class<?> clazz = config.getClass();
        Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Path.class))
                .forEach(field -> {
                    field.setAccessible(true);
                    String path = field.getAnnotation(Path.class).value();
                    try {
                        if (configuration.contains(path)) {
                            Object value = toObjValue(configuration, field, path);
                            if (value != null) {
                                field.set(config, value);
                            }
                        }
                    }catch (IllegalAccessException exception){
                        throw new RuntimeException("Error while reloading Fields!");
                    }
                });

        Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ConfigSection.class))
                .forEach(field -> {
                    field.setAccessible(true);
                    ConfigSection section = field.getAnnotation(ConfigSection.class);
                    ConfigurationSection configSection = configuration.getConfigurationSection(section.name());
                    if(configSection != null){
                        try {
                            field.set(config, configSection);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException("Error while reloading ConfigSection Fields!");
                        }
                    }
                });
        ;
    }
}
