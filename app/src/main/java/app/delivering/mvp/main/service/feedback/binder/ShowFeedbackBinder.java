package app.delivering.mvp.main.service.feedback.binder;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.core.checkin.user.get.entity.GetCheckInsResponse;
import app.delivering.component.BaseActivity;
import app.delivering.component.feedback.FeedbackActivity;
import app.delivering.component.tab.TabActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.main.service.feedback.events.ShowFeedbackEvent;

public class ShowFeedbackBinder extends BaseBinder {

    public ShowFeedbackBinder(BaseActivity activity) {
        super(activity);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onShowEvent(ShowFeedbackEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        if (event.getLastSavedCheckIn() != null || TextUtils.isEmpty(event.getLastSavedCheckIn().getFeedback()))
            showFeedBack(event.getLastSavedCheckIn());
    }

    private void showFeedBack(GetCheckInsResponse lastSavedCheckIn) {
        if (!lastSavedCheckIn.isAutoClosed() && lastSavedCheckIn.getCheckoutTime() != null && lastSavedCheckIn.getRating() == null) {
            Intent intent = new Intent(getActivity(), FeedbackActivity.class);
            Bundle bundle = new Bundle();
            bundle.putLong(TabActivity.TAB_BAR_ID, lastSavedCheckIn.getVendorId());
            bundle.putLong(TabActivity.TAB_CHECK_IN_ID, lastSavedCheckIn.getId());
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            getActivity().startActivity(intent);
        }
    }
}
