package app.gateway.bars.locations.cache.holder;

import java.util.List;

import app.core.bars.locations.entity.LocationsModel;
import rx.Observable;

public class LocationsListRealTimeHolder {
    private static List<LocationsModel> locationsList;
    private static final Object locationsListLock = new Object();

    public static Observable<List<LocationsModel>> setList(List<LocationsModel> models){
        synchronized (locationsListLock){
            LocationsListRealTimeHolder.locationsList = models;
           return getList();
        }
    }

    public static Observable<List<LocationsModel>> getList(){
        synchronized (locationsListLock){
          return Observable.just(LocationsListRealTimeHolder.locationsList);
        }
    }

    public static void clear(){
        synchronized (locationsListLock){
            locationsList = null;
        }
    }
}
