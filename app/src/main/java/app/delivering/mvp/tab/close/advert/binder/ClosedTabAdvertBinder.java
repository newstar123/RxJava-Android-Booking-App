package app.delivering.mvp.tab.close.advert.binder;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.trello.rxlifecycle.android.ActivityEvent;

import app.R;
import app.core.ride.delayed.discount.model.DiscountUpdatesModel;
import app.delivering.component.BaseActivity;
import app.delivering.component.tab.close.CloseTabActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.tab.advert.exceptions.LoadAdvertImageException;
import app.delivering.mvp.tab.advert.presenter.AdvertImagePresenter;
import app.delivering.mvp.tab.close.init.model.FillCloseTabActivityModel;
import butterknife.BindView;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.android.schedulers.AndroidSchedulers;

public class ClosedTabAdvertBinder extends BaseBinder {
    @BindView(R.id.tab_closed_advert_progress) MaterialProgressBar progressBar;
    @BindView(R.id.closed_tab_advert_image) ImageView advertImage;
    private AdvertImagePresenter imagePresenter;
    private final InitExceptionHandler initExceptionHandler;
    private double discountValue;

    private final Target target = new Target() {
        @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            if (progressBar != null)
                progressBar.setVisibility(View.GONE);
            if (advertImage != null)
                advertImage.setImageBitmap(bitmap);
        }

        @Override public void onBitmapFailed(Drawable errorDrawable) {
            onError(new LoadAdvertImageException());
        }

        @Override public void onPrepareLoad(Drawable placeHolderDrawable) {}
    };

    public ClosedTabAdvertBinder(BaseActivity activity) {
        super(activity);
        imagePresenter = new AdvertImagePresenter(activity);
        initExceptionHandler = new InitExceptionHandler(activity);
        FillCloseTabActivityModel model = ((CloseTabActivity) getActivity()).getCloseTabActivityModel();
        discountValue = model.getDiscount();
    }

    @Override public void afterViewsBounded() {
        loadAdvert();
    }

    public void loadAdvert() {
        progressBar.setVisibility(View.VISIBLE);
        imagePresenter.process(discountValue)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::show, this::onError, ()->{});
    }

    private void show(DiscountUpdatesModel response) {
        Picasso.with(getActivity())
                .load(getAdvertUrl(response))
                .resize(advertImage.getMeasuredWidth(), advertImage.getMeasuredHeight())
                .centerCrop()
                .error(R.drawable.inset_broken_resource)
                .into(target);
    }

    private String getAdvertUrl(DiscountUpdatesModel response) {
        return response.isEligible() ? response.getAdvertResponse().getData().getAttributes().getTabClosedEligible()
                : response.getAdvertResponse().getData().getAttributes().getTabClosedNotEligible();
    }

    private void onError(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
        initExceptionHandler.showError(throwable, view -> loadAdvert());
    }
}
