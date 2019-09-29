package app.core.checkin.friends.gateway;

import java.util.List;

import app.core.checkin.friends.entity.CheckinsFriendModel;
import rx.Observable;

public interface GetBarCheckInsListGateway {
    Observable<List<CheckinsFriendModel>> get(long barId);
}
