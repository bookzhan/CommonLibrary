package cn.bookzhan.wheel;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import com.google.gson.Gson;
import cn.bookzhan.library.R;
import cn.bookzhan.model.AllAddress;


/**
 * 地址选择
 *
 * @author zhangyakai
 * @time 2014-7-23 上午10:46:06
 * 使用方法:
 * final AddrWheelPicker addrWheelPicker = new AddrWheelPicker(this, addressString);//传入地址json
 * addrWheelPicker.creatPopupWindow();
 * addrWheelPicker.setAddrSetListenner(new AddrSetListenner() {
 * public void setAddr() {
 * addr = addrWheelPicker.getAddress();
 * etAddr.setText(addr[0] + "-" + addr[1] + "-" + addr[2]);
 * }
 * });
 *
 */
public class AddrWheelPicker {

    private AllAddress address;
    private PopupWindow popupWindow;
    private View popupView;
    private Context context;
    private WheelView province, city, district;
    private String[] provieceName;

    public AddrWheelPicker(Context context, String addrString) {
        this.context = context;
        address = new Gson().fromJson(addrString, AllAddress.class);
        provieceName = address.getAllProvieceName();
    }

    public interface AddrSetListenner {
        void setAddr();
    }

    AddrSetListenner addrSetListenner;

    public void setAddrSetListenner(AddrSetListenner addrSetListenner) {
        this.addrSetListenner = addrSetListenner;
    }

    public void creatPopupWindow() {

        if (popupWindow == null) {
            popupView = View.inflate(context, R.layout.activity_addr_wheel_picker, null);
            initWheel();
            popupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
            popupWindow.setAnimationStyle(R.style.popupwindow);
            popupWindow.setBackgroundDrawable(new ColorDrawable(R.color.transparent));
            popupWindow.showAtLocation(((Activity) context).getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
            popupWindow.setTouchable(true);
            popupWindow.setFocusable(true);
            popupView.findViewById(R.id.btn_addr_set_cancel).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    popupWindow.setFocusable(false);
                    popupWindow.dismiss();
                    popupView = null;
                }
            });
            popupView.findViewById(R.id.btn_addr_set_ok).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    popupView = null;
                    addrSetListenner.setAddr();

                }
            });
        }

    }


    /**
     * 初始化数据
     *
     * @author zhangyakai
     * @time 2014-7-23 上午10:48:05
     */
    private void initWheel() {
        province = (WheelView) popupView.findViewById(R.id.wheel_province);
        city = (WheelView) popupView.findViewById(R.id.wheel_city);
        district = (WheelView) popupView.findViewById(R.id.wheel_district);
        province.setViewAdapter(new ArrayWheelAdapter<>(context, provieceName));
        updateCity(0);
        province.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {

                updateCity(newValue);
            }

        });
        province.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                city.setCurrentItem(0);
            }
        });
        city.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                updateDistrict(province.getCurrentItem(),newValue);
                district.setCurrentItem(0);
            }
        });
        city.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {
                district.setCurrentItem(0);
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                district.setCurrentItem(0);
            }
        });
    }


    /**
     * 更新城市
     * @param proviecePosition
     */
    private void updateCity(int proviecePosition) {
        city.setViewAdapter(new ArrayWheelAdapter<>(context, address.getCityName(proviecePosition)));
        city.setCurrentItem(0);
        updateDistrict(province.getCurrentItem(), city.getCurrentItem());
        district.setCurrentItem(0);
    }

    /**
     * 更新区
     */
    private void updateDistrict(int proviecePosition, int cityPosition) {
        district.setViewAdapter(new ArrayWheelAdapter<>(context, address.getDistrictName(proviecePosition, cityPosition)));
    }

    /**
     * 获取当前选中地址
     * @return
     */
    public String getAddress() {
       return provieceName[province.getCurrentItem()]+" "+address.getCityName(province.getCurrentItem())[city.getCurrentItem()]+" "+
               address.getDistrictName(province.getCurrentItem(),city.getCurrentItem())[district.getCurrentItem()];
    }
}
