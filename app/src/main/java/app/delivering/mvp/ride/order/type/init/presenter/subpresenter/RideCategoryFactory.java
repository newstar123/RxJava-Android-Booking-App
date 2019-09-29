package app.delivering.mvp.ride.order.type.init.presenter.subpresenter;


public class RideCategoryFactory {
    public final static String POOL = "POOL";
    public final static String uberX = "uberX";
    public final static String SELECT = "SELECT";
    public final static String uberSELECT = "uberSELECT";
    public final static String uberXL = "uberXL";
    public final static String BLACK = "BLACK";
    public final static String SUV = "SUV";
    public final static String ASSIST = "ASSIST";
    public final static String TAXI = "TAXI";
    public final static String WAV = "WAV";
    public final static String Economy = "Economy";
    public final static String Premium = "Premium";
    public final static String Extra_Seats = "Extra Seats";
    public final static String Other = "Other";

  public String get(String rideType){
      if (rideType.equalsIgnoreCase(POOL))
          return Economy;
      if (rideType.equalsIgnoreCase(uberX))
          return Economy;
      if (rideType.equalsIgnoreCase(uberXL))
          return Economy;
      if (rideType.equalsIgnoreCase(SELECT))
          return Premium;
      if (rideType.equalsIgnoreCase(uberSELECT))
          return Premium;
      if (rideType.equalsIgnoreCase(BLACK))
          return Premium;
      if (rideType.equalsIgnoreCase(SUV))
          return Premium;
      if (rideType.equalsIgnoreCase(ASSIST))
          return Other;
      if (rideType.equalsIgnoreCase(WAV))
          return Other;
      return "";
  }
}
