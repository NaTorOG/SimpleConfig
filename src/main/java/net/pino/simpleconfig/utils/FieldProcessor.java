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

public class FieldProcessor {


    public static void handleFields(FileConfiguration configuration, Object config){
        Class<?> clazz = config.getClass();
        Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Path.class) || field.isAnnotationPresent(ConfigSection.class))
                .forEach(field -> {
                    field.setAccessible(true);

                    if(field.isAnnotationPresent(Path.class)){
                        handleClassicField(field, configuration, config);
                    }
                    if(field.isAnnotationPresent(ConfigSection.class)){
                        handleConfigSectionField(field, configuration, config);
                    }
                });
    }

    private static void handleClassicField(Field field, FileConfiguration configuration, Object config){
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
        CommentsProcessor.processComments(field, path, configuration);
    }

    private static void handleConfigSectionField(Field field, FileConfiguration configuration, Object config){
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
                CommentsProcessor.processEntryComments(sectionName, configuration, entry);
            });
            try {
                field.set(config, configuration.getConfigurationSection(sectionName));
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error while handling @ConfigSection fields");
            }
        }
        CommentsProcessor.processComments(field, sectionName, configuration);
    }


    public static void writeFieldsToFile(FileConfiguration fileConfiguration, Object config){
        Class<?> clazz = config.getClass();
        Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Path.class) || field.isAnnotationPresent(ConfigSection.class))
                .forEach(field -> {
                    field.setAccessible(true);
                    if(field.isAnnotationPresent(Path.class)){
                                writeClassicField(field, fileConfiguration, config);
                    }
                    if(field.isAnnotationPresent(ConfigSection.class)){
                                writeConfigSectionField(field, fileConfiguration, config);
                    }
                });
    }

    private static void writeClassicField(Field field, FileConfiguration fileConfiguration, Object config){
        String path = field.getAnnotation(Path.class).value();
        try {
            fileConfiguration.set(path, field.get(config));
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error while writing @Path fields");
        }
        CommentsProcessor.processComments(field, path, fileConfiguration);
    }

    private static void writeConfigSectionField(Field field, FileConfiguration fileConfiguration, Object config){
        ConfigSection section = field.getAnnotation(ConfigSection.class);
        String sectionName = section.name();
        fileConfiguration.createSection(sectionName);
        try {
            ConfigurationSection oldSection = (ConfigurationSection) field.get(config);
            oldSection.getKeys(true).forEach(key -> {
                Object value = oldSection.get(key);
                fileConfiguration.set(sectionName+"."+key, value);
            });

            Arrays.stream(section.entries()).toList().forEach(entry -> {
                Objects.requireNonNull(
                                fileConfiguration.getConfigurationSection(section.name()))
                        .set(entry.key(), entry.value());
                CommentsProcessor.processEntryComments(sectionName, fileConfiguration, entry);
            });

        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error while writing @ConfigSection fields");
        }
    }
}
