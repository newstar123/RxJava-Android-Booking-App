package app.delivering.mvp.tab.summary.binder;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import app.R;
import app.core.checkin.user.post.entity.CheckInResponse;
import app.delivering.component.BaseActivity;
import app.delivering.component.tab.adapter.OrderAdapter;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.tab.summary.model.TabBillItemModel;
import app.delivering.mvp.tab.summary.presenter.TabBillItemsPresenter;
import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;

public class TabButtonsBinder extends BaseBinder {
    private static final String GROUP_NAME = "group_name";
    private final TabBillItemsPresenter tabBillItemsPresenter;
    private final OrderAdapter orderAdapter;
    @BindView(R.id.order_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.tab_summary_value) TextView summaryValue;
    @BindView(R.id.scroll_top) ImageView scrollTop;
    @BindView(R.id.scroll_bottom) ImageView scrollBottom;

    public TabButtonsBinder(BaseActivity activity) {
        super(activity);
        tabBillItemsPresenter = new TabBillItemsPresenter(getActivity());
        orderAdapter = new OrderAdapter();
    }

    @Override public void afterViewsBounded() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(orderAdapter);
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override public void onChildViewAttachedToWindow(@NonNull View view) { }

            @Override public void onChildViewDetachedFromWindow(@NonNull View view) {
                checkControlButtonsVisibility((LinearLayoutManager) recyclerView.getLayoutManager());
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 0)
                    checkControlButtonsVisibility((LinearLayoutManager) recyclerView.getLayoutManager());
            }
            @Override public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @OnClick(R.id.scroll_top) public void clickTop() {
        recyclerView.smoothScrollToPosition(0);
    }

    @OnClick(R.id.scroll_bottom) public void clickBottom() {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter != null)
            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
    }

    @Subscribe(threadMode = ThreadMode.MAIN) public void load(CheckInResponse checkIn) {
        tabBillItemsPresenter.process(checkIn.getCheckin())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(val -> show(val, checkIn), err->{}, ()->{});
    }

    private void show(List<TabBillItemModel> tabSummaryBillItemModels, CheckInResponse checkInResponse) {
        List<? extends Map<String, ?>> list = tabSummaryBillItemModels.get(0).getGroupList();
        Map map = list.get(0);

        if (map != null) {
            Object object = map.get(GROUP_NAME);
            if (object != null) {
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                String summaryVal = String.format(Locale.getDefault(),"$%s", checkInResponse.getCheckin().getTotals().getSubTotal() != 0 ?
                        decimalFormat.format((double) checkInResponse.getCheckin().getTotals().getSubTotal() / 100) : 0.00);
                summaryValue.setText(summaryVal);
                orderAdapter.setUpModel(tabSummaryBillItemModels);
            }
        }
    }

    private void setArrowsVisibility(boolean isTopVisible, boolean isBottomVisible) {
        scrollTop.setVisibility(isTopVisible ? View.VISIBLE : View.GONE);
        scrollBottom.setVisibility(isBottomVisible ? View.VISIBLE : View.GONE);
    }

    private void checkControlButtonsVisibility(LinearLayoutManager manager) {
        try {
            int first = manager.findFirstCompletelyVisibleItemPosition();
            int last = manager.findLastCompletelyVisibleItemPosition();

            if (first == 0 && (last == 0 || last == 1))
                setArrowsVisibility(false, false);
            else {
                if (first == 0 && last == orderAdapter.getItemCount() - 1)      //is scroll unavailable
                    setArrowsVisibility(false, false);
                else
                    if (first > 0 && last < orderAdapter.getItemCount() - 1)    //is scroll on middle position
                        setArrowsVisibility(true, true);
                else
                    if (first == 0)                                             //is scroll on top position
                        setArrowsVisibility(false, true);
                else
                    if (last == orderAdapter.getItemCount() - 1)                //is scroll on bottom position
                        setArrowsVisibility(true, false);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
