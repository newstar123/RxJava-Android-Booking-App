package app.delivering.mvp.tab.tip.init.presenter;

import android.util.Pair;

import java.text.DecimalFormat;

import app.R;
import app.core.checkin.tip.entity.ConfirmTipsRequest;
import app.core.checkin.tip.entity.ExactGratuity;
import app.core.checkin.tip.entity.Gratuity;
import app.core.checkin.tip.interactor.ConfirmTipsInteractor;
import app.core.checkin.user.get.entity.GetCheckInsResponse;
import app.core.checkin.user.get.entity.Totals;
import app.core.checkin.user.post.entity.CheckInResponse;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.tab.content.model.TabContentModel;
import app.delivering.mvp.tab.content.model.TabContentRequest;
import app.delivering.mvp.tab.tip.init.binder.TipsInitBinder;
import app.delivering.mvp.tab.tip.init.validator.BaseValidator;
import app.delivering.mvp.tab.tip.init.validator.CustomTipsValidation;
import rx.Observable;

public class TipsInitPresenter extends BaseValidator<TabContentRequest, Observable<TabContentModel>> implements CustomTipsValidation {
    private static final String SINGLE_DOT = ".";

    private ConfirmTipsInteractor confirmTipsInteractor;
    private GetCheckInsResponse checkInResponse;

    public TipsInitPresenter(BaseActivity activity) {
        super(activity);
        confirmTipsInteractor = new ConfirmTipsInteractor(getActivity());
    }

    @Override public Observable<CheckInResponse> process(ConfirmTipsRequest confirmTipsRequest) {
        return confirmTipsInteractor.process(confirmTipsRequest);
    }

    @Override
    public Observable<TabContentModel> validate(TabContentRequest request) {
        return Observable.just(request)
                .map(this::setUpTabContent);
    }

    @Override
    public Observable<Pair<String, Double>> countMinPercentVal(CheckInResponse checkInResponse) {
        final double minVal = (double) checkInResponse.getCheckin().getTotals().getSubTotal() * 18 / 10000.;
        return Observable.just(minVal)
                .map(this::formatDecimal);
    }

    @Override
    public Gratuity setUpGratuity(int gratuity) {
        Gratuity gratuityModel = new Gratuity();
        gratuityModel.setTips(gratuity);
        return gratuityModel;
    }

    @Override
    public ExactGratuity setUpExactGratuity(double exactGratuity) {
        ExactGratuity gratuity = new ExactGratuity();
        double cents = exactGratuity * 100.d;
        gratuity.setTips((int) Math.round(cents));
        return gratuity;
    }

    @Override
    public String formatInput(double val, long roundedVal, String inputVal) {
        if (roundedVal == TipsInitBinder.MAX_CUSTOM_TIPS_VAL)
            return formatCustomTipsText(false, -1f, -1, String.valueOf(TipsInitBinder.MAX_CUSTOM_TIPS_VAL));
        else if (roundedVal == 0 && !inputVal.endsWith(SINGLE_DOT))
            return formatCustomTipsText(false, -1f, 0, inputVal);
        else if (inputVal.endsWith(SINGLE_DOT))
            return formatCustomTipsText(false, -1f, -1, inputVal);
        else {
            boolean isParsingAsDecimal = (inputVal.contains(SINGLE_DOT) && inputVal.length() > 2 && !inputVal.endsWith(".00"));
            return formatCustomTipsText(isParsingAsDecimal, val, roundedVal, "");
        }
    }

    private String formatCustomTipsText(boolean isParsingAsDecimal, double val, long roundedVal, String originVal) {
        if ( val == -1f && roundedVal == 0 )
            return String.format(getActivity().getString(R.string.validation_custom_tips), originVal);
        else if ( val == -1f && roundedVal == -1 )
            return String.format(getActivity().getString(R.string.validation_custom_tips), originVal);
        else if (isParsingAsDecimal)
            return String.format(getActivity().getString(R.string.validation_custom_tips), String.valueOf(val));
        else
            return String.format(getActivity().getString(R.string.validation_custom_tips), String.valueOf(roundedVal));
    }

    private Pair<String, Double> formatDecimal(double value) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String minCaseVal = String.format(getActivity().getString(R.string.validation_min_case), decimalFormat.format(value));
        String confirmTipString = String.format("%s ", getActivity().getString(R.string.min_case)).concat(minCaseVal);
        return new Pair<>(confirmTipString, value);
    }

    private TabContentModel setUpTabContent(TabContentRequest request) {
        if (request.getCheckIn() != null)
            checkInResponse = request.getCheckIn();
        TabContentModel model = new TabContentModel();
        Totals totals = checkInResponse.getTotals();
        model.setSubtotalValue(getSubTotal(totals));
        double tipPercentage = calculateTipPercentage(checkInResponse);
        setTips(model, tipPercentage);
        setPromotionAppDiscount(model, checkInResponse, totals);
        model.setTabTaxValue(getFormatTax(totals));
        model.setFreeDrinkDiscount(format(app.R.string.discount_value_with_two_sign, totals.getFreeDrinksDiscount()));
        model.setTotalValue(getFormatDue(totals.getFreeDrinksDiscount(), request, totals));
        return model;
    }

    private String getSubTotal(Totals totals) {
        double subTotal = totals.getUnDiscountedSubTotal();
        return format(R.string.dollars_value_with_two_sign, subTotal / 100.0);
    }

    private void setTips(TabContentModel model, double tipToPay) {
        String tipPercentValue = format(R.string.dollars_value_with_two_sign, tipToPay / 100.0);
        model.setTipPercentValue(tipPercentValue);
        String tipPercentTitle = getActivity().getString(R.string.tips_dash_value_percent);
        model.setTipPercentTitle(tipPercentTitle);
    }

    private double calculateTipPercentage(GetCheckInsResponse checkin) {
        if (checkin.getExactGratuity() > 0) {
            return checkin.getExactGratuity();
        } else if (checkin.getGratuity() > 0) {
            return checkin.getGratuity() * checkin.getTotals().getUnDiscountedSubTotal() / 100;
        }
        return 0.00;
    }

    private String format(int formatId, double value) {
        return String.format(getActivity().getString(formatId), value);
    }

    private void setPromotionAppDiscount(TabContentModel model, GetCheckInsResponse checkIn, Totals totals) {
        if (checkIn.getDiscount() == 0) {
            model.setOffPercentTitle("");
            model.setOffPercentValue("");
        } else {
            String offPercentTitle = String.format(getActivity().getString(R.string.qorum_dash_value_percent_dots), String.valueOf(checkIn.getDiscount()));
            model.setOffPercentTitle(offPercentTitle);
            String offPercentValue = format(R.string.discount_value_with_two_sign, totals.getPromotionDiscount() / 100.0);
            model.setOffPercentValue(offPercentValue);
        }
    }

    private String getFormatTax(Totals totals) {
        return format(R.string.dollars_value_with_two_sign, totals.getApproximateTax() / 100.0);
    }

    private String getFormatDue(double freeDrinksDiscount, TabContentRequest request, Totals totals) {
        double subtotal = (double) totals.getSubTotal() / 100.;
        double discountPrice = (double) ((totals.getPromotionDiscount() > 0d) ? totals.getPromotionDiscount() : (subtotal * (double) checkInResponse.getDiscount() / 100)) / 100.;
        double gratuityPrice = (request.getExGratuity() > 0d) ? request.getExGratuity() : (((double)request.getGratuity() / 100) * (subtotal + freeDrinksDiscount));
        double approximateTax = (double) totals.getApproximateTax() / 100.;
        double totalPrice = subtotal - discountPrice + gratuityPrice + approximateTax;
        return format(R.string.dollars_value_with_two_sign, totalPrice);
    }

}
