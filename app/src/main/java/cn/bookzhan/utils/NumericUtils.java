package cn.bookzhan.utils;

/**
 * Created by youzi on 2015/10/23.
 */
public class NumericUtils {
    //保留 2位小数！！！！
    public static  String retain(double c) {
        double a = c;
        if(a<0)a=0;
        int b = (int) (a * 10);
        a = (double) b / 10;

        return (a + "");
    }
    public static  String retain(String c) {
        double a = Double.valueOf(c);
        if(a<0)a=0;
        int b = (int) (a * 10);
        a = (double) b / 10;
        return (a + "");
    }
}
