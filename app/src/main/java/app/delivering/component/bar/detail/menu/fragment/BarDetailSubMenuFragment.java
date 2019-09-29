package app.delivering.component.bar.detail.menu.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.R;
import app.delivering.component.BaseFragment;
import app.delivering.mvp.bars.detail.init.menu.submenu.actionbar.BarSubMenuActionBarBinder;
import app.delivering.mvp.bars.detail.init.menu.submenu.init.BarSubMenuBinder;

public class BarDetailSubMenuFragment extends BaseFragment {
    public static final String BAR_SUB_MENU_LIST = "BAR_SUB_MENU_LIST";

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bar_sub_menu, container, false);
        initViews(view);
        loadCityImage(view);
        return view;
    }

    private void initViews(View view) {
        BarSubMenuActionBarBinder actionBarBinder = new BarSubMenuActionBarBinder(this);
        addItemForViewsInjection(actionBarBinder, view);
        BarSubMenuBinder menuBinder = new BarSubMenuBinder(this);
        addItemForViewsInjection(menuBinder, view);
    }
}
