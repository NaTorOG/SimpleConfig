package net.pino.simpleconfig.utils;

import net.pino.simpleconfig.annotations.impl.ConfigEntry;
import net.pino.simpleconfig.annotations.inside.Comment;
import net.pino.simpleconfig.annotations.inside.CommentInLine;
import net.pino.simpleconfig.annotations.inside.ConfigSection;
import net.pino.simpleconfig.annotations.inside.Path;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import static net.pino.simpleconfig.reader.ObjValue.toObjValue;

public class FieldUtils {



    public static void load(FileConfiguration configuration, Object config){
        Class<?> clazz = config.getClass();
        handleClassicFields(clazz, configuration, config);
        handleConfigSectionFields(clazz, configuration, config);
    }

    public static void writeFieldsToFile(FileConfiguration fileConfiguration, Object config){
        Class<?> clazz = config.getClass();
        Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Path.class))
                .filter(field -> !field.isAnnotationPresent(ConfigSection.class))
                .forEach(field -> {
                    field.setAccessible(true);
                    String path = field.getAnnotation(Path.class).value();
                    try {
                        fileConfiguration.set(path, field.get(config));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Error while writing @Path fields");
                    }
                    processComments(field, path, fileConfiguration);
                });
        Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ConfigSection.class))
                .forEach(field -> {
                    field.setAccessible(true);
                    ConfigSection section = field.getAnnotation(ConfigSection.class);
                    String sectionName = section.name();
                    fileConfiguration.createSection(sectionName);
                    try {
                        ConfigurationSection oldSection = (ConfigurationSection) field.get(config);
                        oldSection.getKeys(true).forEach(key -> {
                            Object value = oldSection.get(key);
                            fileConfiguration.set(key, value);
                        });

                        Arrays.stream(section.entries()).toList().forEach(entry -> {
                            Objects.requireNonNull(
                                            fileConfiguration.getConfigurationSection(section.name()))
                                    .set(entry.key(), entry.value());
                            processEntryComments(sectionName, fileConfiguration, entry);
                        });

                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Error while writing @ConfigSection fields");
                    }

                });
    }

    private static void handleClassicFields(Class<?> clazz, FileConfiguration configuration, Object config){
        Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Path.class))
                .filter(field -> !field.isAnnotationPresent(ConfigSection.class))
                .forEach(field -> {
                    field.setAccessible(true);
                    String path = field.getAnnotation(Path.class).value();
                    try {
                        if (configuration.contains(path)) {
                            Object value = toObjValue(configuration, field, path);
                            if (value != null) field.set(config, value);
                        } else {
                            configuration.set(path, field.get(config));
                        }
                    }catch (IllegalAccessException exception){
                        throw new RuntimeException("Error while handling @Path fields");
                    }
                    processComments(field, path, configuration);
                });
    }
    private static void handleConfigSectionFields(Class<?> clazz, FileConfiguration configuration, Object config){
        Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ConfigSection.class))
                .forEach(field -> {
                    field.setAccessible(true);
                    ConfigSection section = field.getAnnotation(ConfigSection.class);
                    String sectionName = section.name();

                    if(configuration.contains(sectionName)){
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
                            processEntryComments(sectionName, configuration, entry);
                        });
                        try {
                            field.set(config, configuration.getConfigurationSection(sectionName));
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException("Error while handling @ConfigSection fields");
                        }
                    }
                    processComments(field, sectionName, configuration);
                });
    }
    private static void processComments(Field field, String path, FileConfiguration configuration){
        if(field.isAnnotationPresent(Comment.class)) {
            configuration.setComments(path, Arrays.asList(field.getAnnotation(Comment.class).value()));
        }
        if(field.isAnnotationPresent(CommentInLine.class)){
            configuration.setInlineComments(path, Arrays.asList(field.getAnnotation(CommentInLine.class).value()));
        }
    }
    private static void processEntryComments(String path, FileConfiguration configuration, ConfigEntry entry){
        if(entry.comment().isEmpty()) return;
        configuration.setInlineComments(path+"."+entry.key(),
                Collections.singletonList(entry.comment()));
    }

}
