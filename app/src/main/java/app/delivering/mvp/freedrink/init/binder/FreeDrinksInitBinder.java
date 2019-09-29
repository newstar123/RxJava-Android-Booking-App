package app.delivering.mvp.freedrink.init.binder;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.List;

import app.R;
import app.delivering.component.invite.FreeDrinksFragment;
import app.delivering.component.verify.VerifyEmailActivity;
import app.delivering.component.verify.VerifyPhoneNumberActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.freedrink.init.events.UpdateVerificationsEvent;
import app.delivering.mvp.profile.edit.actionbar.clicks.binder.ViewActionSetter;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FreeDrinksInitBinder extends BaseBinder {
    @BindView(R.id.friends_checked_in) TextView friendsCheckined;
    @BindView(R.id.invites_accepted) TextView invites;
    @BindView(R.id.free_drinks_waiting) TextView freeDrinks;
    @BindView(R.id.free_drink_container) View freeDrinksContainer;
    @BindView(R.id.root_account_verification) View rootVerificationLayout;
    @BindView(R.id.phone_verification) TextView verifyByPhoneView;
    @BindView(R.id.mail_verification) TextView verifyByMailView;

    @BindViews({R.id.terms_invites_accepted,
                R.id.terms_friends_checked_in,
                R.id.terms_free_drinks_waiting,
                R.id.terms_conditions}) List<View> afterVerificationViews;

    @BindViews({R.id.invite_with_facebook,
                R.id.white_fb_view,
                R.id.invite_with_email,
                R.id.white_email_view,
                R.id.invite_with_other,
                R.id.white_more_view,
                R.id.invite_with_twitter,
                R.id.white_twitter_view,
                R.id.invite_with_sms,
                R.id.white_text_view}) List<View> socialViews;

    private FreeDrinksFragment fragment;

    public FreeDrinksInitBinder(FreeDrinksFragment fragment) {
        super(fragment.getBaseActivity());
        this.fragment = fragment;
    }

    @Override public void afterViewsBounded() {
        int invited = fragment.getArguments().getInt(FreeDrinksFragment.FREE_DRINKS_INVITED, 0);
        int checkined = fragment.getArguments().getInt(FreeDrinksFragment.FREE_DRINKS_CHECKINED, 0);
        double free = fragment.getArguments().getDouble(FreeDrinksFragment.FREE_DRINKS_WAITING, 0);
        friendsCheckined.setText(String.valueOf(checkined));
        invites.setText(String.valueOf(invited));
        freeDrinks.setText(new DecimalFormat("#.##").format(free));
        updateVerification(new UpdateVerificationsEvent());
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void updateVerification(UpdateVerificationsEvent event) {
        EventBus.getDefault().removeStickyEvent(UpdateVerificationsEvent.class);
        Integer isPhoneVerified = fragment.getArguments().getInt(FreeDrinksFragment.PHONE_VERIFICATION, 0);
        Integer isEmailVerified = fragment.getArguments().getInt(FreeDrinksFragment.EMAIL_VERIFICATION, 0);
        setUpVerificationView(isPhoneVerified != 0, isEmailVerified != 0);
    }

    private void setUpVerificationView(boolean isPhoneVerified, boolean isEmailVerified) {
        if (isPhoneVerified && isEmailVerified) {
            rootVerificationLayout.setVisibility(View.GONE);
            setUpViewEnablingSetter(true);
        }
        if (isPhoneVerified && !isEmailVerified) {
            rootVerificationLayout.setVisibility(View.VISIBLE);
            verifyByPhoneView.setVisibility(View.GONE);
            verifyByMailView.setVisibility(View.VISIBLE);
            setUpViewEnablingSetter(false);
        }
        if (!isPhoneVerified && isEmailVerified) {
            rootVerificationLayout.setVisibility(View.VISIBLE);
            verifyByPhoneView.setVisibility(View.VISIBLE);
            verifyByMailView.setVisibility(View.GONE);
            setUpViewEnablingSetter(false);
        }
        if (!isPhoneVerified && !isEmailVerified) {
            rootVerificationLayout.setVisibility(View.VISIBLE);
            verifyByPhoneView.setVisibility(View.VISIBLE);
            verifyByMailView.setVisibility(View.VISIBLE);
            setUpViewEnablingSetter(false);
        }
    }

    private void setUpViewEnablingSetter(boolean isEnableVisible) {
        if (isEnableVisible) {
            ButterKnife.apply(afterVerificationViews, ViewActionSetter.VISIBLE);
            ButterKnife.apply(socialViews, ViewActionSetter.ENABLE);
        } else {
            ButterKnife.apply(afterVerificationViews, ViewActionSetter.INVISIBLE);
            ButterKnife.apply(socialViews, ViewActionSetter.DISABLE);
        }
    }

    @OnClick(R.id.free_drink_container) void rootClick(){}

    @OnClick(R.id.phone_verification) void verifyByPhoneClick() {
        getActivity().startActivityFromFragment(fragment,
                new Intent(fragment.getContext(), VerifyPhoneNumberActivity.class),
                VerifyPhoneNumberActivity.PHONE_VERIFICATION_REQUEST);
    }

    @OnClick(R.id.mail_verification) void verifyByEmailClick() {
        getActivity().startActivity(new Intent(getActivity(), VerifyEmailActivity.class));
    }
}
