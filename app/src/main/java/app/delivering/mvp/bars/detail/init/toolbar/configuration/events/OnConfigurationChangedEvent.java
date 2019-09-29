package app.delivering.mvp.bars.detail.init.toolbar.configuration.events;

import android.content.res.Configuration;

public class OnConfigurationChangedEvent {
    private Configuration newConfig;

    public OnConfigurationChangedEvent(Configuration newConfig) {
        this.newConfig = newConfig;
    }

    public Configuration getNewConfig() {
        return newConfig;
    }
}
