package app.delivering.mvp.bars.detail.actionbar.binder;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.bar.detail.BarDetailActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.detail.actionbar.events.EnableBarViewsEvent;
import app.delivering.mvp.bars.detail.actionbar.presenter.BranchOptionsPresenter;
import app.delivering.mvp.bars.detail.init.get.model.BarDetailModel;
import app.delivering.mvp.bars.detail.init.toolbar.video.events.OnStopPlayerEvent;
import butterknife.BindView;
import butterknife.OnClick;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.util.LinkProperties;
import rx.android.schedulers.AndroidSchedulers;

public class BarDetailActionBarBinder extends BaseBinder {
    @BindView(R.id.bar_detail_title) TextView title;
    @BindView(R.id.bar_detail_app_bar) AppBarLayout appBarLayout;
    @BindView(R.id.bar_detail_invite) View invite;
    private final BranchOptionsPresenter branchOptionsPresenter;
    private BarDetailModel barDetailModel;


    public BarDetailActionBarBinder(BaseActivity activity) {
        super(activity);
        branchOptionsPresenter = new BranchOptionsPresenter(activity);
    }

    @Override public void afterViewsBounded() {
        title.setText(((BarDetailActivity)getActivity()).getInitialModel().getNameValue());
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> updateAppBarState(verticalOffset));

        invite.setOnClickListener(listener -> {
            invite.setEnabled(false);
            branchOptionsPresenter.process(barDetailModel)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::showShareSheet, this::onError, () -> { });
        });
    }

    private void onError(Throwable throwable) {
        invite.setEnabled(true);
        throwable.printStackTrace();
    }

    private void updateAppBarState(int verticalOffset) {
        if (Math.abs(verticalOffset) > getActivity().getResources().getDimensionPixelOffset(R.dimen.dip210))
            EventBus.getDefault().post(new OnStopPlayerEvent());
    }

    @OnClick(R.id.bar_detail_back_button) void back(){
        getActivity().finishAfterTransition();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void resetViewsState(EnableBarViewsEvent enableViewsEvent) {
        EventBus.getDefault().removeStickyEvent(enableViewsEvent);
        invite.setEnabled(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getBarDetailModel(BarDetailModel barDetailModel) {
        this.barDetailModel = barDetailModel;
    }

    private void showShareSheet(Pair<BranchUniversalObject, LinkProperties> pair) {
        pair.first.generateShortUrl(getActivity(), pair.second, (url, error) -> {
            if (error == null) {
                Log.i("BRANCH SDK", "got my Branch link to share: " + url);
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, getInfoForSharing(url));
                shareIntent.setType("text/plain");
                getActivity().startActivity(Intent.createChooser(shareIntent, "Share"));
            }
            else
                Log.i("BRANCH error", error.getMessage());
        });
    }

    private String getInfoForSharing(String url) {
        return "check out " + barDetailModel.getBarModel().getName() + " with Qorum\n" + url;
    }
}
