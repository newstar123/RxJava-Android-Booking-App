package app.core.beacon.bluetooth.gateway;

import app.core.payment.regular.model.EmptyResponse;
import rx.Observable;

public interface CheckBluetoothStateGateway {
    Observable<EmptyResponse> check();
}
