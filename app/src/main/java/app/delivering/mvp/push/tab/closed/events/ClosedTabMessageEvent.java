package app.delivering.mvp.push.tab.closed.events;

import org.json.JSONObject;

public class ClosedTabMessageEvent {

    private JSONObject message;

    public ClosedTabMessageEvent(JSONObject message) {

        this.message = message;
    }

    public JSONObject getMessage() {
        return message;
    }
}
