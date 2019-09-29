package app.delivering.mvp.bars.map.lifecycle.events;

import android.os.Bundle;

public class OnCreateMapEvent {
    private Bundle bundle;

    public OnCreateMapEvent(Bundle bundle) {this.bundle = bundle;}

    public Bundle getBundle() {
        return bundle;
    }
}
