package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigFile {
    String fileName() default "config/Configuration.properties"; // e.g., "Configuration.properties"
}
