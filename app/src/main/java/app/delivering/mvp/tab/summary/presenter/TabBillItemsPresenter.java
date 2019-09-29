package app.delivering.mvp.tab.summary.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.R;
import app.core.checkin.user.get.entity.BillItem;
import app.core.checkin.user.get.entity.GetCheckInsResponse;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.tab.summary.model.SortedBillItem;
import app.delivering.mvp.tab.summary.model.TabBillItemModel;
import rx.Observable;
import rx.schedulers.Schedulers;

public class TabBillItemsPresenter extends BasePresenter<GetCheckInsResponse, Observable<List<TabBillItemModel>>> {
    private static final String SUBTOTAL_GROUP_NAME = "group_name";
    private static final String SUBTOTAL_CHILD_NAME = "child_name";
    private static final String SUBTOTAL_CHILD_COUNT = "child_count";
    private static final String SUBTOTAL_CHILD_PRICE = "child_price";
    private static final String SPLIT_REGEX = "-";
    private static final double FORMATTING_COST = 100.0;

    private double totalTabSummaryValue;

    public TabBillItemsPresenter(BaseActivity activity) {
        super(activity);
        totalTabSummaryValue = 0.0d;
    }

    @Override public Observable<List<TabBillItemModel>> process(GetCheckInsResponse checkIn) {
        return Observable.just(new TabBillItemModel())
                .concatMap(tabBillItemModel -> getChildren(tabBillItemModel, checkIn.getBillItems()))
                .concatMap(tabBillItemModel -> setUpTabSummaryBillItemModel(tabBillItemModel, checkIn))
                .toList()
                .observeOn(Schedulers.io());
    }

    private Observable<TabBillItemModel> getChildren(TabBillItemModel model, List<BillItem> checkInBillItems) {
        return Observable.from(checkInBillItems)
                .toList()
                .doOnNext(billItems -> model.setChildList(sortData(billItems)))
                .map(billItems -> setUpTotalValue(model));
    }

    private Observable<TabBillItemModel> setUpTabSummaryBillItemModel(TabBillItemModel model, GetCheckInsResponse checkIn) {
        model.setGroupFrom(new String[]{SUBTOTAL_GROUP_NAME});
        model.setGroupTo(new int[]{R.id.tab_summary_value});
        model.setChildFrom(new String[]{SUBTOTAL_CHILD_NAME, SUBTOTAL_CHILD_COUNT, SUBTOTAL_CHILD_PRICE});
        model.setChildTo(new int[]{R.id.tab_summary_child_name, R.id.tab_summary_child_count, R.id.tab_summary_child_price});
        return Observable.just(model);
    }

    private TabBillItemModel setUpTotalValue(TabBillItemModel tabBillItemModel) {
        tabBillItemModel.setGroupList(getGroupData(this.totalTabSummaryValue));
        this.totalTabSummaryValue = 0.0d;
        return tabBillItemModel;
    }

    private List<? extends Map<String, ?>> getGroupData(double totalTabSummaryValue) {
        ArrayList<Map<String, String>> groupDataList = new ArrayList<>();
        HashMap<String, String> map = new HashMap<>();
        map.put(SUBTOTAL_GROUP_NAME, format(totalTabSummaryValue));
        groupDataList.add(map);
        return groupDataList;
    }

    private ArrayList<SortedBillItem> sortData(List<BillItem> items) {
        ArrayList<SortedBillItem> sortedItems = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();
        for (BillItem item : items) {
            String uniqueName = item.getName() + SPLIT_REGEX + item.getPricePerUnit();
            if (map.containsKey(uniqueName)) {
                map.put(uniqueName, map.get(uniqueName) + item.getQuantity());
            } else {
                map.put(uniqueName, item.getQuantity());
            }
        }
        for (Map.Entry<String, Integer> e : map.entrySet()) {
            SortedBillItem sortedItem = new SortedBillItem();
            sortedItem.setQuantity(e.getValue());
            String keyValue = e.getKey();
            String[] uniqueNamePlusPrice = keyValue.split(SPLIT_REGEX);
            sortedItem.setBillSum(setUpTabTotalValue(sortedItem.getQuantity() * Double.parseDouble(uniqueNamePlusPrice[1])));
            sortedItem.setBillName(uniqueNamePlusPrice[0]);
            sortedItems.add(sortedItem);
        }
        return sortedItems;
    }

    private String setUpTabTotalValue(double cost) {
        totalTabSummaryValue += cost;
        return format(cost);
    }

    private String format(double cost) {
        return String.format(getActivity().getString(R.string.dollars_value_with_two_sign), cost / FORMATTING_COST);
    }
}
