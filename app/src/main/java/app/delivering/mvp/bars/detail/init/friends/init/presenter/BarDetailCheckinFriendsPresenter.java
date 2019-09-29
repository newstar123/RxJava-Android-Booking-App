package app.delivering.mvp.bars.detail.init.friends.init.presenter;

import java.util.ArrayList;

import app.core.checkin.friends.entity.CheckinsFriendModel;
import app.core.checkin.friends.entity.WhoseHereModel;
import app.core.facebook.photos.interactor.GetCheckInsPhotosInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.bars.detail.init.friends.init.model.CheckInsPeopleType;
import app.delivering.mvp.bars.detail.init.friends.init.model.CheckinsPersonModel;
import rx.Observable;

public class BarDetailCheckinFriendsPresenter extends BasePresenter<Long, Observable<WhoseHereModel>> {
    private GetCheckInsPhotosInteractor getCheckInsPhotosInteractor;

    public BarDetailCheckinFriendsPresenter(BaseActivity activity) {
        super(activity);
        getCheckInsPhotosInteractor = new GetCheckInsPhotosInteractor(getActivity());
    }

    @Override public Observable<WhoseHereModel> process(Long barId) {
        return getCheckInsPhotosInteractor.process(barId)
                .concatMap(this::getCorrectListByTypes);
    }

    private Observable<WhoseHereModel> getCorrectListByTypes(WhoseHereModel whoseHereModel) {
        return Observable.create(subscriber -> {
            if (whoseHereModel.isAuthorized() && whoseHereModel.isFBVisible())
                subscriber.onNext(getCorrectList(whoseHereModel));
            else
                subscriber.onNext(getMockList(whoseHereModel));
            subscriber.onCompleted();
        });
    }

    private WhoseHereModel getCorrectList(WhoseHereModel whoseHereModel) {
        ArrayList<CheckinsPersonModel> people = new ArrayList<>();
        if (whoseHereModel.getFriends() != null)
            for (CheckinsFriendModel model : whoseHereModel.getFriends()) {
                CheckinsPersonModel personModel = new CheckinsPersonModel();
                personModel.setPerson(model);
                personModel.setType(CheckInsPeopleType.FRIENDS);
                people.add(personModel);
            }
        if (whoseHereModel.getCheckIns() != null)
            for (CheckinsFriendModel model : whoseHereModel.getCheckIns()) {
                CheckinsPersonModel personModel = new CheckinsPersonModel();
                personModel.setPerson(model);
                personModel.setType(CheckInsPeopleType.PEOPLE);
                people.add(personModel);
            }
        whoseHereModel.setPeople(people);
        return whoseHereModel;
    }

    private WhoseHereModel getMockList(WhoseHereModel whoseHereModel) {
        ArrayList<CheckinsPersonModel> people = new ArrayList<>();
        for (CheckinsFriendModel model : whoseHereModel.getCheckIns()) {
            CheckinsPersonModel personModel = new CheckinsPersonModel();
            personModel.setPerson(model);
            personModel.setType(CheckInsPeopleType.MOCK);
            people.add(personModel);
        }
        whoseHereModel.setPeople(people);
        return whoseHereModel;
    }
}
