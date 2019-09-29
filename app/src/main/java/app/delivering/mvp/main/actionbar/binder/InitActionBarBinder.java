package app.delivering.mvp.main.actionbar.binder;

import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.core.login.check.entity.GuestUserException;
import app.delivering.component.BaseActivity;
import app.delivering.component.bar.adapter.BarsViewPagerAdapter;
import app.delivering.component.bar.detail.transformers.DepthPageTransformer;
import app.delivering.component.bar.lists.floating.CustomFloatingButton;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.main.actionbar.presenter.GetInitialAccountMarkPresenter;
import app.delivering.mvp.main.init.events.UpdateMainActionBarEvent;
import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;

public class InitActionBarBinder extends BaseBinder {
    private BarsViewPagerAdapter adapter;
    @BindView(R.id.toolbar) Toolbar toolBar;
    @BindView(R.id.bar_list_view_pager) ViewPager viewPagerBarLis;
    @BindView(R.id.tab_layout) TabLayout tabLayout;
    @BindView(R.id.to_map_floating_button) CustomFloatingButton floatingButton;
    private final GetInitialAccountMarkPresenter presenter;
    private boolean isListState;

    public InitActionBarBinder(BaseActivity activity) {
        super(activity);
        presenter = new GetInitialAccountMarkPresenter(activity);
    }

    @Override public void afterViewsBounded() {
        adapter = new BarsViewPagerAdapter(getActivity());
        viewPagerBarLis.setPageTransformer(true, new DepthPageTransformer());
        getActivity().setSupportActionBar(toolBar);
        getActivity().getSupportActionBar().setTitle("");
        getActivity().getSupportActionBar().setHomeAsUpIndicator(R.drawable.inset_account_white);
        getActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(android.R.color.transparent)));
        viewPagerBarLis.setAdapter(adapter);
        viewPagerBarLis.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPagerBarLis);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onAuthorizationCompleted(UpdateMainActionBarEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        isListState = isListState();
        presenter.process()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateAccountMarker, e->{
                    if (e instanceof GuestUserException)
                        updateAccountMarker(true);
                    e.printStackTrace();
                    }, ()->{});
    }

    private void updateAccountMarker(Boolean isVerified) {
        if(toolBar != null)
        toolBar.setNavigationIcon(isListState ? getMarkForListState(isVerified) : getMarkForMapState(isVerified));
    }

    private boolean isListState() {
        if (floatingButton == null) return true;
        else
            return TextUtils.isEmpty(floatingButton.getContentDescription()) || !floatingButton.getText().equals(getString(R.string.word_map));
    }

    private int getMarkForListState(Boolean isVerified) {
        return isVerified ? R.drawable.inset_account_white : R.drawable.layer_account_attention;
    }

    private int getMarkForMapState(Boolean isVerified) {
        return isVerified ? R.drawable.inset_account_black : R.drawable.layer_account_attention_black;
    }

}
