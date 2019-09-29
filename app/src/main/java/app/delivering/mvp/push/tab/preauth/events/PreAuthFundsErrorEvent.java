package app.delivering.mvp.push.tab.preauth.events;

import org.json.JSONObject;

public class PreAuthFundsErrorEvent {
    private JSONObject jsonMessage;

    public PreAuthFundsErrorEvent(JSONObject jsonMessage) {this.jsonMessage = jsonMessage;}

    public JSONObject getJsonMessage() {
        return jsonMessage;
    }
}
