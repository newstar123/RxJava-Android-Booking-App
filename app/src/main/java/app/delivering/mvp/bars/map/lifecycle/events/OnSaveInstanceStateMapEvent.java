package app.delivering.mvp.bars.map.lifecycle.events;

import android.os.Bundle;

public class OnSaveInstanceStateMapEvent {
    private Bundle bundle;

    public OnSaveInstanceStateMapEvent(Bundle bundle) {this.bundle = bundle;}

    public Bundle getBundle() {
        return bundle;
    }
}
