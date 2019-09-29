package app.delivering.mvp.advert.progress.binder.scale;


public class ScaleLimits {
    public static ScaleLimit SCALE_TO_0_100;
    static {
        SCALE_TO_0_100 = new ScaleLimit();
        SCALE_TO_0_100.setMin(0);
        SCALE_TO_0_100.setMax(100);
    }
    private final double baseMin;
    private final double baseMax;
    private final double limitMin;
    private final double limitMax;

    public ScaleLimits(ScaleLimit base, ScaleLimit toScale) {
        baseMax = base.getMax();
        baseMin = base.getMin();
        limitMax = toScale.getMax();
        limitMin = toScale.getMin();
    }

    public double scale(double valueIn) {
        return ((limitMax - limitMin) * (valueIn - baseMin) / (baseMax - baseMin)) + limitMin;
    }

}
