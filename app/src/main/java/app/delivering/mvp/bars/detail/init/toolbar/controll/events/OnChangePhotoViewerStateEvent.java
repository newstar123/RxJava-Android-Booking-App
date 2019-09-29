package app.delivering.mvp.bars.detail.init.toolbar.controll.events;

public class OnChangePhotoViewerStateEvent {
    private Boolean isExpand;

    public OnChangePhotoViewerStateEvent(Boolean isExpand) {
        this.isExpand = isExpand;
    }

    public Boolean getExpand() {
        return isExpand;
    }
}
