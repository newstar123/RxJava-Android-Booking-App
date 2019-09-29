package app.delivering.component.bar.detail.fragment.hours.adapter;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.R;
import app.delivering.mvp.bars.detail.init.tablist.list.hours.model.BarDetailWorkHoursModel;
import app.delivering.mvp.bars.list.init.enums.BarByWorkTime;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BarDetailHoursAdapter extends RecyclerView.Adapter<BarDetailHoursAdapter.ViewHolder> {
    private List<BarDetailWorkHoursModel> models;

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.list_item_bar_detail_work_day) TextView day;
        @BindView(R.id.list_item_bar_detail_work_time) TextView time;
        @BindView(R.id.list_item_bar_detail_work_type) TextView type;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public BarDetailHoursAdapter() {
        this.models = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_bar_detail_hour, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        show(holder, models.get(position));
    }

    private void show(ViewHolder holder, BarDetailWorkHoursModel model) {
        if (model.isToday()){
            holder.day.setTypeface(null, Typeface.BOLD);
            holder.time.setTypeface(null, Typeface.BOLD);
            holder.day.setTextColor(holder.day.getContext().getResources().getColor(android.R.color.white));
            holder.time.setTextColor(holder.time.getContext().getResources().getColor(android.R.color.white));
            if (model.getWorkTypeInformation().getBarWorkTimeType() == BarByWorkTime.CLOSES_SOON)
                holder.type.setVisibility(View.VISIBLE);
        } else {
            holder.type.setVisibility(View.GONE);
        }
        holder.day.setText(model.getDay());
        holder.time.setText(model.getTime());
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public void setModels(List<BarDetailWorkHoursModel> models) {
        this.models = models;
        notifyDataSetChanged();
    }
}
