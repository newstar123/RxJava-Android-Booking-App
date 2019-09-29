package app.core.feedback.put.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FeedbackRequestBody implements Serializable {
    @SerializedName("rating")
    private int rating;
    @SerializedName("num_drinks")
    private int numDrinks;
    @SerializedName("feedback")
    private String feedback;

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getNumDrinks() {
        return numDrinks;
    }

    public void setNumDrinks(int numDrinks) {
        this.numDrinks = numDrinks;
    }
}
