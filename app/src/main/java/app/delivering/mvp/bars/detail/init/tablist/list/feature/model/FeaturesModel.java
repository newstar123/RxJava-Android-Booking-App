package app.delivering.mvp.bars.detail.init.tablist.list.feature.model;

import android.text.Spanned;

import app.core.bars.metadata.entity.ServerFeatureModel;
import app.delivering.mvp.bars.detail.init.tablist.list.feature.enums.FeatureType;

public class FeaturesModel {
    private FeatureType type;
    private String textValue;
    private Spanned combinedText;
    private ServerFeatureModel venueFeatureModel;

    public FeatureType getType() {
        return type;
    }

    public void setType(FeatureType type) {
        this.type = type;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String value) {
        this.textValue = value;
    }

    public Spanned getCombinedText() {
        return combinedText;
    }

    public void setCombinedText(Spanned combinedText) {
        this.combinedText = combinedText;
    }

    public ServerFeatureModel getVenueFeatureModel() {
        return venueFeatureModel;
    }

    public void setServerFeatureModel(ServerFeatureModel venueFeatureModel) {
        this.venueFeatureModel = venueFeatureModel;
    }
}
