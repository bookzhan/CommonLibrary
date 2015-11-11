package cn.bookzhan.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;

import java.util.HashSet;

/**
 * Created by zhandalin on 2015-10-20 17:42.
 * 说明: 支持回传多个ScrollListener
 */
public class GHSListView extends XListView {

    private HashSet<OnScrollListener> scrollListenerSet = new HashSet<>();

    public GHSListView(Context context) {
        this(context, null);
    }

    public GHSListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GHSListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setOnScrollListener(scrollListener);
    }

    OnScrollListener scrollListener = new OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (null != scrollListenerSet && scrollListenerSet.size() > 0) {
                for (OnScrollListener scrollListener : scrollListenerSet) {
                    scrollListener.onScrollStateChanged(view, scrollState);
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (null != scrollListenerSet && scrollListenerSet.size() > 0) {
                for (OnScrollListener scrollListener : scrollListenerSet) {
                    scrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }
            }
        }
    };

    public void addOnScrollListener(OnScrollListener scrollListener) {
        scrollListenerSet.add(scrollListener);
    }
}
