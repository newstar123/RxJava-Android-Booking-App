package app.delivering.component.bar.detail.fragment.feature.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.R;
import app.delivering.mvp.bars.detail.init.tablist.list.feature.model.FeaturesModel;
import app.gateway.rest.client.QorumHttpClient;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BarDetailFeaturesAdapter extends RecyclerView.Adapter<BarDetailFeaturesAdapter.ViewHolder> {
    private List<FeaturesModel> models;

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.list_item_bar_detail_feature_icon) ImageView icon;
        @BindView(R.id.list_item_bar_detail_feature_name) TextView type;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public BarDetailFeaturesAdapter() {
        this.models = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_bar_detail_feature, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        show(holder, models.get(position));
    }

    private void show(ViewHolder holder, FeaturesModel model) {
        if (model.getVenueFeatureModel() != null && !TextUtils.isEmpty(model.getVenueFeatureModel().getIconUrl())){
            String baseUrl = QorumHttpClient.getBaseUrl(holder.icon.getContext());
            String url = baseUrl.substring(0, baseUrl.length() - 5) + model.getVenueFeatureModel().getIconUrl();
            Picasso.with(holder.icon.getContext()).load(url)
                    .error(R.drawable.inset_cocktail)
                    .placeholder(R.drawable.inset_check).into(holder.icon);
        }
        if (model.getType().isTwoColorsName())
            holder.type.setText(model.getCombinedText());
        else
            holder.type.setText(model.getTextValue());
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public void setModels(List<FeaturesModel> models) {
        this.models = models;
        notifyDataSetChanged();
    }
}
