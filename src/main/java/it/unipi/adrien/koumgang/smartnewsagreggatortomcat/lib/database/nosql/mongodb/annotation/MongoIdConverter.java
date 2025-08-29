package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation;


import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.utils.IdConverter;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.utils.StringIdConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MongoIdConverter {

    Class<? extends IdConverter<?>> converter() default StringIdConverter.class;

}
