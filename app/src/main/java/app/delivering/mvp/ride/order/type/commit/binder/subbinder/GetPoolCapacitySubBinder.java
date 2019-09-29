package app.delivering.mvp.ride.order.type.commit.binder.subbinder;


import android.widget.RadioGroup;

import app.R;
import app.delivering.component.BaseActivity;
import butterknife.ButterKnife;

public class GetPoolCapacitySubBinder {
   RadioGroup poolCapacityRadioGroup;

    public GetPoolCapacitySubBinder(BaseActivity activity) {
        poolCapacityRadioGroup = activity.findViewById(R.id.order_ride_pool_capacity);
    }

    public int getPoolCapacity() {
        int checkedRadioButtonId = poolCapacityRadioGroup.getCheckedRadioButtonId();
        if (checkedRadioButtonId == R.id.order_ride_pool_capacity_1)
            return 1;
        else
            return 2;
    }
}
