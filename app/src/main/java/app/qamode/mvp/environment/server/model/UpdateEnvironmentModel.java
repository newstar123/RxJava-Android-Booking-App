package app.qamode.mvp.environment.server.model;

public class UpdateEnvironmentModel {
    private int hostPosition;
    private int typePosition;
    private int urnPosition;

    public UpdateEnvironmentModel(int hostPosition, int typePosition, int urnPosition) {
        this.hostPosition = hostPosition;
        this.typePosition = typePosition;
        this.urnPosition = urnPosition;
    }

    public int getHostPosition() {
        return hostPosition;
    }

    public int getTypePosition() {
        return typePosition;
    }

    public int getUrnPosition() {
        return urnPosition;
    }
}
