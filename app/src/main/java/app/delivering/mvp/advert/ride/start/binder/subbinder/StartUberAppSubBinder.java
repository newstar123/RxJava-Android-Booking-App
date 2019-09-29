package app.delivering.mvp.advert.ride.start.binder.subbinder;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import app.R;
import app.delivering.component.BaseActivity;

public class StartUberAppSubBinder {
    private BaseActivity activity;

    public StartUberAppSubBinder(BaseActivity activity) {
        this.activity = activity;
    }

    public void start() {
        try {
            PackageManager pm = activity.getPackageManager();
            pm.getPackageInfo("com.ubercab", PackageManager.GET_ACTIVITIES);
            String uri = "uber://?client_id=" + activity.getString(R.string.uber_client_id);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(uri));
            activity.startActivity(intent);
        } catch (PackageManager.NameNotFoundException e) {
        }
    }
}
