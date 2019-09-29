package app.core.uber.delete.activity.gateway;


import app.core.uber.delete.activity.entity.DeleteUberRequest;
import app.core.uber.delete.activity.entity.DeleteUberResponse;
import rx.Observable;

public interface DeleteUberGateway {
    Observable<DeleteUberResponse> delete(DeleteUberRequest request);
}
