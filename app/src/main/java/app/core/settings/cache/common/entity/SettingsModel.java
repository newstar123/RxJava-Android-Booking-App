package app.core.settings.cache.common.entity;

public class SettingsModel {
    private boolean isFacebookVisibility;
    private boolean isAutoOpenTab;

    public boolean isFacebookVisibility() {
        return isFacebookVisibility;
    }

    public void setFacebookVisibility(boolean facebookVisibility) {
        isFacebookVisibility = facebookVisibility;
    }

    public boolean isAutoOpenTab() {
        return isAutoOpenTab;
    }

    public void setAutoOpenTab(boolean autoOpenTab) {
        isAutoOpenTab = autoOpenTab;
    }
}
