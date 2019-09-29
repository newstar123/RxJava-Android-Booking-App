package app.gateway.bars.list.cache.holder;

import java.util.List;

import app.core.bars.list.get.entity.BarModel;
import rx.Observable;

public class BarListRealTimeHolder {
    private static List<BarModel> barList;
    private static final Object barListLock = new Object();

    public static Observable<List<BarModel>> setList(List<BarModel> models){
        synchronized (barListLock){
            BarListRealTimeHolder.barList = models;
           return getList();
        }
    }

    public static Observable<List<BarModel>> getList(){
        synchronized (barListLock){
            return Observable.just(BarListRealTimeHolder.barList);
        }
    }

    public static void clear(){
        synchronized (barListLock){
            barList = null;
        }
    }
}
