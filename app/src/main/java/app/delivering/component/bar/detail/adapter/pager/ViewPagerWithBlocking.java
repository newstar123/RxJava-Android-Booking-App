package app.delivering.component.bar.detail.adapter.pager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ViewPagerWithBlocking extends ViewPager {
    private boolean enabled;

    public ViewPagerWithBlocking(Context context) {
        super(context);
        this.enabled = true;
    }

    public ViewPagerWithBlocking(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try{
            if (this.enabled && isValidPointers(event))
                return super.onTouchEvent(event);
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        try{
            if (this.enabled && isValidPointers(event))
                return super.onInterceptTouchEvent(event);
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private boolean isValidPointers(MotionEvent event) {
        int pointerIndex =(event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        return pointerIndex <= event.getPointerCount() - 1;
    }

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
