package app.delivering.component.bar.detail.about.grid.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import app.R;
import app.delivering.mvp.bars.detail.fullscreen.images.viewpager.events.OnSelectPagerPositionEvent;
import app.delivering.mvp.bars.detail.init.toolbar.init.model.AboutBarViewTypeModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutBarGridViewAdapter extends RecyclerView.Adapter<AboutBarGridViewAdapter.ViewHolder> {
    private List<AboutBarViewTypeModel> models;

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.about_bar_grid_view) ImageView photo;
        @BindView(R.id.about_bar_grid_video_icon) ImageView playIcon;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public AboutBarGridViewAdapter() {
        this.models = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_about_bar_grid_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AboutBarViewTypeModel url = models.get(position);
        holder.itemView.setOnClickListener(v -> postOnClickEvent(position));
        show(holder, url);
    }

    private void postOnClickEvent(int position) {
        OnSelectPagerPositionEvent positionEvent = new OnSelectPagerPositionEvent();
        positionEvent.setPosition(position);
        EventBus.getDefault().postSticky(positionEvent);
    }

    private void show(ViewHolder holder, AboutBarViewTypeModel url) {
        Picasso picasso = Picasso.with(holder.photo.getContext());
        picasso.load(url.getUrl())
                .fit()
                .centerCrop()
                .into(holder.photo);
        if (url.isVideoUrl())
            holder.playIcon.setVisibility(View.VISIBLE);
        else
            holder.playIcon.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public void setModel(ArrayList<AboutBarViewTypeModel> urls) {
        this.models = urls;
        notifyDataSetChanged();
    }
}
