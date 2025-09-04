package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.server.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventNamesView {

    private String event;

    private List<String> names;

    public EventNamesView() {}

    public EventNamesView(String event) {
        this.event = event;
    }

    public EventNamesView(String event, List<String> names) {
        this.event = event;
        this.names = names;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public List<String> getNames() {
        return names != null ? names : new ArrayList<>();
    }

    public void setNames(List<String> names) {
        this.names = names;
    }


    public static List<EventNamesView> fromMap(Map<String, List<String>> eventsNames) {
        List<EventNamesView> eventNamesViews = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : eventsNames.entrySet()) {
            eventNamesViews.add(new EventNamesView(entry.getKey(), entry.getValue()));
        }
        return eventNamesViews;
    }
}
