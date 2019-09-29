package app.delivering.mvp.profile.drawer.open.binder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import app.R;
import app.core.login.check.entity.GuestUserException;
import app.core.profile.get.entity.ProfileModel;
import app.delivering.component.BaseActivity;
import app.delivering.component.main.photo.PhotoChooseDialogFragment;
import app.delivering.component.verify.VerifyEmailActivity;
import app.delivering.component.verify.VerifyPhoneNumberActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.detail.checkin.open.events.UpdatePhotoEvent;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.profile.drawer.init.events.OpenNavigationDrawerEvent;
import app.delivering.mvp.profile.drawer.model.NavigationDrawerInitModel;
import app.delivering.mvp.profile.drawer.open.presenter.NavigationDrawerProfilePresenter;
import app.delivering.mvp.profile.edit.init.events.OnResumeProfileModelEvent;
import app.delivering.mvp.verify.events.VerificationEvent;
import app.gateway.auth.AndroidAccountTokenInvalidator;
import app.qamode.qacache.QaModeCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import butterknife.BindView;
import butterknife.OnClick;
import jp.wasabeef.picasso.transformations.BlurTransformation;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class OpenNavigationDrawerBinder extends BaseBinder {
    @BindView(R.id.navigation) NavigationView headerView;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.drawer_header_photo) ImageView headerPhoto;
    @BindView(R.id.drawer_header_number) TextView number;
    @BindView(R.id.drawer_header_name) TextView name;
    @BindView(R.id.navigation_body) LinearLayout navigationContainer;
    @BindView(R.id.root_account_verification) LinearLayout verificationContainer;
    @BindView(R.id.phone_verification) TextView phoneVerification;
    @BindView(R.id.mail_verification) TextView emailVerification;
    @BindView(R.id.qa_mode_root_layout) View qaModeButton;
    private final NavigationDrawerProfilePresenter presenter;
    private final InitExceptionHandler initExceptionHandler;
    private ProfileModel initProfileModel;


    private Target target = new Target() {
        @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Drawable cityBackgroundImage = new BitmapDrawable(getActivity().getResources(), bitmap);
            cityBackgroundImage.setColorFilter(getActivity().getResources().getColor(R.color.color_CC050D24), PorterDuff.Mode.DARKEN);
            if (headerView != null)
                headerView.setBackground(cityBackgroundImage);
        }

        @Override public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    public OpenNavigationDrawerBinder(BaseActivity activity) {
        super(activity);
        initExceptionHandler = new InitExceptionHandler(activity);
        presenter = new NavigationDrawerProfilePresenter(activity);
    }

    @Override
    public void afterViewsBounded() {
        super.afterViewsBounded();
        setUpNavigationViewWidth(headerView);
    }

    private void setUpNavigationViewWidth(NavigationView nv) {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) nv.getLayoutParams();
        params.width = metrics.widthPixels;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void updateModel(OnResumeProfileModelEvent event) {
        EventBus.getDefault().removeStickyEvent(OnResumeProfileModelEvent.class);
        onOpenDrawer(new OpenNavigationDrawerEvent());
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onOpenDrawer(OpenNavigationDrawerEvent event) {
        qaModeButton.setVisibility(QaModeCache.isQaModeActive().get(BaseCacheType.BOOLEAN) ? View.VISIBLE : View.GONE);

        EventBus.getDefault().removeStickyEvent(OpenNavigationDrawerEvent.class);
        presenter.process()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::show, this::showError);
    }

    private void show(NavigationDrawerInitModel model) {
        this.initProfileModel = model.getProfileModel();
        drawerLayout.openDrawer(GravityCompat.START);
        fillingFields(initProfileModel);
        loadCityImage(model.getImageUrl());
        EventBus.getDefault().post(initProfileModel);
    }

    private void loadCityImage(String imageUrl) {
        Picasso.with(getActivity()).load(imageUrl)
                .centerCrop()
                .resize(headerView.getWidth(), headerView.getHeight())
                .transform(new BlurTransformation(getActivity(), 15))
                .into(target);
    }

    private void fillingFields(ProfileModel profileModel) {
        checkVerification(profileModel);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        name.setText(String.format("%s %s", profileModel.getFirstName(), profileModel.getLastName()));
        number.setText(getSavedValue(profileModel.getTotalSaved()));
        Picasso picasso = Picasso.with(getActivity());
        picasso.load(profileModel.getImageUrl())
                .resize(headerPhoto.getMeasuredWidth(), headerPhoto.getMeasuredHeight())
                .error(getActivity().getResources().getDrawable(R.drawable.inset_account_white, null))
                .centerCrop()
                .into(headerPhoto);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getVerificationResult(VerificationEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        checkVerification(initProfileModel);
    }

    private void checkVerification(ProfileModel profileModel) {
        if (profileModel.getIsPhoneVerified() == 0 || profileModel.getIsEmailVerified() == 0) {
            navigationContainer.setVisibility(View.GONE);
            verificationContainer.setVisibility(View.VISIBLE);
            if (profileModel.getIsPhoneVerified() == 1) phoneVerification.setVisibility(View.GONE);
            if (profileModel.getIsEmailVerified() == 1) emailVerification.setVisibility(View.GONE);
        } else {
            navigationContainer.setVisibility(View.VISIBLE);
            verificationContainer.setVisibility(View.GONE);
        }
    }

    private Spanned getSavedValue(int savedCoins) {
        BigDecimal unscaled = new BigDecimal(savedCoins);
        BigDecimal scaled = unscaled.scaleByPowerOfTen(-2);
        return formatHtmlToSpanned(String.format(getString(R.string.you_have_saved), scaled));
    }

    private void showError(Throwable throwable) {
        if (throwable instanceof GuestUserException) {
            AndroidAccountTokenInvalidator.invalidateToken()
                    .subscribe(o -> onOpenDrawer(new OpenNavigationDrawerEvent()), e->{}, ()->{});
        } else
            initExceptionHandler.showError(throwable, view -> onOpenDrawer(new OpenNavigationDrawerEvent()));
    }

    @OnClick(R.id.phone_verification) void verifyByPhoneClick() {
        getActivity().startActivityForResult(new Intent(getActivity(), VerifyPhoneNumberActivity.class),
                VerifyPhoneNumberActivity.PHONE_VERIFICATION_REQUEST);
    }

    @OnClick(R.id.mail_verification) void verifyByEmailClick() {
        getActivity().startActivity(new Intent(getActivity(), VerifyEmailActivity.class));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void updateMissingProfilePhoto(UpdatePhotoEvent event) {
        EventBus.getDefault().removeStickyEvent(UpdatePhotoEvent.class);
        onOpenDrawer(new OpenNavigationDrawerEvent());
        Observable.timer(1L, TimeUnit.SECONDS)
                .subscribe(show -> showPhotoChooseDialog(), err -> {}, () -> {});
    }

    private void showPhotoChooseDialog() {
        PhotoChooseDialogFragment photoChooseDialogFragment = new PhotoChooseDialogFragment();
        photoChooseDialogFragment.setUpOpeningOption(true);
        photoChooseDialogFragment.show(getActivity().getSupportFragmentManager(), "PhotoChooseDialogFragment");
    }
}
