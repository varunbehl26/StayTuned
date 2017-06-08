package varunbehl.showstime.util;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by varunbehl on 07/06/17.
 */


public class CustomScrollView extends NestedScrollView {
    private Runnable scrollerTask;
    private int initialPosition;

    private int newCheck = 100;
    private boolean mScrollable = true;
    private OnScrollStoppedListener onScrollStoppedListener;

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        scrollerTask = new Runnable() {

            public void run() {

                int newPosition = getScrollY();
                if (initialPosition - newPosition == 0) {//has stopped

                    if (onScrollStoppedListener != null) {

                        onScrollStoppedListener.onScrollStopped();
                    }
                } else {
                    initialPosition = getScrollY();
                    CustomScrollView.this.postDelayed(scrollerTask, newCheck);
                }
            }
        };
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // if we can scroll pass the event to the superclass
                if (mScrollable) return super.onTouchEvent(ev);
                // only continue to handle the touch event if scrolling enabled
                return mScrollable; // mScrollable is always false at this point
            default:
                return super.onTouchEvent(ev);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Don't do anything with intercepted touch events if
        // we are not scrollable
        if (!mScrollable) return false;
        else return super.onInterceptTouchEvent(ev);
    }

    public void setScrollingEnabled(boolean enabled) {
        mScrollable = enabled;
    }

    public void setOnScrollStoppedListener(CustomScrollView.OnScrollStoppedListener listener) {
        onScrollStoppedListener = listener;
    }

    public void startScrollerTask() {

        initialPosition = getScrollY();
        CustomScrollView.this.postDelayed(scrollerTask, newCheck);
    }

    public interface OnScrollStoppedListener {
        void onScrollStopped();
    }

}