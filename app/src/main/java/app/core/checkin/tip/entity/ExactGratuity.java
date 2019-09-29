package app.core.checkin.tip.entity;

import com.google.gson.annotations.SerializedName;

public class ExactGratuity {
    @SerializedName("exact_gratuity")
    private int exactGratuity;

    public int getTips() {
        return exactGratuity;
    }

    public void setTips(int exactGratuity) {
        this.exactGratuity = exactGratuity;
    }
}
