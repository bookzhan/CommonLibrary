package cn.bookzhan.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * 解决ScrollView嵌套ViewPager出现的滑动冲突问题
 */
public class MyScrollView extends ScrollView{
    private boolean canScroll;

    public View getContentView() {
        return contentView;
    }

    public void setContentView(View contentView) {
        this.contentView = contentView;
    }

    private View contentView;
//    OnBorderListener onBorderListener;
    private GestureDetector mGestureDetector;
    OnTouchListener mGestureListener;

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(new YScrollDetector());
        canScroll = true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        Log.i("GHS","onInterceptTouchEvent自动调用么");
        if (ev.getAction() == MotionEvent.ACTION_UP) {
//            doOnBorderListener();
            canScroll = true;
        }
        return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev);
    }

    class YScrollDetector extends SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (canScroll)
                if (Math.abs(distanceY) >= Math.abs(distanceX))
                    canScroll = true;
                else
                    canScroll = false;
            return canScroll;
        }
    }

//    滚动到顶部判断：getScrollY() == 0

    /**
     * OnBorderListener, Called when scroll to top or bottom* * @author
     * Trinea 2013-5-22
     */
   /* public interface OnBorderListener {
        *//**
         * Called when scroll to bottom
         *//*
        public void onBottom();

        *//**
         * Called when scroll to top
         *//*
        public void
        onTop();
    }
//    这个接口允许用户自定义到达底部和顶部的响应事件


    public void setOnBorderListener(OnBorderListener onBorderListener){
        this.onBorderListener = onBorderListener;
        this.doOnBorderListener();

    }

    public void doOnBorderListener() {
        Log.i("GHS","contentView.getMeasuredHeight()" + contentView.getMeasuredHeight());
        Log.i("GHS","getScrollY()" + getScrollY());
        Log.i("GHS","getHeight()" + getHeight());
        onBorderListener = new OnBorderListener() {
            @Override
            public void onBottom() {

                Log.i("GHS","到达底部");
            }

            @Override
            public void onTop() {
                Log.i("GHS","到达顶部");
            }
        };
        if (contentView != null && contentView.getMeasuredHeight() <= getScrollY() + getHeight()) {
            if (onBorderListener != null) {
                onBorderListener.onBottom();
            }
        } else if (getScrollY() == 0) {
            if (onBorderListener != null) {
                onBorderListener.onTop();
            }
        }else{
            Log.i("GHS","其他的状态");
        }
    }

    private boolean isOnBottom() {
        View contentView = this.getChildAt(0);
       if(contentView.getMeasuredHeight() <= getScrollY() + getHeight()){
        return true;
       }else{
           return false;
       }
    }*/

  /*  @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        Log.i("GHS","自行调用onScrollChanged");
        doOnBorderListener();
    }*/



}
