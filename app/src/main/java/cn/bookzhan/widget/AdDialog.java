package cn.bookzhan.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import cn.bookzhan.library.Constants;
import cn.bookzhan.library.R;
import cn.bookzhan.model.HomeBasesData;
import cn.bookzhan.response.HomeResponse;
import cn.bookzhan.utils.CommonUtils;
import cn.bookzhan.utils.FileUtils;

/**
 * Created by zhandalin on 2015-11-03 17:05.
 * 说明:
 */
public class AdDialog extends Dialog {
    private Context context;
    private View view;
    private int time = 5;
    private int NEXT_SECOND = 34;
    private ImageView ivAd;
    private TextView tvAdTime;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (time <= 0) {
                handler.removeMessages(NEXT_SECOND);
                dismiss();
            } else {
                tvAdTime.setText(time + "");
                handler.sendEmptyMessageDelayed(NEXT_SECOND, 1000);
            }
            time--;
        }
    };

    public AdDialog(Context context) {
        super(context, R.style.ad_dialog);
        this.context = context;
        initView();
    }


    private void initView() {
        getWindow().setWindowAnimations(R.style.main_menu_animstyle);
        view = View.inflate(context, R.layout.dialog_ad, null);
        ivAd = (ImageView) view.findViewById(R.id.iv_ad);
        tvAdTime = (TextView) view.findViewById(R.id.tv_ad_time);
        tvAdTime.setText(time + "");
        view.findViewById(R.id.ll_skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        try {
            String jsonFile = FileUtils.readFile(Constants.BIG_IMAGE_AD_JSON_DIR);
            HomeResponse response = new Gson().fromJson(jsonFile, HomeResponse.class);
            if (null != response && null != response.getData() && response.getData().size() > 0) {
                final HomeBasesData homeBasesData = response.getData().get(0);
                ivAd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MobclickAgent.onEvent(context, "home_big_image_ad");
                        CommonUtils.judgeWhereToGo(context, homeBasesData);
                        if (null != handler)
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (isShowing())
                                        dismiss();
                                }
                            }, 200);

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentView(view);
        getWindow().setLayout(RadioGroup.LayoutParams.FILL_PARENT, RadioGroup.LayoutParams.FILL_PARENT);

    }

    public void setImageDrawable(Drawable drawable) {
        ivAd.setImageDrawable(drawable);
    }

    @Override
    public void show() {
        super.show();
        handler.sendEmptyMessageDelayed(NEXT_SECOND, 1000);
    }
}
