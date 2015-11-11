package cn.bookzhan.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.lang.ref.SoftReference;

import cn.bookzhan.library.R;

/**
 * Created by guoyi on 2015/9/20.
 * <p/>
 * 要在使用时，标明 scaleType bgColor stroke_color radius
 */
public class StrokeColorImageView extends ImageView {
    private Paint paint;
    private int radius = 5;
    private float lineWidth = 1f;//就用1px吧多了会导致圆角变粗,原因待查
    private int color = Color.WHITE;
    private int bgColor = Color.WHITE;
    private Paint paint2;
    private Paint paint3;
    private SoftReference<Bitmap> sr;

    public StrokeColorImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    public StrokeColorImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public StrokeColorImageView(Context context) {
        super(context);
        init(context, null);
    }


    private void init(Context context, AttributeSet attrs) {
//        lineWidth = DensityUtil.dip2px(context, lineWidth);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StrokeColorImageView);
            radius = a.getDimensionPixelSize(R.styleable.StrokeColorImageView_radius, radius);
            color = a.getColor(R.styleable.StrokeColorImageView_stroke_color, color);
            bgColor = a.getColor(R.styleable.StrokeColorImageView_bg_color, bgColor);

        } else {
            float density = context.getResources().getDisplayMetrics().density;
            radius = (int) (radius * density);
        }
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStrokeWidth(lineWidth);              //线宽
        paint.setStyle(Paint.Style.STROKE);                   //空心效果

        paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2.setAntiAlias(true);
        paint2.setXfermode(null);
        paint2.setColor(bgColor);

        paint3 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint3.setAntiAlias(true);
        paint3.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        paint3.setColor(Color.WHITE);
       setScaleType(getScaleType());
    }


    @Override
    public void draw(Canvas canvas) {

        super.draw(canvas);

        Bitmap bmpCache = null;
        if (sr != null) {
            bmpCache = sr.get();
        }

        if (bmpCache == null || bmpCache.isRecycled()
                || bmpCache.getWidth() != getWidth()
                || bmpCache.getHeight() != getHeight()) {
            if (bmpCache != null) {
                bmpCache.recycle();
            }
            bmpCache = Bitmap.createBitmap(getWidth(), getHeight(),
                    Bitmap.Config.ARGB_8888);
            sr = new SoftReference<Bitmap>(bmpCache);
        }

        Canvas canvas2 = new Canvas(bmpCache);
        canvas.drawBitmap(bmpCache, 0, 0, paint2);//将上面的，画到view 上
        canvas2.drawRoundRect(new RectF(0, 0, getWidth(), getHeight()), 0, 0, paint2);//画矩形
        canvas2.drawRoundRect(new RectF(0, 0, getWidth(), getHeight()), radius, radius, paint3);//画圆角矩形并与上面的矩形取 dst_out
        canvas2.drawRoundRect(new RectF(0, 0, getWidth(), getHeight()), radius, radius, paint); //画外圈
    }

}

