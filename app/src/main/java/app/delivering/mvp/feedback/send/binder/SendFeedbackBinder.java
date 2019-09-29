package app.delivering.mvp.feedback.send.binder;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.core.feedback.put.entity.FeedbackRequestBody;
import app.core.feedback.put.entity.FeedbackRequestModel;
import app.core.payment.regular.model.EmptyResponse;
import app.delivering.component.feedback.FeedbackActivity;
import app.delivering.component.tab.TabActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.feedback.init.events.FeedbackOnStartEvent;
import app.delivering.mvp.feedback.send.presenter.SendFeedbackPresenter;
import app.delivering.mvp.feedback.share.send.model.SuccessFeedback;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.ReplaySubject;

public class SendFeedbackBinder extends BaseBinder {
    @BindView(R.id.progress) MaterialProgressBar progressBar;
    @BindView(R.id.editTextFeedback) EditText feedback;
    @BindView(R.id.feedback_send) Button send;
    @BindView(R.id.feedback_share_love_buttons) View shareLove;
    @BindView(R.id.feedback_rate_positive) RadioButton ratePositive;
    @BindView(R.id.feedback_rate_negative) RadioButton rateNegative;
    private SendFeedbackPresenter presenter;
    private final InitExceptionHandler initExceptionHandler;
    private ReplaySubject<EmptyResponse> replaySubject;
    private Subscription binderSubscribe;
    private FeedbackActivity activity;
    private int ratingType;

    public SendFeedbackBinder(FeedbackActivity activity) {
        super(activity);
        this.activity = activity;
        presenter = new SendFeedbackPresenter(getActivity());
        initExceptionHandler = new InitExceptionHandler(getActivity());
        replaySubject = ReplaySubject.create();
        ratingType = 1;
    }

    @Override
    public void afterViewsBounded() {
        feedback.setImeOptions(EditorInfo.IME_ACTION_DONE);
        feedback.setRawInputType(InputType.TYPE_CLASS_TEXT);
    }

    @OnCheckedChanged({R.id.feedback_rate_positive})
    void onCheckedRatePositive(CompoundButton button, boolean isChecked) {
        if (isChecked) {
            shareLove.setVisibility(View.VISIBLE);
            send.setText(checkSocials() ? getString(R.string.feedback_share_love) : getString(R.string.button_submit));
        }
    }

    @OnCheckedChanged({R.id.feedback_rate_negative})
    void onCheckedRateNegative(CompoundButton button, boolean isChecked) {
        if (isChecked) {
            shareLove.setVisibility(View.GONE);
            send.setText(getString(R.string.button_submit));
        }
    }

    @OnCheckedChanged({R.id.share_facebook, R.id.share_twitter, R.id.share_email})
    void onSocialChecked(CompoundButton button, boolean isChecked) {
        if (isChecked)
            send.setText(getString(R.string.feedback_share_love));
        else if (!checkSocials())
            send.setText(getString(R.string.button_submit));
    }

    private boolean checkSocials() {
        return ((CheckBox)shareLove.findViewById(R.id.share_facebook)).isChecked() ||
                ((CheckBox)shareLove.findViewById(R.id.share_twitter)).isChecked() ||
                ((CheckBox)shareLove.findViewById(R.id.share_email)).isChecked();
    }

    @OnClick(R.id.feedback_send) void send() {
        send.setEnabled(false);
        binderSubscribe = presenter.process(getRequestModel()).subscribe(replaySubject);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartEvent(FeedbackOnStartEvent event) {
        if (hasToRestore(binderSubscribe))
            showProgress();
        replaySubject.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::show, this::onError, () -> {});
    }

    private FeedbackRequestModel getRequestModel() {
        FeedbackRequestModel requestModel = new FeedbackRequestModel();
        FeedbackRequestBody body = new FeedbackRequestBody();
        body.setFeedback(feedback.getText().toString());
        body.setRating(ratingType);
        requestModel.setBody(body);
        Bundle bundle = activity.getIntent().getExtras();
        long checkInId = bundle.getLong(TabActivity.TAB_CHECK_IN_ID, 0);
        requestModel.setCheckInId(checkInId);
        return requestModel;
    }

    private void show(EmptyResponse response) {
        resetState();
        EventBus.getDefault().post(new SuccessFeedback(ratingType));
    }

    private void onError(Throwable throwable) {
        resetState();
        initExceptionHandler.showError(throwable, view -> send());
    }

    private void resetState() {
        hideProgress();
        send.setEnabled(true);
        replaySubject = ReplaySubject.create();
        onStartEvent(new FeedbackOnStartEvent());
    }

}
