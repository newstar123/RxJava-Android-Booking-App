package app.delivering.component.tab;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.service.checkin.TabStatusForegroundService;
import app.delivering.component.verify.VerifyPhoneNumberActivity;
import app.delivering.mvp.coach.tab.check.binder.TabCoachMarkBinder;
import app.delivering.mvp.main.service.feedback.events.CheckOutEvent;
import app.delivering.mvp.tab.advert.binder.AdvertBinder;
import app.delivering.mvp.tab.button.close.binder.CloseButtonBinder;
import app.delivering.mvp.tab.button.uber.binder.TabUberCallBinder;
import app.delivering.mvp.tab.discount.binder.TabDiscountInitBinder;
import app.delivering.mvp.tab.init.binder.TabInitBinder;
import app.delivering.mvp.tab.init.events.PauseTabEvent;
import app.delivering.mvp.tab.init.events.StartTabEvent;
import app.delivering.mvp.tab.init.events.StopTabEvent;
import app.delivering.mvp.tab.init.model.InitialTabActivityModel;
import app.delivering.mvp.tab.summary.binder.TabButtonsBinder;
import app.delivering.mvp.tab.tip.init.binder.TipsInitBinder;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class TabActivity extends BaseActivity {
    public static final int INSTALL_UBER_KEY = 19191;
    public static final String TAB_INITIAL_MODEL = "component.tab.tab_initial_model";
    public static final String TAB_BAR_NAME = "tab_bar_name";
    public static final String TAB_CHECK_IN_ID = "tab_check_in_id";
    public static final String TAB_BAR_ID = "tab_bar_id";
    private CloseButtonBinder closeButtonBinder;
    private TabUberCallBinder uberCallBinder;


    public static void launch(Context context, @Nullable InitialTabActivityModel model) {
        Intent intent = new Intent(context, TabActivity.class);
        context.startActivity(model == null ? intent : fillExtras(intent, model));
    }

    private static Intent fillExtras(Intent intent, InitialTabActivityModel model) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(TAB_INITIAL_MODEL, model);
        intent.putExtra(TAB_INITIAL_MODEL, bundle);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        if (getIntent().getExtras() == null || getInitialTabModel().getCheckInId() == 0)
            Observable.zip(Observable.just(QorumSharedCache.checkSharedCheckInId().get(BaseCacheType.LONG)),
                    Observable.just((long)QorumSharedCache.checkBarCacheId().get(BaseCacheType.LONG)), (checkInID, barID) -> {
                        InitialTabActivityModel model = new InitialTabActivityModel();
                        model.setCheckInId((long)checkInID);
                        model.setBarId(barID);
                        return model;
                    })
                    .doOnNext(this::isCheckAlreadyCleaned)
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(bindUntilEvent(ActivityEvent.STOP))
                    .subscribe(this::prepareTab, e -> {
                        EventBus.getDefault().postSticky(new CheckOutEvent());
                        onBackPressed();
                    }, () -> {});
        else {
            init();
            startForeground();
        }
    }

    private void isCheckAlreadyCleaned(InitialTabActivityModel model) {
        if (model.getCheckInId() == 0) throw new RuntimeException();
    }

    private void prepareTab(InitialTabActivityModel initialModel) {
        setIntent(fillExtras(getIntent(), initialModel));
        init();
        startForeground();
    }

    private void startForeground() {
        long checkInId = getIntent().getLongExtra(TAB_CHECK_IN_ID, 0);
        Intent startIntent = new Intent(this, TabStatusForegroundService.class);
        startIntent.setAction(TabStatusForegroundService.START_FOREGROUND_ACTION);
        startIntent.putExtra(TAB_CHECK_IN_ID, checkInId);
        startService(startIntent);
    }

    private void init() {
        AdvertBinder advertBinder = new AdvertBinder(this);
        addToEventBusAndViewInjection(advertBinder);
        TabInitBinder initBinder = new TabInitBinder(this);
        addToEventBusAndViewInjection(initBinder);
        closeButtonBinder = new CloseButtonBinder(this);
        addToEventBusAndViewInjection(closeButtonBinder);
        uberCallBinder = new TabUberCallBinder(this);
        addToEventBusAndViewInjection(uberCallBinder);
        TabDiscountInitBinder tabRoundInitBinder = new TabDiscountInitBinder(this);
        addToEventBusAndViewInjection(tabRoundInitBinder);
        TabButtonsBinder tabButtonsBinder = new TabButtonsBinder(this);
        addToEventBusAndViewInjection(tabButtonsBinder);
        TabCoachMarkBinder tabCoachMarkBinder = new TabCoachMarkBinder(this);
        addItemForViewsInjection(tabCoachMarkBinder);
        TipsInitBinder tipsInitBinder = new TipsInitBinder(this);
        addToEventBusAndViewInjection(tipsInitBinder);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().post(new StartTabEvent());
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().post(new StopTabEvent());
        super.onStop();
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().post(new PauseTabEvent());
        super.onPause();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.animation_stay, R.anim.animation_slide_to_down);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VerifyPhoneNumberActivity.PHONE_VERIFICATION_REQUEST && resultCode == Activity.RESULT_OK)
            closeButtonBinder.close();
        if (resultCode == VerifyPhoneNumberActivity.PHONE_VERIFICATION_EMPTY_RESULT)
            closeButtonBinder.showPhoneVerificationDialog();
        if (requestCode == VerifyPhoneNumberActivity.PHONE_VERIFICATION_REQUEST_FOR_UBER && resultCode == Activity.RESULT_OK)
            uberCallBinder.uberCall();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public InitialTabActivityModel getInitialTabModel() {
        return getIntent().getBundleExtra(TAB_INITIAL_MODEL) == null ? new InitialTabActivityModel()
                : getIntent().getBundleExtra(TAB_INITIAL_MODEL).getParcelable(TabActivity.TAB_INITIAL_MODEL);
    }
}
