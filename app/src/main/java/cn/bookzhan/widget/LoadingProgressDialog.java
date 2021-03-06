package cn.bookzhan.widget;

import android.app.Dialog;
import android.content.Context;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.bookzhan.library.R;


/**
 * Created by bookzhan on 2015/8/6.
 * 最后修改者: bookzhan  version 1.0
 * 说明:加载的对话框
 */
public class LoadingProgressDialog extends Dialog {
    private ProgressBar progressBar;
    private TextView tvMsg;

    public LoadingProgressDialog(Context context) {
        super(context, R.style.mydialog);
        init();
    }

    public LoadingProgressDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    private void init() {
        setContentView(R.layout.loading_layout);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        tvMsg = (TextView) findViewById(R.id.tv_msg);
    }

    /**
     * 默认是"加载中..."
     * @param message
     */
    public void setMessage(String message) {
        tvMsg.setText(message);
    }
}
