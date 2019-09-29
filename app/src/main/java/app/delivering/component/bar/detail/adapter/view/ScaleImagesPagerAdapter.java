package app.delivering.component.bar.detail.adapter.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import app.R;
import app.delivering.mvp.animation.ShowViewSubBinder;
import app.delivering.mvp.bars.detail.fullscreen.images.video.start.events.AboutBarClickVideoEvent;
import app.delivering.mvp.bars.detail.init.toolbar.init.model.AboutBarViewTypeModel;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ScaleImagesPagerAdapter extends PagerAdapter {
    private Context context;
    private String video;
    private ArrayList<AboutBarViewTypeModel> imageUrls;


    public ScaleImagesPagerAdapter(Context context, ArrayList<AboutBarViewTypeModel> urls, String videoUrl) {
        this.context = context;
        this.video = videoUrl;
        this.imageUrls = urls;
    }

    @Override public int getCount() {
        return imageUrls.size();
    }

    @Override public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override public Parcelable saveState() {
        return super.saveState();
    }

    @Override public CharSequence getPageTitle(int position) {
        return "";
    }

    @Override public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pager_item_scale_bar_view, null);
        container.addView(view);
        final ImageView image = (ImageView) view.findViewById(R.id.bar_detail_scale_view);
        final ImageView brokenImage = (ImageView) view.findViewById(R.id.bar_detail_scale_broken_view);
        final ImageView videoIcon = (ImageView) view.findViewById(R.id.bar_detail_scale_video_icon);
        final MaterialProgressBar progressBar = (MaterialProgressBar) view.findViewById(R.id.bar_detail_scale_image_progress);
        image.setOnClickListener(view1 -> onClick(position));
        Picasso.with(context)
                .load(imageUrls.get(position).getUrl())
                .into(new Target() {
                          @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                              image.setImageBitmap(bitmap);
                              progressBar.setVisibility(View.GONE);
                              if (imageUrls.get(position).isVideoUrl())
                                  videoIcon.setVisibility(View.VISIBLE);
                              else
                                  videoIcon.setVisibility(View.GONE);
                              PhotoViewAttacher photoAttacher;
                              photoAttacher= new PhotoViewAttacher(image);
                              photoAttacher.update();
                          }

                          @Override public void onBitmapFailed(Drawable errorDrawable) {
                              progressBar.setVisibility(View.GONE);
                              ShowViewSubBinder.animateShow(brokenImage, 200);
                          }

                          @Override public void onPrepareLoad(Drawable placeHolderDrawable) {}
                      });
        return view;
    }

    private void onClick(int position) {
        if (imageUrls.get(position).isVideoUrl()) {
            AboutBarClickVideoEvent clickVideoEvent = new AboutBarClickVideoEvent();
            clickVideoEvent.setVideoUrl(video);
            EventBus.getDefault().post(clickVideoEvent);
        }
    }
}
