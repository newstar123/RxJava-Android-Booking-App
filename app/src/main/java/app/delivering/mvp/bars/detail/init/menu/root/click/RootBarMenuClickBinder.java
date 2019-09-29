package app.delivering.mvp.bars.detail.init.menu.root.click;

import android.os.Bundle;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.core.bars.menu.entity.BarMenuModel;
import app.delivering.component.BaseActivity;
import app.delivering.component.bar.detail.menu.fragment.BarDetailSubMenuFragment;
import app.delivering.mvp.BaseBinder;

public class RootBarMenuClickBinder extends BaseBinder {

    public RootBarMenuClickBinder(BaseActivity activity) {
        super(activity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void clickMenu(BarMenuModel menu) {
        BarDetailSubMenuFragment subMenuFragment = new BarDetailSubMenuFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(BarDetailSubMenuFragment.BAR_SUB_MENU_LIST, menu);
        subMenuFragment.setArguments(bundle);
        getActivity().start(subMenuFragment);
    }

}
