package app.core.uber.fares.entity;


import java.util.List;

public class PostUberEstimatesResponse {
    private List<PostUberEstimateResponse> estimates;

    public void setEstimates(List<PostUberEstimateResponse> estimates) {
        this.estimates = estimates;
    }

    public List<PostUberEstimateResponse> getEstimates() {
        return estimates;
    }
}
