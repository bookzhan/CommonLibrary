package cn.bookzhan.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return mWifi.isConnected();
    }


    /**
     * 获取版本更新的信息,直接调用这个就好了
     *
     * @param context
     * @param isComeMainActivity 是否是来自首页创建的时候
     */

//    public static void getUpdateData(final Context context, final boolean isComeMainActivity) {
//        RequestParams params = new RequestParams();
//        params.put("device_type", "Android");
//        params.put("version", getVersion(context));
//
//        HttpClient.post(Constant.GHS_UPDATE, params,
//                new TextHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers,
//                                          String responseString) {
//                        Log.i("wzc", responseString);
//                        BaseResponse response = JSONParser.fromJson(
//                                responseString, BaseResponse.class);
//                        if (response.isSuccess()) {
//                            setUpdateData(context, responseString, isComeMainActivity);
//                        } else {
//                            if (response.isShowServerMessage()) {
//                                ToastUtils.showToastAtCenter(context, response.getMessage());
//                            } else {
//                                // 显示客户端定义的错误信息
//                                ToastUtils.showToastAtCenter(context, "服务器错误");
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers,
//                                          String responseString, Throwable throwable) {
//                        ToastUtils.showToastAtCenter(context, "网络或服务器错误");
//                    }
//                });
//
//    }

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


//    public static void setUpdateData(final Context context, String responseString, final boolean isComeMainActivity) {
//        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View dialogview = layoutInflater.inflate(R.layout.common_dialog_layout, null);
//        TextView ok = (TextView) dialogview.findViewById(R.id.dialog_right);
//        TextView cancle = (TextView) dialogview.findViewById(R.id.dialog_left);
//        cancle.setText("稍候再说");
//        ok.setText("知道了");
//        View line = dialogview.findViewById(R.id.line_3);
//        UpdateResponse response = JSONParser.fromJson(responseString, UpdateResponse.class);
//        if (null != response && null != response.getData()) {
//            final UpdateModel update = response.getData();
////            String last_cancle_version = (String) SpUtils.getInstant().get(context, "last_cancle_version", "");
////            if (last_cancle_version.equals(update.getVersion()) && isComeMainActivity) {
////                return;//上一次忽略了这个版本了
////            }
//            if (getVersion(context).equals(update.getVersion())) {//版本号是最新的
//                if (isComeMainActivity) {//来自首页创建的时候不显示对话框
//                    return;
//                }
//                final Dialog dialog = new Dialog(context, R.style.mydialog);
//                line.setVisibility(View.GONE);
//                cancle.setVisibility(View.GONE);
//                ok.setBackgroundResource(R.drawable.dialog_button_back_single);
//                dialog.setContentView(dialogview);
//                dialog.setCanceledOnTouchOutside(true);
//                dialog.show();
//                ok.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View arg0) {
//                        dialog.dismiss();
//                    }
//                });
//            } else {
//                ok.setText("立即更新");
//                final Dialog dialog = new Dialog(context, R.style.mydialog);
//                TextView content = (TextView) dialogview.findViewById(R.id.dialog_content);
//                if (!StringUtil.isEmpty(update.getSummary())) {
//                    content.setText(update.getSummary());
//                } else {
//                    content.setText("检测到有新版本是否更新?");
//                }
//                dialog.setContentView(dialogview);
//                if (update.getForce_update().equals("1")) {//强制更新
//                    dialog.setCanceledOnTouchOutside(false);
//                    line.setVisibility(View.GONE);
//                    cancle.setVisibility(View.GONE);
//                    ok.setBackgroundResource(R.drawable.dialog_button_back_single);
//                    dialog.setCancelable(false);
//                } else {
//                    dialog.setCancelable(true);
//                    dialog.setCanceledOnTouchOutside(true);
//                    line.setVisibility(View.VISIBLE);
//                    cancle.setVisibility(View.VISIBLE);
//                    ok.setBackgroundResource(R.drawable.dialog_button_back_right);
//                }
//                dialog.show();
//                ok.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View arg0) {
//                        Uri uri = Uri.parse(update.getDownload_url());
//                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                        ((Activity) context).startActivity(intent);
//                    }
//                });
//                cancle.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View arg0) {
////                        if (isComeMainActivity) {
////                            SpUtils.getInstant().put(context, "last_cancle_version", update.getVersion());
////                        }
//                        dialog.dismiss();
//                    }
//                });
//            }
//        }
//    }

}
