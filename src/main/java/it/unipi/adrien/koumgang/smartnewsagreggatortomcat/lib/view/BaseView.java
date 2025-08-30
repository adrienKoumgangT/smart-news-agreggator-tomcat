package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.safe.MissingRequiredFiledException;

import java.util.List;

public class BaseView {

    public BaseView() {}

    public void checkIfValid() throws MissingRequiredFiledException {
        List<String> missing = RequiredValidator.validateRequiredFields(this);

        if (!missing.isEmpty()) {
            throw new MissingRequiredFiledException("Missing required fields: " + missing);
        }
    }

}
