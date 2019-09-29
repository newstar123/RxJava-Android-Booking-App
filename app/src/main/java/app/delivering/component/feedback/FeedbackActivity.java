package app.delivering.component.feedback;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.feedback.init.binder.FeedbackInitBinder;
import app.delivering.mvp.feedback.init.events.FeedbackOnStartEvent;
import app.delivering.mvp.feedback.send.binder.SendFeedbackBinder;
import app.delivering.mvp.feedback.share.send.binder.ShareFeedbackBinder;

public class FeedbackActivity extends BaseActivity {

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initUseCases();
    }

    private void initUseCases() {
        FeedbackInitBinder feedbackInitBinder = new FeedbackInitBinder(this);
        addToEventBusAndViewInjection(feedbackInitBinder);
        SendFeedbackBinder sendFeedbackBinder = new SendFeedbackBinder(this);
        addToEventBusAndViewInjection(sendFeedbackBinder);
        ShareFeedbackBinder shareFeedbackBinder = new ShareFeedbackBinder(this);
        addToEventBusAndViewInjection(shareFeedbackBinder);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ShareFeedbackBinder.SHARE_REQUEST)
            finish();
    }

    @Override protected void onStart() {
        super.onStart();
        EventBus.getDefault().post(new FeedbackOnStartEvent());
    }
}
