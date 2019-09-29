package app.delivering.mvp.profile.edit.actionbar.clicks.model;


import app.core.profile.put.entity.PutProfileModel;

public class PutProfileBinderModel {
    private PutProfileModel profileModel;
    private String birthday;

    public void setProfileModel(PutProfileModel profileModel) {
        this.profileModel = profileModel;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public PutProfileModel getProfileModel() {
        return profileModel;
    }

    public String getBirthday() {
        return birthday;
    }
}
