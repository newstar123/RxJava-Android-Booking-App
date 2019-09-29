package app.delivering.mvp.bars.detail.init.tablist.tab.events;

public class BarDetailOnTabItemSelectedEvent {
    private int selectedTabPosition;

    public BarDetailOnTabItemSelectedEvent(int selectedTabPosition) {this.selectedTabPosition = selectedTabPosition;}


    public int getSelectedTabPosition() {
        return selectedTabPosition;
    }
}
