package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.safe;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.SafeException;

public class InvalidTokenException extends SafeException {

    public InvalidTokenException() {
        super();
    }

    public InvalidTokenException(String errorMessage) {
        super(errorMessage);
    }

}
