package app.delivering.mvp.coach.login.init.binder;

import android.widget.Button;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.core.coach.login.put.interactor.PutLoginCoachMarkStateInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.detail.checkin.signup.events.SignUpFromBarDetailEvent;
import app.delivering.mvp.coach.login.init.events.HideFBLoginCoachMarkEvent;
import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;

public class InitFacebookLoginCoachMarkBinder extends BaseBinder {
    @BindView(R.id.connect_with_facebook_coach_button) Button button;
    private final PutLoginCoachMarkStateInteractor interactor;

    public InitFacebookLoginCoachMarkBinder(BaseActivity activity) {
        super(activity);
        interactor = new PutLoginCoachMarkStateInteractor();
    }

    @Override public void afterViewsBounded() {
        button.setText(formatHtmlToSpanned(R.string.connect_facebook));
        interactor.process()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {}, e -> {}, ()-> {});
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void hide(HideFBLoginCoachMarkEvent event) {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.close_cm) void onClickClose() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.connect_with_facebook_coach_button) void onLoginClick() {
        hide(new HideFBLoginCoachMarkEvent());
        EventBus.getDefault().post(new SignUpFromBarDetailEvent(false));
    }

}
