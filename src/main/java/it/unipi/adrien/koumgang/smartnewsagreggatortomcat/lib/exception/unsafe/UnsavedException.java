package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.unsafe;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.UnsafeException;

public class UnsavedException extends UnsafeException {
    public UnsavedException() {
        super();
    }

    public UnsavedException(String errorMessage) {
        super(errorMessage);
    }
}
