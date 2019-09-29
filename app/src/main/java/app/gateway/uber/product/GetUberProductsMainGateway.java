package app.gateway.uber.product;


import com.google.android.gms.maps.model.LatLng;

import app.core.uber.product.entity.UberProductsResponse;
import app.core.uber.product.gateway.GetUberProductsGateway;
import app.delivering.component.BaseActivity;
import rx.Observable;

public class GetUberProductsMainGateway implements GetUberProductsGateway {
      private final GetUberProductsSdkGateway getUberProductsSdkGateway;

    public GetUberProductsMainGateway(BaseActivity activity) {
        getUberProductsSdkGateway = new GetUberProductsSdkGateway(activity);
    }

    @Override public Observable<UberProductsResponse> get(LatLng start) {
        return getUberProductsSdkGateway.get(start);
    }
}
