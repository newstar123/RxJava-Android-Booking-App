package app.delivering.mvp.bars.detail.init.menu.button.binder;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.bar.detail.menu.BarDetailRootMenuActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.detail.init.get.model.BarDetailModel;
import app.delivering.mvp.bars.detail.init.menu.button.model.MenuKitchenWorkTimeModel;
import app.delivering.mvp.bars.detail.init.menu.button.presenter.BarDetailMenuPresenter;
import butterknife.BindView;
import butterknife.OnClick;

public class BarDetailMenuBinder extends BaseBinder {
    @BindView(R.id.bar_detail_served_time) LinearLayout servedTimeContainer;
    @BindView(R.id.bar_detail_menu_time) TextView kitchenWorkTime;
    private BarDetailMenuPresenter menuPresenter;
    private BarDetailModel detailModel;

    public BarDetailMenuBinder(BaseActivity activity) {
        super(activity);
        menuPresenter = new BarDetailMenuPresenter(getActivity());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showDetail(BarDetailModel detailModel) {
        this.detailModel = detailModel;
        menuPresenter.process(detailModel.getBarModel())
                .subscribe(this::showMenuButton, this::onErrorResult);
    }

    private void showMenuButton(MenuKitchenWorkTimeModel model) {
        servedTimeContainer.setVisibility(View.VISIBLE);
        kitchenWorkTime.setText(model.getKitchenWorkTime());
    }

    private void onErrorResult(Throwable e) {
       /* if (e instanceof KitchenWorkTimeEmptyException)
            Toast.makeText(getActivity(), "EMPTY_TIME", Toast.LENGTH_LONG).show();
        if (e instanceof KitchenNotWorkTodayException)
            Toast.makeText(getActivity(), "KITCHEN_NOT_WORK", Toast.LENGTH_LONG).show();*/
    }

    @OnClick(R.id.bar_detail_menu_container) void viewMenu() {
        startMenuActivity();
    }

    private void startMenuActivity() {
        Intent intent = new Intent(getActivity(), BarDetailRootMenuActivity.class);
        intent.putExtra(BarDetailRootMenuActivity.BAR_ID_FOR_MENU, detailModel.getBarModel().getId());
        intent.putExtra(BarDetailRootMenuActivity.BAR_NAME_FOR_MENU, detailModel.getBarModel().getName());
        getActivity().startActivity(intent);
    }

}
