package app.core.profile.get.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProfileModel {
    private long id;
    @SerializedName("first_name") private String firstName;
    @SerializedName("last_name") private String lastName;
    private String gender;
    private String birthdate;
    private String email;
    private String zip;
    private String phone;

    @SerializedName("email_verified") private int isEmailVerified;
    @SerializedName("phone_verified") private int isPhoneVerified;
    private boolean isAccountVerified;

    @SerializedName("image_url") private String imageUrl;
    private String token;
    private String city;
    @SerializedName("facebook_visible") private String facebookVisible;
    @SerializedName("free_drinks") private double freeDrinks;
    @SerializedName("invites_accepted") private int invitesAccepted;
    @SerializedName("friends_who_have_checked_in") private int friendsWhoHaveCheckedIn;
    @SerializedName("total_saved") private int totalSaved;
    @SerializedName("referralCodes") private List<ReferralCode> referralCode;
    @SerializedName("facebook_id") private long facebookId;
    @SerializedName("facebook_supplied") private List<String> facebookSupplied;
    @SerializedName("country_code") private String countryCode;


    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getIsPhoneVerified() {
        return isPhoneVerified;
    }

    public void setIsPhoneVerified(boolean isPhoneVerified) {
        if (isPhoneVerified)
            this.isPhoneVerified = 1;
        else
            this.isPhoneVerified = 0;
    }

    public Integer getIsEmailVerified() { return isEmailVerified; }

    public void setIsEmailVerified(boolean isEmailVerified) {

    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public double getFreeDrinks() {
        return freeDrinks;
    }

    public void setFreeDrinks(double freeDrinks) {
        this.freeDrinks = freeDrinks;
    }

    public ReferralCode getReferralCode() {
        return referralCode.get(0);
    }

    public int getInvitesAccepted() {
        return invitesAccepted;
    }

    public void setInvitesAccepted(int invitesAccepted) {
        this.invitesAccepted = invitesAccepted;
    }

    public int getFriendsWhoHaveCheckedIn() {
        return friendsWhoHaveCheckedIn;
    }

    public void setFriendsWhoHaveCheckedIn(int friendsWhoHaveCheckedIn) {
        this.friendsWhoHaveCheckedIn = friendsWhoHaveCheckedIn;
    }

    public String getFacebookVisible() {
        return facebookVisible;
    }

    public void setFacebookVisible(String facebookVisible) {
        this.facebookVisible = facebookVisible;
    }

    public long getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(long facebookId) {
        this.facebookId = facebookId;
    }

    public long getId() {
        return id;
    }

    public int getTotalSaved() {
        return totalSaved;
    }

    public void setTotalSaved(int totalSaved) {
        this.totalSaved = totalSaved;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public boolean isAccountVerified() {
        return isAccountVerified;
    }

    public void setAccountVerified(boolean accountVerified) {
        isAccountVerified = accountVerified;
    }
}
