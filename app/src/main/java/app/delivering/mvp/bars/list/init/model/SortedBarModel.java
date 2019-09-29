package app.delivering.mvp.bars.list.init.model;

public class SortedBarModel {
    private long id;
    private String name;
    private String barImageUrl;
    private String type;
    private String discount;
    private String address;
    private String workTime;
    private double distance;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWorkTime() {
        return workTime;
    }

    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }

    public String getBarImageUrl() {
        return barImageUrl;
    }

    public void setBarImageUrl(String barImageUrl) {
        this.barImageUrl = barImageUrl;
    }
}
