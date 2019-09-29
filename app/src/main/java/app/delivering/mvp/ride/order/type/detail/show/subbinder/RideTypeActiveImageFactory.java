package app.delivering.mvp.ride.order.type.detail.show.subbinder;


import android.support.annotation.DrawableRes;

import app.R;
import app.delivering.mvp.ride.order.type.init.presenter.subpresenter.RideCategoryFactory;

public class RideTypeActiveImageFactory {


 @DrawableRes public int get(String rideType){
      if (rideType.equals(RideCategoryFactory.POOL))
          return R.drawable.inset_uber_type_x_active;
      if (rideType.equals(RideCategoryFactory.uberX))
          return R.drawable.inset_uber_type_x_active;
      if (rideType.equals(RideCategoryFactory.SELECT))
          return R.drawable.inset_uber_type_select_active;
     if (rideType.equals(RideCategoryFactory.uberSELECT))
         return R.drawable.inset_uber_type_select_active;
      if (rideType.equals(RideCategoryFactory.BLACK))
          return R.drawable.inset_uber_type_black_active;
      if (rideType.equals(RideCategoryFactory.SUV))
          return R.drawable.inset_uber_type_suv_active;
      if (rideType.equals(RideCategoryFactory.uberXL))
          return R.drawable.inset_uber_type_xl_active;
      if (rideType.equals(RideCategoryFactory.ASSIST))
          return R.drawable.inset_uber_type_assist_active;
      if (rideType.equals(RideCategoryFactory.WAV))
          return R.drawable.inset_uber_type_wav_active;
      return R.drawable.svg_uber_type_default_active;
  }
}
