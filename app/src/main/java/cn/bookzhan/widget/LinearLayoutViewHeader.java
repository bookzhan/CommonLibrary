/**
 * @file XListViewHeader.java
 * @create Apr 18, 2012 5:22:27 PM
 * @author Maxwin
 * @description XListView's header
 */
package cn.bookzhan.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.bookzhan.library.R;


public class LinearLayoutViewHeader extends LinearLayout {
    private static final int RESET_HEADER = 4;
    private static final int RESET_REFRESH = 3;
    private LinearLayout mContainer;
    private ImageView mArrowImageView;
    private ProgressBar mProgressBar;
    private TextView mHintTextView;

    private int mState = STATE_NORMAL;
    private Animation mRotateUpAnim;
    private Animation mRotateDownAnim;
    private final int ROTATE_ANIM_DURATION = 180;
    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_REFRESHING = 2;
    private LayoutParams layoutParams;
    private int distance;
    private int headerHeight;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RESET_HEADER:
                    layoutParams.height = layoutParams.height - distance;
                    if (layoutParams.height < 0)
                        layoutParams.height = 0;

                    mContainer.setLayoutParams(layoutParams);
                    if (layoutParams.height != 0)
                        handler.sendEmptyMessageDelayed(4, 10);
                    break;
                case RESET_REFRESH:
                    layoutParams.height = layoutParams.height - distance;
                    if (layoutParams.height >headerHeight)
                        layoutParams.height = headerHeight;

                    mContainer.setLayoutParams(layoutParams);
                    if (layoutParams.height != headerHeight)
                        handler.sendEmptyMessageDelayed(4, 10);
                    break;
            }
        }
    };



    public LinearLayoutViewHeader(Context context) {
        super(context);
        initView(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public LinearLayoutViewHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        // 初始情况，设置下拉刷新view高度为0
        LayoutParams lp = new LayoutParams(
                LayoutParams.FILL_PARENT, 0);
        mContainer = (LinearLayout) LayoutInflater.from(context).inflate(
                R.layout.xlistview_header, null);
        addView(mContainer, lp);
        setGravity(Gravity.BOTTOM);

        mArrowImageView = (ImageView) findViewById(R.id.xlistview_header_arrow);
        mHintTextView = (TextView) findViewById(R.id.xlistview_header_hint_textview);
        mProgressBar = (ProgressBar) findViewById(R.id.xlistview_header_progressbar);

        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);
    }

    public void setState(int state) {
        if (state == mState) return;

        if (state == STATE_REFRESHING) {    // 显示进度
            mArrowImageView.clearAnimation();
            mArrowImageView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {    // 显示箭头图片
            mArrowImageView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        switch (state) {
            case STATE_NORMAL:
                if (mState == STATE_READY) {
                    mArrowImageView.startAnimation(mRotateDownAnim);
                }
                if (mState == STATE_REFRESHING) {
                    mArrowImageView.clearAnimation();
                }
                mHintTextView.setText(R.string.xlistview_header_hint_normal);
                break;
            case STATE_READY:
                if (mState != STATE_READY) {
                    mArrowImageView.clearAnimation();
                    mArrowImageView.startAnimation(mRotateUpAnim);
                    mHintTextView.setText(R.string.xlistview_header_hint_ready);
                }
                break;
            case STATE_REFRESHING:
                mHintTextView.setText(R.string.xlistview_header_hint_loading);
                break;
            default:
        }
        mState = state;
    }

    public void setVisiableHeight(int height) {
        if (height < 0)
            height = 0;
        LayoutParams lp = (LayoutParams) mContainer
                .getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);
    }

    public int getVisiableHeight() {
        return mContainer.getHeight();
    }

    public void resetHeaderHeight(int height, int delay) {
        if (height < 0)
            height = 0;
        layoutParams = (LayoutParams) mContainer.getLayoutParams();
        distance = height * 10 / delay;
        if (distance == 0) {
            distance = 1;
        }
        layoutParams.height = height;
        if(mState==STATE_REFRESHING){
            handler.sendEmptyMessageDelayed(RESET_REFRESH, 10);
        }else {
            handler.sendEmptyMessageDelayed(RESET_HEADER, 10);
        }
    }

    public void setHeight(int height) {
        this.headerHeight = height;
    }
}