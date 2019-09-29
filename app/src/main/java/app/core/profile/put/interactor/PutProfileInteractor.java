package app.core.profile.put.interactor;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import app.core.BaseInteractor;
import app.core.login.facebook.entity.LoginResponse;
import app.core.profile.get.entity.ProfileModel;
import app.core.profile.get.interactor.GetProfileInteractor;
import app.core.profile.put.entity.IdenticalProfilesException;
import app.core.profile.put.entity.PutProfileModel;
import app.delivering.component.BaseActivity;
import app.gateway.profile.get.GetProfileRestGateway;
import app.gateway.profile.put.PutProfileRestGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class PutProfileInteractor implements BaseInteractor<PutProfileModel, Observable<LoginResponse>> {
    private final PutProfileRestGateway putProfileRestGateway;
    private final GetProfileInteractor getProfileInteractor;

    public PutProfileInteractor(BaseActivity activity) {
        getProfileInteractor = new GetProfileInteractor(activity, new GetProfileRestGateway(activity));
        putProfileRestGateway = new PutProfileRestGateway(activity);
    }

    @Override public Observable<LoginResponse> process(PutProfileModel updateProfileModel) {
        return getProfileInteractor.process()
                .concatMap(profileModel -> checkProfileUpdated(updateProfileModel, profileModel))
                .map(aBoolean -> (long) QorumSharedCache.checkUserId().get(BaseCacheType.LONG))
                .concatMap(userId -> putProfileRestGateway.put(userId, updateProfileModel));
    }

    private Observable<Boolean> checkProfileUpdated(PutProfileModel newProfile, ProfileModel existProfile) {
        return Observable.create(subscriber -> {
            Comparator<String> modelComparator = getComparator();
            int differences = 0;
            differences = modelComparator.compare(newProfile.getFirstName(), existProfile.getFirstName());
            differences = differences + modelComparator.compare(newProfile.getLastName(), existProfile.getLastName());
            differences = differences + modelComparator.compare(newProfile.getBirthdate(), existProfile.getBirthdate());
            String newGender = "";
            if (!TextUtils.isEmpty(newProfile.getGender()))
                newGender = newProfile.getGender().substring(0, 1);
            differences = differences + modelComparator.compare(newGender, existProfile.getGender());
            differences = differences + modelComparator.compare(newProfile.getEmail(), existProfile.getEmail());
            differences = differences + modelComparator.compare(newProfile.getZip(), existProfile.getZip());
            differences = differences + modelComparator.compare(newProfile.getPhone(), existProfile.getPhone());
            if (differences == 0)
                subscriber.onError(new IdenticalProfilesException());
            else {
                subscriber.onNext(true);
                subscriber.onCompleted();
            }
        });
    }
    private Comparator<String> getComparator() {
        return (field1, field2) -> {
            if (TextUtils.equals(field1, field2))
                return 0;
            else
                return 1;
        };
    }

    private Comparator<Date> getDateComparator() {
        return (date1, date2) -> {
            SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            String newDate = format.format(date1);
            String existDate = format.format(date2);
            if (newDate.compareTo(existDate) == 0)
                return 0;
            else
                return 1;
        };
    }

}
