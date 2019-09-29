package app.delivering.component.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;

import app.CustomApplication;
import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.bar.map.BarMapFragment;
import app.delivering.component.verify.VerifyPhoneNumberActivity;
import app.delivering.mvp.bars.floating.BarListFloatingButtonBinder;
import app.delivering.mvp.bars.floating.events.ClickListMapButtonEvent;
import app.delivering.mvp.bars.list.freeride.binder.FreeRideMarkBinder;
import app.delivering.mvp.bars.list.item.branchclick.binder.BarListItemClickBranchBinder;
import app.delivering.mvp.bars.list.refresh.binder.RefreshBarListBinder;
import app.delivering.mvp.main.nearest.NearestMarketBinder;
import app.delivering.mvp.main.show.binder.SearchBarsByCityBinder;
import app.delivering.mvp.main.show.model.CitiesModel;
import app.delivering.mvp.log.firebase.binder.MainInitFirebaseLogBinder;
import app.delivering.mvp.main.actionbar.binder.InitActionBarBinder;
import app.delivering.mvp.main.coach.binder.CheckCoachMarkBinder;
import app.delivering.mvp.main.init.binder.MainInitBinder;
import app.delivering.mvp.main.init.events.OnStartMainActivityEvent;
import app.delivering.mvp.main.photo.facebook.binder.RestoreFBPhotoBinder;
import app.delivering.mvp.main.photo.init.binder.ChangePhotoBinder;
import app.delivering.mvp.main.service.beacon.binder.StartBarNearbyServiceBinder;
import app.delivering.mvp.main.service.checkin.binder.StartCheckActiveCheckInBinder;
import app.delivering.mvp.main.service.feedback.binder.ShowFeedbackBinder;
import app.delivering.mvp.main.service.init.binder.ActivateRootServicesBinder;
import app.delivering.mvp.profile.age.binder.InitialCheckAgeBinder;
import app.delivering.mvp.profile.drawer.close.binder.CloseNavigationDrawerBinder;
import app.delivering.mvp.profile.drawer.init.binder.InitNavigationDrawerBinder;
import app.delivering.mvp.profile.drawer.init.events.EnableNavigationDrawerViewsEvent;
import app.delivering.mvp.profile.drawer.init.events.OpenNavigationDrawerEvent;
import app.delivering.mvp.profile.drawer.logout.binder.NavigationDrawerLogOutBinder;
import app.delivering.mvp.profile.drawer.open.binder.OpenNavigationDrawerBinder;
import app.gateway.location.settings.GoogleLocationSettingsGateway;

public class MainActivity extends BaseActivity {
    private ChangePhotoBinder changePhotoBinder;
    private MainInitBinder mainInitBinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementExitTransition(TransitionInflater.from(this).inflateTransition(R.transition.shared_to_bar_detail_transition));
        }
        setContentView(R.layout.activity_main);
        initUseCases();
    }

    private void initUseCases() {
        mainInitBinder = new MainInitBinder(this);
        addToEventBusAndViewInjection(mainInitBinder);

        BarListItemClickBranchBinder barListItemClickBranchBinder = new BarListItemClickBranchBinder(this);
        addToEventBus(barListItemClickBranchBinder);
        CheckCoachMarkBinder checkCoachMarkBinder = new CheckCoachMarkBinder(this);
        addToEventBus(checkCoachMarkBinder);
        InitActionBarBinder actionBarBinder = new InitActionBarBinder(this);
        addToEventBusAndViewInjection(actionBarBinder);
        OpenNavigationDrawerBinder openNavigationDrawerBinder = new OpenNavigationDrawerBinder(this);
        addToEventBusAndViewInjection(openNavigationDrawerBinder);
        changePhotoBinder = new ChangePhotoBinder(this);
        addToEventBusAndViewInjection(changePhotoBinder);
        InitNavigationDrawerBinder drawerBinder = new InitNavigationDrawerBinder(this);
        addToEventBusAndViewInjection(drawerBinder);
        CloseNavigationDrawerBinder closeNavigationDrawerBinder = new CloseNavigationDrawerBinder(this);
        addToEventBusAndViewInjection(closeNavigationDrawerBinder);
        NavigationDrawerLogOutBinder logOutBinder = new NavigationDrawerLogOutBinder(this);
        addToEventBusAndViewInjection(logOutBinder);
        SearchBarsByCityBinder searchBarsByCityBinder = new SearchBarsByCityBinder(this);
        addToEventBusAndViewInjection(searchBarsByCityBinder);
        BarListFloatingButtonBinder barListFloatingButtonBinder = new BarListFloatingButtonBinder(this);
        addToEventBusAndViewInjection(barListFloatingButtonBinder);
        RefreshBarListBinder refreshBarListBinder = new RefreshBarListBinder(this);
        addToEventBus(refreshBarListBinder);
        ActivateRootServicesBinder rootServicesBinder = new ActivateRootServicesBinder(this);
        addToEventBus(rootServicesBinder);
        StartCheckActiveCheckInBinder checkActiveCheckInBinder = new StartCheckActiveCheckInBinder(this);
        addToEventBus(checkActiveCheckInBinder);
        StartBarNearbyServiceBinder beaconsServiceBinder = new StartBarNearbyServiceBinder(this);
        addToEventBus(beaconsServiceBinder);
        ShowFeedbackBinder showFeedbackBinder = new ShowFeedbackBinder(this);
        addToEventBus(showFeedbackBinder);
        MainInitFirebaseLogBinder initFirebaseLogBinder = new MainInitFirebaseLogBinder(this);
        addToEventBus(initFirebaseLogBinder);
        FreeRideMarkBinder freeRideMarkBinder = new FreeRideMarkBinder(this);
        addToEventBusAndViewInjection(freeRideMarkBinder);
        RestoreFBPhotoBinder restoreFBPhotoBinder = new RestoreFBPhotoBinder(this);
        addToEventBusAndViewInjection(restoreFBPhotoBinder);
        NearestMarketBinder nearestMarketBinder = new NearestMarketBinder(this);
        addToEventBusAndViewInjection(nearestMarketBinder);
    }

    @Override protected void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().post(new OnStartMainActivityEvent());
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().postSticky(new EnableNavigationDrawerViewsEvent());
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isValidRequestCode(requestCode) && resultCode == Activity.RESULT_OK)
            EventBus.getDefault().post(new OnStartMainActivityEvent());
        if (requestCode == VerifyPhoneNumberActivity.PHONE_VERIFICATION_REQUEST)
            EventBus.getDefault().post(new OnStartMainActivityEvent());
        changePhotoBinder.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isValidRequestCode(int requestCode) {
        return GoogleLocationSettingsGateway.CHECK_GPS_ACTIVE == requestCode ||
                InitialCheckAgeBinder.AGE_CHECKING == requestCode;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0 && isOpenMapFragment())
            EventBus.getDefault().post(new ClickListMapButtonEvent());
        else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_gift, menu);
        menu.findItem(R.id.action_gift).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0 && !isOpenMapFragment())
                    getSupportFragmentManager().popBackStack();
                else
                    EventBus.getDefault().post(new OpenNavigationDrawerEvent());
                return true;
            }
        return super.onOptionsItemSelected(item);
    }

    private boolean isOpenMapFragment() {
        int index = getSupportFragmentManager().getBackStackEntryCount() - 1;
        FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(index);
        return backEntry.getName().equals(BarMapFragment.class.getSimpleName());
    }

    @Override
    protected void onDestroy() {
        mainInitBinder.onDestroy();
        EventBus.getDefault().removeStickyEvent(CitiesModel.class);
        CustomApplication.get().getMixpanelInstance().flush();
        Log.d("LaunchQ", "________________________________________________________________");
        super.onDestroy();
    }
}
