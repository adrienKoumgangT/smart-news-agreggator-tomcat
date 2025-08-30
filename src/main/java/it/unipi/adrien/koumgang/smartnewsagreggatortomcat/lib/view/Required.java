package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Required {
    // Optionally allow custom message or code
    String message() default "Field is required";
}
