package net.pino.simpleconfig.annotations.inside;

import it.unimi.dsi.fastutil.Pair;
import net.pino.simpleconfig.annotations.impl.ConfigEntry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigSection {
    String name();
    ConfigEntry[] entries();
}
