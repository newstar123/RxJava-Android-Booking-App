package app.core.bars.list.get.entity;

import java.util.List;

import app.delivering.mvp.bars.list.init.enums.BarByWorkTime;
import app.delivering.mvp.bars.list.init.enums.BarType;

public class BarListModel {
    private long id;
    private String backgroundUrl;
    private String name;
    private BarType type;
    private String discountText;
    private double discount;
    private String city;
    private double distKm;
    private double distMiles;
    private double latitude;
    private double longitude;
    private String routing;
    private String workTypeText;
    private List<String> hours;
    private BarByWorkTime barWorkTimeType;
    private String neighborhood;
    private String specialNotice;
    private String specialNoticeStatus;
    private int checkinedFriendsNumber;
    private Long checkInId;

    public double getDistMiles() {
        return distMiles;
    }

    public void setDistMiles(double distMiles) {
        this.distMiles = distMiles;
    }

    public Long getCheckInId() {
        return checkInId;
    }

    public void setCheckInId(Long checkInId) {
        this.checkInId = checkInId;
    }

    public double getDistKm() {
        return distKm;
    }

    public void setDistKm(double distKm) {
        this.distKm = distKm;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public BarType getType() {
        return type;
    }

    public void setType(BarType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBackgroundUrl() {
        return backgroundUrl;
    }

    public void setBackgroundUrl(String backgroundUrl) {
        this.backgroundUrl = backgroundUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRouting() {
        return routing;
    }

    public void setRouting(String routing) {
        this.routing = routing;
    }

    public List<String> getHours() {
        return hours;
    }

    public void setHours(List<String> hours) {
        this.hours = hours;
    }

    public String getDiscountText() {
        return discountText;
    }

    public void setDiscountText(String discountText) {
        this.discountText = discountText;
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

    public BarByWorkTime getBarWorkTimeType() {
        return barWorkTimeType;
    }

    public void setBarWorkTimeType(BarByWorkTime barWorkTimeType) {
        this.barWorkTimeType = barWorkTimeType;
    }

    public String getWorkTypeText() {
        return workTypeText;
    }

    public void setWorkTypeText(String workTypeText) {
        this.workTypeText = workTypeText;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
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

    public int getCheckinedFriendsNumber() {
        return checkinedFriendsNumber;
    }

    public void setCheckinedFriendsNumber(int checkinedFriendsNumber) {
        this.checkinedFriendsNumber = checkinedFriendsNumber;
    }
}
