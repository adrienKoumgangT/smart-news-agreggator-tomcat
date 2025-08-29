package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception;

public class SafeException extends Exception {

    public SafeException() {
        super();
    }

    public SafeException(String errorMessage) {
        super(errorMessage);
    }

}
