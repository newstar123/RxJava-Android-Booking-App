package app.delivering.mvp.ride.order.fare.apply.model;


import rx.Subscription;

public class OnOrderRideFareReset {
    public static Subscription subscribe;
    public static void send(){
        if (subscribe != null && !subscribe.isUnsubscribed())
            subscribe.unsubscribe();
    }
}
