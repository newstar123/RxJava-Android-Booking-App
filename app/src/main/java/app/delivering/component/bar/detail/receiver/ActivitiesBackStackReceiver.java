package app.delivering.component.bar.detail.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import app.delivering.component.bar.detail.BarDetailActivity;

public class ActivitiesBackStackReceiver extends BroadcastReceiver {

    private final BarDetailActivity activity;

    public static final String REMOVE_BAR_DETAIL_ACTIVITY_INTENT = "app.delivering.component.bar.detail.receiver.REMOVE_BAR_DETAIL_ACTIVITY_INTENT";

    public ActivitiesBackStackReceiver(BarDetailActivity activity) {
        super();
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals(REMOVE_BAR_DETAIL_ACTIVITY_INTENT)) {
                Log.i("Receiver", REMOVE_BAR_DETAIL_ACTIVITY_INTENT);
                activity.finishAfterTransition();
            }
        }
    }
}
