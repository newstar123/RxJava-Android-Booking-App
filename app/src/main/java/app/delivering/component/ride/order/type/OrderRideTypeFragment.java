package app.delivering.component.ride.order.type;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.R;
import app.delivering.component.BaseFragment;
import app.delivering.mvp.ride.order.type.init.binder.RideTypesInCategoryInitBinder;
import app.delivering.mvp.ride.order.type.update.binder.UpdateTypeBinder;

public class OrderRideTypeFragment extends BaseFragment {
    public static final String ORDER_TYPE_ID = "ORDER_TYPE_ID";

    @Nullable @Override public View onCreateView(LayoutInflater inflater,
                                                 @Nullable ViewGroup container,
                                                 @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ride_type, container, false);
        initUseCases(rootView);
        return rootView;
    }

    private void initUseCases(View rootView) {
        RideTypesInCategoryInitBinder rideTypesInCategoryInitBinder = new RideTypesInCategoryInitBinder(this);
        addItemForViewsInjection(rideTypesInCategoryInitBinder, rootView);
        UpdateTypeBinder updateTypeBinder = new UpdateTypeBinder(this);
        addToEventBusAndViewInjection(updateTypeBinder, rootView);
    }
}
