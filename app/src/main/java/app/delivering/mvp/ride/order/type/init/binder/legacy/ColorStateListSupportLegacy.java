package app.delivering.mvp.ride.order.type.init.binder.legacy;


import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.ColorRes;

public class ColorStateListSupportLegacy {
    public static ColorStateList get(Context context, @ColorRes int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return context.getResources().getColorStateList(colorId, context.getTheme());
        return context.getResources().getColorStateList(colorId);
    }
}
