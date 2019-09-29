package app.delivering.component.bar.detail.description;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import app.R;

public class DescriptionsPagerAdapter extends PagerAdapter {
    private final LayoutInflater layoutInflater;
    private List<String> descriptions;

    public DescriptionsPagerAdapter(Context context, List<String> descriptions) {
        this.descriptions = descriptions;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override public int getCount() {
        return descriptions.size();
    }

    @Override public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((TextView)object);
    }

    @NonNull @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        TextView itemView = (TextView) layoutInflater.inflate(R.layout.item_bar_detail_description, container, false);
        itemView.setText(descriptions.get(position));
        container.addView(itemView);
        return itemView;
    }

    @Override public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

    }
}
