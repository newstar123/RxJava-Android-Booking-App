package app.delivering.mvp.bars.detail.init.tablist.height.binder;

import android.support.v4.view.ViewPager;
import android.widget.RelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.detail.init.get.events.BarDetailResumeEvent;
import app.delivering.mvp.bars.detail.init.get.model.BarDetailModel;
import app.delivering.mvp.bars.detail.init.tablist.height.model.BarDetailFeatureModel;
import app.delivering.mvp.bars.detail.init.tablist.height.model.BarDetailHoursModel;
import app.delivering.mvp.bars.detail.init.tablist.height.model.BarDetailInsideTipsModel;
import app.delivering.mvp.bars.detail.init.tablist.list.inside.model.BarDetailInsideTipsHeightEvent;
import app.delivering.mvp.bars.detail.init.tablist.tab.events.BarDetailOnTabItemSelectedEvent;
import app.delivering.mvp.bars.work.BarWorkTypeParser;
import butterknife.BindView;

public class BarDetailTabsAndListContainerBinder extends BaseBinder {
    @BindView(R.id.bar_detail_feature_tabs_container) RelativeLayout container;
    @BindView(R.id.bar_detail_lists_pager) ViewPager viewPager;
    private BarDetailModel detailModel;

    public BarDetailTabsAndListContainerBinder(BaseActivity activity) {
        super(activity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadList(BarDetailModel detailModel) {
        this.detailModel = detailModel;
        List<String> list = getFullInsideTips(detailModel);
        if (list != null && !list.isEmpty()) {
            resizeContainers(list.size(), 0, 0);
            EventBus.getDefault().post(new BarDetailInsideTipsModel(list));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTabChanged(BarDetailOnTabItemSelectedEvent event) {
        if (detailModel == null) return;
        List<String> list = getListBy(event.getSelectedTabPosition());
        if (list != null && !list.isEmpty()) {
            resizeContainers(list.size(), 0, event.getSelectedTabPosition());
            postReloadList(list, event.getSelectedTabPosition());
        }
    }

    private void postReloadList(List<String> list, int selectedTabPosition) {
        switch (selectedTabPosition) {
            case 1:
                EventBus.getDefault().post(new BarDetailFeatureModel(list));
                break;
            case 2:
                EventBus.getDefault().post(new BarDetailHoursModel(list, BarWorkTypeParser.createWorkTypeInformation(detailModel.getBarModel())));
                break;
            default:
                EventBus.getDefault().post(new BarDetailInsideTipsModel(list));
        }
    }

    private List<String> getListBy(int selectedTabPosition) {
        switch (selectedTabPosition) {
            case 1:
                return detailModel.getBarModel().getFeatures();
            case 2:
                return detailModel.getBarModel().getHours();
            default:
                return getFullInsideTips(detailModel);
        }
    }

    private List<String> getFullInsideTips(BarDetailModel detailModel) {
        ArrayList<String> fullInsideTips = new ArrayList<>();
        int price = detailModel.getBarModel().getPriceAvg();
        if (price != 0)
            fullInsideTips.add(String.format(getString(R.string.average_drink_price_format), price / 100.d));
        int minimalAge = detailModel.getBarModel().getPatronAgeAvg();
        if (minimalAge != 0)
            fullInsideTips.add("avgGuestAge=" + minimalAge);
        fullInsideTips.addAll(detailModel.getBarModel().getInsiderTips());
        return fullInsideTips;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInsideTipsWithFooter(BarDetailInsideTipsHeightEvent event) {
        int footerHeight = event.getFooterHight();
        resizeContainers(event.getListSize(), footerHeight, 0);
        EventBus.getDefault().post(new BarDetailResumeEvent());
    }

    private void resizeContainers(int size, int footer, int tabPosition) {
        int listHeight = getListHeight(size, tabPosition);
        int minimalHeight = getContainerHeight();
        container.setMinimumHeight(listHeight + minimalHeight + footer);
        viewPager.setMinimumHeight(listHeight + footer);
        viewPager.getLayoutParams().height = listHeight + footer;
    }

    private int getListHeight(int size, int tabPosition) {
        int listHeight = 0;
        if (tabPosition == 2)
            listHeight = size * getActivity().getResources().getDimensionPixelSize(R.dimen.dip25);
        else
            listHeight = size * getActivity().getResources().getDimensionPixelSize(R.dimen.dip44);
        return listHeight;
    }

    private int getContainerHeight() {
        int tabBarHeight = getActivity().getResources().getDimensionPixelSize(R.dimen.dip44);
        int paddings = 2 * getActivity().getResources().getDimensionPixelSize(R.dimen.dip16);
        return tabBarHeight + paddings;
    }

}
