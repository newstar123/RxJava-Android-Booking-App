package app.delivering.mvp.settings.facebook.model;

import app.core.facebook.visibility.entity.FacebookVisibilityModel;

public class FacebookVisibilityResponse {
    private boolean isCheckedState;
    private FacebookVisibilityModel visibilityModel;

    public boolean isCheckedState() {
        return isCheckedState;
    }

    public void setCheckedState(boolean checkedState) {
        isCheckedState = checkedState;
    }

    public FacebookVisibilityModel getVisibilityModel() {
        return visibilityModel;
    }

    public void setVisibilityModel(FacebookVisibilityModel visibilityModel) {
        this.visibilityModel = visibilityModel;
    }
}
