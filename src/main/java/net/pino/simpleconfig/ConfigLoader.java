package net.pino.simpleconfig;

import net.pino.simpleconfig.annotations.Config;
import net.pino.simpleconfig.annotations.ConfigFile;
import net.pino.simpleconfig.annotations.inside.Comment;
import net.pino.simpleconfig.annotations.inside.Path;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;

public class ConfigLoader {

    public static void loadConfig(Plugin plugin, Object config, File dataFolder) throws Exception{
        Class<?> clazz = config.getClass();

        if(!clazz.isAnnotationPresent(Config.class)) throw new IllegalArgumentException("The class must be annotated with @Config");

        if(!clazz.isAnnotationPresent(ConfigFile.class)) throw new IllegalArgumentException("The class must be annotated with @ConfigFile");

        String fileName = clazz.getAnnotation(ConfigFile.class).value();
        File configFile = new File(dataFolder, fileName);

        if(!configFile.exists()){
            plugin.saveResource(fileName, false);
        }

        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
        readFields(fileConfiguration, config);
        fileConfiguration.options().parseComments(true);
        fileConfiguration.save(configFile);
    }

    private static void readFields(FileConfiguration configuration, Object config) throws IllegalAccessException {
        Class<?> clazz = config.getClass();
        for(Field field : clazz.getDeclaredFields()){
            field.setAccessible(true);

            if(!field.isAnnotationPresent(Path.class)){
                throw new IllegalArgumentException(field.getName() + " must be annoteted with @Path");
            }
            String path = field.getAnnotation(Path.class).value();

            if(configuration.get(path) != null){
                Object value = configuration.getObject(path, field.getType());
                field.set(config, value);

            }else{
                configuration.set(path, field.get(config));
                if(field.isAnnotationPresent(Comment.class)){
                    configuration.setComments(path, Arrays.asList(field.getAnnotation(Comment.class).value()));
                }
            }
        }
    }
}
