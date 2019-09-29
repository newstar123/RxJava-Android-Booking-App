package app.delivering.mvp.profile.edit.init.presenter;

import android.text.TextUtils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import app.core.profile.get.entity.ProfileModel;
import app.core.profile.get.interactor.GetProfileInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseOutputPresenter;
import app.delivering.mvp.profile.edit.init.model.InitProfileModel;
import app.gateway.profile.get.GetProfileRestGateway;
import rx.Observable;

public class ProfileInitPresenter extends BaseOutputPresenter<Observable<InitProfileModel>> {
    private final GetProfileInteractor getProfileInteractor;

    public ProfileInitPresenter(BaseActivity activity) {
        super(activity);
        getProfileInteractor = new GetProfileInteractor(activity, new GetProfileRestGateway(activity));
    }

    @Override public Observable<InitProfileModel> process() {
        return getProfileInteractor.process().map(this::prepare);
    }

    private InitProfileModel prepare(ProfileModel profileModel) {
        try {
            return tryPrepare(profileModel);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }

    private InitProfileModel tryPrepare(ProfileModel profileModel) throws ParseException {
        InitProfileModel initProfileModel = new InitProfileModel();
        initProfileModel.setFormattedBirthday(parseBirthday(profileModel.getBirthdate()));
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        String phoneNumber = "";
        String countryCode = "";
        try {
            phoneNumber = getPhoneNumber(phoneUtil, profileModel.getPhone());
            countryCode = getCountryCode(phoneUtil, profileModel.getPhone());
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
        initProfileModel.setCountryCode(countryCode);
        initProfileModel.setPhone(phoneNumber);
        initProfileModel.setProfileModel(profileModel);
        return initProfileModel;
    }

    private String parseBirthday(String formattedBirthday) {
        try {
            SimpleDateFormat parseDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
            Date date = parseDateFormat.parse(formattedBirthday);
            return simpleDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String getPhoneNumber(PhoneNumberUtil phoneUtil, String phone) throws NumberParseException {
        if (TextUtils.isEmpty(phone)) throw new NumberParseException(NumberParseException.ErrorType.NOT_A_NUMBER, "empty");
        if (phone.contains("+")) {
            Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(phone, "");
            return String.valueOf(phoneNumber.getNationalNumber());
        } else
            return phone;
    }

    private String getCountryCode(PhoneNumberUtil phoneUtil, String phone) throws NumberParseException {
        if (TextUtils.isEmpty(phone)) throw new NumberParseException(NumberParseException.ErrorType.NOT_A_NUMBER, "empty");
        if (phone.contains("+")) {
            Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(phone, "");
            return "+" + String.valueOf(phoneNumber.getCountryCode());
        } else
            return "";
    }
}
