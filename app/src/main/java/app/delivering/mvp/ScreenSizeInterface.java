package app.delivering.mvp;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public interface ScreenSizeInterface {
    public default Point getDisplaySize(Context context) {
        Point size = new Point();
        try {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            display.getSize(size);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return size;
    }
}
