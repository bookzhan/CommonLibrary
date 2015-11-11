package cn.bookzhan.utils;

import android.content.Context;

import cn.bookzhan.library.Constants;
import cn.bookzhan.library.MyApplication;
import cn.bookzhan.model.UserInfo;

/**
 * Created by bookzhan on 2015/8/9.
 * 最后修改者: bookzhan  version 1.0
 * 说明:获取用户信息
 */
public class UserInfoUtils {

    public static boolean isLogin(Context context) {
        if (StringUtil.isEmpty((String) SpUtils.getInstant().get(context, Constants.USER_ID, ""))) {
            return false;
        }
        return true;
    }

    public static String getUserId(Context context) {
        return (String) SpUtils.getInstant().get(context, Constants.USER_ID, "");
    }

    public static String getUserMobile(Context context) {
        return (String) SpUtils.getInstant().get(context, Constants.USER_MOBILE, "");
    }


    /**
     * 返回全部的地址字段,包括详细地址
     *
     * @param context
     * @return
     */
    public static String getDefaultAddress(Context context) {
        return (String) SpUtils.getInstant().get(context, Constants.DEFAULT_ADDRESS, "");
    }

    /**
     * 设置全部的地址字段,包括详细地址
     *
     * @param context
     * @param defaultAddress
     */
    public static void setDefaultAddress(Context context, String defaultAddress) {
        SpUtils.getInstant().put(context, Constants.DEFAULT_ADDRESS, defaultAddress);
    }


    /**
     * 返回默认的收货地址的省份
     *
     * @param context
     * @return
     */
    public static String getDefaultProvince(Context context) {
        return (String) SpUtils.getInstant().get(context, Constants.DEFAULT_PROVINCE, "");
    }

    /**
     * 设置默认的收货地址的省份
     *
     * @param context
     * @param defaultProvince
     */
    public static void setDefaultProvince(Context context, String defaultProvince) {
        SpUtils.getInstant().put(context, Constants.DEFAULT_PROVINCE, defaultProvince);
    }


    /**
     * 返回默认的收货地址的省份编码
     *
     * @param context
     * @return
     */
    public static String getDefaultProvinceCode(Context context) {
        return (String) SpUtils.getInstant().get(context, Constants.DEFAULT_PROVINCE_CODE, "");
    }

    /**
     * 设置默认的收货地址的省份编码
     *
     * @param context
     * @param defaultProvinceCode
     */
    public static void setDefaultProvinceCode(Context context, String defaultProvinceCode) {
        SpUtils.getInstant().put(context, Constants.DEFAULT_PROVINCE_CODE, defaultProvinceCode);
    }


    /**
     * 返回定位的的省份
     *
     * @param context
     * @return
     */
    public static String getLocationProvince(Context context) {
        return (String) SpUtils.getInstant().get(context, Constants.LOCATION_PROVINCE, "");
    }

    /**
     * 设置定位的省份
     *
     * @param context
     * @param locationProvince
     */
    public static void setLocationProvince(Context context, String locationProvince) {
        SpUtils.getInstant().put(context, Constants.LOCATION_PROVINCE, locationProvince);
    }

    /**
     * 返回定位的省份编码
     *
     * @param context
     * @return
     */
    public static String getLocationProvinceCode(Context context) {
        return (String) SpUtils.getInstant().get(context, Constants.LOCATION_PROVINCE_CODE, "");
    }

    /**
     * 设置定位的的省份编码
     *
     * @param context
     * @param locationProvinceCode
     */
    public static void setLocationProvinceCode(Context context, String locationProvinceCode) {
        SpUtils.getInstant().put(context, Constants.LOCATION_PROVINCE_CODE, locationProvinceCode);
    }

    public static void clearUerInfo(Context context) {
        SpUtils spUtils = SpUtils.getInstant();
        spUtils.remove(context, Constants.USER_ID);
        MyApplication.member_id = "";
        spUtils.remove(context, Constants.USER_NAME);
       // spUtils.remove(context, Constants.USER_MOBILE);
        spUtils.remove(context, Constants.USER_GENDER);
        spUtils.remove(context, Constants.USER_AVATAR);
        spUtils.remove(context, Constants.USER_LEVEL);
        spUtils.remove(context, Constants.USER_BIRTHDAY);
        spUtils.remove(context, Constants.USER_CARD_ID);
        spUtils.remove(context, Constants.DEFAULT_ADDRESS);
        spUtils.remove(context, Constants.DEFAULT_ADDRESS_ID);
        spUtils.remove(context, Constants.DEFAULT_ADDRESS_PHONE);
        spUtils.remove(context, Constants.DEFAULT_ADDRESS_NAME);
        spUtils.remove(context, Constants.DEFAULT_ADDRESS_AREA);
        spUtils.remove(context, Constants.DEFAULT_ADDRESS_DETAIL);
        spUtils.remove(context, Constants.DEFAULT_PROVINCE_CODE);
        MyApplication.member_id = "";
    }

    public static UserInfo getUerInfo(Context context) {
        SpUtils spUtils = SpUtils.getInstant();
        UserInfo userInfo = new UserInfo();
        String userId = (String) spUtils.get(context, Constants.USER_ID, "0");
        userInfo.setMember_id(userId);
        MyApplication.member_id = userId;
        userInfo.setNick_name((String) spUtils.get(context, Constants.USER_NAME, ""));
        userInfo.setMobile((String) spUtils.get(context, Constants.USER_MOBILE, ""));
        userInfo.setGender((String) spUtils.get(context, Constants.USER_GENDER, ""));
        String aa=(String) spUtils.get(context, "birthday","1970-01-01");
        String bb=(String) spUtils.get(context, "pw_id", "");

        userInfo.setBirthday((String) spUtils.get(context, Constants.USER_BIRTHDAY,"1970-01-01"));
        userInfo.setAvatar((String) spUtils.get(context, Constants.USER_AVATAR, ""));
        userInfo.setLevel((String)spUtils.get(context, Constants.USER_LEVEL, ""));
        userInfo.setCard_id((String) spUtils.get(context, Constants.USER_CARD_ID, ""));
        return userInfo;
    }


    public static void login(Context context){
//        Intent intent=new Intent(context, LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//        context.startActivity(intent);
    }
}
