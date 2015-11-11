package cn.bookzhan.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by youzi on 2015/11/4.
 */
public class UnScrollViewPager extends ViewPager {
    private float startX;
    private float startY;
    private float tempX;
    private float tempY;

    public UnScrollViewPager(Context context) {
        super(context);
    }

    public UnScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return myTouchEvent(ev);
    }

    public boolean myTouchEvent(MotionEvent ev) {
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                startX = ev.getX();
//                startY = ev.getY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                tempX = ev.getX() - startX;
//                tempY = ev.getY() - startY;
//                if (Math.abs(tempX) > Math.abs(tempY)) {
//                    return true;
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                if (Math.abs(tempX) > Math.abs(tempY)) {
//                    return true;
//                }
//                break;
//        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}
