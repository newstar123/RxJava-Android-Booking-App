package app.core.bars.metadata.entity;

public class ServerFeatureModel {
    private String id;
    private String label;
    private String iconUrl;
    private boolean noIcon;

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isNoIcon() {
        return noIcon;
    }

    public void setNoIcon(boolean noIcon) {
        this.noIcon = noIcon;
    }
}
