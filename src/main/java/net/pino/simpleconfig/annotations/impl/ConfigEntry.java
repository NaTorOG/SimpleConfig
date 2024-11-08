package net.pino.simpleconfig.annotations.impl;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigEntry {
    String key();
    String value();
    String comment() default "";
    Class<?> clazz();
}
