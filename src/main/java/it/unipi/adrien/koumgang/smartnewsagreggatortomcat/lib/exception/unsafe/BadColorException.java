package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.unsafe;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.UnsafeException;

public class BadColorException extends UnsafeException {
    public BadColorException() {
        super();
    }

    public BadColorException(String errorMessage) {
        super(errorMessage);
    }

}
