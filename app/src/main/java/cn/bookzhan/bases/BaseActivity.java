package cn.bookzhan.bases;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import cn.bookzhan.library.Constants;
import cn.bookzhan.library.R;
import cn.bookzhan.utils.DensityUtil;
import cn.bookzhan.utils.SpUtils;
import cn.bookzhan.utils.StringUtil;
import cn.bookzhan.widget.LoadingProgressDialog;
import cn.bookzhan.widget.MessageDialog;


public abstract class BaseActivity extends FragmentActivity {
    protected boolean isDestroyed;
    protected Context context;
    protected LoadingProgressDialog loadingProgress;
    protected MessageDialog msgDialog;
    public View rootView;
    public View errorView;
    private View currentView;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        isDestroyed = false;
    }
    protected void initRootAndErrorView(int rootViewId,int errorViewId){
       rootView= LayoutInflater.from(this).inflate(rootViewId,null);
        errorView=LayoutInflater.from(this).inflate(errorViewId,null);
        errorView.findViewById(R.id.bt_to_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reTry();
            }
        });
    }
    protected abstract void reTry();
    protected void initRootView(int rootViewId){
        rootView=LayoutInflater.from(this).inflate(rootViewId,null);
    }

    @Override
    public void setContentView(View view) {
           // if(currentView==view)return;
            if(view==null)return;//从onFalure 进来,view 可能为null
            if (currentView != null) {
                if (view == currentView) return;
                currentView = view;
            } else {
                currentView = view;
            }
       // currentView=view;
        super.setContentView(view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //umeng统计开始
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        hiddenDialogMsg();
        //umeng统计结束
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        isDestroyed = true;
        if (loadingProgress != null && loadingProgress.isShowing()) {
            loadingProgress.dismiss();
        }
        if (msgDialog != null && msgDialog.isShowing()) {
            msgDialog.dismiss();
        }
        super.onDestroy();
    }

    /**
     * 很久才会弹一个Toast,不提升为成员变量
     *
     * @param message 传null则默认显示为 网络或服务器错误
     */
    public void showToastAtCenter(String message) {
        if (isDestroyed) {
            return;
        }
        if (null == message) {
            message = "网络或服务器错误~";
        }
        hiddenKeyboard();
        Toast mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        mToast.setMargin(0, 0);
        View view = mToast.getView();
        view.setBackgroundResource(R.drawable.shape_large_corner_rectangle);
        TextView tv = (TextView) view.findViewById(android.R.id.message);
        tv.setTextColor(Color.WHITE);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

    public void showToastAtBottom(String message) {
        if (isDestroyed) {
            return;
        }
        hiddenKeyboard();
        Toast mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        mToast.setMargin(0, 0);
        View view = mToast.getView();
        view.setBackgroundResource(R.drawable.shape_large_corner_rectangle);
        TextView tv = (TextView) view.findViewById(android.R.id.message);
        tv.setTextColor(Color.WHITE);
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
        showLoading(null, true, true);
    }

    /**
     * 显示加载中对话框
     *
     * @param messageID 默认为“加载中...”
     * @return
     */
    public void showLoading(int messageID) {
        showLoading(getString(messageID));
    }

    /**
     * 显示加载中对话框
     *
     * @param message 默认为“加载中...”
     * @return
     */
    public void showLoading(String message) {
        showLoading(message, true, true);
    }

    /**
     * 显示加载中对话框
     *
     * @param message              默认为“加载中...”
     * @param cancelable           设置进度条是否可以按退回键取消
     * @param canceledTouchOutside 设置点击进度对话框外的区域对话框不消失
     */
    public void showLoading(String message, boolean cancelable, boolean canceledTouchOutside) {
        //由于于要调用hiddenLoadingView,会把loadingProgress置空,所以是不影响的,而且这样也能保证只显示一个
        if (loadingProgress == null) {
            loadingProgress = new LoadingProgressDialog(this);
        }
        if (message != null) {
            loadingProgress.setMessage(message);
        }
        loadingProgress.setCancelable(cancelable);
        loadingProgress.setCanceledOnTouchOutside(canceledTouchOutside);
        loadingProgress.show();
    }


    /**
     * 隐藏加载框
     */
    public void hiddenLoadingView() {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (loadingProgress != null && loadingProgress.isShowing()) {
                    loadingProgress.dismiss();
                    loadingProgress = null;
                }
            }
        }, 800);
    }

    /**
     * 显示dialog风格的Toast
     *
     * @param message
     */
    public void showDialogMsg(String message) {
        msgDialog = new MessageDialog(this);
        if (message != null) {
            msgDialog.setMessage(message);
        }
        msgDialog.setCancelable(true);
        msgDialog.setCanceledOnTouchOutside(true);
        msgDialog.show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hiddenDialogMsg();
            }
        }, 1500);
    }

    public void showDialogMsgAtBottom(String message) {
        msgDialog = new MessageDialog(this);
        if (message != null) {
            msgDialog.setMessage(message);
        }
        msgDialog.setCancelable(true);
        msgDialog.setCanceledOnTouchOutside(true);
        Window dialogWindow = msgDialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER | Gravity.BOTTOM);
        WindowManager.LayoutParams attributes = dialogWindow.getAttributes();
        attributes.y = DensityUtil.dip2px(context, 100);
        dialogWindow.setAttributes(attributes);
        msgDialog.show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hiddenDialogMsg();
            }
        }, 1500);
    }


    /**
     * 隐藏dialog风格的Toast
     */
    public void hiddenDialogMsg() {
        if (null != msgDialog && msgDialog.isShowing()) {
            msgDialog.dismiss();
            msgDialog = null;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            finish();
            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void startActivity(Intent intent) {

        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }


    public void startActivityNoAnimation(Intent intent) {
        super.startActivity(intent);
    }

    public boolean isLogin() {
        if (StringUtil.isEmpty((String) SpUtils.getInstant().get(context, Constants.USER_ID, ""))) {
            return false;
        }
        return true;
    }


}
