package cn.bookzhan.http;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import cn.bookzhan.bases.BaseActivity;
import cn.bookzhan.library.Constants;
import cn.bookzhan.library.MyApplication;
import cn.bookzhan.library.R;
import cn.bookzhan.utils.LogUtil;
import cn.bookzhan.utils.StringUtil;

/**
 * Created by zhandalin on 2015-10-28 16:04.
 * 说明:基于OkHttpClient与自身的逻辑进行了简单封装
 */
public class GHSHttpClient {
    private OkHttpClient okHttpClienth;

    private static GHSHttpClient ghsHttpClient;
    private String TAG = "GHSHttpClient";
    private final Handler uiHandler;

    private GHSHttpClient() {
        okHttpClienth = new OkHttpClient();
        uiHandler = new Handler(Looper.getMainLooper());
    }

    public static GHSHttpClient getInstance() {
        if (null == ghsHttpClient) {
            synchronized (GHSHttpClient.class) {
                if (null == ghsHttpClient) {
                    ghsHttpClient = new GHSHttpClient();
                }
            }
        }
        return ghsHttpClient;
    }

    /**
     * post请求,错误的时候会弹Toast,status为false的时候会弹message所包含的信息
     *
     * @param url
     * @param params
     * @param responseHandler
     */
    public void post(Context context, String url, GHSRequestParams params, GHSHttpResponseHandler responseHandler) {
        sendPostRequest((BaseActivity) context, transformInfo4Post(params, url), responseHandler, false);
    }

    /**
     * 不带参数的post请求,错误的时候会弹Toast,status为false的时候会弹message所包含的信息
     *
     * @param context
     * @param url
     * @param responseHandler
     */
    public void post(Context context, String url, GHSHttpResponseHandler responseHandler) {
        sendPostRequest((BaseActivity) context, transformInfo4Post(new GHSRequestParams(), url), responseHandler, false);
    }

    /**
     * 不带参数
     * 这个只会弹status为false的时候,message所包含的信息
     * 适合调用者自定义错误信息
     *
     * @param context
     * @param url
     * @param responseHandler
     */
    public void post4NoErrorToast(Context context, String url, GHSHttpResponseHandler responseHandler) {
        sendPostRequest((BaseActivity) context, transformInfo4Post(new GHSRequestParams(), url), responseHandler, true);
    }

    /**
     * 这个只会弹status为false的时候,message所包含的信息
     * 适合调用者自定义错误信息
     *
     * @param context
     * @param url
     * @param params
     */
    public void post4NoErrorToast(Context context, String url, GHSRequestParams params, GHSHttpResponseHandler responseHandler) {
        sendPostRequest((BaseActivity) context, transformInfo4Post(params, url), responseHandler, true);
    }


    /**
     * 带参数的post请求,错误的时候不弹任何Toast
     *
     * @param url
     * @param params
     * @param responseHandler
     */
    public void post4NoToast(String url, GHSRequestParams params, GHSHttpResponseHandler responseHandler) {
        sendPostRequest(null, transformInfo4Post(params, url), responseHandler, true);
    }

    /**
     * 不带参数的post请求,错误的时候不弹任何Toast
     *
     * @param url
     * @param responseHandler
     */
    public void post4NoToast(final String url, GHSHttpResponseHandler responseHandler) {
        sendPostRequest(null, transformInfo4Post(new GHSRequestParams(), url), responseHandler, true);
    }

    /**
     * 直接返回网络请求的数据不做任何预处理,如果预处理的话会把有些转义字符处理掉
     * 有相应需求直接调用这个方法
     *
     * @param url
     * @param params
     * @param responseHandler
     */
    public void post4NoParseJson(final String url, GHSRequestParams params, GHSHttpResponseHandler responseHandler) {
        sendPostRequest(transformInfo4Post(params, url), responseHandler);
    }

    /**
     * 异步下载文件
     *
     * @param url
     * @param destFile        全路径
     * @param responseHandler
     */
    public void downloadAsyn(final String url, final String destFile, final GHSHttpResponseHandler responseHandler) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Call call = okHttpClienth.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                callFail(e.toString(), responseHandler, null, true);
            }

            @Override
            public void onResponse(Response response) {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    File file = new File(destFile);
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    callSuccess(destFile + "下载成功", responseHandler, null);
                } catch (IOException e) {
                    callFail(e.toString(), responseHandler, null, true);
                } finally {
                    try {
                        if (is != null) is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null) fos.close();
                    } catch (IOException e) {
                    }
                }

            }
        });
    }

    private Request transformInfo4Post(GHSRequestParams params, String method) {
        params.addParams("device_type", "976888");
        params.addParams("member_id", MyApplication.member_id);
        params.addParams("method", method);
        FormEncodingBuilder encodingBuilder = new FormEncodingBuilder();
        for (Map.Entry<String, String> temp : params.getParamMap().entrySet()) {
            encodingBuilder.add(temp.getKey(), temp.getValue());
        }
        encodingBuilder.add("sign", getSign(params, Constants.TOKEN));
        Request request = new Request.Builder()
                .url(Constants.SERVER_URL)
                .post(encodingBuilder.build()).build();

        if (Constants.DEVELOPER_MODE) {
            String url = Constants.SERVER_URL + "?";
            for (Map.Entry<String, String> temp : params.getParamMap().entrySet()) {
                url += temp.getKey() + "=" + temp.getValue() + "&";
            }
            url += "sign=" + getSign(params, Constants.TOKEN);
            LogUtil.d(TAG, "URL=" + url);
        }
        return request;
    }

    public String getRequestUrl(GHSRequestParams params, String method) {
        params.addParams("device_type", "976888");
        params.addParams("member_id", MyApplication.member_id);
        params.addParams("method", method);

        String url = Constants.SERVER_URL + "?";
        for (Map.Entry<String, String> temp : params.getParamMap().entrySet()) {
            url += temp.getKey() + "=" + temp.getValue() + "&";
        }
        url += "sign=" + getSign(params, Constants.TOKEN);
        return url;
    }

    /**
     * 这个会预处理json
     *
     * @param baseActivity
     * @param request
     * @param responseHandler
     */
    private void sendPostRequest(final BaseActivity baseActivity, Request request, final GHSHttpResponseHandler responseHandler, final boolean noErrorToast) {
        okHttpClienth.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                callFail(e.toString(), responseHandler, baseActivity, noErrorToast);
            }

            @Override
            public void onResponse(final Response response) {
                try {
                    parseJson(response.body().string(), responseHandler, baseActivity, noErrorToast);
                } catch (final Exception e) {
                    LogUtil.d(TAG, e.toString());
                    callFail(e.toString(), responseHandler, baseActivity, noErrorToast);
                }
            }
        });
    }


    /**
     * 这个方法不会预处理json,直接返回服务器结果
     *
     * @param request
     * @param responseHandler
     */
    private void sendPostRequest(Request request, final GHSHttpResponseHandler responseHandler) {
        okHttpClienth.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                callFail(e.toString(), responseHandler, null, true);
            }

            @Override
            public void onResponse(final Response response) {
                try {
                    callSuccess(response.body().string(), responseHandler, null);
                } catch (final Exception e) {
                    LogUtil.d(TAG, e.toString());
                    callFail(e.toString(), responseHandler, null, true);
                }
            }
        });

    }


    /**
     * @param params
     * @param token
     * @return
     */
    private String getSign(GHSRequestParams params, String token) {
        String sign = getMD5(getMD5(assemble(params)).toUpperCase() + token).toUpperCase();
        return sign;
    }

    private String assemble(GHSRequestParams params) {
        String sign = "";
        for (String key : params.getParamMap().keySet()) {//这个map是treemap默认就是自然排序
            sign += key + params.getParamMap().get(key);
        }
        return sign;
    }

    /**
     * 将字符串转成MD5值
     *
     * @param string
     * @return
     */
    private String getMD5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }

    /**
     * 初步的解析出json,来判定是否应该显示来自服务端的消息
     *
     * @param content
     * @param responseHandler
     * @param baseActivity
     * @throws JSONException
     */
    private void parseJson(String content, GHSHttpResponseHandler responseHandler, BaseActivity baseActivity, boolean noErrorToast) throws Exception {
        LogUtil.d(TAG, "result=" + content);
        JSONObject jsonObject = new JSONObject(content);
        String errorMsg = "网络或服务器错误~";
        if (jsonObject.has("rsp") && "fail".equals(jsonObject.getString("rsp"))) {
            callFail(errorMsg, responseHandler, baseActivity, noErrorToast);
            return;
        }
        Object tempData = jsonObject.get("data");
        if (null == tempData) {
            callSuccess("data数据为空", responseHandler, baseActivity);
            return;
        }
        if (tempData instanceof JSONObject) {
            JSONObject data = (JSONObject) tempData;
            String returndata = null;
            if (data.has("returndata")) {
                returndata = data.getString("returndata");
                if (StringUtil.isEmpty(returndata)) {
                    returndata = "{\"flag\":\"jsonError\"}";
                } else {
                    returndata = "{\"data\":" + returndata + "}";
                }
            }
            if (data.has("status")) {
                if (data.getBoolean("status")) {
                    callSuccess(returndata, responseHandler, baseActivity);
                } else {
                    if (data.has("message")) {
                        if (null != baseActivity)
                            showToast(baseActivity, data.getString("message"));
                    } else {
                        if (null != baseActivity)
                            showToast(baseActivity, data.getString(errorMsg));
                    }
                }
            } else {
                callSuccess(returndata, responseHandler, baseActivity);
            }
        } else {
            if (null != baseActivity)
                showToast(baseActivity, errorMsg);
            callFail(errorMsg, responseHandler, baseActivity, noErrorToast);
        }
    }


    private void callSuccess(final String content, final GHSHttpResponseHandler responseHandler, final BaseActivity baseActivity) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (null != baseActivity) {
                        baseActivity.hiddenLoadingView();
                        baseActivity.setContentView(baseActivity.rootView);
                    }
                    responseHandler.onSuccess(content);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }


    /**
     * 当网络错误,或者json格式错误就会调这个方法
     *
     * @param content
     * @param responseHandler
     * @param baseActivity
     */
    private void callFail(final String content, final GHSHttpResponseHandler responseHandler, final BaseActivity baseActivity, final boolean noErrorToast) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (null != baseActivity) {
                        baseActivity.hiddenLoadingView();
                        if (baseActivity.errorView != null) {
                            baseActivity.errorView.findViewById(R.id.error_naviagation).setVisibility(View.VISIBLE);
                            baseActivity.errorView.setVisibility(View.VISIBLE);
                            baseActivity.setContentView(baseActivity.errorView);
                        } else if (!noErrorToast) {
                            baseActivity.showToastAtCenter(null);
                        }
                    }
                    responseHandler.onFailure(content);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void showToast(final BaseActivity baseActivity, final String content) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (null != baseActivity) {
                    baseActivity.hiddenLoadingView();
                    baseActivity.showToastAtCenter(content);
                }
            }
        });
    }

}
