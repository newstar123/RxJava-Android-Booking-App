package app.core.checkin.tip.entity;

import com.google.gson.annotations.SerializedName;

public class Gratuity {
    @SerializedName("gratuity")
    private int gratuity;

    public int getTips() {
        return gratuity;
    }

    public void setTips(int gratuity) {
        this.gratuity = gratuity;
    }
}
