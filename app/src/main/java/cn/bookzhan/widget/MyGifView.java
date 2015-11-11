package cn.bookzhan.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by bookzhan on 2015/7/24.
 * 最后修改者: bookzhan  @version 1.0
 * 说明: 加载gif动图 注意所在Activity必须加上  android:hardwareAccelerated="false" 即取消硬件加速
 */
public class MyGifView extends View {
    private long movieStart;
    private Movie movie;

    public MyGifView(Context context,int resource) {
        super(context);
        getRawResource(resource);//以文件流（InputStream）读取进gif图片资源
    }

    private void getRawResource(int resource) {
        movie = Movie.decodeStream(getResources().openRawResource(resource));
    }

    public MyGifView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        long curTime = android.os.SystemClock.uptimeMillis();
        //第一次播放
        if (movieStart == 0) {
            movieStart = curTime;
        }
        if (movie != null) {
            int duration = movie.duration();
            int relTime = (int) ((curTime - movieStart) % duration);
            movie.setTime(relTime);
            movie.draw(canvas, 0, 0);
            //强制重绘
            invalidate();
        }
        super.onDraw(canvas);
    }
}