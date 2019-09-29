package app.delivering.mvp.main.photo.init.events;

public class ChooseDialogResultEvent {
    private int result;
    private boolean isOpeningStateFromWarning;

    public ChooseDialogResultEvent(int result) {
        this.result = result;
    }

    public int getResult() {
        return result;
    }

    public boolean getIsOpeningStateFromWarning() {
        return isOpeningStateFromWarning;
    }

    public void setIsOpeningStateFromWarning(boolean b) {
        isOpeningStateFromWarning = b;
    }
}
