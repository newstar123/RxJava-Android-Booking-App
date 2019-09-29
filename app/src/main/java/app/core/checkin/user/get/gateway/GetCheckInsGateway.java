package app.core.checkin.user.get.gateway;


import java.util.List;

import app.core.checkin.user.get.entity.GetCheckInsResponse;
import rx.Observable;

public interface GetCheckInsGateway {
   Observable<List<GetCheckInsResponse>> get(int recent);
}
