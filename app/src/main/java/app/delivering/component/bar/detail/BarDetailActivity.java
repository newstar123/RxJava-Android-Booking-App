package app.delivering.component.bar.detail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.transition.TransitionInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;

import org.greenrobot.eventbus.EventBus;

import app.R;
import app.delivering.component.authenticator.AccountAuthenticatorActivity;
import app.delivering.component.bar.detail.receiver.ActivitiesBackStackReceiver;
import app.delivering.component.payment.add.AddPaymentActivity;
import app.delivering.mvp.bars.detail.actionbar.binder.BarDetailActionBarBinder;
import app.delivering.mvp.bars.detail.actionbar.events.EnableBarViewsEvent;
import app.delivering.mvp.bars.detail.checkin.click.binder.BarDetailCheckinClickButtonBinder;
import app.delivering.mvp.bars.detail.checkin.click.events.OpenTabEvent;
import app.delivering.mvp.bars.detail.checkin.open.binder.BarDetailCheckinOpenTabBinder;
import app.delivering.mvp.bars.detail.checkin.signup.binder.SignUpFromBarDetailBinder;
import app.delivering.mvp.bars.detail.checkin.view.binder.BarDetailCheckinViewTabBinder;
import app.delivering.mvp.bars.detail.fullscreen.images.video.start.binder.AboutBarClickControlsBinder;
import app.delivering.mvp.bars.detail.init.action.binder.BarDetailActionButtonBinder;
import app.delivering.mvp.bars.detail.init.action.click.binder.BarDetailClickActionButtonBinder;
import app.delivering.mvp.bars.detail.init.call.binder.BarDetailCallBinder;
import app.delivering.mvp.bars.detail.init.content.binder.BarDetailContentBinder;
import app.delivering.mvp.bars.detail.init.friends.init.binder.BarDetailCheckInFriendsBinder;
import app.delivering.mvp.bars.detail.init.get.binder.GetBarDetailBinder;
import app.delivering.mvp.bars.detail.init.get.events.BarDetailStartEvent;
import app.delivering.mvp.bars.detail.init.map.binder.BarDetailMapBinder;
import app.delivering.mvp.bars.detail.init.menu.button.binder.BarDetailMenuBinder;
import app.delivering.mvp.bars.detail.init.model.InitialVenueDetailModel;
import app.delivering.mvp.bars.detail.init.tablist.height.binder.BarDetailTabsAndListContainerBinder;
import app.delivering.mvp.bars.detail.init.tablist.tab.binder.BarDetailTabsBinder;
import app.delivering.mvp.bars.detail.init.toolbar.configuration.binder.BarDetailConfigurationBinder;
import app.delivering.mvp.bars.detail.init.toolbar.configuration.events.OnConfigurationChangedEvent;
import app.delivering.mvp.bars.detail.init.toolbar.init.binder.BarDetailToolBarBinder;
import app.delivering.mvp.bars.detail.init.ubercall.call.binder.BarDetailUberCallBinder;
import app.delivering.mvp.coach.checkin.binder.FirstCheckInCoachMarkBinder;
import app.delivering.mvp.coach.login.check.binder.CheckLoginCoachMarkBinder;
import app.delivering.mvp.main.service.feedback.binder.ShowFeedbackBinder;
import app.gateway.facebook.FacebookCallbackManagerInstance;

import static app.delivering.component.bar.detail.receiver.ActivitiesBackStackReceiver.REMOVE_BAR_DETAIL_ACTIVITY_INTENT;

public class BarDetailActivity extends AccountAuthenticatorActivity {
    private final static String VENUE_DETAIL_INITIAL_MODEL = ".component.bar.detail.VENUE_DETAIL_INITIAL_MODEL";
    private final static String BUNDLE_KEY_MAP_STATE = "BUNDLE_KEY_MAP_STATE_DATA";
    private MapView mapView;
    private BarDetailConfigurationBinder configurationBinder;
    private ActivitiesBackStackReceiver activitiesBackStackReceiver;


    public static Intent getIntentWithExtras(Context context, InitialVenueDetailModel model) {
        Intent intent = new Intent(context, BarDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(VENUE_DETAIL_INITIAL_MODEL, model);
        intent.putExtra(VENUE_DETAIL_INITIAL_MODEL, bundle);
        return intent;
    }

    public static Intent updateIntent(Intent intent, InitialVenueDetailModel model) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(VENUE_DETAIL_INITIAL_MODEL, model);
        intent.putExtra(VENUE_DETAIL_INITIAL_MODEL, bundle);
        return intent;
    }

    public InitialVenueDetailModel getInitialModel(){
        return getIntent().getBundleExtra(VENUE_DETAIL_INITIAL_MODEL) == null ? new InitialVenueDetailModel()
                : getIntent().getBundleExtra(VENUE_DETAIL_INITIAL_MODEL).getParcelable(VENUE_DETAIL_INITIAL_MODEL);
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.scene_hide_transition));
            getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.shared_to_bar_detail_transition));
        }
        setContentView(R.layout.activity_bar_detail);
        mapView = findViewById(R.id.bar_detail_map_container);
        Bundle mapState = null;
        if (savedInstanceState != null)
            mapState = savedInstanceState.getBundle(BUNDLE_KEY_MAP_STATE);
        mapView.onCreate(mapState);
        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        initViews();
        activitiesBackStackReceiver = new ActivitiesBackStackReceiver(this);
        registerBroadcastReceiver();

    }

    private void registerBroadcastReceiver() {
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(activitiesBackStackReceiver,
                        new IntentFilter(REMOVE_BAR_DETAIL_ACTIVITY_INTENT));
    }

    private void initViews() {
        BarDetailActionBarBinder actionBarBinder = new BarDetailActionBarBinder(this);
        addToEventBusAndViewInjection(actionBarBinder);
        GetBarDetailBinder getBarDetailBinder = new GetBarDetailBinder(this);
        addToEventBusAndViewInjection(getBarDetailBinder);
        BarDetailToolBarBinder barDetailToolBarBinder = new BarDetailToolBarBinder(this);
        addToEventBusAndViewInjection(barDetailToolBarBinder);
        BarDetailMapBinder barDetailMapBinder = new BarDetailMapBinder(this);
        addToEventBusAndViewInjection(barDetailMapBinder);
        BarDetailContentBinder detailContentBinder = new BarDetailContentBinder(this);
        addToEventBusAndViewInjection(detailContentBinder);
        BarDetailCallBinder callBinder = new BarDetailCallBinder(this);
        addToEventBusAndViewInjection(callBinder);
        BarDetailUberCallBinder uberCallBinder = new BarDetailUberCallBinder(this);
        addToEventBus(uberCallBinder);
        BarDetailTabsBinder featuresTabsBinder = new BarDetailTabsBinder(this);
        addItemForViewsInjection(featuresTabsBinder);
        BarDetailMenuBinder menuBinder = new BarDetailMenuBinder(this);
        addToEventBusAndViewInjection(menuBinder);
        BarDetailTabsAndListContainerBinder tabsAndListContainerBinder = new BarDetailTabsAndListContainerBinder(this);
        addToEventBusAndViewInjection(tabsAndListContainerBinder);
        BarDetailCheckInFriendsBinder checkInFriendsBinder = new BarDetailCheckInFriendsBinder(this);
        addToEventBusAndViewInjection(checkInFriendsBinder);
        BarDetailCheckinClickButtonBinder checkinClickButtonBinder = new BarDetailCheckinClickButtonBinder(this);
        addToEventBusAndViewInjection(checkinClickButtonBinder);
        BarDetailCheckinOpenTabBinder checkinOpenTabBinder = new BarDetailCheckinOpenTabBinder(this);
        addToEventBus(checkinOpenTabBinder);
        BarDetailCheckinViewTabBinder checkinViewTabBinder = new BarDetailCheckinViewTabBinder(this);
        addToEventBus(checkinViewTabBinder);
        ShowFeedbackBinder feedbackBinder = new ShowFeedbackBinder(this);
        addToEventBus(feedbackBinder);
        CheckLoginCoachMarkBinder checkLoginCoachMarkBinder = new CheckLoginCoachMarkBinder(this);
        addToEventBusAndViewInjection(checkLoginCoachMarkBinder);
        FirstCheckInCoachMarkBinder firstCheckInCoachMarkBinder = new FirstCheckInCoachMarkBinder(this);
        addToEventBus(firstCheckInCoachMarkBinder);
        AboutBarClickControlsBinder startVideoBinder = new AboutBarClickControlsBinder(this);
        addToEventBus(startVideoBinder);
        configurationBinder = new BarDetailConfigurationBinder(this);
        addToEventBusAndViewInjection(configurationBinder);
        SignUpFromBarDetailBinder tabSignUpBinder = new SignUpFromBarDetailBinder(this);
        addToEventBusAndViewInjection(tabSignUpBinder);
        BarDetailActionButtonBinder actionButtonBinder = new BarDetailActionButtonBinder(this);
        addToEventBusAndViewInjection(actionButtonBinder);
        BarDetailClickActionButtonBinder clickActionButtonBinder = new BarDetailClickActionButtonBinder(this);
        addItemForViewsInjection(clickActionButtonBinder);
    }

    @Override public void onConfigurationChanged(Configuration newConfig) {
        EventBus.getDefault().post(new OnConfigurationChangedEvent(newConfig));
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        EventBus.getDefault().post(new BarDetailStartEvent());
    }

    @Override protected void onStart() {
        super.onStart();
        EventBus.getDefault().post(new BarDetailStartEvent());
        if (mapView != null)
            mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null)
            mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null)
            mapView.onPause();
    }

    @Override public void onStop() {
        super.onStop();
        if (mapView != null)
            mapView.onStop();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus)
            EventBus.getDefault().postSticky(new EnableBarViewsEvent());
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        Bundle mapState = new Bundle();
        if (mapView != null)
            mapView.onSaveInstanceState(mapState);
        outState.putBundle(BUNDLE_KEY_MAP_STATE, mapState);
        super.onSaveInstanceState(outState);
    }

    @Override public void finishAfterTransition() {
        super.finishAfterTransition();
    }

    @Override
    public void onDestroy() {
        if (mapView != null)
            mapView.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(activitiesBackStackReceiver);
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null)
            mapView.onLowMemory();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0)
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                else
                    checkFinish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onBackPressed() {
        if (configurationBinder.isExpandState())
            configurationBinder.onBackForExpandState();
        else
            super.onBackPressed();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddPaymentActivity.PAYMENT_ADDED_CODE && resultCode == Activity.RESULT_OK) {
            OpenTabEvent event = new OpenTabEvent();
            event.setCurrentBarId(getInitialModel().getBarId());
            EventBus.getDefault().postSticky(event);
        }
        FacebookCallbackManagerInstance.get().onActivityResult(requestCode, resultCode, data);
    }

    private void checkFinish() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
    }
}
