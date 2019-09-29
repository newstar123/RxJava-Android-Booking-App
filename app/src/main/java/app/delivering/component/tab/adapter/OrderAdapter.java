package app.delivering.component.tab.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import app.R;
import app.delivering.mvp.tab.summary.model.SortedBillItem;
import app.delivering.mvp.tab.summary.model.TabBillItemModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private static final String MULTIPLY_QUANTITY = "x";
    private static final String FORMAT = "%s%d";

    private List<TabBillItemModel> listOfModels;


    public OrderAdapter() {
        listOfModels = Collections.emptyList();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tab_summary_child_name) TextView summaryChildName;
        @BindView(R.id.tab_summary_child_price) TextView summaryChildPrice;
        @BindView(R.id.tab_summary_child_count) TextView summaryChildCount;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_tab_subtotal_child, parent, false);
        return new OrderAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position) {
        if (!listOfModels.isEmpty()) {
            List<SortedBillItem> model = listOfModels.get(0).getChildList();

            if (!model.isEmpty()) {
                String childName = model.get(position).getBillName();
                String childCount = String.format(Locale.getDefault(),
                        FORMAT, MULTIPLY_QUANTITY, model.get(position).getQuantity());
                String childPrice = model.get(position).getBillSum();

                holder.summaryChildName.setText(childName);
                holder.summaryChildCount.setText(childCount);
                holder.summaryChildPrice.setText(childPrice);
            }
        } else
            holder.summaryChildName.setText(holder.summaryChildName.getResources().getString(R.string.no_orders));
    }

    @Override
    public int getItemCount() {
        return listOfModels.isEmpty() ? 1 :
                listOfModels.get(0).getChildList().isEmpty() ? 1
                        : listOfModels.get(0).getChildList().size();
    }

    public void setUpModel(List<TabBillItemModel> model) {
        this.listOfModels = model;
        notifyDataSetChanged();
    }
}
