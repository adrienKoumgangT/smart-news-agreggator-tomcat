package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.safe;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.SafeException;

public class InvalidParamException extends SafeException {

    public InvalidParamException() {
        super();
    }

    public InvalidParamException(String errorMessage) {
        super(errorMessage);
    }
}
