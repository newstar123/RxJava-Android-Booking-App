package app.delivering.component.bar.detail.menu.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import app.R;
import app.core.bars.menu.entity.BarMenuModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BarRootMenuAdapter extends RecyclerView.Adapter<BarRootMenuAdapter.ViewHolder> {
    private List<BarMenuModel> models;

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.bar_root_menu_item_name) TextView name;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public BarRootMenuAdapter() {
        this.models = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_root_bar_menu, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BarMenuModel model = models.get(position);
        holder.itemView.setOnClickListener(v -> postOnClickEvent(model));
        holder.name.setText(model.getName());
    }

    private void postOnClickEvent(BarMenuModel model) {
        EventBus.getDefault().post(model);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public void setModels(List<BarMenuModel> models) {
        this.models = models;
        notifyDataSetChanged();
    }
}
