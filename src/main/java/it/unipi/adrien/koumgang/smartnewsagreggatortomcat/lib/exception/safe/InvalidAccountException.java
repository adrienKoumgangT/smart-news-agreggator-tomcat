package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.safe;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.SafeException;

public class InvalidAccountException extends SafeException {

    public InvalidAccountException() {
        super();
    }

    public InvalidAccountException(String errorMessage) {
        super(errorMessage);
    }

}
