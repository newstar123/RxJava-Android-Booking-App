package app.delivering.mvp.bars.detail.fullscreen.images.video.init.events;

import com.google.android.exoplayer2.SimpleExoPlayer;

public class OnInitializePlayerEvent {
    private SimpleExoPlayer player;
    private int window;
    private long position;
    private String videoUrl;

    public void setPlayer(SimpleExoPlayer player) {
        this.player = player;
    }

    public void setWindow(int window) {
        this.window = window;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public SimpleExoPlayer getPlayer() {
        return player;
    }

    public int getWindow() {
        return window;
    }

    public long getPosition() {
        return position;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
