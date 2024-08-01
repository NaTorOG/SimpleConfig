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
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.pino.simpleconfig.reader.ObjValue.toObjValue;

public class FieldUtils {



    public static void load(FileConfiguration configuration, Object config){
        Class<?> clazz = config.getClass();
        handleClassicFields(clazz, configuration, config);
        handleConfigSectionFields(clazz, configuration, config);
    }

    private static void handleClassicFields(Class<?> clazz, FileConfiguration configuration, Object config){
        Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Path.class))
                .forEach(field -> {
                    field.setAccessible(true);
                    String path = field.getAnnotation(Path.class).value();
                    try {
                        if (configuration.contains(path)) {
                            Object value = toObjValue(configuration, field, path);
                            if (value != null) field.set(config, value);
                        } else {
                            configuration.set(path, field.get(config));
                            processComments(field, path, configuration);
                        }
                    }catch (IllegalAccessException exception){
                        throw new RuntimeException("Error while handling @Path fields");
                    }
                });
    }
    private static void handleConfigSectionFields(Class<?> clazz, FileConfiguration configuration, Object config){
        Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ConfigSection.class))
                .forEach(field -> {
                    field.setAccessible(true);
                    ConfigSection section = field.getAnnotation(ConfigSection.class);
                    String sectionName = section.name();

                    if(configuration.getConfigurationSection(sectionName) != null){
                        try {
                            field.set(config, configuration.getConfigurationSection(sectionName));
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException("Error while handling @ConfigSection fields");
                        }
                    }else{
                        configuration.createSection(sectionName);
                        Arrays.stream(section.entries()).toList().forEach(entry -> {
                            Objects.requireNonNull(
                                    configuration.getConfigurationSection(section.name()))
                                    .set(entry.key(), entry.value());
                        });
                        processComments(field, sectionName, configuration);
                    }
                });
    }
    private static void processComments(Field field, String path, FileConfiguration configuration){
        if(field.isAnnotationPresent(Comment.class)) return;
        configuration.setComments(path, Arrays.asList(field.getAnnotation(Comment.class).value()));
    }
}
