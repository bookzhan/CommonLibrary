package cn.bookzhan.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2015/4/18.
 */
public class DashedLineView extends View {
    public DashedLineView(Context context) {
        super(context);
    }

    public DashedLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        int color = Color.argb(255,239,223,240);
        paint.setColor(color);
        PathEffect effects = new DashPathEffect(new float[]{15, 3, 15, 3}, 1);
        paint.setPathEffect(effects);
        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(1500, 0);
        canvas.drawPath(path, paint);
    }
}
