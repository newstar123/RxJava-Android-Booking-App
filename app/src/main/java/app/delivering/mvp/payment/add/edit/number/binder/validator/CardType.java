package app.delivering.mvp.payment.add.edit.number.binder.validator;

import android.text.TextUtils;

import java.util.regex.Pattern;

import app.R;


public enum CardType {

    VISA("^4\\d*",
            R.drawable.inset_visa_logo,
            16, 16,
            3),
    MASTERCARD("^5[1-5]\\d*",
            R.drawable.inset_mastercard_logo,
            16, 16,
            3),
    DISCOVER("^(6011|65|64[4-9]|622)\\d*",
            R.drawable.inset_discover_logo,
            16, 16,
            3),
    AMEX("^3[47]\\d*",
            R.drawable.inset_amex_logo,
            15, 15,
            4),
    DINERS_CLUB("^(36|38|30[0-5])\\d*",
            R.drawable.inset_diners_logo,
            14, 14,
            3),
    JCB("^35\\d*",
            R.drawable.inset_jcb_logo,
            16, 16,
            3),
    MAESTRO("^(5018|5020|5038|6304|6759|676[1-3])\\d*",
            R.drawable.inset_maestro_logo,
            12, 19,
            3),
    UNION_PAY("^62\\d*",
            R.drawable.inset_unionpay_logo,
            16, 19,
            3),
    UNKNOWN("",
            R.drawable.inset_payment_blue,
            12, 19,
            3);

    private static final int[] AMEX_SPACE_INDICES = {4, 10};
    private static final int[] DEFAULT_SPACE_INDICES = {4, 8, 12};

    private final Pattern mPattern;
    private final int mFrontResource;
    private final int mMinCardLength;
    private final int mMaxCardLength;
    private final int mSecurityCodeLength;

    private CardType(String regex, int frontResource, int minCardLength, int maxCardLength,
                     int securityCodeLength) {
        mPattern = Pattern.compile(regex);
        mFrontResource = frontResource;
        mMinCardLength = minCardLength;
        mMaxCardLength = maxCardLength;
        mSecurityCodeLength = securityCodeLength;
    }

    /**
     * @return The regex matching this card type.
     */
    public Pattern getPattern() {
        return mPattern;
    }

    /**
     * @return The android resource id for the front card image, highlighting card number format.
     */
    public int getFrontResource() {
        return mFrontResource;
    }

    public int getSecurityCodeLength() {
        return mSecurityCodeLength;
    }

    public int getMinCardLength() {
        return mMinCardLength;
    }

    public int getMaxCardLength() {
        return mMaxCardLength;
    }

    public int[] getSpaceIndices() {
        return this == AMEX ? AMEX_SPACE_INDICES : DEFAULT_SPACE_INDICES;
    }

    public boolean isLegalCardLength(String cardNumber) {
        final int len = cardNumber.length();
        return len >= mMinCardLength && len <= mMaxCardLength;
    }

    /**
     * @param cardNumber The card number to validate.
     * @return {@code true} if this card number is locally valid.
     */
    public boolean validate(String cardNumber) {
        if (TextUtils.isEmpty(cardNumber)) {
            return false;
        }

        final int numberLength = cardNumber.length();
        if (numberLength < mMinCardLength || numberLength > mMaxCardLength) {
            return false;
        } else if (!mPattern.matcher(cardNumber).matches()) {
            return false;
        }
        return CardUtils.isLuhnValid(cardNumber);
    }

    public static CardType forCardNumber(String cardNumber) {
        for (final CardType cardType : values()) {
            final Pattern pattern = cardType.getPattern();
            if (pattern.matcher(cardNumber).matches()) {
                return cardType;
            }
        }
        return UNKNOWN;
    }

    public static CardType toTypeFromName(String typeName) {
        for (final CardType cardType : values()) {
            if (cardType.name().equalsIgnoreCase(typeName))
                return cardType;
        }
        return UNKNOWN;
    }

}