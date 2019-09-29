package app.gateway.bars.list.cache.get;

import java.util.List;

import app.core.bars.list.get.entity.BarModel;
import app.gateway.bars.list.cache.holder.BarListRealTimeHolder;
import rx.Observable;

public class GetBarListRealTimeGateway implements GetBarListInRealTimeGateway {

    @Override public Observable<List<BarModel>> get() {
        return BarListRealTimeHolder.getList();
    }
}
