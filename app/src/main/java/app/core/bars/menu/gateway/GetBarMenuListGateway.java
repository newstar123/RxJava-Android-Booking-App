package app.core.bars.menu.gateway;

import app.core.bars.menu.entity.BarMenuRestModel;
import rx.Observable;

public interface GetBarMenuListGateway {
    Observable<BarMenuRestModel> get(long barId);
}
