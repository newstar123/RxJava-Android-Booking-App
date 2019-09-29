package app.delivering.mvp.push.ride.complete.to.events;

import org.json.JSONObject;

public class PushRideToBarCompleteEvent {
    private JSONObject message;

    public PushRideToBarCompleteEvent(JSONObject message) {this.message = message;}

    public JSONObject getMessage() {
        return message;
    }
}
