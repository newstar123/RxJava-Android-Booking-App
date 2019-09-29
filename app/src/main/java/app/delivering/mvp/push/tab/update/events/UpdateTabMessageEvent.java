package app.delivering.mvp.push.tab.update.events;

import org.json.JSONObject;

public class UpdateTabMessageEvent {

    private JSONObject jsonMessage;

    public UpdateTabMessageEvent(JSONObject jsonMessage) {

        this.jsonMessage = jsonMessage;
    }

    public JSONObject getJsonMessage() {
        return jsonMessage;
    }
}
