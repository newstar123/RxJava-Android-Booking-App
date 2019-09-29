package app.gateway.uber.product;


import com.google.android.gms.maps.model.LatLng;
import com.uber.sdk.rides.client.UberRidesApi;
import com.uber.sdk.rides.client.model.Product;
import com.uber.sdk.rides.client.model.ProductsResponse;
import com.uber.sdk.rides.client.services.RidesService;

import java.io.IOException;
import java.util.List;

import app.core.uber.auth.entity.UberAuthToken;
import app.core.uber.product.entity.UberProductResponse;
import app.core.uber.product.entity.UberProductsResponse;
import app.core.uber.product.gateway.GetUberProductsGateway;
import app.delivering.component.BaseActivity;
import app.gateway.uber.auth.GetUberAuthTokenSdkGateway;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class GetUberProductsSdkGateway implements GetUberProductsGateway {
    private final GetUberAuthTokenSdkGateway getUberAuthTokenGateway;

    public GetUberProductsSdkGateway(BaseActivity activity) {
        getUberAuthTokenGateway = new GetUberAuthTokenSdkGateway(activity);
    }

    @Override public Observable<UberProductsResponse> get(LatLng start) {
        return getUberAuthTokenGateway.get()
                .concatMap(uberAuthToken -> doProductsRequest(uberAuthToken, start));
    }

    private Observable<UberProductsResponse> doProductsRequest(UberAuthToken uberToken, LatLng start) {
        return Observable.create(new Observable.OnSubscribe<UberProductsResponse>() {
            @Override public void call(Subscriber<? super UberProductsResponse> subscriber) {
                RidesService service = UberRidesApi.with(uberToken.getSession()).build().createService();
                Call<ProductsResponse> rideEstimateCall =
                        service.getProducts((float) start.latitude, (float) start.longitude);
                try {
                    UberProductsResponse uberProductsResponse = convert(rideEstimateCall);
                    subscriber.onNext(uberProductsResponse);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    private UberProductsResponse convert(Call<ProductsResponse> rideEstimateCall) throws IOException {
        Response<ProductsResponse> execute = rideEstimateCall.execute();
        ProductsResponse body = execute.body();
        List<Product> sdkProducts = body.getProducts();
        return Observable.from(sdkProducts)
                .filter(Product::isUpfrontFareEnabled)
                .map(this::convert)
                .toList()
                .map(this::convert)
                .toBlocking()
                .first();
    }

    private UberProductResponse convert(Product product) {
        UberProductResponse response = new UberProductResponse();
        response.setCapacity(product.getCapacity());
        response.setDescription(product.getDescription());
        response.setProductId(product.getProductId());
        response.setShared(product.isShared());
        response.setUpfrontFareEnabled(product.isUpfrontFareEnabled());
        response.setDisplayName(product.getDisplayName());
        return response;
    }

    private UberProductsResponse convert(List<UberProductResponse> products) {
        return new UberProductsResponse(products);
    }
}
