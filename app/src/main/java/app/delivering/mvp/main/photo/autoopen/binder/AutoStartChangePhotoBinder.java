package app.delivering.mvp.main.photo.autoopen.binder;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import app.R;
import app.delivering.component.profile.ProfileFragment;
import app.delivering.mvp.BaseBinder;
import butterknife.BindView;

public class AutoStartChangePhotoBinder extends BaseBinder {
    @BindView(R.id.change_photo_floating_button) FloatingActionButton actionButton;
    public static final String AUTO_OPEN_CHANGE_PHOTO = "AUTO_OPEN_CHANGE_PHOTO";
    private boolean isAutoChangePhotoActive;

    public AutoStartChangePhotoBinder(ProfileFragment fragment) {
        super(fragment.getBaseActivity());
        Bundle bundle = fragment.getArguments();
        if (bundle != null)
            isAutoChangePhotoActive = bundle.getBoolean(AUTO_OPEN_CHANGE_PHOTO, false);
    }

    @Override
    public void afterViewsBounded() {
        if (isAutoChangePhotoActive) {
            actionButton.performClick();
            isAutoChangePhotoActive = false;
        }
    }

}
