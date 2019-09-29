package app.delivering.mvp.settings.facebook.binder;

import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.SwitchCompat;

import com.trello.rxlifecycle.android.ActivityEvent;

import app.R;
import app.core.facebook.visibility.entity.FacebookVisibilityModel;
import app.core.facebook.visibility.entity.UpdateVisibilityPeriodException;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.dialog.RxDialogHandler;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.settings.facebook.model.FacebookVisibilityResponse;
import app.delivering.mvp.settings.facebook.presenter.FacebookVisibilityUpdatePresenter;
import butterknife.BindView;
import butterknife.OnClick;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.android.schedulers.AndroidSchedulers;

public class FacebookVisibilityBinder extends BaseBinder {
    @BindView(R.id.settings_progress) MaterialProgressBar progressBar;
    @BindView(R.id.facebook_visibility_switcher) SwitchCompat visibility;
    @BindView(R.id.settings_container) CoordinatorLayout container;
    private FacebookVisibilityUpdatePresenter updatePresenter;
    private final InitExceptionHandler initExceptionHandler;
    private RxDialogHandler rxDialogHandler;
    private FacebookVisibilityResponse facebookVisibility;

    public FacebookVisibilityBinder(BaseActivity activity) {
        super(activity);
        updatePresenter = new FacebookVisibilityUpdatePresenter(getActivity());
        initExceptionHandler = new InitExceptionHandler(getActivity());
        rxDialogHandler = new RxDialogHandler(getActivity());
    }

    @Override public void afterViewsBounded() {
        setProgress(progressBar);
    }

    @OnClick(R.id.facebook_visibility_switcher) void onVisibilityChange() {
        int message;
        facebookVisibility = new FacebookVisibilityResponse();
        facebookVisibility.setCheckedState(!visibility.isChecked());
        FacebookVisibilityModel visibilityModel = new FacebookVisibilityModel();
        if (visibility.isChecked()) {
            visibilityModel.setFacebookVisible("on");
            message = R.string.facebook_visibility_on_message;
        } else {
            visibilityModel.setFacebookVisible("off");
            message = R.string.facebook_visibility_off_message;
        }
        facebookVisibility.setVisibilityModel(visibilityModel);
        rxDialogHandler.showOneButtonWithTitle(R.string.facebook_visibility, message, R.string.okay)
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::checkUpdate, e -> resetSwitchState(), () -> {});
    }

    private void checkUpdate(Boolean isOk) {
        if (isOk)
            update(facebookVisibility);
        else
            resetSwitchState();
    }

    private void update(FacebookVisibilityResponse value) {
        showProgress();
        updatePresenter.process(value)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::show, this::showError);
    }

    private void show(FacebookVisibilityResponse aBoolean) {
        changeState(!facebookVisibility.isCheckedState());
    }

    private void showError(Throwable throwable) {
        resetSwitchState();
        if (throwable instanceof UpdateVisibilityPeriodException) {
            showDialogMessage(R.string.fb_visibility_period_error);
            facebookVisibility.setCheckedState(true);
        }
        else
            initExceptionHandler.showError(throwable, view -> update(facebookVisibility));
        changeState(facebookVisibility.isCheckedState());
    }

    private void resetSwitchState() {
        visibility.setChecked(facebookVisibility.isCheckedState());
    }

    private void changeState(boolean value) {
        hideProgress();
        visibility.setChecked(value);
    }
}
