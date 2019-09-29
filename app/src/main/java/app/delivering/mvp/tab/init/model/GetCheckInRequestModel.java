package app.delivering.mvp.tab.init.model;

public class GetCheckInRequestModel {
    private long id;
    private boolean isForceRequest;

    public GetCheckInRequestModel(long id, boolean isForceRequest) {
        this.id = id;
        this.isForceRequest = isForceRequest;
    }

    public long getId() {
        return id;
    }

    public boolean isForceRequest() {
        return isForceRequest;
    }
}
