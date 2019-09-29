package app.delivering.mvp.bars.detail.fullscreen.images.video.start.events;

public class AboutBarClickVideoEvent {
    private String videoUrl;
    private long position;
    private int window;
    private String poster;

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public void setWindow(int window) {
        this.window = window;
    }

    public long getPosition() {
        return position;
    }

    public int getWindow() {
        return window;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getPoster() {
        return poster;
    }
}
