package app.delivering.mvp.feedback.init.binder;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.core.bars.list.get.entity.BarModel;
import app.core.feedback.init.entity.FeedbackOutputModel;
import app.core.feedback.init.interactor.FeedbackInitInteractor;
import app.delivering.component.feedback.FeedbackActivity;
import app.delivering.component.tab.TabActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.feedback.init.binder.handler.PicassoImageHandler;
import app.delivering.mvp.feedback.init.events.FeedbackOnStartEvent;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.main.init.binder.InitLocationImageHandler;
import app.delivering.mvp.profile.edit.actionbar.clicks.binder.ViewActionSetter;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.ReplaySubject;

public class FeedbackInitBinder extends BaseBinder {
    private final FeedbackInitInteractor feedbackInitInteractor;
    private final InitExceptionHandler initExceptionHandler;
    private FeedbackActivity activity;
    @BindView(R.id.city_background) ImageView cityBackground;
    @BindView(R.id.feedback_header_image) ImageView feedbackHeaderImage;
    @BindView(R.id.feedback_bar_name) TextView feedbackBarName;
    @BindView(R.id.feedback_send) View controls;
    private InitLocationImageHandler initLocationImageHandler;
    private ReplaySubject<FeedbackOutputModel> replaySubject;
    private Subscription binderSubscribe;
    private PicassoImageHandler picassoImageHandler;

    public FeedbackInitBinder(FeedbackActivity activity) {
        super(activity);
        feedbackInitInteractor = new FeedbackInitInteractor(activity);
        initExceptionHandler = new InitExceptionHandler(activity);
        this.activity = activity;
        replaySubject = ReplaySubject.create();
    }

    @Override public void afterViewsBounded() {
        Intent intent = activity.getIntent();
        Bundle bundle = intent.getExtras();
        initLocationImageHandler = new InitLocationImageHandler(cityBackground);
        picassoImageHandler = new PicassoImageHandler(feedbackHeaderImage);
        long barId = bundle.getLong(TabActivity.TAB_BAR_ID, 0);
        binderSubscribe = feedbackInitInteractor.process(barId).subscribe(replaySubject);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartEvent(FeedbackOnStartEvent event) {
        if (hasToRestore(binderSubscribe))
            progressState();
        replaySubject.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::show, this::showError, () ->{});
    }

    private void progressState() {
        showProgress();
        initExceptionHandler.dismiss();
        ButterKnife.apply(controls, ViewActionSetter.DISABLE);
    }

    private void show(FeedbackOutputModel feedbackOutputModel) {
        resetState();
        BarModel bar = feedbackOutputModel.getBar();
        if (bar != null) {
            showBarData(bar);
            EventBus.getDefault().post(feedbackOutputModel);
        }
        String imageURL = feedbackOutputModel.getImageURL();
        if (!TextUtils.isEmpty(imageURL))
            initLocationImageHandler.showBackground(imageURL);
    }

    private void showBarData(BarModel bar) {
        ButterKnife.apply(controls, ViewActionSetter.ENABLE);
        feedbackBarName.setText(bar.getName());
        RequestCreator requestCreator = Picasso.with(getActivity())
                .load(bar.getBackgroundImageUrl())
                .fit()
                .centerCrop();
        picassoImageHandler.apply(requestCreator)
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(r -> {}, e ->{});
    }

    private void showError(Throwable throwable) {
        resetState();
        initExceptionHandler.showError(throwable, v -> afterViewsBounded());
    }

    private void resetState() {
        hideProgress();
        replaySubject = ReplaySubject.create();
        onStartEvent(new FeedbackOnStartEvent());
    }
}
