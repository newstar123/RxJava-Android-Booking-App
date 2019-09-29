package app.delivering.mvp.bars.detail.init.menu.submenu.actionbar;

import android.support.v7.widget.Toolbar;

import app.R;
import app.core.bars.menu.entity.BarMenuModel;
import app.delivering.component.bar.detail.menu.fragment.BarDetailSubMenuFragment;
import app.delivering.mvp.BaseBinder;
import butterknife.BindView;

public class BarSubMenuActionBarBinder extends BaseBinder {
    @BindView(R.id.bar_sub_menu_toolbar) Toolbar toolBar;
    private BarDetailSubMenuFragment fragment;

    public BarSubMenuActionBarBinder(BarDetailSubMenuFragment fragment) {
        super(fragment.getBaseActivity());
        this.fragment = fragment;
    }

    @Override public void afterViewsBounded() {
        getActivity().setSupportActionBar(toolBar);
        getActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().getSupportActionBar().setHomeButtonEnabled(true);
        getActivity().getSupportActionBar().setTitle(getMenuBarName());
    }

    private String getMenuBarName() {
        BarMenuModel menu = fragment.getArguments().getParcelable(BarDetailSubMenuFragment.BAR_SUB_MENU_LIST);
        if (menu != null)
            return menu.getName();
        else
            return getString(R.string.menu_title);
    }
}
