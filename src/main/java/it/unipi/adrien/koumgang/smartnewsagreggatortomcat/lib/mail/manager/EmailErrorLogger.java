package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.mail.manager;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.server.service.ServerEventLogService;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.server.view.ServerEventLogView;

public class EmailErrorLogger {

    private static EmailErrorLogger instance;

    private EmailErrorLogger() {}

    public static EmailErrorLogger getInstance() {
        if(instance == null) {
            instance = new EmailErrorLogger();
        }
        return instance;
    }

    public void log(Throwable error){

        ServerEventLogView serverEventLogView = ServerEventLogService.getInstance()
                .saveServerEventLog(
                        "mail",
                        error.getClass().getSimpleName(),
                        error.getMessage(),
                        null,
                        null,
                        null
                );

    }

}