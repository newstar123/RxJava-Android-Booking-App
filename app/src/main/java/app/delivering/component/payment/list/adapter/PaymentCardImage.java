package app.delivering.component.payment.list.adapter;

import app.R;

public class PaymentCardImage {
    private static final String VISA = "Visa";
    private static final String MASTER_CARD = "MasterCard";
    private static final String AMERICAN = "American Express";
    private static final String JCB = "JCB";
    private static final String DISCOVER = "Discover";
    private static final String DINERS_CLUB = "Diners Club";

    public static int get(String type) {
        switch (type) {
            case VISA:
                return R.drawable.inset_visa_logo;
            case MASTER_CARD:
                return R.drawable.inset_mastercard_logo;
            case AMERICAN:
                return R.drawable.inset_amex_logo;
            case JCB:
                return R.drawable.inset_jcb_logo;
            case DISCOVER:
                return R.drawable.inset_discover_logo;
            case DINERS_CLUB:
                return R.drawable.inset_diners_logo;
            default:
                return R.drawable.inset_payment_card_number;
        }
    }
}
