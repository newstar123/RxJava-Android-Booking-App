package app.delivering.mvp.main.photo.facebook.binder;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.main.photo.facebook.events.OnRestoreFBPhotoEvent;
import app.delivering.mvp.main.photo.facebook.presenter.RestoreFBPhotoPresenter;
import butterknife.BindView;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.android.schedulers.AndroidSchedulers;

public class RestoreFBPhotoBinder extends BaseBinder {
    @BindView(R.id.photo) ImageView actionButton;
    @BindView(R.id.drawer_header_photo) ImageView photo;
    @BindView(R.id.update_photo_progress) MaterialProgressBar photoProgress;
    private final RestoreFBPhotoPresenter photoPresenter;
    private final InitExceptionHandler exceptionHandler;

    public RestoreFBPhotoBinder(BaseActivity activity) {
        super(activity);
        photoPresenter = new RestoreFBPhotoPresenter(getActivity());
        exceptionHandler = new InitExceptionHandler(getActivity());
    }

    @Override public void afterViewsBounded() {
        setProgress(photoProgress);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRestoreProfilePhoto(OnRestoreFBPhotoEvent event) {
       showProgress();
       actionButton.setEnabled(false);
        photoPresenter.process()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::showPhoto, this::onError, ()->{});
    }

    private void showPhoto(Bitmap bitmap) {
        hideProgress();
        actionButton.setEnabled(true);
        photo.setImageBitmap(bitmap);
    }

    private void onError(Throwable e) {
        hideProgress();
        actionButton.setEnabled(true);
        exceptionHandler.showError(e, v -> onRestoreProfilePhoto(new OnRestoreFBPhotoEvent()));
    }
}
