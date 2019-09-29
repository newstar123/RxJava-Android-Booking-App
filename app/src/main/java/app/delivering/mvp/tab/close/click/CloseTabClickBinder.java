package app.delivering.mvp.tab.close.click;

import android.content.Intent;
import android.os.Bundle;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.feedback.FeedbackActivity;
import app.delivering.component.tab.TabActivity;
import app.delivering.component.tab.close.CloseTabActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.tab.close.init.model.FillCloseTabActivityModel;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import butterknife.BindView;
import butterknife.OnClick;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.Observable;

public class CloseTabClickBinder extends BaseBinder {
    @BindView(R.id.close_tab_line_progress) MaterialProgressBar progressBar;
    private final long checkInId;
    private final long vendorId;
    private final boolean isAutoClosed;

    public CloseTabClickBinder(BaseActivity activity) {
        super(activity);
        final FillCloseTabActivityModel model = ((CloseTabActivity) activity).getCloseTabActivityModel();
        checkInId = model.getCheckInId();
        vendorId = model.getVendorId();
        isAutoClosed = model.isTabAutoClosed();
    }

    @OnClick(R.id.tab_closed_home_button) void onBackPressed() {
        getActivity().finish();
        showFeedBack(vendorId, checkInId);
    }

    @OnClick(R.id.close_without_uber) void onRefuseRide() {
        getActivity().finish();
        showFeedBack(vendorId, checkInId);
    }

    private void showFeedBack(long barId, long checkinId) {
        if (!isAutoClosed) {
            Intent intent = new Intent(getActivity(), FeedbackActivity.class);
            Bundle bundle = new Bundle();
            bundle.putLong(TabActivity.TAB_BAR_ID, barId);
            bundle.putLong(TabActivity.TAB_CHECK_IN_ID, checkinId);
            intent.putExtras(bundle);
            getActivity().startActivity(intent);
        } else Observable.just(QorumSharedCache.checkCheckoutId().save(BaseCacheType.LONG, 0))
                .subscribe(s -> {}, err -> {}, () -> {});
    }
}
