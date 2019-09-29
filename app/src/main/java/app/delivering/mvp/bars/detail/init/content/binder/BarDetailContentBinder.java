package app.delivering.mvp.bars.detail.init.content.binder;

import android.support.v4.view.ViewPager;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rd.PageIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.core.checkin.user.get.entity.GetCheckInsResponse;
import app.delivering.component.BaseActivity;
import app.delivering.component.bar.detail.description.DescriptionsPagerAdapter;
import app.delivering.component.tab.close.CloseTabActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.detail.init.get.events.BarDetailStartEvent;
import app.delivering.mvp.bars.detail.init.get.model.BarDetailModel;
import app.delivering.mvp.main.service.feedback.events.CheckOutEvent;
import app.delivering.mvp.tab.close.init.model.FillCloseTabActivityModel;
import butterknife.BindView;
import butterknife.OnPageChange;

public class BarDetailContentBinder extends BaseBinder {
    @BindView(R.id.bar_detail_descriptions_container) RelativeLayout container;
    @BindView(R.id.bar_detail_descriptions_view_pager) ViewPager pager;
    @BindView(R.id.bar_detail_descriptions_indicator) PageIndicatorView indicatorView;


    public BarDetailContentBinder(BaseActivity activity) {
        super(activity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showDetail(BarDetailModel detailModel) {
        DescriptionsPagerAdapter pagerAdapter = new DescriptionsPagerAdapter(getActivity(), detailModel.getBarDescriptions());
        pager.setOffscreenPageLimit(detailModel.getBarDescriptions().size());
        pager.setClipToPadding(false);
        pager.setPadding(0, 0, getActivity().getResources().getDimensionPixelOffset(R.dimen.dip60), 0);
        pager.setPageMargin(getActivity().getResources().getDimensionPixelOffset(R.dimen.dip16));
        pager.setAdapter(pagerAdapter);
        indicatorView.setViewPager(pager);
        pager.setCurrentItem(0);
        if (pager.getChildAt(0) != null)
            pager.getChildAt(0).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override public void onGlobalLayout() {
                    pager.getChildAt(0).setEnabled(true);
                    resizeDescription(getTextHeight((TextView) pager.getChildAt(0)));
                    pager.getChildAt(0).getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
    }

    @OnPageChange(R.id.bar_detail_descriptions_view_pager) void onPageSelected(int position){
        if (pager.getAdapter() != null && position != pager.getAdapter().getCount() - 1)
            pager.getChildAt(position + 1).setEnabled(false);
        TextView description = (TextView) pager.getChildAt(position);
        description.setEnabled(true);
        resizeDescription(getTextHeight(description));
    }

    private int getTextHeight(TextView textView) {
        int lineCount = textView.getLineCount();
        int lineHeight = textView.getLineHeight();
        return lineCount * lineHeight + getActivity().getResources().getDimensionPixelOffset(R.dimen.dip10) * 2;
    }

    private void resizeDescription(int heightEvent) {
        pager.setMinimumHeight(heightEvent);
        container.getLayoutParams().height = heightEvent + getActivity().getResources().getDimensionPixelOffset(R.dimen.dip35);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onCheckOut(CheckOutEvent event) {
        EventBus.getDefault().removeStickyEvent(CheckOutEvent.class);
        EventBus.getDefault().post(new BarDetailStartEvent());

        if (event.getCheckInResponse().getCheckin() != null) {
            GetCheckInsResponse checkInsResponse = event.getCheckInResponse().getCheckin(); // TODO: NPE
            if (checkInsResponse.getId() > 0 && checkInsResponse.getVendorId() > 0) {
                FillCloseTabActivityModel fillCloseTabActivityModel = new FillCloseTabActivityModel();
                fillCloseTabActivityModel.setCheckInId(checkInsResponse.getId());
                fillCloseTabActivityModel.setDiscount(checkInsResponse.getRideDiscount() != null
                        ? checkInsResponse.getRideDiscount().getDiscountValue() : 0.d);
                fillCloseTabActivityModel.setTabAutoClosed(checkInsResponse.isAutoClosed());
                fillCloseTabActivityModel.setVendorId(checkInsResponse.getVendorId());
                getActivity().startActivity(CloseTabActivity.startCloseTabActivity(getActivity(), fillCloseTabActivityModel));
            }
        }
    }
}
