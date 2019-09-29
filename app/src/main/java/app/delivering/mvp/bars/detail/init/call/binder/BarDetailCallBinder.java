package app.delivering.mvp.bars.detail.init.call.binder;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.detail.init.call.presenter.BarDetailCallPhonePresenter;
import app.delivering.mvp.bars.detail.init.get.model.BarDetailModel;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import butterknife.BindView;
import butterknife.OnClick;

public class BarDetailCallBinder extends BaseBinder {
    @BindView(R.id.bar_detail_call_phone) RelativeLayout callButton;
    @BindView(R.id.bar_detail_phone) TextView phone;
    private BarDetailModel detailModel;
    private final InitExceptionHandler initExceptionHandler;
    private BarDetailCallPhonePresenter callPhonePresenter;

    public BarDetailCallBinder(BaseActivity activity) {
        super(activity);
        initExceptionHandler = new InitExceptionHandler(getActivity());
        callPhonePresenter = new BarDetailCallPhonePresenter(getActivity());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showDetail(BarDetailModel detailModel) {
        this.detailModel = detailModel;
        callButton.setEnabled(true);
        phone.setVisibility(View.VISIBLE);
        phone.setText(detailModel.getBarModel().getPhone());
    }

    @OnClick(R.id.bar_detail_call_phone) void callPhone() {
        callPhonePresenter.process()
                .subscribe(isOk -> call(), this::onError);
    }

    private void call() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + detailModel.getBarModel().getPhone()));
        getActivity().startActivity(intent);
    }

    private void onError(Throwable throwable) {
        initExceptionHandler.showError(throwable, view -> callPhone());
    }

}
