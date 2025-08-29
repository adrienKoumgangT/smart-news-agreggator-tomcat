package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.safe;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.SafeException;

public class InvalidAuthentificationException extends SafeException {

    public InvalidAuthentificationException() {
        super();
    }

    public InvalidAuthentificationException(String errorMessage) {
        super(errorMessage);
    }

}
