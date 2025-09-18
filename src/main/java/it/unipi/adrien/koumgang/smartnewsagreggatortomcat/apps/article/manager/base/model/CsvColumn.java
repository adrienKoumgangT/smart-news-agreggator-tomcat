package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.manager.base.model;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CsvColumn {

    /** Header name. If empty, field name is used. */
    String name() default "";

    /** Ordering in CSV. Lower number comes first. Default = 1000. */
    int order() default 1000;

    /** Optional java.text.SimpleDateFormat pattern for Date fields. */
    String dateFormat() default "";

}
