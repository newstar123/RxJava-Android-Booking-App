package app.delivering.mvp.feedback.share.send.model;

public class SuccessFeedback {
    private int ratingType;

    public SuccessFeedback(int ratingType) {

        this.ratingType = ratingType;
    }

    public int getRatingType() {
        return ratingType;
    }
}
