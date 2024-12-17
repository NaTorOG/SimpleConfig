package net.pino.simpleconfig.annotations.impl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigEntry {
    String key();
    String value() default "";
    String[] values() default {};
    String comment() default "";
    Class<?> clazz();
    boolean persist() default false;
}
