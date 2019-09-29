package app.core.checkin.tip.entity;

public class ConfirmTipsRequest {
    private ExactGratuity exactGratuity;
    private Gratuity gratuity;
    private long checkInID;

    public ConfirmTipsRequest(ExactGratuity exactGratuity, Gratuity gratuity, boolean isExact) {
        setUpParams(exactGratuity, gratuity, isExact);
    }

    private void setUpParams(ExactGratuity exactGratuity, Gratuity gratuity, boolean isExact) {
        if (isExact)
            this.exactGratuity = exactGratuity;
        else
            this.gratuity = gratuity;
    }

    public ExactGratuity getExactGratuity() {
        return exactGratuity;
    }

    public void setExactGratuity(ExactGratuity exactGratuity) {
        this.exactGratuity = exactGratuity;
    }

    public long getCheckInID() {
        return checkInID;
    }

    public void setCheckInID(long checkInID) {
        this.checkInID = checkInID;
    }

    public Gratuity getGratuity() {
        return gratuity;
    }

    public void setGratuity(Gratuity gratuity) {
        this.gratuity = gratuity;
    }
}
