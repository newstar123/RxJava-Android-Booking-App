package app.core.uber.delete.activity.entity;


public class DeleteUberResponse {
    private transient boolean isOk;


    public DeleteUberResponse(boolean isOk) {
        this.isOk = isOk;
    }

    public DeleteUberResponse() {
    }

    public boolean isOk() {
        return isOk;
    }

}
