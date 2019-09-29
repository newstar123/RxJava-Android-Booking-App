package app.delivering.mvp.starttour.init.binder;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Button;

import com.rd.PageIndicatorView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.bar.detail.transformers.ZoomOutPageTransformer;
import app.delivering.component.starttour.pager.StartTourPagerAdapter;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.starttour.init.events.OnBackgroundLoadedEvent;
import app.delivering.mvp.starttour.init.presenter.StartTourPagerPresenter;
import app.gateway.analytics.mixpanel.MixpanelSendGateway;
import app.gateway.analytics.mixpanel.events.MixpanelEvents;
import butterknife.BindView;
import butterknife.OnClick;

public class StartTourPagerBinder extends BaseBinder {
    private static final int START_TOUR_SCREENS_NUMBER = 4;
    private StartTourPagerAdapter pagerAdapter;
    @BindView(R.id.start_tour_view_pager) ViewPager viewPager;
    @BindView(R.id.start_tour_spring_indicator) PageIndicatorView springIndicator;
    @BindView(R.id.start_tour_next_button) Button nextButton;
    private StartTourPagerPresenter presenter;
    private int position;

    public StartTourPagerBinder(BaseActivity activity) {
        super(activity);
        presenter = new StartTourPagerPresenter(activity);
    }

    @Override public void afterViewsBounded() {
        pagerAdapter = new StartTourPagerAdapter(getActivity());
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setPosition(position);
                viewPager.setCurrentItem(position, true);
                presenter.process(position).subscribe(integer -> {}, e->{}, ()->{});
            }
        });
        springIndicator.setViewPager(viewPager);

        MixpanelSendGateway.sendWithSubscription(MixpanelEvents.getTutorialEvent(0));
    }

    @OnClick(R.id.start_tour_next_button) void onNext() {
        if (pagerAdapter != null && position + 1 < START_TOUR_SCREENS_NUMBER)
            viewPager.setCurrentItem(position + 1, true);
        else
            getActivity().finish();
    }

    public void setPosition(int position) {
        this.position = position;

        MixpanelSendGateway.sendWithSubscription(MixpanelEvents.getTutorialEvent(position));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateBackground(OnBackgroundLoadedEvent event) {
        Log.d("CITY", "OnBackgroundLoadedEvent");
        getActivity().loadCityImage();
    }
}
