package app.gateway.ride.token.register;

import rx.Observable;


public interface RegisterRideAuthGateway{
    Observable<RegisterRideAuthResponse> post(RegisterRideAuthRequest request);
}
