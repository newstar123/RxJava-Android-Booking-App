package app.gateway.permissions;

public enum PermissionState {
    NO_SAVED_VALUE(0),
    ALLOWED(1),
    DENIED(2),
    DO_NOT_ASK_AGAIN(3);

    private int index;

    PermissionState(int index) {
        this.index = index;
    }

    public static PermissionState convertToState(int index){
        for (PermissionState state : PermissionState.values())
            if (state.index == index)
                return state;
        return NO_SAVED_VALUE;
    }

    public int getIndex() {
        return index;
    }
}
