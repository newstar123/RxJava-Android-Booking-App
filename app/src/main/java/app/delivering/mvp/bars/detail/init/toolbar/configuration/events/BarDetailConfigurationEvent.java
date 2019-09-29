package app.delivering.mvp.bars.detail.init.toolbar.configuration.events;

public class BarDetailConfigurationEvent {
    private boolean isPortraitState;
    private boolean isExpandState;

    public BarDetailConfigurationEvent(boolean isPortraitState, boolean isExpandState) {

        this.isPortraitState = isPortraitState;
        this.isExpandState = isExpandState;
    }

    public boolean isPortraitState() {
        return isPortraitState;
    }

    public boolean isExpandState() {
        return isExpandState;
    }
}
