package app.delivering.mvp.profile.edit.init.model;


import app.core.profile.get.entity.ProfileModel;

public class InitProfileModel {
    private ProfileModel profileModel;
    private String formattedBirthday;
    private String phone;
    private String countryCode;

    public ProfileModel getProfileModel() {
        return profileModel;
    }

    public void setProfileModel(ProfileModel profileModel) {
        this.profileModel = profileModel;
    }

    public String getFormattedBirthday() {
        return formattedBirthday;
    }

    public void setFormattedBirthday(String formattedBirthday) {
        this.formattedBirthday = formattedBirthday;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhone() {
        return phone;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
