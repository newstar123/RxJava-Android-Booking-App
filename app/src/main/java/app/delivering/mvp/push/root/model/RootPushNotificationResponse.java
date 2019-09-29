package app.delivering.mvp.push.root.model;

import org.json.JSONObject;

import app.delivering.component.push.status.PushNotificationStatus;

public class RootPushNotificationResponse {
    private PushNotificationStatus status;
    private String serverMessage;
    private JSONObject jsonMessage;


    public PushNotificationStatus getStatus() {
        return status;
    }

    public void setStatus(PushNotificationStatus status) {
        this.status = status;
    }

    public JSONObject getJsonMessage() {
        return jsonMessage;
    }

    public void setJsonMessage(JSONObject jsonMessage) {
        this.jsonMessage = jsonMessage;
    }

    public void setServerMessage(String string) {
        this.serverMessage = string;
    }

    public String getServerMessage() {
        return serverMessage;
    }
}
