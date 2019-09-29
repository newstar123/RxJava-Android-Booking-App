package app.core.bars.metadata.entity;

import java.util.List;

public class ServerMetadataModel {
    private List<ServerFeatureModel> features;
    private List<ServerFeatureModel> insiderTips;

    public List<ServerFeatureModel> getInsiderTips() {
        return insiderTips;
    }

    public void setInsiderTips(List<ServerFeatureModel> insiderTips) {
        this.insiderTips = insiderTips;
    }

    public List<ServerFeatureModel> getFeatures() {
        return features;
    }

    public void setFeatures(List<ServerFeatureModel> features) {
        this.features = features;
    }
}
