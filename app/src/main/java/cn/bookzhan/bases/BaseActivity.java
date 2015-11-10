package cn.bookzhan.bases;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import java.util.List;
import cn.bookzhan.library.R;
import cn.bookzhan.utils.FileUtils;


public class BaseActivity extends Activity {
    private static final String TAG = "BaseActivity";
    private Toast mToast;
    protected boolean isDestroyed = false;
    protected Context context;
    protected ProgressDialog loadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        isDestroyed = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

//        BaseApplication application = (BaseApplication) this.getApplication();
//        if (application.wasInBackground) {
//            application.applicationWillEnterForeground();
//        }
//
//        // Android 4.0以下的方案
//        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//            application.stopActivityTransitionTimer();
//        }
        //umeng统计开始
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        // Android 4.0以下的方案
//        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//            ((BaseApplication) this.getApplication())
//                    .startActivityTransitionTimer();
//        }
        //umeng统计时常结束
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        isDestroyed = true;
        if (mToast != null) {
            mToast.cancel();
        }
        super.onDestroy();
    }

    protected void goBack(View v) {
        finish();
    }

    public <T extends View> T findView(int resId) {
        View v = findViewById(resId);
        if (v == null) return null;
        return (T) v;
    }

    /**
     * 读取文本数据
     * @param fileName 文件名
     * @return String, 读取到的文本内容，失败返回null
     */
    public String readFile(String fileName) {
        return FileUtils.readFile(getApplicationContext(), fileName);
    }

    /**
     * 存储文本数据，一般用作网络数据的硬盘缓存
     *
     * @param fileName 文件名，要在系统内保持唯一
     * @param content  文本内容
     * @return boolean 存储成功的标志
     */
    public boolean writeFile(String fileName, String content) {
        return FileUtils.writeFile(getApplicationContext(), fileName, content);
    }

    /**
     * 读取数据对象数组
     * 已废弃，请使用：readListFromJsonFile
     *
     * @param fileName 文件名
     * @param classOfT 对象类别，例如Order.class
     * @return List, 读取到的数据对象数组，失败返回null
     */
    @Deprecated
    public <T extends Parcelable> List<T> readParcelableList(String fileName, Class<T> classOfT) {
        return FileUtils.readParcelableList(getApplicationContext(), fileName, classOfT);
    }

    /**
     * 读取数据对象
     * 已废弃，请使用：readObjectFromJsonFile
     *
     * @param fileName 文件名
     * @param classOfT 对象类别，例如Order.class
     * @return List, 读取到的数据对象数组，失败返回null
     */
    @Deprecated
    public <T extends Parcelable> T readParcelable(String fileName, Class<T> classOfT) {
        return FileUtils.readParcelable(getApplicationContext(), fileName, classOfT);
    }

    /**
     * 存储文本数据，一般用作网络数据的硬盘缓存
     * 已废弃，请使用：writeListToJsonFile
     *
     * @param fileName 文件名，要在系统内保持唯一
     * @param list     数组对象，对象需要实现Parcelable接口
     * @return boolean 存储成功的标志
     */
    @Deprecated
    public <T extends Parcelable> boolean writeParcelableList(String fileName, List<T> list) {
        return FileUtils.writeParcelableList(getApplicationContext(), fileName, list);
    }

    /**
     * 存储文本数据，一般用作网络数据的硬盘缓存
     * 已废弃，请使用：writeObjectToJsonFile
     *
     * @param fileName     文件名，要在系统内保持唯一
     * @param parcelObject 实现了Parcelable接口的对象
     * @return boolean 存储成功的标志
     */
    @Deprecated
    public <T extends Parcelable> boolean writeParcelable(String fileName, T parcelObject) {
        return FileUtils.writeParcelable(getApplicationContext(), fileName, parcelObject);
    }

    /**
     * 读取数据对象
     *
     * @param fileName 文件名
     * @param classOfT 对象类别，例如Order.class
     * @return T, 读取到的POJO对象，失败返回null
     */
    public <T> T readObjectFromJsonFile(String fileName, Class<T> classOfT) {
        return FileUtils.readObjectFromJsonFile(context, fileName, classOfT);
    }

    /**
     * 存储文本数据，一般用作网络数据的硬盘缓存
     *
     * @param fileName 文件名，要在系统内保持唯一
     * @param list     数组对象，包含任意的POJO对象
     * @return boolean 存储成功的标志
     */
    public boolean writeListToJsonFile(String fileName, List list) {
        return FileUtils.writeListToJsonFile(context, fileName, list);
    }

    /**
     * 存储文本数据，一般用作网络数据的硬盘缓存
     *
     * @param fileName 文件名，要在系统内保持唯一
     * @param object   任意POJO对象
     * @return boolean 存储成功的标志
     */
    public boolean writeObjectToJsonFile(String fileName, Object object) {
        return FileUtils.writeObjectToJsonFile(context, fileName, new Gson().toJson(object));
    }

    /**
     * 显示Toast消息
     *
     * @param message
     */
    public void showToast(String message) {
        this.showToast(message, Gravity.CENTER);
    }

    /**
     * 显示Toast消息
     *
     * @param messageResourceID
     */
    public void showToast(int messageResourceID) {
        showToast(getString(messageResourceID), Gravity.CENTER);
    }

    public void showToast(String message, int gravity) {
        if (isDestroyed) {
            return;
        }

        hiddenKeyboard();

        if (mToast == null) {
            mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        }
        mToast.setGravity(gravity, 0, 0);
        mToast.setText(message);
        mToast.show();
    }

    public void hiddenKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView()
                        .getApplicationWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    /**
     * 显示"加载中"对话框
     */
    public void showLoading() {
        showLoading(getString(R.string.bases_activity_loading_info));
    }

    /**
     * 显示加载中对话框
     *
     * @param messageID 默认为“加载中...”
     * @return
     */
    public void showLoading(int messageID) {
        showLoading(null, getString(messageID));
    }

    /**
     * 显示加载中对话框
     *
     * @param message 默认为“加载中...”
     * @return
     */
    public void showLoading(String message) {
        showLoading(null, message);
    }

    /**
     * 显示加载中对话框
     *
     * @param titleID
     * @param messageID 默认为“加载中...”
     * @return
     */
    public void showLoading(int titleID, int messageID) {
        showLoading(getString(titleID), getString(messageID));
    }

    /**
     * 显示加载中对话框
     *
     * @param title
     * @param message 默认为“加载中...”
     * @return
     */
    public void showLoading(String title, String message) {
        if (message == null) {
            message = getString(R.string.bases_activity_loading_info);
        }
        if (loadingProgress == null) {
            loadingProgress = new ProgressDialog(this);
            loadingProgress.setTitle(title);
            loadingProgress.setMessage(message);
            loadingProgress.show();
            loadingProgress.setCancelable(true);// 设置进度条是否可以按退回键取消
            // 设置点击进度对话框外的区域对话框不消失
            loadingProgress.setCanceledOnTouchOutside(false);
        }
    }

    /**
     * 延迟结束自己
     */
    public void finishAfterDelay(long delayMillis) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, delayMillis);
    }

    /**
     * 隐藏加载框
     */
    public void hiddenLoadingView() {
        if (loadingProgress != null) {
            loadingProgress.dismiss();
            loadingProgress = null;
        }
    }
}
