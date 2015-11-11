package cn.bookzhan.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.bookzhan.library.R;
import cn.bookzhan.utils.DensityUtil;


/**
 * Created by zhandalin on 2015-10-24 16:19.
 * 说明:自带RadioButton在各个手机上表现的形式不一样,故自定义
 */
public class MyRadioButton extends LinearLayout {

    private ImageView imageView;
    private OnCheckedChangeListener onCheckedChangeListener;
    private TextView textView;

    public MyRadioButton(Context context) {
        this(context, null);
    }

    public MyRadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(HORIZONTAL);
        setClickable(true);
        imageView = new ImageView(context);
        imageView.setImageResource(R.mipmap.checkbox_unckecked);
        addView(imageView);
        textView = new TextView(context);
        textView.setPadding(DensityUtil.dip2px(context, 6), 0, 0, 0);
        textView.setText(attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "text"));
        addView(textView);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onCheckedChangeListener) {
                    onCheckedChangeListener.onCheckedChange(true);
                }
                imageView.setImageResource(R.mipmap.checkbox_ischecked);
            }
        });
    }

    public void setChecked(boolean checked) {
        if (null != onCheckedChangeListener) {
            onCheckedChangeListener.onCheckedChange(checked);
        }
        if (checked) {
            imageView.setImageResource(R.mipmap.checkbox_ischecked);
        } else {
            imageView.setImageResource(R.mipmap.checkbox_unckecked);
        }
    }

    public void setText(String text) {
        textView.setText(text);
    }

    public String getText() {
        return textView.getText().toString();
    }


    public interface OnCheckedChangeListener {
        void onCheckedChange(boolean isChecked);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

}
