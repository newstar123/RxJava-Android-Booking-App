package app.delivering.mvp;


import android.app.Service;

public abstract class BaseServiceBinder {
    private Service service;

    public BaseServiceBinder(Service service) {
        this.service = service;
    }

    public Service getService() {
        return service;
    }
}
