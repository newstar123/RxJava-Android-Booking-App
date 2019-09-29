package app.delivering.mvp.bars.detail.init.menu.submenu.init;

import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import app.R;
import app.core.bars.menu.entity.BarMenuModel;
import app.delivering.component.bar.detail.menu.fragment.BarDetailSubMenuFragment;
import app.delivering.component.bar.detail.menu.fragment.adapter.BarSubMenuAdapter;
import app.delivering.mvp.BaseBinder;
import butterknife.BindView;

public class BarSubMenuBinder extends BaseBinder {
    private final BarSubMenuAdapter adapter;
    @BindView(R.id.bar_sub_menu_recycler) RecyclerView recyclerView;
    @BindView(R.id.bar_sub_menu_container) CoordinatorLayout container;
    private BarDetailSubMenuFragment fragment;

    public BarSubMenuBinder(BarDetailSubMenuFragment fragment) {
        super(fragment.getBaseActivity());
        this.fragment = fragment;
        adapter = new BarSubMenuAdapter();
    }

    @Override public void afterViewsBounded() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        loadMenu();
    }

    private void loadMenu() {
        BarMenuModel menu = fragment.getArguments().getParcelable(BarDetailSubMenuFragment.BAR_SUB_MENU_LIST);
        if (menu != null)
            adapter.setModels(menu.getItems());
    }
}
