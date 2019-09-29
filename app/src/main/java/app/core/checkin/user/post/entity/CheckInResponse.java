package app.core.checkin.user.post.entity;

import java.io.Serializable;

import app.core.checkin.user.get.entity.GetCheckInsResponse;

public class CheckInResponse implements Serializable {
   private GetCheckInsResponse checkin;
   private boolean isDiscountAvailable;
   private boolean isMarkAlreadyDisplayed;
   private boolean isFreeRideAlreadyAvailable;

   public GetCheckInsResponse getCheckin() {
      return checkin;
   }

   public boolean isMarkAlreadyDisplayed() {
      return isMarkAlreadyDisplayed;
   }

   public void setMarkAlreadyDisplayed(boolean markAlreadyDisplayed) {
      isMarkAlreadyDisplayed = markAlreadyDisplayed;
   }

   public void setCheckin(GetCheckInsResponse checkin) {
      this.checkin = checkin;
   }

   public boolean isDiscountAvailable() {
      return isDiscountAvailable;
   }

   public void setDiscountAvailable(boolean discountAvailable) {
      isDiscountAvailable = discountAvailable;
   }

   public boolean isFreeRideAlreadyAvailable() {
      return isFreeRideAlreadyAvailable;
   }

   public void setFreeRideAlreadyAvailable(boolean freeRideAlreadyAvailable) {
      isFreeRideAlreadyAvailable = freeRideAlreadyAvailable;
   }
}
