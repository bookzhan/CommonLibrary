package cn.bookzhan.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import cn.bookzhan.library.R;


/**
 * Created by zhandalin 2015年09月20日 12:09.
 * 最后修改者: zhandalin  version 1.0
 * 说明:实现进入动画,并且全屏变暗,目前支持从屏幕坐标,右边进入,下边以及上边进入
 * 由于对传进来的View进行测量的时候外面还没有父布局,所以只能测得这个View字布局的信息,
 * 这样的话传进来的View的根部局就不能指定宽和高的具体数值,只能在其子布局中指定,并且对listView指定是无效的,不然死了别找我,哈哈...
 * <p>
 * 使用示例:
 * PopupWindow pw = new MyPopupWindow(view, Gravity.RIGHT, true);
 * <p>
 * pw.showAtLocation(rootView, Gravity.CENTER, 0, 0);
 */
public class MyPopupWindow extends PopupWindow {
    private TranslateAnimation hideAnimation;
    private TranslateAnimation showAnimation;
    private View contentView;

    private MyPopupWindow(Context context) {
        super(context);
    }

    private MyPopupWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private MyPopupWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private MyPopupWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private MyPopupWindow() {
    }

    private MyPopupWindow(View contentView) {
        super(contentView);
    }

    private MyPopupWindow(int width, int height) {
        super(width, height);
    }

    private MyPopupWindow(View contentView, int width, int height) {
        super(contentView, width, height);
    }

    private MyPopupWindow(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
    }

    public MyPopupWindow(View contentView, int gravity, boolean touchable) {
        this.contentView = contentView;
        Context context = contentView.getContext();
        LinearLayout rootView = new LinearLayout(context);
        rootView.setGravity(gravity);
        rootView.setBackgroundResource(R.color.half_transparent);
        contentView.setBackgroundResource(R.color.white);

        if (Gravity.RIGHT == gravity) {
            contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            contentView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            showAnimation = new TranslateAnimation(contentView.getMeasuredWidth(), 0, 0, 0);
            showAnimation.setDuration(200);
            showAnimation.setFillAfter(true);

            hideAnimation = new TranslateAnimation(0, contentView.getMeasuredWidth(), 0, 0);
            hideAnimation.setDuration(200);
            hideAnimation.setFillAfter(true);
        } else if (Gravity.BOTTOM == gravity) {
            contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            contentView.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            showAnimation = new TranslateAnimation(0, 0, contentView.getMeasuredHeight(), 0);
            showAnimation.setDuration(200);
            showAnimation.setFillAfter(true);

            hideAnimation = new TranslateAnimation(0, 0, 0, contentView.getMeasuredHeight());
            hideAnimation.setDuration(200);
            hideAnimation.setFillAfter(true);
        } else if (Gravity.LEFT == gravity) {
            contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            contentView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            showAnimation = new TranslateAnimation(-contentView.getMeasuredWidth(), 0, 0, 0);
            showAnimation.setDuration(200);
            showAnimation.setFillAfter(true);

            hideAnimation = new TranslateAnimation(0, -contentView.getMeasuredWidth(), 0, 0);
            hideAnimation.setDuration(200);
            hideAnimation.setFillAfter(true);
        } else if (Gravity.TOP == gravity) {
            contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            contentView.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            showAnimation = new TranslateAnimation(0, 0, -contentView.getMeasuredHeight(), 0);
            showAnimation.setDuration(200);
            showAnimation.setFillAfter(true);

            hideAnimation = new TranslateAnimation(0, 0, 0, -contentView.getMeasuredHeight());
            hideAnimation.setDuration(200);
            hideAnimation.setFillAfter(true);
        }
        contentView.setClickable(true);
        setBackgroundDrawable(context.getResources().getDrawable(R.color.transparent));//用来使返回键能够dismiss

        rootView.addView(contentView, 0);
        setContentView(rootView);
        setAnimationStyle(R.style.mypopwindow_anim_style);
        setWindowLayoutMode(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);

        if (touchable) {
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        contentView.startAnimation(showAnimation);
        super.showAtLocation(parent, gravity, x, y);
    }

    @Override
    public void dismiss() {
        contentView.startAnimation(hideAnimation);
        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                MyPopupWindow.super.dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
