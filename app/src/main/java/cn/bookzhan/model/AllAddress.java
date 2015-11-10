package cn.bookzhan.model;

import java.util.List;

/**
 * Created by bookzhan on 2015/7/21.
 * 最后修改者: bookzhan  @version 1.0 .
 * 获取全国所有地址信息的类
 */
public class AllAddress {
    public List<ProvinceModel> children;

    public class ProvinceModel {
        public String name;
        public String code;
        public List<CityModel> children;
    }

    public class CityModel {
        public String name;
        public String code;
        public List<DistrictModel> children;
    }

    public class DistrictModel {
        public String name;
        public String code;
    }


    public String[] getAllProvieceName() {
        String temp[] = new String[children.size()];
        for (int i = 0; i < children.size(); i++) {
            temp[i] = children.get(i).name;
        }
        return temp;
    }

    public String[] getAllProvieceCode() {
        String temp[] = new String[children.size()];
        for (int i = 0; i < children.size(); i++) {
            temp[i] = children.get(i).code;
        }
        return temp;
    }

    /**
     * @param proviecePosition 省份的位置
     * @return 城市的名字
     */
    public String[] getCityName(int proviecePosition) {
        String temp[] = new String[children.get(proviecePosition).children.size()];
        for (int i = 0; i < children.get(proviecePosition).children.size(); i++) {
            temp[i] = children.get(proviecePosition).children.get(i).name;
        }
        return temp;
    }

    /**
     * @param proviecePosition 省份的位置
     * @return 城市的编码
     */
    public String[] getCityCode(int proviecePosition) {
        String temp[] = new String[children.get(proviecePosition).children.size()];
        for (int i = 0; i < children.get(proviecePosition).children.size(); i++) {
            temp[i] = children.get(proviecePosition).children.get(i).code;
        }
        return temp;
    }

    /**
     * @param proviecePosition 省份的位置
     * @param cityPosition     城市的位置
     * @return 区的名字
     */
    public String[] getDistrictName(int proviecePosition, int cityPosition) {
        List<DistrictModel> districtModelList = this.children.get(proviecePosition).children.get(cityPosition).children;
        String temp[] = new String[districtModelList.size()];
        for (int i = 0; i < districtModelList.size(); i++) {
            temp[i] = districtModelList.get(i).name;
        }
        return temp;
    }

    /**
     * @param proviecePosition 省份的位置
     * @param cityPosition     城市的位置
     * @return 区的编码
     */
    public String[] getDistrictCode(int proviecePosition, int cityPosition) {
        List<DistrictModel> districtModelList = this.children.get(proviecePosition).children.get(cityPosition).children;
        String temp[] = new String[districtModelList.size()];
        for (int i = 0; i < districtModelList.size(); i++) {
            temp[i] = districtModelList.get(i).code;
        }
        return temp;
    }

}
