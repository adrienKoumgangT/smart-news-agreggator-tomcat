package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.safe;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.SafeException;

public class UnregisteredUserException extends SafeException {

    public UnregisteredUserException() {
        super();
    }

    public UnregisteredUserException(String errorMessage) {
        super(errorMessage);
    }

}
