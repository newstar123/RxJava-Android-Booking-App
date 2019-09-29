package app.delivering.mvp.dialog.base.events;

import app.delivering.mvp.notification.notifier.NotificationType;

public class HideNotificationDialogEvent {
    private NotificationType type;

    public HideNotificationDialogEvent(NotificationType type) {

        this.type = type;
    }

    public NotificationType getType() {
        return type;
    }
}
