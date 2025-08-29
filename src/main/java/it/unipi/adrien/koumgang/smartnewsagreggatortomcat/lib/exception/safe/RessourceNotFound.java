package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.safe;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.SafeException;

public class RessourceNotFound extends SafeException {
    public RessourceNotFound() {
        super();
    }

    public RessourceNotFound(String errorMessage) {
        super(errorMessage);
    }
}
