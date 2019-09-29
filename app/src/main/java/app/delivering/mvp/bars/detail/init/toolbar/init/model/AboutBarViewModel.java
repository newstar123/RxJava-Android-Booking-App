package app.delivering.mvp.bars.detail.init.toolbar.init.model;

import java.util.ArrayList;

public class AboutBarViewModel{
    private ArrayList<AboutBarViewTypeModel> urls;
    private String videoUrl;
    private String promotion;

    public AboutBarViewModel(ArrayList<AboutBarViewTypeModel> urls, String videoUrl, String promotion) {
        this.urls = urls;
        this.videoUrl = videoUrl;
        this.promotion = promotion;
    }

    public ArrayList<AboutBarViewTypeModel> getUrls() {
        return urls;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getPromotion() {
        return promotion;
    }
}
