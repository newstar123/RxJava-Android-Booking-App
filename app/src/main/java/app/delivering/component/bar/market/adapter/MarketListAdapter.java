package app.delivering.component.bar.market.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.CustomApplication;
import app.R;
import app.core.bars.locations.entity.LocationsModel;
import app.delivering.mvp.main.show.model.CitiesModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MarketListAdapter extends RecyclerView.Adapter<MarketListAdapter.ViewHolder> {
    private List<LocationsModel> filteredModels;
    private List<LocationsModel> models;
    private final ClickListener clickListener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.market_name) TextView marketName;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public MarketListAdapter(ClickListener clickListener) {
        this.models = new ArrayList<>();
        this.filteredModels = new ArrayList<>();
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public MarketListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_search_market, parent, false);
        return new MarketListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MarketListAdapter.ViewHolder holder, int pos) {
        holder.marketName.setText(filteredModels.get(pos).getLabel());
        holder.marketName.setOnClickListener(v -> {
            if (filteredModels.get(pos).getId() != null) {
                CitiesModel citiesModel = new CitiesModel();
                citiesModel.setCities(models);
                citiesModel.setManualRefreshing(false);
                citiesModel.setSelectCityName(filteredModels.get(pos).getLabel());
                clickListener.onItemClicked(filteredModels, citiesModel, pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredModels.size();
    }

    public void setModels(List<LocationsModel> locations) {
        models.clear();
        models.addAll(locations);
        filteredModels.clear();
        filteredModels.addAll(locations);
        checkEmptyListState();
        notifyDataSetChanged();
    }

    public void filterBy(String query) {
        filteredModels.clear();
        query = query.toLowerCase();
        if (query.length() == 0)
            filteredModels.addAll(models);
        else
            filteredLocations(query);
        checkEmptyListState();
        notifyDataSetChanged();
    }

    private void filteredLocations(String query) {
        for (LocationsModel model : models) {
            if (model.getLabel().toLowerCase().contains(query))
                filteredModels.add(model);
        }
    }

    private void checkEmptyListState() {
        if (filteredModels.size() > 0) return;
        LocationsModel emptyListModel = new LocationsModel();
        emptyListModel.setLabel(CustomApplication.get().getString(R.string.sorry_no_results));
        filteredModels.add(emptyListModel);
    }

    public interface ClickListener {
        void onItemClicked(List<LocationsModel> filteredModels, CitiesModel citiesModel, int pos);
    }
}