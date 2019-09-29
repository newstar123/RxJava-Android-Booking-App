package app.core.feedback.init.entity;


import app.core.bars.list.get.entity.BarModel;

public class FeedbackOutputModel {
    private String imageURL;
    private BarModel bar;

    public FeedbackOutputModel(String imageURL) {
        this.imageURL = imageURL;
    }

    public FeedbackOutputModel(BarModel bar) {
        this.bar = bar;
    }

    public String getImageURL() {
        return imageURL;
    }

    public BarModel getBar() {
        return bar;
    }
}
