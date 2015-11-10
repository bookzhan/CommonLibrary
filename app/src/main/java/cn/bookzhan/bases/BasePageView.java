package cn.bookzhan.bases;

import android.content.Context;
import android.view.View;


/**
 * 个人详细信息的页面
 * Created by luoye on 2015/6/7.
 */
public abstract class BasePageView{
    public Context context;
    private View view;

    public BasePageView(Context context) {
        this.context = context;
        view = initView();
        initData();
    }

    public abstract View initView();

    public void initData() {
    }

    public View getRootView() {
        return view;
    }

    public void setRootView(View view) {
        this.view = view;
    }
}
