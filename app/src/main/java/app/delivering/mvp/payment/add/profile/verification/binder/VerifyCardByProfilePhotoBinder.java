package app.delivering.mvp.payment.add.profile.verification.binder;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionInflater;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.profile.ProfileFragment;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.main.photo.autoopen.binder.AutoStartChangePhotoBinder;
import app.delivering.mvp.main.photo.autoopen.events.ProfileUpdatedEvent;
import app.delivering.mvp.payment.add.profile.verification.events.VerifyCardByPhotoEvent;
import app.delivering.mvp.payment.add.profile.verification.presenter.VerifyCardByProfilePhotoPresenter;
import app.delivering.mvp.dialog.RxDialogHandler;
import rx.android.schedulers.AndroidSchedulers;


public class VerifyCardByProfilePhotoBinder extends BaseBinder {
    private VerifyCardByProfilePhotoPresenter presenter;
    private RxDialogHandler rxDialogHandler;

    public VerifyCardByProfilePhotoBinder(BaseActivity activity) {
        super(activity);
        presenter = new VerifyCardByProfilePhotoPresenter(activity);
        rxDialogHandler = new RxDialogHandler(activity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddPaymentEvent(VerifyCardByPhotoEvent event) {
        presenter.process()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(response -> showUpdatePhotoDialog(), e -> stopWithResult(), () -> {});
    }

    private void showUpdatePhotoDialog() {
        rxDialogHandler
                .showTwoButtonsWithTitle(R.string.open_tab_dialog_title, R.string.error_no_photo, R.string.cancel, R.string.ok)
                .subscribe(this::tryUpdateProfilePhoto, e -> stopWithResult(), () -> {});
    }

    private void tryUpdateProfilePhoto(Boolean isOk) {
        if (isOk) updateProfile();
        else stopWithResult();
    }

    private void updateProfile() {
        ProfileFragment fragment = new ProfileFragment();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            fragment.setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.fragment_enter_profile_transition));
            fragment.setReturnTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.fragment_return_profile_transition));
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean(AutoStartChangePhotoBinder.AUTO_OPEN_CHANGE_PHOTO, true);
        fragment.setArguments(bundle);
        getActivity().start(fragment);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onProfileUpdatedEvent(ProfileUpdatedEvent event) {
        stopWithResult();
    }

    private void stopWithResult() {
        Intent returnIntent = new Intent();
        getActivity().setResult(Activity.RESULT_OK, returnIntent);
        getActivity().finish();
    }
}
