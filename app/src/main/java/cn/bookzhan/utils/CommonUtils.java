package cn.bookzhan.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.bookzhan.activity.PlayerActivity;
import cn.bookzhan.bases.BaseActivity;
import cn.bookzhan.exovideo.ExoPlayerActivity;
import cn.bookzhan.http.GHSHttpClient;
import cn.bookzhan.http.GHSHttpResponseHandler;
import cn.bookzhan.http.GHSRequestParams;
import cn.bookzhan.library.Constants;
import cn.bookzhan.library.R;
import cn.bookzhan.model.HomeBasesData;
import cn.bookzhan.model.UpdateModel;
import cn.bookzhan.response.HomeResponse;
import cn.bookzhan.response.UpdateResponse;
import cn.bookzhan.widget.AdDialog;
import cn.bookzhan.widget.CommonDialog;

/**
 * @Author bookzhan
 * @Date 2015-7-01
 * @Desc 一些比较通用的工具类
 */
public class CommonUtils {

    public static String getCurrentTime(String format) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        String currentTime = sdf.format(date);
        return currentTime;
    }

    public static String getCurrentTime() {
        return getCurrentTime("yyyy-MM-dd  HH:mm:ss");
    }


    /**
     * 是否有网络连接
     *
     * @return
     */
    public static boolean hasInternet(Context context) {
        try {
            if (context != null) {
                ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                if (mNetworkInfo != null) {
                    return mNetworkInfo.isAvailable();
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 获取版本更新的信息,直接调用这个就好了
     *
     * @param context
     * @param isComeMainActivity 是否是来自首页创建的时候
     */

    public static void checkUpdate(final Context context, final boolean isComeMainActivity) {
        final BaseActivity baseActivity = (BaseActivity) context;
        GHSRequestParams params = new GHSRequestParams();
        params.addParams("version", getVersion(context));
        if (!isComeMainActivity) {
            baseActivity.showLoading();
        }
        GHSHttpClient.getInstance().post4NoToast(Constants.Url.GHS_UPDATE, params, new GHSHttpResponseHandler() {
            @Override
            public void onSuccess(String content) {
                try {
                    if (!isComeMainActivity) {
                        baseActivity.hiddenLoadingView();
                    }
                    UpdateResponse response = new Gson().fromJson(content, UpdateResponse.class);
                    if (null != response && null != response.getData()) {
                        setUpdateData(context, response.getData(), isComeMainActivity);
//                        showUpdateDialog(context, response.getData());
                    }
                } catch (Exception e) {
                    if (!isComeMainActivity) {
                        baseActivity.showToastAtCenter(null);
                    }
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String content) {
                if (!isComeMainActivity) {
                    baseActivity.hiddenLoadingView();
                    baseActivity.showToastAtCenter(null);
                }
            }
        });

    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }


    public static void setUpdateData(final Context context, final UpdateModel update, final boolean isComeMainActivity) {
        CommonDialog commonDialog = new CommonDialog(context);
        commonDialog.setLeftButtonMsg("知道了");
        commonDialog.setRightButtonMsg("稍候再说");
//            String last_cancle_version = (String) SpUtils.getInstant().get4NoToast(context, "last_cancle_version", "");
//            if (last_cancle_version.equals(update.getVersion()) && isComeMainActivity) {
//                return;//上一次忽略了这个版本了
//            }
        if (getVersion(context).equals(update.getVersion())) {//版本号是最新的
            if (isComeMainActivity) {//来自首页创建的时候不显示对话框
                return;
            }
            commonDialog.setContentMsg("恭喜您，已经是最新版本");
            commonDialog.hideRightButton();
            commonDialog.setCanceledOnTouchOutside(true);
            commonDialog.show();
        } else {
            commonDialog.setRightButtonMsg("立即更新");
            if (!StringUtil.isEmpty(update.getSummary())) {
                commonDialog.setContentMsg(update.getSummary());
            } else {
                commonDialog.setContentMsg("检测到有新版本是否更新?");
            }
            if (1 == update.getForce_update()) {//强制更新
                commonDialog.setCanceledOnTouchOutside(false);
                commonDialog.setCancelable(false);
                commonDialog.hideRightButton();
            } else {
                commonDialog.setCancelable(true);
                commonDialog.setCanceledOnTouchOutside(true);
                commonDialog.showRightButton();
            }
            commonDialog.setOnRightButtonOnClick(new CommonDialog.RightButtonOnClickListener() {
                @Override
                public void onRightButtonOnClick() {
                    Uri uri = Uri.parse(update.getDownload_url());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                }
            });
            commonDialog.setOnLeftButtonOnClick(new CommonDialog.LeftButtonOnClickListener() {
                @Override
                public void onLeftButtonOnClick() {
//                        if (isComeMainActivity) {
//                            SpUtils.getInstant().put(context, "last_cancle_version", update.getVersion());
//                        }
                }
            });
            commonDialog.show();
        }
    }


    /**
     * @param price 格式化数字
     * @return
     */
    public static String formatPrice(double price) {
        return String.format("%.1f", price);
    }

    /**
     * 计算抵扣
     *
     * @param price 传小数进来就好了
     * @return
     */
    public static String getDiscount(double price) {
        String temp = String.format("%.1f", price *= 10);
        if (temp.endsWith("0")) {
            return temp.substring(0, 1);
        }
        return temp;
    }


    /**
     * 拨打电话
     */
    public static void dialServicePhone(final Context context) {
        CommonDialog commonDialog = new CommonDialog(context);
        commonDialog.setContentMsg("现在就拨打客服电话?\n" + Constants.SERVICE_PHONE_NUM);
        commonDialog.setOnRightButtonOnClick(new CommonDialog.RightButtonOnClickListener() {
            @Override
            public void onRightButtonOnClick() {
                // 点击拨打跳转到拨号界面
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_CALL);
                intent.setData(Uri.parse(Constants.TELPHONENUM));
                context.startActivity(intent);
            }
        });
        commonDialog.show();
    }

    /**
     * 发送短信
     *
     * @param phoneNumber
     * @param message
     */
    public static void sendMessage(Context context, String phoneNumber, String message) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("smsto", phoneNumber, null));
        intent.putExtra("sms_body", message);
        context.startActivity(Intent.createChooser(intent, null));
    }

    /**
     * 浏览网页
     *
     * @param webUrl
     */
    public static void viewUrl(Context context, String webUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.valueOf(webUrl)));
        context.startActivity(Intent.createChooser(intent, null));
    }


    /**
     * 检测网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean checkNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }
        return true;
    }

    /**
     * 检测wifi是否可用
     *
     * @param context
     * @return
     */
    public static boolean checkWifiAvailabl(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        NetworkInfo.State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

        if (NetworkInfo.State.CONNECTED == wifi) {
            return true;
        } else if (NetworkInfo.State.CONNECTED == mobile) {
            return false;
        }
        return false;
    }


    /**
     * 打印
     *
     * @param
     * @return
     */
    public static String prettyPrint(double d) {
        if (d == (long) d)
            return String.format("%d", (long) d);
        else
            return String.format("%s", d);
    }

    public static void clearCache(Context context) {
        File directory = context.getCacheDir();
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }

    /**
     * 判断首页条目点击的去向
     *
     * @param context
     * @param homeBasesData
     * @return 如果返回true就需要跳转到默认的地方
     */
    public static void judgeWhereToGo(Context context, HomeBasesData homeBasesData) {
        if (homeBasesData != null) {
            switch (homeBasesData.getModel()) {
                case 1:
//                    context.startActivity(new Intent(context, ProductDetailActivity.class).putExtra("sku", homeBasesData.getId_or_url()));
                    break;
                case 2:
//                    Intent intent1 = new Intent(context, GoodsCategoryDetailActivity.class);
//                    intent1.putExtra("cat_id", homeBasesData.getId_or_url());
//                    intent1.putExtra("subject_title", homeBasesData.getTitle());
//                    context.startActivity(intent1);
                    break;
                case 3:
//                    Intent intent = new Intent(context, ProductWebActivity.class);
//                    intent.putExtra("Url", homeBasesData.getId_or_url());
//                    intent.putExtra("title", homeBasesData.getTitle());
//                    context.startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * @param startTime 单位为秒
     * @param end_time  单位为秒
     * @return 精确到小时
     */
    public static String getTime(long startTime, long end_time) {
        long second = end_time - startTime;

        long hh = second / 3600;
        long mm = second % 3600 / 60;
        long ss = second % 60;
        String strTemp;
        if (0 != hh) {
            strTemp = String.format("倒计时 <font color=\"#7f1084\">%02d</font>小时<font color=\"#7f1084\">%02d</font>分<font color=\"#7f1084\">%02d</font>秒", hh, mm, ss);
        } else {
            strTemp = String.format("倒计时 <font color=\"#7f1084\">%02d</font>分<font color=\"#7f1084\">%02d</font>秒", mm, ss);
        }
        return strTemp;
    }


    /**
     * @param second 单位为秒
     * @return 返回格式为 23:21
     */
    public static String getTime(long second) {
        if (second < 0) {
            return "00:00";
        }
        long hh = second / 3600;
        long mm = second % 3600 / 60;
        long ss = second % 60;
        String strTemp;
        if (0 != hh) {
            strTemp = String.format("%02d:%02d:%02d", hh, mm, ss);
        } else {
            strTemp = String.format("%02d:%02d", mm, ss);
        }
        return strTemp;
    }

    /**
     * @param second 单位为秒
     * @return 返回格式为 23:21
     */
    public static String getTimes(long second) {
        if (second < 0) {
            return "00:00";
        }
        long hh = second / 3600;
        long mm = second % 3600 / 60;
        return String.format("%02d:%02d", hh, mm);
    }

    /**
     * 大于一天的截取掉
     *
     * @param second 单位为秒
     * @return
     */
    public static String getTime4HH(long second) {
        if (second < 0) {
            return "00:00";
        }
        Date date = new Date(second * 1000);
        int hours = date.getHours();
        int minutes = date.getMinutes();
        return String.format("%02d:%02d", hours, minutes);
    }

    public static void startPlayerActivity(final Context context, final String sku, final String url) {
        if (!checkWifiAvailabl(context)) {
            final CommonDialog commonDialog = new CommonDialog(context);
            commonDialog.setTitleMsg(context.getResources().getString(R.string.warm_tip));
            commonDialog.setContentMsg(context.getResources().getString(R.string.wram_tip_message));
            commonDialog.setRightButtonMsg(context.getResources().getString(R.string.continue_watch));
            commonDialog.setOnRightButtonOnClick(new CommonDialog.RightButtonOnClickListener() {
                @Override
                public void onRightButtonOnClick() {
                    toPlayerActivity(context, sku, url);
                }
            });
            commonDialog.show();
        } else {
            toPlayerActivity(context, sku, url);
        }
    }

    private static void toPlayerActivity(Context context, String sku, String url) {
        MobclickAgent.onEvent(context, "watch_live");
        Intent intent;

        if ("huawei".equalsIgnoreCase(Build.MANUFACTURER)) {
            intent = new Intent(context, PlayerActivity.class);
        } else if (Build.VERSION.SDK_INT > 16) {
            intent = new Intent(context, ExoPlayerActivity.class);
        } else {
            intent = new Intent(context, PlayerActivity.class);
        }
        intent.putExtra("sku", sku);
        if (StringUtil.isEmpty(url)) {
            url = Constants.Url.VIDEO_COUNTRY_URL;
        }
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    /**
     * 展示大图广告
     */
    public static void showBigImageAD(Context context) {
        File file = new File(Constants.BIG_IMAGE_AD_DIR);
        if (file.exists() && file.length() > 0) {
            AdDialog adDialog = new AdDialog(context);
            adDialog.setImageDrawable(Drawable.createFromPath(Constants.BIG_IMAGE_AD_DIR));
            adDialog.show();
//            context.startActivity(new Intent(context, AdActivity.class));
        }
        final GHSHttpClient httpClient = GHSHttpClient.getInstance();
        GHSRequestParams params = new GHSRequestParams();
        params.addParams("seat", "appstart");
        httpClient.post4NoToast(Constants.Url.HOME_AD, params, new GHSHttpResponseHandler() {
            @Override
            public void onSuccess(String content) {
                FileUtils.writeFile(Constants.BIG_IMAGE_AD_JSON_DIR, content);

                HomeResponse response = new Gson().fromJson(content, HomeResponse.class);
                if (null != response && null != response.getData() && response.getData().size() > 0) {
                    HomeBasesData homeBasesData = response.getData().get(0);
                    httpClient.downloadAsyn(homeBasesData.getImage(), Constants.BIG_IMAGE_AD_DIR, new GHSHttpResponseHandler() {
                        @Override
                        public void onSuccess(String content) {

                        }

                        @Override
                        public void onFailure(String content) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(String content) {

            }
        });

    }


}
