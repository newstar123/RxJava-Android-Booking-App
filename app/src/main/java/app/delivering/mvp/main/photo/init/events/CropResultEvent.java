package app.delivering.mvp.main.photo.init.events;

public class CropResultEvent {
    private String path;

    public CropResultEvent(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
