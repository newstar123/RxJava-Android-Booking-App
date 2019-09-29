package app.delivering.component.profile.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

public class SpinnerHintAdapter extends ArrayAdapter<String> {

    public SpinnerHintAdapter(Context context, int theLayoutResId, List<String> objects) {
        super(context, theLayoutResId, objects);
    }

    @Override
    public int getCount() {
        int count = super.getCount();
        return count > 0 ? count - 1 : count;
    }
}
