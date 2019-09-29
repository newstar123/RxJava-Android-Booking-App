package app.delivering.mvp.bars.detail.init.toolbar.photo.binder;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import app.R;
import app.delivering.component.bar.detail.photo.BarDetailPhotoFragment;
import app.delivering.component.bar.detail.photo.adapter.BarsDetailPhotosAdapter;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.animation.ShowViewSubBinder;
import app.delivering.mvp.bars.detail.init.toolbar.controll.events.OnChangePhotoViewerStateEvent;
import app.delivering.mvp.bars.detail.init.toolbar.init.model.AboutBarViewTypeModel;
import app.delivering.mvp.bars.detail.init.toolbar.video.events.OnStopPlayerEvent;
import app.delivering.mvp.profile.edit.actionbar.clicks.binder.ViewActionSetter;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import uk.co.senab.photoview.PhotoViewAttacher;

public class BarDetailPhotoBinder extends BaseBinder {
    @BindView(R.id.bar_detail_item_image_progress) MaterialProgressBar progressBar;
    @BindView(R.id.bar_detail_item_broken_view) ImageView brokenView;
    @BindViews({R.id.bar_detail_item_play_video, R.id.bar_detail_expand_video}) List<View> videoControls;
    @BindView(R.id.bar_detail_item_view) ImageView itemView;
    private ArrayList<AboutBarViewTypeModel> typeModels;
    private int position;


    public BarDetailPhotoBinder(BarDetailPhotoFragment barDetailPhotoFragment) {
        super(barDetailPhotoFragment.getBaseActivity());
        typeModels = new ArrayList<>();
        Bundle arguments = barDetailPhotoFragment.getArguments();
        typeModels = arguments.getParcelableArrayList(BarsDetailPhotosAdapter.BAR_DETAIL_PHOTO_MODEL);
        position = arguments.getInt(BarsDetailPhotosAdapter.BAR_DETAIL_PHOTO_POSITION, 0);
    }

    @Override public void afterViewsBounded() {
        if (!typeModels.get(position).isVideoUrl())
            ButterKnife.apply(videoControls, ViewActionSetter.GONE);
        itemView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.with(getActivity())
                .load(typeModels.get(position).getUrl())
                .into(itemView, new Callback() {
                    @Override public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                        PhotoViewAttacher photoAttacher;
                        photoAttacher= new PhotoViewAttacher(itemView);
                        photoAttacher.update();
                        photoAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                            @Override public void onPhotoTap(View view, float x, float y) {
                                onItemClick();
                            }

                            @Override public void onOutsidePhotoTap() {}
                        });
                    }

                    @Override public void onError() {
                        progressBar.setVisibility(View.GONE);
                        ShowViewSubBinder.animateShow(brokenView, 200);
                    }
                });
    }

    private void onItemClick(){
        if (typeModels.get(position).isVideoUrl())
            EventBus.getDefault().post(new OnStopPlayerEvent());
        else
            EventBus.getDefault().post(new OnChangePhotoViewerStateEvent(true));
    }
}
