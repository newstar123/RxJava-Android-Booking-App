package app.core.advert.entity;

import com.google.gson.annotations.SerializedName;

public class AdvertAttributes {
    @SerializedName("image_url")  private String imageUrl;
    @SerializedName("video_url") private String videoUrl;
    @SerializedName("video_url2") private String videoUrl2;
    @SerializedName("video_url3") private String videoUrl3;
    @SerializedName("my_tab_screen") private String myTab;
    @SerializedName("uber_to_the_bar") private String uberToVenue;
    @SerializedName("uber_from_the_bar_eligible") private String uberFromVenueEligible;
    @SerializedName("uber_from_the_bar_not_eligible") private String uberFromVenueNotEligible;
    @SerializedName("tab_closed_eligible") private String tabClosedEligible;
    @SerializedName("tab_closed_not_eligible") private String tabClosedNotEligible;

    public String getImageUrl() {
        return imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getVideoUrl2() {
        return videoUrl2;
    }

    public String getVideoUrl3() {
        return videoUrl3;
    }

    public String getMyTabAdvert() {
        return myTab;
    }

    public String getUberToVenue() {
        return uberToVenue;
    }

    public String getUberFromVenueEligible() {
        return uberFromVenueEligible;
    }

    public String getUberFromVenueNotEligible() {
        return uberFromVenueNotEligible;
    }

    public String getTabClosedEligible() {
        return tabClosedEligible;
    }

    public String getTabClosedNotEligible() {
        return tabClosedNotEligible;
    }
}
