package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.safe;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.SafeException;

public class NotOwnerException extends SafeException {
    public NotOwnerException() {
        super();
    }

    public NotOwnerException(String errorMessage) {
        super(errorMessage);
    }
}
