package app.delivering.component.bar.detail.menu.fragment.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import app.R;
import app.core.bars.menu.entity.SubMenuModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BarSubMenuAdapter extends RecyclerView.Adapter<BarSubMenuAdapter.ViewHolder> {
    private static final String SUB_MENU_PRICE_FORMAT = "$ #.##";
    private List<SubMenuModel> models;

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.bar_sub_menu_item_container) RelativeLayout container;
        @BindView(R.id.bar_sub_menu_item_name) TextView name;
        @BindView(R.id.bar_sub_menu_item_price) TextView price;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public BarSubMenuAdapter() {
        this.models = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_bar_sub_menu, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SubMenuModel model = models.get(position);
        holder.container.setEnabled(false);
        holder.itemView.setOnClickListener(v -> postOnClickEvent(v, model.getId()));
        holder.name.setText(model.getName());
        String priceText = new DecimalFormat(SUB_MENU_PRICE_FORMAT).format(model.getPrice()/100);
        holder.price.setText(priceText);
    }

    private void postOnClickEvent(View view, String id) {
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public void setModels(List<SubMenuModel> models) {
        this.models = models;
        notifyDataSetChanged();
    }
}
