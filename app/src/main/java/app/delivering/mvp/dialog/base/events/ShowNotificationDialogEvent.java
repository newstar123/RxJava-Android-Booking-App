package app.delivering.mvp.dialog.base.events;

import java.util.HashMap;

import app.delivering.mvp.notification.notifier.NotificationType;

public class ShowNotificationDialogEvent {
    private NotificationType type;
    private HashMap params;

    public ShowNotificationDialogEvent(NotificationType type, HashMap params) {
        this.type = type;
        this.params = params;
    }

    public NotificationType getType() {
        return type;
    }

    public HashMap getParams() {
        return params;
    }
}
