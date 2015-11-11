package cn.bookzhan.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.bookzhan.bases.BaseActivity;
import cn.bookzhan.library.R;
import cn.bookzhan.utils.ScreenUtils;
import cn.bookzhan.utils.StringUtil;


/**
 * Created by bookzhan on 2015/8/9.
 * 最后修改者: bookzhan  version 1.0
 * 说明:统一的导航栏
 */
public class CommonNavigation extends LinearLayout {
    private Context context = null;
    private View view;
    private TextView bar_title;
    private TextView bar_right_text;
    private TextView bar_left_text;
    private String jumpClassName;
    private View leftLayoutView;
    private OnRightTextClick onRightTextClick;
    private OnLeftLayoutClick onLeftLayoutClick;
    private boolean needPadding;
    private View rl_margin;
    private ImageView leftImg;
    private ImageView iv_share;

    private CommonNavigation(Context context) {
        super(context);
    }

    public CommonNavigation(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        String namespace = "http://schemas.android.com/apk/res-auto";
        needPadding = attrs.getAttributeBooleanValue(namespace, "needPadding", false);
        init();
//        setTranslucentStatus();
        String rightText = attrs.getAttributeValue(namespace, "rightText");
        if (!StringUtil.isEmpty(rightText)) {
            bar_right_text.setVisibility(VISIBLE);
            bar_right_text.setText(rightText);
        }
        String leftText = attrs.getAttributeValue(namespace, "leftText");
        if (!StringUtil.isEmpty(leftText)) {
            bar_left_text.setVisibility(VISIBLE);
            bar_left_text.setText(leftText);
        }
        String title = attrs.getAttributeValue(namespace, "title");
        if (!StringUtil.isEmpty(title)) {
            bar_title.setVisibility(VISIBLE);
            bar_title.setText(title);
        }
        jumpClassName = attrs.getAttributeValue(namespace, "jumpClass");
        initListener();
    }

//    private void setTranslucentStatus() {
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                Window win = ((Activity) context).getWindow();
//                WindowManager.LayoutParams winParams = win.getAttributes();
//                final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//                winParams.flags |= bits;
//                win.setAttributes(winParams);
//            }
//            SystemBarTintManager tintManager = new SystemBarTintManager((Activity) context);
//            tintManager.setStatusBarTintEnabled(true);
//            tintManager.setStatusBarTintResource(0);//状态栏无背景
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    private void init() {
        view = View.inflate(context, R.layout.navigation_layout, null);
        leftImg = (ImageView) view.findViewById(R.id.iv_back);
        leftLayoutView = view.findViewById(R.id.leftLayoutView);
        rl_margin = view.findViewById(R.id.rl_margin);
        bar_title = ((TextView) view.findViewById(R.id.bar_title));
        bar_right_text = ((TextView) view.findViewById(R.id.bar_right_text));
        bar_left_text = ((TextView) view.findViewById(R.id.bar_left_text));
        iv_share = ((ImageView) view.findViewById(R.id.iv_share));

        int barHeight = ScreenUtils.getStatusBarHeight(context);
        if (needPadding && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//这个是用来处理沉浸式体验的
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rl_margin.getLayoutParams();
            layoutParams.setMargins(0, barHeight, 0, 0);
            rl_margin.setLayoutParams(layoutParams);
        }
        addView(view);
    }


    public void setTitle(String str) {
        bar_title.setVisibility(VISIBLE);
        bar_title.setText(str);
    }

    public void setRightText(String rightStr) {
        bar_right_text.setText(rightStr);
        bar_right_text.setVisibility(View.VISIBLE);
    }

    public String getRightText() {
        return bar_right_text.getText().toString();
    }

    public void setRightTextDrawableLeft(int rsid) {
        bar_right_text.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(rsid), null, null, null);
    }

    public void setLedttText(String rightStr) {
        bar_left_text.setText(rightStr);
    }

    public void setRightBg(int id) {
        bar_right_text.setVisibility(VISIBLE);
        bar_right_text.setBackgroundResource(id);
    }

    public void hideRightTextView() {
        bar_right_text.setVisibility(GONE);
    }


    public void showRightTextView() {
        bar_right_text.setVisibility(VISIBLE);
    }

    public void showLeftLayout() {
        leftLayoutView.setVisibility(VISIBLE);
    }

    public void hideLeftLayout() {
        leftLayoutView.setVisibility(GONE);
    }

    private void initListener() {
        leftLayoutView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onLeftLayoutClick != null) {
                    onLeftLayoutClick.onClick(v);
                    return;
                }
                if (!StringUtil.isEmpty(jumpClassName)) {
                    Intent intent = new Intent();
                    intent.setClassName(context.getPackageName(), "net.ghs.activity." + jumpClassName);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }
                ((BaseActivity) context).finish();
                ((BaseActivity) context).overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
            }
        });
        bar_right_text.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRightTextClick != null) {
                    onRightTextClick.onClick(v);
                }
            }
        });


    }

    public interface OnRightTextClick {
        void onClick(View v);
    }

    public void setOnRightTextClickListener(OnRightTextClick onRightTextClick) {
        this.onRightTextClick = onRightTextClick;
    }

    public interface OnLeftLayoutClick {
        void onClick(View v);
    }

    public void setOnLeftLayoutClickListener(OnLeftLayoutClick onLeftLayoutClick) {
        this.onLeftLayoutClick = onLeftLayoutClick;
    }

    public ImageView getIvShare() {
        return iv_share;
    }

}
