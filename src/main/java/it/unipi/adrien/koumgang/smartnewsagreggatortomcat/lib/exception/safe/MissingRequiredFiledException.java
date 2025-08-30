package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.safe;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.SafeException;

public class MissingRequiredFiledException  extends SafeException {

    public MissingRequiredFiledException() {
        super();
    }

    public MissingRequiredFiledException(String errorMessage) {
        super(errorMessage);
    }

}
