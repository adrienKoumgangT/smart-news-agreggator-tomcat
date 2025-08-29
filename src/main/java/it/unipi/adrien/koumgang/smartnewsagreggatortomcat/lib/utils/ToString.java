package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.utils;

import java.util.Arrays;

public final class ToString {
    private final StringBuilder result;
    private final int startingLength;

    private ToString(String className) {
        this.result = (new StringBuilder(className)).append("(");
        this.startingLength = this.result.length();
    }

    public static String create(String className) {
        return className + "()";
    }

    public static ToString builder(String className) {
        return new ToString(className);
    }

    public ToString add(String fieldName, Object field) {
        if(field != null) {
            String value;
            if(field.getClass().isArray()) {
                if(field instanceof byte[]) {
                    value = String.valueOf(field);
                } else {
                    value = Arrays.toString((Object[])field);
                }
            } else {
                value = String.valueOf(field);
            }

            this.result.append(fieldName).append("=").append(value).append(", ");
        }

        return this;
    }

    public String build() {
        if(this.result.length() > this.startingLength) {
            this.result.setLength(this.result.length() - 2);
        }

        return this.result.append(")").toString();
    }
}
