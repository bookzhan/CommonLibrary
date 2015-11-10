package cn.bookzhan.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
*
* 主要用来解决点击tab的不松手 同时按下home键崩溃的Bug
*
 */
public class MyFragmentTabHost extends FragmentTabHost {
	private boolean needResponseClick = true;
	public boolean isNeedResponseClick() {
		return needResponseClick;
	}
	public void setNeedResponseClick(boolean needResponseClick) {
		this.needResponseClick = needResponseClick;
	}

	public MyFragmentTabHost(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyFragmentTabHost(Context context) {
		super(context);
	}

	@Override
	public void setCurrentTab(int index) {
		if(needResponseClick)
			super.setCurrentTab(index);
	}

}
