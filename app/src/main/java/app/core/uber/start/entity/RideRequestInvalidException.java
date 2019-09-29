package app.core.uber.start.entity;


public class RideRequestInvalidException extends RuntimeException {
    private Throwable throwable;

    public RideRequestInvalidException(Throwable throwable) {
        this.throwable = throwable;
    }
}
