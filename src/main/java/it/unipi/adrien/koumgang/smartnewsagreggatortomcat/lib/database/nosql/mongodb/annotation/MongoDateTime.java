package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MongoDateTime {

    boolean utc() default true;
    String pattern() default "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

}
