package app.core.coach.entity;

public class BooleanResponse {
    private boolean isOk;

    public BooleanResponse(boolean isOk) {
        this.isOk = isOk;
    }

    public boolean isOk() {
        return isOk;
    }

    public void setOk(boolean ok) {
        isOk = ok;
    }
}
