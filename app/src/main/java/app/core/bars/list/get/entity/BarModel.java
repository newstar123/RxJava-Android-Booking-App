package app.core.bars.list.get.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import app.delivering.mvp.bars.list.init.enums.BarType;

public class BarModel implements Serializable, Parcelable {
    private long id;
    private String name;
    private String contact;
    private String phone;
    private String email;
    private String website;
    private String address;
    private String city;
    private String state;
    private long zip;
    private String type;
    private String neighborhood;
    private String slogan;
    private String description;
    private double latitude;
    private double longitude;
    private String timezone;
    @SerializedName("menu_url") private String menuUrl;
    @SerializedName("menu_text") private String menuText;
    @SerializedName("image_urls") private List<String> imageUrls;
    @SerializedName("background_image_url") private String backgroundImageUrl;
    private List<String> features;
    @SerializedName("insider_tips") private List<String> insiderTips;
    private List<String> hours;
    @SerializedName("kitchen_hours") private List<String> kitchenHours;
    @SerializedName("created_at") private Date createdAt;
    @SerializedName("updated_at") private Date updatedAt;
    @SerializedName("location_id") private long locationId;
    @SerializedName("country_code") private String countryCode;
    @SerializedName("ridesafe_rounds_min") private double ridesafeRoundsMin;
    @SerializedName("ridesafe_spend_round_min") private double ridesafeSpendRoundMin;
    @SerializedName("price_avg") private int priceAvg;
    @SerializedName("patron_age_avg") private int patronAgeAvg;
    @SerializedName("twitter_handle") private String twitterHandle;
    @SerializedName("special_notice") private String specialNotice;
    @SerializedName("special_notice_status") private String specialNoticeStatus;                 //!!!!!
    private String facebook;
    private String instagram;
    @SerializedName("gimbal_place_id") private String gimbalPlaceId;
    @SerializedName("dist_km") private double distKm;
    @SerializedName("dist_miles") private double distMiles;
    @SerializedName("current_discount") private double currentDiscount;
    @SerializedName("video_url") private String videoUrl;
    @SerializedName("video_thumb_url") private String videoThumbUrl;
    @SerializedName("patronsWithOpenedCheckin") private List<CheckinedPersonModel> checkinedPeople;
    @SerializedName("gimbalBeacons") private List<BeaconModel> beacons;
    private BarLocationModel location;

    public BarModel(){}

    protected BarModel(Parcel in) {
        id = in.readLong();
        name = in.readString();
        contact = in.readString();
        phone = in.readString();
        email = in.readString();
        website = in.readString();
        address = in.readString();
        city = in.readString();
        state = in.readString();
        zip = in.readLong();
        type = in.readString();
        neighborhood = in.readString();
        slogan = in.readString();
        description = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        timezone = in.readString();
        menuUrl = in.readString();
        menuText = in.readString();
        imageUrls = in.createStringArrayList();
        backgroundImageUrl = in.readString();
        features = in.createStringArrayList();
        insiderTips = in.createStringArrayList();
        hours = in.createStringArrayList();
        kitchenHours = in.createStringArrayList();
        locationId = in.readLong();
        countryCode = in.readString();
        ridesafeRoundsMin = in.readDouble();
        ridesafeSpendRoundMin = in.readDouble();
        priceAvg = in.readInt();
        patronAgeAvg = in.readInt();
        twitterHandle = in.readString();
        specialNotice = in.readString();
        specialNoticeStatus = in.readString();
        facebook = in.readString();
        instagram = in.readString();
        gimbalPlaceId = in.readString();
        distKm = in.readDouble();
        distMiles = in.readDouble();
        currentDiscount = in.readDouble();
        videoUrl = in.readString();
        videoThumbUrl = in.readString();
    }

    public static final Creator<BarModel> CREATOR = new Creator<BarModel>() {
        @Override
        public BarModel createFromParcel(Parcel in) {
            return new BarModel(in);
        }

        @Override
        public BarModel[] newArray(int size) {
            return new BarModel[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getZip() {
        return zip;
    }

    public void setZip(long zip) {
        this.zip = zip;
    }

    public BarType getType() {
        return BarType.toType(type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getMenuUrl() {
        return menuUrl;
    }

    public void setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl;
    }

    public String getMenuText() {
        return menuText;
    }

    public void setMenuText(String menuText) {
        this.menuText = menuText;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public void setBackgroundImageUrl(String backgroundImageUrl) {
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public List<String> getInsiderTips() {
        return insiderTips;
    }

    public void setInsiderTips(List<String> insiderTips) {
        this.insiderTips = insiderTips;
    }

    public List<String> getHours() {
        return hours;
    }

    public void setHours(List<String> hours) {
        this.hours = hours;
    }

    public List<String> getKitchenHours() {
        return kitchenHours;
    }

    public void setKitchenHours(List<String> kitchenHours) {
        this.kitchenHours = kitchenHours;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public double getRidesafeRoundsMin() {
        return ridesafeRoundsMin;
    }

    public void setRidesafeRoundsMin(double ridesafeRoundsMin) {
        this.ridesafeRoundsMin = ridesafeRoundsMin;
    }

    public double getRidesafeSpendRoundMin() {
        return ridesafeSpendRoundMin;
    }

    public void setRidesafeSpendRoundMin(double ridesafeSpendRoundMin) {
        this.ridesafeSpendRoundMin = ridesafeSpendRoundMin;
    }

    public int getPriceAvg() {
        return priceAvg;
    }

    public void setPriceAvg(int priceAvg) {
        this.priceAvg = priceAvg;
    }

    public int getPatronAgeAvg() {
        return patronAgeAvg;
    }

    public void setPatronAgeAvg(int patronAgeAvg) {
        this.patronAgeAvg = patronAgeAvg;
    }

    public String getTwitterHandle() {
        return twitterHandle;
    }

    public void setTwitterHandle(String twitterHandle) {
        this.twitterHandle = twitterHandle;
    }

    public String getSpecialNotice() {
        return specialNotice;
    }

    public void setSpecialNotice(String specialNotice) {
        this.specialNotice = specialNotice;
    }

    public String getSpecialNoticeStatus() {
        return specialNoticeStatus;
    }

    public void setSpecialNoticeStatus(String specialNoticeStatus) {
        this.specialNoticeStatus = specialNoticeStatus;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getGimbalPlaceId() {
        return gimbalPlaceId;
    }

    public void setGimbalPlaceId(String gimbalPlaceId) {
        this.gimbalPlaceId = gimbalPlaceId;
    }

    public double getDistKm() {
        return distKm;
    }

    public void setDistKm(double distKm) {
        this.distKm = distKm;
    }

    public double getDistMiles() {
        return distMiles;
    }

    public void setDistMiles(double distMiles) {
        this.distMiles = distMiles;
    }

    public double getCurrentDiscount() {
        return currentDiscount;
    }

    public void setCurrentDiscount(double currentDiscount) {
        this.currentDiscount = currentDiscount;
    }

    public List<BeaconModel> getBeacons() {
        return beacons;
    }

    public void setBeacons(List<BeaconModel> beacons) {
        this.beacons = beacons;
    }

    public BarLocationModel getLocation() {
        return location;
    }

    public void setLocation(BarLocationModel location) {
        this.location = location;
    }

    public String getVideoThumbUrl() {
        return videoThumbUrl;
    }

    public void setVideoThumbUrl(String videoThumbUrl) {
        this.videoThumbUrl = videoThumbUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public List<CheckinedPersonModel> getCheckinedPeople() {
        return checkinedPeople;
    }

    public void setCheckinedPeople(List<CheckinedPersonModel> checkinedPeople) {
        this.checkinedPeople = checkinedPeople;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(contact);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeString(website);
        dest.writeString(address);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeLong(zip);
        dest.writeString(type);
        dest.writeString(neighborhood);
        dest.writeString(slogan);
        dest.writeString(description);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(timezone);
        dest.writeString(menuUrl);
        dest.writeString(menuText);
        dest.writeStringList(imageUrls);
        dest.writeString(backgroundImageUrl);
        dest.writeStringList(features);
        dest.writeStringList(insiderTips);
        dest.writeStringList(hours);
        dest.writeStringList(kitchenHours);
        dest.writeLong(locationId);
        dest.writeString(countryCode);
        dest.writeDouble(ridesafeRoundsMin);
        dest.writeDouble(ridesafeSpendRoundMin);
        dest.writeInt(priceAvg);
        dest.writeInt(patronAgeAvg);
        dest.writeString(twitterHandle);
        dest.writeString(specialNotice);
        dest.writeString(specialNoticeStatus);
        dest.writeString(facebook);
        dest.writeString(instagram);
        dest.writeString(gimbalPlaceId);
        dest.writeDouble(distKm);
        dest.writeDouble(distMiles);
        dest.writeDouble(currentDiscount);
        dest.writeString(videoUrl);
        dest.writeString(videoThumbUrl);
    }
}