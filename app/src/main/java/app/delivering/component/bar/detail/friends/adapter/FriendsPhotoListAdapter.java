package app.delivering.component.bar.detail.friends.adapter;

import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.R;
import app.delivering.mvp.animation.ShowViewSubBinder;
import app.delivering.mvp.bars.detail.init.friends.init.model.CheckinsPersonModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.blurry.Blurry;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class FriendsPhotoListAdapter extends RecyclerView.Adapter<FriendsPhotoListAdapter.ViewHolder> {
    private List<CheckinsPersonModel> models;

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.check_in_friend_photo) ImageView photo;
        @BindView(R.id.check_friend_broken_photo) ImageView brokenPhoto;
        @BindView(R.id.check_in_friend_name) TextView name;
        @BindView(R.id.check_in_friend_last_name) TextView lastName;
        @BindView(R.id.friend_item_image_progress) MaterialProgressBar progress;
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public FriendsPhotoListAdapter() {
        this.models = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_check_in_friend, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CheckinsPersonModel model = models.get(position);
        show(holder, model);
    }

    private void show(ViewHolder holder, CheckinsPersonModel model) {
        switch (model.getType()){
            case MOCK:
                setBluredPhoto(holder, model);
                break;
            case PEOPLE:
                setFacebookPhoto(holder, model);
                break;
            default:
                setFacebookPhoto(holder, model);
                holder.name.setText(model.getPerson().getFirstName());
                holder.lastName.setText(model.getPerson().getLastName());
        }
    }

    private void setBluredPhoto(ViewHolder holder, CheckinsPersonModel model) {
        load(holder, model, () -> {
            ImageView photo = holder.photo;
            BitmapDrawable drawable = (BitmapDrawable) photo.getDrawable();
            Blurry.with(photo.getContext())
                    .async()
                    .radius(3)
                    .sampling(1)
                    .from(drawable.getBitmap())
                    .into(photo);
        });
    }

    private void setFacebookPhoto(ViewHolder holder, CheckinsPersonModel model) {
        load(holder, model, null);
    }

    private void load(ViewHolder holder, CheckinsPersonModel model, Runnable blurryRunnable) {
        Picasso.with(holder.photo.getContext()).load(model.getPerson().getImageUrl())
                .fit()
                .centerCrop()
                .into(holder.photo, new Callback() {
                    @Override public void onSuccess() {
                        holder.progress.setVisibility(View.GONE);
                        if (blurryRunnable != null) {
                            blurryRunnable.run();
                        }
                    }

                    @Override public void onError() {
                        holder.progress.setVisibility(View.GONE);
                        ShowViewSubBinder.animateShow(holder.brokenPhoto, 200);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public void setModels(List<CheckinsPersonModel> barModels) {
        this.models = barModels;
        notifyDataSetChanged();
    }
}
