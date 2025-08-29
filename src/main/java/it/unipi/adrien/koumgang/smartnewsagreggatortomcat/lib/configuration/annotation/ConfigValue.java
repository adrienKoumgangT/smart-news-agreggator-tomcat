package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigValue {
    String key();
    String defaultValue() default "";
}
