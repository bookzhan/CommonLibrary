package cn.bookzhan.library;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.util.EncodingUtils;

import java.io.IOException;
import java.io.InputStream;

import cn.bookzhan.utils.LogUtil;
import cn.bookzhan.view.RefreshLinearLayout;
import cn.bookzhan.wheel.AddrWheelPicker;


public class MainActivity extends ActionBarActivity {
    private Context context;
    private TextView mTextView;
    private ImageView mImageView;
    private String TAG = "MainActivity";
    private RefreshLinearLayout myRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myRelativeLayout = (RefreshLinearLayout) findViewById(R.id.myRelativeLayout);
        context = this;
//        mTextView = (TextView) findViewById(R.id.result);
//        mImageView = (ImageView) findViewById(R.id.qrcode_bitmap);
    }

    public void start(View view) {

       /*---------------------------省县市三级联动使用示例开始----------------------------*/
        try {
            String cityJson = readFile(R.raw.city);
            cityJson = "{\"children\":" + cityJson + "}";
            // cityCodeJson = readFile(R.raw.id_to_name);
            final AddrWheelPicker addrWheelPicker = new AddrWheelPicker(this, cityJson);
            addrWheelPicker.creatPopupWindow();
            addrWheelPicker.setAddrSetListenner(new AddrWheelPicker.AddrSetListenner() {
                @Override
                public void setAddr() {
                    String address = addrWheelPicker.getAddress();
                    LogUtil.d(TAG, address);
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "读取地址文件失败", Toast.LENGTH_SHORT).show();
        }
        /*---------------------------省县市三级联动使用示例结束----------------------------*/



        /*---------------------------二维码扫描使用示例开始----------------------------*/
        //注意不要startActivityForResult,会出问题,处理二维码信息的逻辑在MipcaCaptureActivity处理就好了
//        Intent intent = new Intent();
//        intent.setClass(MainActivity.this, MipcaCaptureActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
        /*---------------------------二维码扫描使用示例结束----------------------------*/


        /*---------------------------CommonDialog使用示例开始----------------------------*/
//        CommonDialog dialog = new CommonDialog(this);
//        dialog.setContentMsg("hahahhahah");
//        dialog.setOnLeftButtonOnClick(new CommonDialog.LeftButtonOnClickListener() {
//            @Override
//            public void onLeftButtonOnClick() {
//                ToastUtils.showToastAtCenter(context, "左边单击");
//            }
//        });
//        dialog.setOnRightButtonOnClick(new CommonDialog.RightButtonOnClickListener() {
//            @Override
//            public void onRightButtonOnClick() {
//                ToastUtils.showToastAtCenter(context, "右边单击");
//            }
//        });
//        dialog.hideRightButton();
//        dialog.show();
        /*---------------------------CommonDialog使用示例结束----------------------------*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String readFile(int fileResource) throws IOException {
        InputStream in = getResources().openRawResource(fileResource);
        int length = in.available();
        byte[] buffer = new byte[length];
        in.read(buffer);
        return EncodingUtils.getString(buffer, "utf-8");
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        myRelativeLayout.myTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }
}
