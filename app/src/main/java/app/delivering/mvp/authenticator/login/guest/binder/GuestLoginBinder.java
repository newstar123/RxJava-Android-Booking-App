package app.delivering.mvp.authenticator.login.guest.binder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.greenrobot.eventbus.EventBus;

import app.R;
import app.core.login.guest.interactor.GuestLoginInteractor;
import app.delivering.component.authenticator.AuthenticatorActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.main.init.events.UpdateMainActionBarEvent;
import butterknife.OnClick;


public class GuestLoginBinder extends BaseBinder {
    private final GuestLoginInteractor guestLoginInteractor;
    private AuthenticatorActivity activity;

    public GuestLoginBinder(AuthenticatorActivity activity) {
        super(activity);
        guestLoginInteractor = new GuestLoginInteractor(activity);
        this.activity = activity;
    }

    @OnClick(R.id.continue_as_guest) void facebookSignUp() {
        guestLoginInteractor.process()
                .subscribe(this::show, e -> {});
    }

    private void show(Bundle bundle) {
        EventBus.getDefault().post(new UpdateMainActionBarEvent());
        activity.setAccountAuthenticatorResult(bundle);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }
}
