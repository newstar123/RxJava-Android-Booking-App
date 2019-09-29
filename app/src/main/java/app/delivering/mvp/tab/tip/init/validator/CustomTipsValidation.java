package app.delivering.mvp.tab.tip.init.validator;

import android.util.Pair;

import app.core.checkin.tip.entity.ExactGratuity;
import app.core.checkin.tip.entity.Gratuity;
import app.core.checkin.user.post.entity.CheckInResponse;
import rx.Observable;

public interface CustomTipsValidation {
    public Observable<Pair<String, Double>> countMinPercentVal(CheckInResponse checkInResponse);
    public String formatInput(double val, long roundedVal, String inputVal);
    public Gratuity setUpGratuity(int gratuity);
    public ExactGratuity setUpExactGratuity(double exactGratuity);
}
