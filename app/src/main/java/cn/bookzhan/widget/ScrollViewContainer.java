package cn.bookzhan.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 包含两个ScrollView的容器
 * 更多详解见博客http://blog.csdn.net/zhongkejingwang/article/details/38656929
 * @author chenjing
 *
 * 修改了一下,加了一个回调接口
 */
public class ScrollViewContainer extends RelativeLayout {

    public static final int AUTO_UP = 0;
    public static final int AUTO_DOWN = 1;
    public static final int DONE = 2;
    public static final float SPEED = 6.5f;

    private boolean isMeasured = false;

    private VelocityTracker vt;

    private int mViewHeight;
    private int mViewWidth;

    private View topView;
    private View bottomView;

    private boolean canPullDown;
    private boolean canPullUp;
    private int state = DONE;

    private int mCurrentViewIndex = 0;
    private float mMoveLen;
    private MyTimer mTimer;
    private float mLastY;
    private int mEvents;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (mMoveLen != 0) {
                if (state == AUTO_UP) {
                    mMoveLen -= SPEED;
                    if (mMoveLen <= -mViewHeight) {
                        mMoveLen = -mViewHeight;
                        state = DONE;
                        mCurrentViewIndex = 1;
                    }
                } else if (state == AUTO_DOWN) {
                    mMoveLen += SPEED;
                    if (mMoveLen >= 0) {
                        mMoveLen = 0;
                        state = DONE;
                        mCurrentViewIndex = 0;
                    }
                } else {
                    mTimer.cancel();
                }
            }
            requestLayout();
        }

    };
    private BackTopStateChanged backTopStateChanged;

    public ScrollViewContainer(Context context) {
        super(context);
        init();
    }

    public ScrollViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrollViewContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mTimer = new MyTimer(handler);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (vt == null)
                    vt = VelocityTracker.obtain();
                else
                    vt.clear();
                mLastY = ev.getY();
                vt.addMovement(ev);
                mEvents = 0;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                mEvents = -1;
                break;
            case MotionEvent.ACTION_MOVE:
                vt.addMovement(ev);
                if (canPullUp && mCurrentViewIndex == 0 && mEvents == 0) {
                    mMoveLen += (ev.getY() - mLastY);
                    if (mMoveLen > 0) {
                        mMoveLen = 0;
                        mCurrentViewIndex = 0;
                    } else if (mMoveLen < -mViewHeight) {
                        mMoveLen = -mViewHeight;
                        mCurrentViewIndex = 1;

                    }
                    if (mMoveLen < -8) {
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                    }
                } else if (canPullDown && mCurrentViewIndex == 1 && mEvents == 0) {
                    mMoveLen += (ev.getY() - mLastY);
                    if (mMoveLen < -mViewHeight) {
                        mMoveLen = -mViewHeight;
                        mCurrentViewIndex = 1;
                    } else if (mMoveLen > 0) {
                        mMoveLen = 0;
                        mCurrentViewIndex = 0;
                    }
                    if (mMoveLen > 8 - mViewHeight) {
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                    }
                } else
                    mEvents++;
                mLastY = ev.getY();
                requestLayout();
                break;
            case MotionEvent.ACTION_UP:
                mLastY = ev.getY();
                vt.addMovement(ev);
                vt.computeCurrentVelocity(700);
                float mYV = vt.getYVelocity();
                if (mMoveLen == 0 || mMoveLen == -mViewHeight)
                    break;
                if (Math.abs(mYV) < 500) {
                    if (mMoveLen <= -mViewHeight / 2) {
                        state = AUTO_UP;
                    } else if (mMoveLen > -mViewHeight / 2) {
                        state = AUTO_DOWN;
                    }
                } else {
                    if (mYV < 0)
                        state = AUTO_UP;
                    else
                        state = AUTO_DOWN;
                }
                mTimer.schedule(2);
//                try {
//                    vt.recycle();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                break;

        }
        super.dispatchTouchEvent(ev);
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        topView.layout(0, (int) mMoveLen, mViewWidth,
                topView.getMeasuredHeight() + (int) mMoveLen);
        //因为在ScollViewContainer的下方有一组按钮，影响了container的高度，所以在此处需要将其减去那组按钮的高度
        final float scale = getResources().getDisplayMetrics().density;
        int lenght = (int) (54 * scale + 0.5f);
        bottomView.layout(0, topView.getMeasuredHeight() + (int) mMoveLen,
                mViewWidth, topView.getMeasuredHeight() + (int) mMoveLen
                        + bottomView.getMeasuredHeight() + lenght);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!isMeasured) {
            isMeasured = true;

            mViewHeight = getMeasuredHeight();
            mViewWidth = getMeasuredWidth();

            topView = getChildAt(0);
            bottomView = getChildAt(1);

            bottomView.setOnTouchListener(bottomViewTouchListener);
            topView.setOnTouchListener(topViewTouchListener);
        }
    }

    private OnTouchListener topViewTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ScrollView sv = (ScrollView) v;
            if (sv.getScrollY() == (sv.getChildAt(0).getMeasuredHeight() - sv
                    .getMeasuredHeight()) && mCurrentViewIndex == 0)
                canPullUp = true;
            else
                canPullUp = false;
            return false;
        }
    };
    private OnTouchListener bottomViewTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ScrollView sv = (ScrollView) v;
            if (sv.getScrollY() <= 0 && mCurrentViewIndex == 1) {
                canPullDown = true;
                if (backTopStateChanged != null) {
                    backTopStateChanged.onBackTopStateChanged(false);
                }
            } else {
                canPullDown = false;
                if (sv.getScrollY()>100&&backTopStateChanged != null) {
                    backTopStateChanged.onBackTopStateChanged(true);
                }
            }
            return false;
        }
    };

    class MyTimer {
        private Handler handler;
        private Timer timer;
        private MyTask mTask;

        public MyTimer(Handler handler) {
            this.handler = handler;
            timer = new Timer();
        }

        public void schedule(long period) {
            if (mTask != null) {
                mTask.cancel();
                mTask = null;
            }
            mTask = new MyTask(handler);
            timer.schedule(mTask, 0, period);
        }

        public void cancel() {
            if (mTask != null) {
                mTask.cancel();
                mTask = null;
            }
        }

        class MyTask extends TimerTask {
            private Handler handler;

            public MyTask(Handler handler) {
                this.handler = handler;
            }

            @Override
            public void run() {
                handler.obtainMessage().sendToTarget();
            }

        }
    }

    public interface BackTopStateChanged {
        void onBackTopStateChanged(boolean isShow);
    }

    public void setOnBackTopStateChangedListener(BackTopStateChanged backTopStateChanged) {
        this.backTopStateChanged = backTopStateChanged;
    }

    /**
     * 返回顶部
     */
    public void setBackTop(){
        state=AUTO_DOWN;
        mMoveLen=getMeasuredHeight();
        handler.sendEmptyMessage(1);
        if (backTopStateChanged != null) {
            backTopStateChanged.onBackTopStateChanged(false);
        }
        ScrollView topScrollView= (ScrollView)topView;
        topScrollView.scrollTo(0, 0);
        ScrollView bottomScrollView= (ScrollView)bottomView;
        bottomScrollView.scrollTo(0,0);
    }
}
