package cn.bookzhan.library;

import android.app.Application;
import com.squareup.picasso.Picasso;
import cn.bookzhan.http.GHSHttpClient;
import cn.bookzhan.utils.LogUtil;
import cn.bookzhan.utils.UserInfoUtils;

/**
 * Created by bookzhan on 2015/8/5.
 * 最后修改者: bookzhan  version 1.0
 * 说明: 主要是用来定位,开启推送服务,启动网络框架,然后把定位的省份名字以及编码存到sp中
 */
public class MyApplication extends Application {
    private String TAG = "LocationApp";
    public static String member_id;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "Application启动");
        init();
    }

    private void init() {
        member_id = UserInfoUtils.getUserId(this);
        GHSHttpClient.getInstance();
        Picasso.setSingletonInstance(new Picasso.Builder(getApplicationContext()).loggingEnabled(Constants.DEVELOPER_MODE).build());
    }

}
