package app.delivering.component.bar.map.cluster;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class TitledMarkerCreator {
    private View icon;
    private int titleID;

    public TitledMarkerCreator(Context context, int iconLayoutId, int markerTitleID) {
        this.titleID = markerTitleID;
        icon = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(iconLayoutId, null);
    }

    public void addMarker(GoogleMap map, LatLng barPoint, String color, String title) {
        map.addMarker(new MarkerOptions()
                .icon(getMarkerBitmap(title, Color.parseColor(color)))
                .position(barPoint)
                .draggable(false)
                .anchor(0.5f, 1f));
    }

    public BitmapDescriptor getMarkerBitmap(String title, int color) {
        ((TextView) icon.findViewById(titleID)).setText(title);
        icon.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        icon.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        icon.layout(0, 0, icon.getMeasuredWidth(), icon.getMeasuredHeight());
        final Bitmap bitmap = Bitmap.createBitmap(icon.getMeasuredWidth(), icon.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        icon.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
