package app.delivering.mvp.authenticator.init.binder;

import android.view.View;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.authenticator.init.events.OnStartAuthenticatorActivityEvent;
import app.delivering.mvp.authenticator.init.presenter.AuthenticatorInitPresenter;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.starttour.init.events.OnBackgroundLoadedEvent;
import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;

public class AuthenticatorInitBinder extends BaseBinder {
    @BindView(R.id.city_background) ImageView cityBackground;
    private final InitExceptionHandler initExceptionHandler;
    private final AuthenticatorInitPresenter presenter;


    public AuthenticatorInitBinder(BaseActivity activity) {
        super(activity);
        presenter = new AuthenticatorInitPresenter(activity);
        initExceptionHandler = new InitExceptionHandler(activity);
    }

    @Override public void afterViewsBounded() {
        super.afterViewsBounded();
        cityBackground.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartEvent(OnStartAuthenticatorActivityEvent event) {
        presenter.process()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    getActivity().loadCityImage();
                    EventBus.getDefault().post(new OnBackgroundLoadedEvent());
                    }, this::showError);
    }

    private void showError(Throwable throwable) {
        initExceptionHandler.showError(throwable, view -> onStartEvent(new OnStartAuthenticatorActivityEvent()));
    }
}
