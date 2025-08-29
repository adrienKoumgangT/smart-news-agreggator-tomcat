package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.unsafe;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.UnsafeException;

public class QueryException extends UnsafeException {

    public QueryException() {
        super();
    }

    public QueryException(String errorMessage) {
        super(errorMessage);
    }

}
