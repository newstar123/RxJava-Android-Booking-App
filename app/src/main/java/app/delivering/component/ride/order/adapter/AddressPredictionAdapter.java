package app.delivering.component.ride.order.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.List;

import app.R;
import app.core.location.geocode.prediction.entity.PlacePrediction;
import app.delivering.mvp.ride.order.address.change.events.ChangeAddressEvent;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AddressPredictionAdapter extends RecyclerView.Adapter<AddressPredictionAdapter.ViewHolder>{
    private List<PlacePrediction> models;

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.list_item_order_ride_primary) TextView primaryTextView;
        @BindView(R.id.list_item_order_ride_secondary) TextView secondaryTextView;
        @BindView(R.id.list_item_order_ride_divider) View dividerView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public AddressPredictionAdapter() {
        models = Collections.emptyList();
    }

    @Override
    public AddressPredictionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_order_ride_address, parent, false);
        return new AddressPredictionAdapter.ViewHolder(itemView);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        PlacePrediction barModel = models.get(position);
        holder.itemView.setOnClickListener(v -> postOnClickEvent(v, barModel));
        holder.primaryTextView.setText(barModel.getPrimaryText());
        holder.secondaryTextView.setText(barModel.getSecondaryText());
        if (position >= getItemCount() - 1)
            holder.dividerView.setVisibility(View.INVISIBLE);
        else
            holder.dividerView.setVisibility(View.VISIBLE);
    }

    private void postOnClickEvent(View v, PlacePrediction model) {
       EventBus.getDefault().post(new ChangeAddressEvent(model));
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public void setModels(List<PlacePrediction> models) {
        this.models = models;
        notifyDataSetChanged();
    }
}
