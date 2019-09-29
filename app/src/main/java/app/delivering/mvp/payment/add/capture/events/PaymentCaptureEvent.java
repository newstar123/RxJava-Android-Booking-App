package app.delivering.mvp.payment.add.capture.events;


import android.content.Intent;

public class PaymentCaptureEvent {
    private Intent data;

    public PaymentCaptureEvent(Intent data) {
        this.data = data;
    }

    public Intent getData() {
        return data;
    }
}
