package app.delivering.mvp.push.tab.tiket.events;

import org.json.JSONObject;

public class TicketNotFoundEvent {
    private JSONObject message;

    public TicketNotFoundEvent(JSONObject message) {
        this.message = message;
    }

    public JSONObject getMessage() {
        return message;
    }
}
