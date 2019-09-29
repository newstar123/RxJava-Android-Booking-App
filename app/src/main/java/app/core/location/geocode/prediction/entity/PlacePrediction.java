package app.core.location.geocode.prediction.entity;


public class PlacePrediction {
    private String fullText;
    private String placeId;
    private final String primaryText;
    private final String secondaryText;

    public PlacePrediction(String fullText, String placeId, String primaryText, String secondaryText) {
        this.fullText = fullText;
        this.placeId = placeId;
        this.primaryText = primaryText;
        this.secondaryText = secondaryText;
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPrimaryText() {
        return primaryText;
    }

    public String getSecondaryText() {
        return secondaryText;
    }
}
