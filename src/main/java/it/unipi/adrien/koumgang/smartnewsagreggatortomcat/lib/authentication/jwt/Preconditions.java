package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.jwt;


public class Preconditions {

    public static void checkState(boolean expression, String errorMessage) {
        if (!expression) {
            throw new IllegalStateException(errorMessage);
        }
    }

    public static void checkArgument(boolean expression, String errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void checkValveInit(boolean expression, String errorMessage) {
        if (!expression) {
            throw new RuntimeException(errorMessage);
        }
    }
}