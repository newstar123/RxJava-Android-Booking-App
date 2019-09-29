package app.delivering.mvp.payment.list.delete.model;


public class PaymentsErrorDeleteModel {
    private Throwable throwable;
    private PaymentsDeleteModel event;
    private boolean hasError;
    private String defaultCard;

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public PaymentsDeleteModel getEvent() {
        return event;
    }

    public void setEvent(PaymentsDeleteModel event) {
        this.event = event;
    }

    public boolean hasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public String getDefaultCard() {
        return defaultCard;
    }

    public void setDefaultCard(String defaultCard) {
        this.defaultCard = defaultCard;
    }
}
