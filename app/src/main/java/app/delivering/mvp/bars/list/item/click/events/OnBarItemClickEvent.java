package app.delivering.mvp.bars.list.item.click.events;

import android.widget.ImageView;
import android.widget.TextView;

import app.delivering.mvp.bars.list.init.enums.BarByWorkTime;
import app.delivering.mvp.bars.list.init.enums.BarListFilterType;

public class OnBarItemClickEvent {
    private long barId;
    private double distanceKm;

    private ImageView startedView;
    private ImageView typeIndicator;
    private TextView name;
    private String nameValue;
    private TextView type;
    private TextView discount;
    private TextView route;

    private String image;
    private String distance;
    private String swipingText;

    private BarByWorkTime barWorkType;
    private BarListFilterType filterType;

    private boolean isClickFromMap;
    private boolean shouldOpenBySwiping = false;
    private boolean isSwipingBeforeThreshold;


    public String getSwipingText() {
        return swipingText;
    }

    public void setSwipingText(String swipingText) {
        this.swipingText = swipingText;
    }

    public boolean getShouldOpenBySwiping() {
        return shouldOpenBySwiping;
    }

    public void setShouldOpenBySwiping(boolean b) {
        this.shouldOpenBySwiping = b;
    }

    public boolean getSwipingBeforeThreshold() {
        return isSwipingBeforeThreshold;
    }

    public void setSwipingBeforeThreshold(boolean b) {
        this.isSwipingBeforeThreshold = b;
    }

    public OnBarItemClickEvent(BarListFilterType filterType) {
        this.filterType = filterType;
    }

    public long getBarId() {
        return barId;
    }

    public void setBarId(long barId) {
        this.barId = barId;
    }

    public ImageView getStartedView() {
        return startedView;
    }

    public void setStartedView(ImageView startedView) {
        this.startedView = startedView;
    }

    public BarListFilterType getFilterType() {
        return filterType;
    }

    public void setFilterType(BarListFilterType filterType) {
        this.filterType = filterType;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setTypeIndicator(ImageView typeIndicator) {
        this.typeIndicator = typeIndicator;
    }

    public ImageView getTypeIndicator() {
        return typeIndicator;
    }

    public void setName(TextView name) {
        this.name = name;
    }

    public void setType(TextView type) {
        this.type = type;
    }

    public void setDiscount(TextView discount) {
        this.discount = discount;
    }

    public void setRoute(TextView route) {
        this.route = route;
    }

    public TextView getName() {
        return name;
    }

    public TextView getType() {
        return type;
    }

    public TextView getDiscount() {
        return discount;
    }

    public TextView getRoute() {
        return route;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setBarWorkType(BarByWorkTime barWorkType) {
        this.barWorkType = barWorkType;
    }

    public BarByWorkTime getBarWorkType() {
        return barWorkType;
    }

    public boolean isClickFromMap() {
        return isClickFromMap;
    }

    public void setClickFromMap(boolean clickFromMap) {
        isClickFromMap = clickFromMap;
    }

    public String getNameValue() {
        return nameValue;
    }

    public void setNameValue(String nameValue) {
        this.nameValue = nameValue;
    }
}
