package cn.bookzhan.http;

import java.util.TreeMap;

/**
 * Created by bookzhan on 2015/8/7.
 * 最后修改者: bookzhan  version 1.0
 * 说明: 对请求参数的简单封装,使得不再区分post和get
 */
public class GHSRequestParams {
    private TreeMap<String, String> paramMap = new TreeMap<>();

    public void addParams(String key, String param) {
        if (key == null || param == null) {
            return;
        }
        paramMap.put(key, param);
    }

    public TreeMap<String, String> getParamMap() {
        return paramMap;
    }
}
