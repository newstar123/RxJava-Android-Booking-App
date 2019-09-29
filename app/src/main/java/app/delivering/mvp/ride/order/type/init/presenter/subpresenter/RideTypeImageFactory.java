package app.delivering.mvp.ride.order.type.init.presenter.subpresenter;


import android.support.annotation.DrawableRes;

import app.R;

public class RideTypeImageFactory {


 @DrawableRes public int get(String rideType){
      if (rideType.equalsIgnoreCase(RideCategoryFactory.POOL))
          return R.drawable.selector_uber_type_pool;
      if (rideType.equalsIgnoreCase(RideCategoryFactory.uberX))
          return R.drawable.selector_uber_type_x;
      if (rideType.equalsIgnoreCase(RideCategoryFactory.SELECT))
          return R.drawable.selector_uber_type_select;
     if (rideType.equalsIgnoreCase(RideCategoryFactory.uberSELECT))
         return R.drawable.selector_uber_type_select;
      if (rideType.equalsIgnoreCase(RideCategoryFactory.BLACK))
          return R.drawable.selector_uber_type_black;
      if (rideType.equalsIgnoreCase(RideCategoryFactory.SUV))
          return R.drawable.selector_uber_type_suv;
      if (rideType.equalsIgnoreCase(RideCategoryFactory.uberXL))
          return R.drawable.selector_uber_type_xl;
      if (rideType.equalsIgnoreCase(RideCategoryFactory.ASSIST))
          return R.drawable.selector_uber_type_assist;
      if (rideType.equalsIgnoreCase(RideCategoryFactory.WAV))
          return R.drawable.selector_uber_type_wav;
      return R.drawable.selector_uber_type_default;
  }
}
