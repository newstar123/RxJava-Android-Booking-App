package app.core.feedback.put.entity;

public class FeedbackRequestModel {
    private FeedbackRequestBody body;
    private long checkInId;

    public long getCheckInId() {
        return checkInId;
    }

    public void setCheckInId(long checkInId) {
        this.checkInId = checkInId;
    }

    public FeedbackRequestBody getBody() {
        return body;
    }

    public void setBody(FeedbackRequestBody body) {
        this.body = body;
    }
}
