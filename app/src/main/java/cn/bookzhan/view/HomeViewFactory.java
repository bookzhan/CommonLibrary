package cn.bookzhan.view;

/**
 * Created by zhandalin 2015年08月26日 13:36.
 * 最后修改者: zhandalin  version 1.0
 * 说明:首页View的Factory
 */
public class HomeViewFactory {
    //这是在主页的不用销毁
//    private static HomeViewFactory factory;
//    private static final HashMap<Integer, HomeBaseView> viewHashMap = new HashMap<>();
//
//    private static final int HOME_CAROUSEL_IMAGE = 0;
//    private static final int HOME_BUTTON_GROUP = 1;
//    private static final int HOME_CAROUSEL_TEXT = 2;
//    private static final int HOME_TVLIVE = 3;
//    private static final int HOME_IMAGE_GROUP = 4;
//    private static final int HOME_SECKILL_VIEW = 5;
//    private static final int HOME_DUTYFREE_SHOP = 6;
//    private HomeResponseData homeResponseData;
//
//    private HomeViewFactory() {
//    }
//
//    public static synchronized HomeViewFactory getInstance() {
//        if (factory == null) {
//            factory = new HomeViewFactory();
//        }
//        return factory;
//    }
//
//    public View getView(Context context, int position) {
//        if (viewHashMap.containsKey(position)) {
//            return viewHashMap.get(position).getView();
//        }
//        HomeBaseView baseView = null;
//        switch (position) {
//            case HOME_CAROUSEL_IMAGE:
//                baseView = new HomeCarouselImage(context, homeResponseData.getCarousel_data());
//                break;
//            case HOME_BUTTON_GROUP:
//                baseView = new HomeButtonGroup(context, homeResponseData.getButton_group());
//                break;
//            case HOME_CAROUSEL_TEXT:
//                baseView = new HomeCarouselText(context, homeResponseData.getCarousel_text());
//                break;
//            case HOME_TVLIVE:
//                baseView = new HomeTVLiveView(context);
//                break;
//            case HOME_IMAGE_GROUP:
//                baseView = new HomeImageGroup(context, homeResponseData.getImage_group());
//                break;
//            case HOME_SECKILL_VIEW:
//                baseView = new HomeSeckillView(context);
//                break;
//            case HOME_DUTYFREE_SHOP:
//                baseView = new HomeDutyFreeShop(context, homeResponseData.getDuty_free_shop());
//                break;
//        }
//        viewHashMap.put(position, baseView);
//        return baseView.getView();
//    }
//
//    public void setJsonData(HomeResponseData homeResponseData) {
//        this.homeResponseData = homeResponseData;
//    }
//
//    public void refresh(HomeResponseData homeResponseData) {
//        this.homeResponseData = homeResponseData;
//        if (null != viewHashMap) {
//            if (null != viewHashMap.get(HOME_CAROUSEL_IMAGE)) {
//                viewHashMap.get(HOME_CAROUSEL_IMAGE).refresh(homeResponseData.getCarousel_data());
//            }
//            if (null != viewHashMap.get(HOME_BUTTON_GROUP)) {
//                viewHashMap.get(HOME_BUTTON_GROUP).refresh(homeResponseData.getButton_group());
//            }
//            if (null != viewHashMap.get(HOME_CAROUSEL_TEXT)) {
//                viewHashMap.get(HOME_CAROUSEL_TEXT).refresh(homeResponseData.getCarousel_text());
//            }
//            if (null != viewHashMap.get(HOME_TVLIVE)) {
//                viewHashMap.get(HOME_TVLIVE).refresh(null);
//            }
//            if (null != viewHashMap.get(HOME_IMAGE_GROUP)) {
//                viewHashMap.get(HOME_IMAGE_GROUP).refresh(homeResponseData.getImage_group());
//            }
//            if (null != viewHashMap.get(HOME_SECKILL_VIEW)) {
//                viewHashMap.get(HOME_SECKILL_VIEW).refresh(null);
//            }
//            if (null != viewHashMap.get(HOME_DUTYFREE_SHOP)) {
//                viewHashMap.get(HOME_DUTYFREE_SHOP).refresh(homeResponseData.getDuty_free_shop());
//            }
//        }
//    }

}
