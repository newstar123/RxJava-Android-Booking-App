package app.delivering.component.bar.lists;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.R;
import app.delivering.component.BaseFragment;
import app.delivering.mvp.bars.list.init.binder.BarsListBinder;
import app.delivering.mvp.bars.list.init.enums.BarListFilterType;
import app.delivering.mvp.bars.list.item.branchclick.binder.BarListItemClickBranchBinder;
import app.delivering.mvp.bars.list.item.click.binder.BarListItemClickBinder;

public class BarsListByDiscountFragment extends BaseFragment {

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bar_list, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        BarsListBinder listBinder = new BarsListBinder(this, BarListFilterType.DISCOUNT);
        addToEventBusAndViewInjection(listBinder, view);
        BarListItemClickBinder barListItemClickBinder = new BarListItemClickBinder(this, BarListFilterType.DISCOUNT);
        addToEventBus(barListItemClickBinder);
    }
}
