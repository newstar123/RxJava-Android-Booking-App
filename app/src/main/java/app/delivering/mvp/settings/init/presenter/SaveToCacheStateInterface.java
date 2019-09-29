package app.delivering.mvp.settings.init.presenter;


import rx.Observable;

public interface SaveToCacheStateInterface {
    public Observable<Boolean> save(boolean value);
}
