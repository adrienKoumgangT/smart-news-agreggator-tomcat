package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.safe.MissingRequiredFieldException;

import java.util.List;

public abstract class BaseView {

    public BaseView() {}

    public void checkIfValid() throws MissingRequiredFieldException {
        List<String> missing = RequiredValidator.validateRequiredFields(this);

        if (!missing.isEmpty()) {
            throw new MissingRequiredFieldException("Missing required fields: " + missing);
        }
    }

}
