package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.unsafe;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.UnsafeException;

public class UnauthorizedException extends UnsafeException {
    public UnauthorizedException() {
        super();
    }

    public UnauthorizedException(String errorMessage) {
        super(errorMessage);
    }
}
