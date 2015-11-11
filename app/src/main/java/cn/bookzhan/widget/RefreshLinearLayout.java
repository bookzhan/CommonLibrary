package cn.bookzhan.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import cn.bookzhan.library.R;
import cn.bookzhan.utils.DensityUtil;
import cn.bookzhan.utils.LogUtil;

/**
 * Created by zhandalin 2015年09月14日 10:58.
 * 最后修改者: zhandalin  version 1.0
 * 说明:下拉刷新的LinearLayout,为了防止内部的其他控件的影响必须从外层控件分发一个触摸事件过来
 */
public class RefreshLinearLayout extends LinearLayout {
    private float mLastY;

    private float startX;
    private float startY;
    private int threshold = 5;//dp

    private LinearLayoutHeader mHeaderView;
    private boolean enablePullRefresh = true;
    private final static float OFFSET_RADIO = 1.8f;
    private boolean isRefreshing = false;
    private int mHeaderViewHeight;
    private RelativeLayout mHeaderViewContent;
    private TextView mHeaderTimeView;
    private RefreshListener refreshListener;
    private Scroller mScroller;
    private int SCROLL_DURATION = 500;
    private Handler handler = new Handler();
    private String TAG = "RefreshLinearLayout";


    public RefreshLinearLayout(Context context) {
        super(context);
        init(context);
    }

    public RefreshLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        threshold = DensityUtil.dip2px(context, threshold);
        mScroller = new Scroller(context, new DecelerateInterpolator());
        mHeaderView = new LinearLayoutHeader(context);
        mHeaderViewContent = (RelativeLayout) mHeaderView
                .findViewById(R.id.xlistview_header_content);
        mHeaderTimeView = (TextView) mHeaderView
                .findViewById(R.id.xlistview_header_time);
        mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mHeaderViewHeight = mHeaderViewContent.getHeight();
                        mHeaderView.setHeight(mHeaderViewHeight);
                        getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                    }
                });
        addView(mHeaderView, 0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getRefreshState() != LinearLayoutHeader.STATE_NORMAL) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    public boolean myTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                startY = ev.getRawY();
                startX = ev.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                float offsetX = ev.getRawX() - startX;
                float offsetY = ev.getRawY() - startY;
                final float deltaY = ev.getRawY() - mLastY;

                if (Math.abs(offsetX) + threshold > Math.abs(offsetY)) {//屏蔽左右滑动的影响
                    LogUtil.d(TAG, "左右滑动");
                    break;
                }
                mLastY = ev.getRawY();
                if (!isRefreshing && enablePullRefresh) {
                    updateHeaderHeight(deltaY / OFFSET_RADIO);
                }
                break;
            default:
                if (!isRefreshing) {//防止点按影响
                    if (enablePullRefresh
                            && mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                        isRefreshing = true;
                        mHeaderView.setState(LinearLayoutHeader.STATE_REFRESHING);
                        resetHeaderHeight();
                        if (null != refreshListener) {
                            refreshListener.onRefresh();
                        }
                    } else {
                        completeRefresh();
                    }
                    break;
                }
                mLastY = -1;
                break;
        }
        return super.onTouchEvent(ev);
    }


    private void updateHeaderHeight(float delta) {
        mHeaderView.setVisiableHeight((int) delta
                + mHeaderView.getVisiableHeight());
        if (enablePullRefresh && !isRefreshing) { // 未处于刷新状态，更新箭头
            if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                mHeaderView.setState(LinearLayoutHeader.STATE_READY);
            } else {
                mHeaderView.setState(LinearLayoutHeader.STATE_NORMAL);
            }
        }
    }

    private void resetHeaderHeight() {
        int height = mHeaderView.getVisiableHeight();
        if (height == 0) // not visible.
            return;
//        if (isRefreshing && height <= mHeaderViewHeight) {
//            return;
//        }
        int finalHeight = 0; // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (isRefreshing && height > mHeaderViewHeight) {
            finalHeight = mHeaderViewHeight;
        }
        mScroller.startScroll(0, height, 0, finalHeight - height,
                SCROLL_DURATION);
        // trigger computeScroll
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mHeaderView.setVisiableHeight(mScroller.getCurrY());
            if (mScroller.getCurrY() <= 0) {
                isRefreshing = false;
                mHeaderView.setState(LinearLayoutHeader.STATE_NORMAL);
            }
            postInvalidate();
//            invokeOnScrolling();
        }
        super.computeScroll();
    }


    public void setEnablePullRefresh(boolean enablePullRefresh) {
        this.enablePullRefresh = enablePullRefresh;
    }


    public void completeRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                resetHeaderHeight();
            }
        }, 500);
    }

    public interface RefreshListener {
        void onRefresh();
    }

    public void setRefreshListener(RefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    public int getRefreshState() {
        return mHeaderView.getHeaderState();
    }
}
