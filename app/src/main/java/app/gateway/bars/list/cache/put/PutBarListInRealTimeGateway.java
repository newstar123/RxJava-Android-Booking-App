package app.gateway.bars.list.cache.put;

import java.util.List;

import app.core.bars.list.get.entity.BarModel;
import app.gateway.bars.list.cache.holder.BarListRealTimeHolder;
import rx.Observable;

public class PutBarListInRealTimeGateway implements PutBarsInRealTimeGateway{
    @Override public Observable<List<BarModel>> put(List<BarModel> list) {
        return BarListRealTimeHolder.setList(list);
    }
}
