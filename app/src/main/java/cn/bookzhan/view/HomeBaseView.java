package cn.bookzhan.view;

import android.content.Context;
import android.view.View;

/**
 * Created by bookzhan on 2015/8/13.
 * 最后修改者: bookzhan  version 1.0
 * 说明:作为ListView的基础View,这么设计的目的在于模块化代码,而且相对于其他View显得太轻量级了
 *
 */
public abstract class HomeBaseView<T> {
    protected final View view;
    protected final Context context;

    public HomeBaseView(Context context) {
        this.context = context;
        view = initView();
        initData();
    }

    /**
     * 让子类去初始化View
     *
     * @return
     */
    public abstract View initView();

    public View getView() {
        return view;
    }

    /**
     * 让子类决定是否需要初始化数据
     */
    public void initData() {
    }
    //FIXME 看看是否有问题
    public abstract void refresh(T data);

    public abstract void onPause();

    public abstract void onResume();

}
