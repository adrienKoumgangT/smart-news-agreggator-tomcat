package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.safe;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.SafeException;

public class MissingRequiredFieldException extends SafeException {

    public MissingRequiredFieldException() {
        super();
    }

    public MissingRequiredFieldException(String errorMessage) {
        super(errorMessage);
    }

}
