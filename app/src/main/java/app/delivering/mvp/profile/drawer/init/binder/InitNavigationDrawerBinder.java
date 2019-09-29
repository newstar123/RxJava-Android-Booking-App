package app.delivering.mvp.profile.drawer.init.binder;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.transition.TransitionInflater;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.payment.list.PaymentsActivity;
import app.delivering.component.profile.ProfileFragment;
import app.delivering.component.settings.SettingsFragment;
import app.delivering.component.zendesk.ZendeskActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.profile.drawer.init.events.EnableNavigationDrawerViewsEvent;
import app.qamode.component.QaModeActivity;
import butterknife.BindView;
import butterknife.OnClick;

public class InitNavigationDrawerBinder extends BaseBinder  {
    @BindView(R.id.navigation) NavigationView headerView;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.account_root_layout) View account;
    @BindView(R.id.support_root_layout) View support;
    @BindView(R.id.payment_root_layout) View payment;
    @BindView(R.id.settings_root_layout) View settings;

    public InitNavigationDrawerBinder(BaseActivity activity) {
        super(activity);
    }

    @Override public void afterViewsBounded() {
        headerView.setItemIconTintList(null);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            account.setEnabled(true);
            support.setEnabled(true);
        }
    }

    @OnClick(R.id.back_arrow) public void closeNavigationLayout() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
    }

    @OnClick(R.id.support_root_layout) public void onClickSupport() {
        startNewActivityWithAnim(ZendeskActivity.class);
    }

    @OnClick(R.id.account_root_layout) public void openAccount() {
        startNewFragmentWithAnim(new ProfileFragment());
    }

    @OnClick(R.id.payment_root_layout) public void openPayment() {
        startNewActivityWithAnim(PaymentsActivity.class);
    }

    @OnClick(R.id.settings_root_layout) public void openSettings() {
        startNewFragmentWithAnim(new SettingsFragment());
    }

    @OnClick(R.id.qa_mode_root_layout) public void openQaMode() {
        getActivity().startActivity(new Intent(getActivity(), QaModeActivity.class));
        getActivity().overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_stay);
        closeNavigationLayout();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void resetViewsState(EnableNavigationDrawerViewsEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            support.setEnabled(true);
            account.setEnabled(true);
        }
    }

    private void startNewFragmentWithAnim(Fragment fragment) {
        fragment.setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.fragment_enter_profile_transition));
        fragment.setReturnTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.fragment_return_profile_transition));
        getActivity().add(fragment);
    }

    private void startNewActivityWithAnim(Class<?> newActivity) {
        getActivity().startActivity(new Intent(getActivity(), newActivity));
        getActivity().overridePendingTransition(R.anim.animation_left_to_right, R.anim.animation_stay);
    }

}
