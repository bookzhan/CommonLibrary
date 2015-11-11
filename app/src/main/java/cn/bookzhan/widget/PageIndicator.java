package cn.bookzhan.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import cn.bookzhan.library.R;
import cn.bookzhan.utils.DensityUtil;

public class PageIndicator extends LinearLayout implements ViewPager.OnPageChangeListener {

    private Context context;
    private int pageCount;
    private int lastPosition = 0;
    private List<ImageView> pageIndicatorList = new ArrayList<>();
    private LayoutParams params;

    public PageIndicator(Context context) {
        this(context, null);
    }

    public PageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setOrientation(HORIZONTAL);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(DensityUtil.dip2px(context, 6), 0, 0, 0);
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
        if (pageCount <= 1) {
            return;
        }
        pageIndicatorList.clear();
        removeAllViews();
        for (int i = 0; i < pageCount; i++) {
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(R.drawable.selector_page_indicator);
            pageIndicatorList.add(imageView);
            addView(imageView, params);
        }
        pageIndicatorList.get(lastPosition % pageCount).setEnabled(false);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (pageCount <= 1) {
            return;
        }
        pageIndicatorList.get(position % pageCount).setEnabled(false);
        pageIndicatorList.get(lastPosition % pageCount).setEnabled(true);
        lastPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
