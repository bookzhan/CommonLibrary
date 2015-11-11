package cn.bookzhan.http;

public interface GHSHttpResponseHandler {
    void onSuccess(String content);

    void onFailure(String content);
}