package app.gateway.analytics.mixpanel;

import org.json.JSONObject;

public class MixpanelLogModel {
    private String eventName;
    private JSONObject properties;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public JSONObject getProperties() {
        return properties;
    }

    public void setProperties(JSONObject properties) {
        this.properties = properties;
    }
}
