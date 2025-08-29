package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.unsafe;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.UnsafeException;

public class CacheContainerException extends UnsafeException {
    public CacheContainerException() {
        super();
    }

    public CacheContainerException(String errorMessage) {
        super(errorMessage);
    }
}
