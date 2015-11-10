package cn.bookzhan.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.bookzhan.library.R;
import cn.bookzhan.widget.LinearLayoutViewHeader;

/**
 * Created by zhandalin 2015年09月14日 10:58.
 * 最后修改者: zhandalin  version 1.0
 * 说明:
 */
public class RefreshLinearLayout extends LinearLayout {
    private float mLastY;
    private LinearLayoutViewHeader mHeaderView;
    private boolean isNeedRefresh;
    private boolean mEnablePullRefresh;
    private final static float OFFSET_RADIO = 1.8f;
    private boolean mPullRefreshing = false;
    private int mHeaderViewHeight;
    private RelativeLayout mHeaderViewContent;
    private TextView mHeaderTimeView;
    private RefreshListener refreshListener;

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

        isNeedRefresh = true;
        mEnablePullRefresh = true;
        mHeaderView = new LinearLayoutViewHeader(context);
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
        myTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    public boolean myTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (isNeedRefresh
                        && (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
                    updateHeaderHeight(deltaY / OFFSET_RADIO);
                }
                break;
            default:
                mLastY = -1;
                if (isNeedRefresh) {
                    if (mEnablePullRefresh
                            && mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                        mPullRefreshing = true;
                        mHeaderView.setState(LinearLayoutViewHeader.STATE_REFRESHING);
                        updateHeaderHeight(LinearLayoutViewHeader.STATE_REFRESHING);
                        resetHeaderHeight();
                        if(null!=refreshListener){
//                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH时mm分ss秒");
//                            String format = dateFormat.format(new Date());
//                            mHeaderTimeView
                            refreshListener.onRefresh();
                        }
                    }
                    break;
                }
        }
        return super.onTouchEvent(ev);
    }


    private void updateHeaderHeight(float delta) {
        mHeaderView.setVisiableHeight((int) delta
                + mHeaderView.getVisiableHeight());
        if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
            if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                mHeaderView.setState(LinearLayoutViewHeader.STATE_READY);
            } else {
                mHeaderView.setState(LinearLayoutViewHeader.STATE_NORMAL);
            }
        }
    }

    private void resetHeaderHeight() {
        int height = mHeaderView.getVisiableHeight();
        if (height == 0) // not visible.
            return;
        if (mPullRefreshing && height <= mHeaderViewHeight) {
            return;
        }
        mHeaderView.resetHeaderHeight(height-mHeaderViewHeight, 100);
    }

    /**
     * 完成刷新以后要主动调用这个方法
     */
    public void completeRefresh(){
        if(null!=mHeaderView){
            mHeaderView.resetHeaderHeight(mHeaderViewHeight, 100);
        }
    }
    public  interface RefreshListener{
        void onRefresh();
    }
    public void setRefreshListener(RefreshListener refreshListener){
        this.refreshListener=refreshListener;
    }

}
