package app.gateway.payment.token;


import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import app.CustomApplication;
import app.R;
import app.core.payment.add.entity.AddPaymentModel;
import app.core.payment.add.gateway.GetPaymentTokenGateway;
import rx.Observable;
import rx.Subscriber;

public class GetStripePaymentTokenGateway implements GetPaymentTokenGateway {

    @Override public Observable<String> get(AddPaymentModel addPaymentModel) {
        String expMonthString = addPaymentModel.getExpMonth();
        int expMonthInteger = Integer.valueOf(expMonthString);
        String expYearString = addPaymentModel.getExpYear();
        int expYearInteger = Integer.valueOf(expYearString);
        Card.Builder cardBuilder = new Card.Builder(addPaymentModel.getNumber(),
                expMonthInteger,
                expYearInteger,
                addPaymentModel.getCvc());
        Card card = cardBuilder.addressZip(addPaymentModel.getZipCode())
                .build();
        return Observable.create(subscriber -> {
            try {
                tryGetToken(subscriber, card);
            } catch (AuthenticationException e) {
                subscriber.onError(e);
            }
        });
    }

    private void tryGetToken(Subscriber<? super String> subscriber, Card card) throws AuthenticationException {
        Stripe stripe = new Stripe(CustomApplication.get().getString(R.string.stripe_key));
        stripe.createToken(
                card,
                new TokenCallback() {
                    public void onSuccess(Token token) {
                        subscriber.onNext(token.getId());
                    }

                    public void onError(Exception error) {
                        subscriber.onError(error);
                    }
                }
        );
    }
}
