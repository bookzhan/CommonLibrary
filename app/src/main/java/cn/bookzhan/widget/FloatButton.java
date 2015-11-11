package cn.bookzhan.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.bookzhan.library.R;


/**
 * Created by zhandalin 2015年09月17日 09:55.
 * 最后修改者: zhandalin  version 1.0
 * 说明:悬浮button
 */
public class FloatButton extends RelativeLayout {

    private ImageView iv_up_arrows;
    private View ll_page_number;
    private TextView tv_page_index, tv_total_page;
    private BackTopListener backTopListener;

    public FloatButton(Context context) {
        super(context);
        initView(context);
    }

    public FloatButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        View view = View.inflate(context, R.layout.floating_button_layout, null);
        iv_up_arrows = (ImageView) view.findViewById(R.id.iv_up_arrows);
        ll_page_number = view.findViewById(R.id.ll_page_number);
        tv_page_index = (TextView) view.findViewById(R.id.tv_page_index);
        tv_total_page = (TextView) view.findViewById(R.id.tv_total_page);
        iv_up_arrows.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != backTopListener) {
                    iv_up_arrows.setVisibility(GONE);
                    backTopListener.onBackTopClick(v);
                }
            }
        });

        addView(view);
    }

    public interface BackTopListener {
        void onBackTopClick(View view);
    }

    public void setBackTopListener(BackTopListener backTopListener) {
        this.backTopListener = backTopListener;
    }

    /**
     * 隐藏返回顶部的箭头,同时显示页码索引
     */
    public void hideBackTop() {
        iv_up_arrows.setVisibility(GONE);
        ll_page_number.setVisibility(VISIBLE);
    }

    /**
     * 隐藏页码数字,同时显示返回顶部的箭头
     */
    public void hidePageNum() {
        ll_page_number.setVisibility(GONE);
        iv_up_arrows.setVisibility(VISIBLE);
    }

    public void setPageIndex(int pageIndex) {
        if (pageIndex <= 0) {
            pageIndex = 1;
        }
        tv_page_index.setText(pageIndex + "");
    }

    public void setPageTotalNum(int totalNum) {
        if (totalNum <= 0) {
            totalNum = 1;
        }
        tv_total_page.setText(totalNum + "");
    }

}
