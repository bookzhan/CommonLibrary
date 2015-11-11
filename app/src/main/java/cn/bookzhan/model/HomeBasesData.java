package cn.bookzhan.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zhandalin 2015年08月27日 17:30.
 * 最后修改者: zhandalin  version 1.0
 * 说明:首页的基础信息封装
 */
public class HomeBasesData {
    private String image;
    @SerializedName("type")
    private int model;
    private String title;
    @SerializedName("link")
    private String id_or_url;

    private long starttime;
    private long endtime;


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId_or_url() {
        return id_or_url;
    }

    public void setId_or_url(String id_or_url) {
        this.id_or_url = id_or_url;
    }

    public long getStarttime() {
        return starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }
}
