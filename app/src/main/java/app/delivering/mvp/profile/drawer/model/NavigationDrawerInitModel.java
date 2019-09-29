package app.delivering.mvp.profile.drawer.model;

import app.core.profile.get.entity.ProfileModel;

public class NavigationDrawerInitModel {

    private ProfileModel profileModel;
    private String imageUrl;

    public NavigationDrawerInitModel(ProfileModel profileModel, String imageUrl) {
        this.profileModel = profileModel;
        this.imageUrl = imageUrl;
    }

    public ProfileModel getProfileModel() {
        return profileModel;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
