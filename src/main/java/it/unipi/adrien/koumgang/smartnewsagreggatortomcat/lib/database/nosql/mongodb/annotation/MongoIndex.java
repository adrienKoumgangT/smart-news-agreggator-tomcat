package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@Repeatable(MongoIndexes.class)
public @interface MongoIndex {

    /**
     * For class-level (compound) indexes, list fields as storage names.
     * You can include direction as ":1" or ":-1", e.g. {"is_active:1", "created_at:-1"}.
     * For field-level indexes, leave this empty.
     */
    String[] fields() default {};
    boolean unique() default false;
    boolean sparse() default false;

}
