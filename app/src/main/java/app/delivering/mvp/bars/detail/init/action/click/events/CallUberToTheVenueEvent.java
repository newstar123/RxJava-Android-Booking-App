package app.delivering.mvp.bars.detail.init.action.click.events;

public class CallUberToTheVenueEvent {
    private String estimationResult;

    public CallUberToTheVenueEvent(String estimationResult) {
        this.estimationResult = estimationResult;
    }

    public String getEstimationResult() {
        return estimationResult;
    }
}
