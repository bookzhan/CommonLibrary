package cn.bookzhan.emoji;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import cn.bookzhan.library.R;
import cn.bookzhan.utils.DisplayUtil;
import cn.bookzhan.utils.LogUtil;
import cn.bookzhan.utils.StringUtil;
import cn.bookzhan.utils.ToastUtils;

/**
 *
 * 带表情的自定义输入框,使用方法直接把<include layout="@layout/custom_facerelativelayout" />放在布局文件里面就好了
 * 显示表情的时候使用 {@link MyTextView)
 * 注意{@link FaceConversionUtil#ParseData(List, Context)} 里面是用的mipmap,如果是eclipse的话就改成drawable
 *
 */
public class FaceRelativeLayout extends RelativeLayout implements
        OnItemClickListener, OnClickListener {

    private Context context;

    /**
     * 表情页的监听事件
     */
    private OnCorpusSelectedListener mListener;

    /**
     * 显示表情页的viewpager
     */
    private ViewPager vp_face;

    /**
     * 表情页界面集合
     */
    private ArrayList<View> pageViews;

    /**
     * 游标显示布局
     */
    private LinearLayout layout_point;

    /**
     * 游标点集合
     */
    private ArrayList<ImageView> pointViews;

    /**
     * 表情集合
     */
    private List<List<ChatEmoji>> emojis;

    /**
     * 表情区域
     */
    private View view;

    /**
     * 输入框
     */
    private EditText et_sendmessage;

    /**
     * 表情数据填充器
     */
    private List<FaceAdapter> faceAdapters;

    /**
     * 当前表情页
     */
    private int current = 0;

    /**
     * 发送按钮
     */
    private TextView tv_send;
    private String TAG = "FaceRelativeLayout";

    private boolean isLongPress = false;

    private RadioButton rb_face;
    /**
     * 用handler实现删除多个
     */
    private Handler hander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    deleteText(msg.arg1);
                    Message message = new Message();
                    message.what = 1;
                    message.arg1 = msg.arg1;
                    hander.sendMessageDelayed(message, 50);
                    break;
                case 2:
                    break;
            }

        }
    };


    public FaceRelativeLayout(Context context) {
        super(context);
        this.context = context;
        FaceConversionUtil.getInstace().getFileText(context);
    }

    public FaceRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        FaceConversionUtil.getInstace().getFileText(context);
    }

    public FaceRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        FaceConversionUtil.getInstace().getFileText(context);
    }

    public void setOnCorpusSelectedListener(OnCorpusSelectedListener listener) {
        mListener = listener;
    }

    /**
     * 表情选择监听
     *
     * @author naibo-liao
     * @时间： 2013-1-15下午04:32:54
     */
    public interface OnCorpusSelectedListener {
        void onCorpusSelected(ChatEmoji emoji);

        void onCorpusDeleted();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        emojis = FaceConversionUtil.getInstace().emojiLists;
        onCreate();
    }

    private void onCreate() {
        Init_View();
        Init_viewPager();
        Init_Point();
        Init_Data();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb_face:
                // 隐藏表情选择框
                if (view.getVisibility() == View.VISIBLE) {
                    rb_face.setChecked(false);
                    view.setVisibility(View.GONE);
                    if (StringUtil.isEmpty(et_sendmessage.getText().toString().trim())) {
                        et_sendmessage.requestFocus();
                        tv_send.setEnabled(false);
                    }
                } else {
                    rb_face.setChecked(true);
                    hideKeyboard();
                    et_sendmessage.requestFocus();
                }
                break;
            case R.id.et_sendmessage:
                showKeyboard(et_sendmessage);
                rb_face.setChecked(false);
                // 隐藏表情选择框
                if (view.getVisibility() == View.VISIBLE) {
                    view.setVisibility(View.GONE);
                }
                break;
            case R.id.tv_send:
                // 隐藏表情选择框
                if (view.getVisibility() == View.VISIBLE) {
                    view.setVisibility(View.GONE);
                }
                break;

        }
    }

    /**
     * 隐藏表情选择框
     */
    public boolean hideFaceView() {
        // 隐藏表情选择框
        if (view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    /**
     * 初始化控件
     */
    private void Init_View() {
        vp_face = (ViewPager) findViewById(R.id.vp_contains);
        et_sendmessage = (EditText) findViewById(R.id.et_sendmessage);
        layout_point = (LinearLayout) findViewById(R.id.iv_image);
        et_sendmessage.setOnClickListener(this);
        rb_face = (RadioButton) findViewById(R.id.rb_face);
        rb_face.setOnClickListener(this);
        view = findViewById(R.id.ll_facechoose);
        tv_send = (TextView) findViewById(R.id.tv_send);

        et_sendmessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>198){
                    ToastUtils.showToastAtCenter(context, "最大字数为200哦");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    tv_send.setEnabled(true);
                } else {
                    tv_send.setEnabled(false);
                }
            }
        });
        tv_send.setOnClickListener(this);
    }

    /**
     * 初始化显示表情的viewpager
     */
    private void Init_viewPager() {
        pageViews = new ArrayList<View>();
        // 左侧添加空页
        View nullView1 = new View(context);
        // 设置透明背景
        nullView1.setBackgroundColor(Color.TRANSPARENT);
        pageViews.add(nullView1);

        // 中间添加表情页
        faceAdapters = new ArrayList<FaceAdapter>();
        for (int i = 0; i < emojis.size(); i++) {
            GridView view = new GridView(context);
            FaceAdapter adapter = new FaceAdapter(context, emojis.get(i));
            view.setAdapter(adapter);
            faceAdapters.add(adapter);
            view.setOnItemClickListener(this);
            view.setNumColumns(7);
            view.setBackgroundColor(Color.TRANSPARENT);
            view.setCacheColorHint(0);
            view.setPadding(DisplayUtil.dip2px(context, 10), DisplayUtil.dip2px(context, 10), DisplayUtil.dip2px(context, 10), 0);
            view.setSelector(new ColorDrawable(Color.TRANSPARENT));
            view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            view.setGravity(Gravity.CENTER);

            view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    isLongPress = true;
                    LogUtil.d(TAG, "onItemLongClick");
                    Message msg = new Message();
                    msg.what = 1;
                    msg.arg1 = position;
                    hander.sendMessageDelayed(msg, 50);

                    return false;
                }
            });
            pageViews.add(view);
        }

        // 右侧添加空页面
        View nullView2 = new View(context);
        // 设置透明背景
        nullView2.setBackgroundColor(Color.TRANSPARENT);
        pageViews.add(nullView2);
    }


    /**
     * 初始化游标
     */
    private void Init_Point() {

        pointViews = new ArrayList<ImageView>();
        ImageView imageView;
        for (int i = 0; i < pageViews.size(); i++) {
            imageView = new ImageView(context);
            imageView.setBackgroundResource(R.drawable.shape_loading2);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = DisplayUtil.dip2px(context, 10);
            layoutParams.rightMargin = DisplayUtil.dip2px(context, 10);
            layoutParams.width = DisplayUtil.dip2px(context, 4);
            layoutParams.height = DisplayUtil.dip2px(context, 4);
            layout_point.addView(imageView, layoutParams);
            if (i == 0 || i == pageViews.size() - 1) {
                imageView.setVisibility(View.GONE);
            }
            if (i == 1) {
                imageView.setBackgroundResource(R.drawable.shape_loading1);
            }
            pointViews.add(imageView);

        }
    }

    /**
     * 填充数据
     */
    private void Init_Data() {
        vp_face.setAdapter(new ViewPagerAdapter(pageViews));

        vp_face.setCurrentItem(1);
        current = 0;
        vp_face.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                current = arg0 - 1;
                // 描绘分页点
                draw_Point(arg0);
                // 如果是第一屏或者是最后一屏禁止滑动，其实这里实现的是如果滑动的是第一屏则跳转至第二屏，如果是最后一屏则跳转到倒数第二屏.
                if (arg0 == pointViews.size() - 1 || arg0 == 0) {
                    if (arg0 == 0) {
                        vp_face.setCurrentItem(arg0 + 1);// 第二屏 会再次实现该回调方法实现跳转.
                        pointViews.get(1).setBackgroundResource(R.drawable.shape_loading1);
                    } else {
                        vp_face.setCurrentItem(arg0 - 1);// 倒数第二屏
                        pointViews.get(arg0 - 1).setBackgroundResource(
                                R.drawable.shape_loading1);
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

    }

    /**
     * 绘制游标背景
     */
    public void draw_Point(int index) {
        for (int i = 1; i < pointViews.size(); i++) {
            if (index == i) {
                pointViews.get(i).setBackgroundResource(R.drawable.shape_loading1);
            } else {
                pointViews.get(i).setBackgroundResource(R.drawable.shape_loading2);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        LogUtil.d(TAG, "onItemClick");
        if (isLongPress) {
            hander.removeMessages(1);
            isLongPress = false;
            return;
        }
        ChatEmoji emoji = (ChatEmoji) faceAdapters.get(current).getItem(arg2);
        deleteText(arg2);
        if (!TextUtils.isEmpty(emoji.getCharacter())) {
            if (mListener != null)
                mListener.onCorpusSelected(emoji);
            SpannableString spannableString = FaceConversionUtil.getInstace()
                    .addFace(getContext(), emoji.getId(), emoji.getCharacter());
            Editable editable = et_sendmessage.getText();
//            if (!editable.toString().endsWith(" ")) {
//                editable.insert(et_sendmessage.getSelectionStart(), " ");
//            }
            editable.insert(et_sendmessage.getSelectionStart(), spannableString);
//            editable.insert(et_sendmessage.getSelectionStart(), " ");
        }
    }

    /**
     * 删除输入框的东西
     *
     * @param position
     */
    private void deleteText(int position) {
        ChatEmoji emoji = (ChatEmoji) faceAdapters.get(current).getItem(position);
        if (emoji.getId() == R.drawable.selector_face_del_icon) {
            int selection = et_sendmessage.getSelectionStart();
            String text = et_sendmessage.getText().toString();
            if (selection > 0) {
                String text2 = text.substring(selection - 1);
                if ("]".equals(text2)) {
                    int start = text.lastIndexOf("[");
                    int end = selection;
                    et_sendmessage.getText().delete(start, end);
                    return;
                }
                et_sendmessage.getText().delete(selection - 1, selection);
            }
        }
    }


    /**
     * 隐藏键盘
     *
     * @author Medivh
     * @date 2014-7-9 下午4:11:40
     */
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(((Activity) (context)).getWindow().getDecorView().getApplicationWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        hander.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.VISIBLE);
            }
        }, 50);
    }

    /**
     * 显示键盘
     *
     * @author Medivh
     * @date 2014-7-9 下午4:11:40
     */
    public void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }
}
