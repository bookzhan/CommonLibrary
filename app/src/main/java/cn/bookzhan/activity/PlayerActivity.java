package cn.bookzhan.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.VideoView;

import cn.bookzhan.bases.BaseActivity;
import cn.bookzhan.library.Constants;
import cn.bookzhan.library.R;
import cn.bookzhan.utils.LogUtil;
import cn.bookzhan.utils.StringUtil;


/**
 * Created by zhandalin 2015年10月14日 18:08.
 * 最后修改者: zhandalin  version 1.0
 * 说明:
 */
public class PlayerActivity extends BaseActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener, MediaPlayer.OnCompletionListener {

    private VideoView videoView;
    private String TAG = "PlayerActivity";
    private final int SHOW_FIRST_ANIMATION = 25;
    private final int ANIMATION_TIME = 400;
    private boolean isInited;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_FIRST_ANIMATION:
                    hideController();
                    break;

            }
        }
    };
    private ImageButton ibPlay;
    private SeekBar seekBar;
    private Button btBuy;
    private boolean isPlay = true;
    private AudioManager audioService;
    private ImageView ivExit;
    private View rlController;
    private boolean isHanding;
    private boolean controllerIsShow = true;
    private TranslateAnimation showControlAnimation;
    private TranslateAnimation hideControlAnimation;
    private TranslateAnimation hideExitAnimation;
    private TranslateAnimation showExitAnimation;
    private String sku;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palyer_layout);
        Intent intent = getIntent();
        sku = intent.getStringExtra("sku");
        String url = intent.getStringExtra("url");

        audioService = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        ibPlay = (ImageButton) findViewById(R.id.ib_play);
        rlController = findViewById(R.id.rl_controller);

        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        seekBar.setMax(audioService.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekBar.setProgress(audioService.getStreamVolume(AudioManager.STREAM_MUSIC));
        btBuy = (Button) findViewById(R.id.bt_buy);
        ivExit = (ImageView) findViewById(R.id.iv_exit);

        videoView = (VideoView) findViewById(R.id.videoview);

        if (StringUtil.isEmpty(url)) {
            videoView.setVideoURI(Uri.parse(Constants.Url.VIDEO_COUNTRY_URL));
        } else {
            videoView.setVideoURI(Uri.parse(url));
        }

        videoView.setOnPreparedListener(this);
        videoView.setOnCompletionListener(this);
        videoView.setOnErrorListener(this);
        videoView.requestFocus();

        videoView.setDrawingCacheEnabled(true);
        videoView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        initListener();
    }


    private void initListener() {
        ibPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlay) {
                    videoView.pause();
                    ibPlay.setImageResource(R.mipmap.play_icon);
                } else {
                    restart();
                    ibPlay.setImageResource(R.mipmap.pause_icon);
                }
                isPlay = !isPlay;
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                LogUtil.d(TAG, "progress=" + progress);
                audioService.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        ivExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (StringUtil.isEmpty(sku)) {
//                    showDialogMsg("对不起没有商品信息");
//                } else {
//                    Intent intent = new Intent(context, ProductDetailActivity.class);
//                    intent.putExtra("sku", sku);
//                    startActivity(intent);
//                }

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        videoView.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.resume();
        showLoading("玩命加载中...", true, false);
    }


    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        videoView.stopPlayback();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.destroyDrawingCache();
        videoView = null;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        hiddenLoadingView();
        if (!isInited) {
            handler.sendEmptyMessageDelayed(SHOW_FIRST_ANIMATION, 3000);
            isInited = true;
        }
        LogUtil.d(TAG, "onPrepared---" + "isLooping=" + mp.isLooping() + "isPlaying=" + mp.isPlaying());
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        LogUtil.d(TAG, "onError---" + mp.toString());
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (Build.VERSION.SDK_INT > 16) {
                    showLoading("玩命加载中...", true, false);
                }
                LogUtil.d(TAG, "BUFFERING_START");
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                hiddenLoadingView();
                LogUtil.d(TAG, "BUFFERING_END");
                break;
            case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                LogUtil.d(TAG, "INTERLEAVING");
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                LogUtil.d(TAG, "RENDERING_START");
                break;
            case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                LogUtil.d(TAG, "METADATA_UPDATE");
                break;
            case MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                LogUtil.d(TAG, "SUBTITLE_TIMED_OUT");
                break;
            case MediaPlayer.MEDIA_INFO_UNKNOWN:
                LogUtil.d(TAG, "UNKNOWN");
                break;
            case MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                LogUtil.d(TAG, "UNSUPPORTED_SUBTITLE");
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                LogUtil.d(TAG, "VIDEO_TRACK_LAGGING");
                break;
            case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                LogUtil.d(TAG, "NOT_SEEKABLE");
                break;
        }

        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        LogUtil.d(TAG, "onCompletion");
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            LogUtil.d(TAG, "ACTION_UP");
            handAnimation();
        }
        return super.onTouchEvent(event);
    }

    private void restart() {
        videoView.pause();
        videoView.stopPlayback();
        videoView.start();
        videoView.resume();
    }

    private void handAnimation() {
        if (handler.hasMessages(SHOW_FIRST_ANIMATION)) {
            handler.removeMessages(SHOW_FIRST_ANIMATION);
        }
        if (isHanding) {
            return;
        }
        if (controllerIsShow) {
            hideController();
        } else {
            showController();
        }
    }

    private void showController() {
        isHanding = true;
        if (null == showControlAnimation) {
            showControlAnimation = new TranslateAnimation(0, 0, rlController.getMeasuredHeight(), 0);
            showControlAnimation.setDuration(ANIMATION_TIME);
            showControlAnimation.setFillAfter(true);
            showControlAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    rlController.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    isHanding = false;
                    controllerIsShow = true;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        }
        if (null == showExitAnimation) {
            showExitAnimation = new TranslateAnimation(ivExit.getMeasuredWidth() + ivExit.getPaddingRight(), 0, 0, 0);
            showExitAnimation.setFillAfter(true);
            showExitAnimation.setDuration(ANIMATION_TIME);
        }

        ivExit.startAnimation(showExitAnimation);
        rlController.startAnimation(showControlAnimation);
    }

    private void hideController() {
        isHanding = true;
        if (null == hideControlAnimation) {
            hideControlAnimation = new TranslateAnimation(0, 0, 0, rlController.getMeasuredHeight());
            hideControlAnimation.setDuration(ANIMATION_TIME);
            hideControlAnimation.setFillAfter(true);
            hideControlAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    isHanding = false;
                    controllerIsShow = false;
                    rlController.clearAnimation();
                    rlController.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        if (null == hideExitAnimation) {
            hideExitAnimation = new TranslateAnimation(0, ivExit.getMeasuredWidth() + ivExit.getPaddingRight(), 0, 0);
            hideExitAnimation.setFillAfter(true);
            hideExitAnimation.setDuration(ANIMATION_TIME);
        }

        ivExit.startAnimation(hideExitAnimation);
        rlController.startAnimation(hideControlAnimation);
    }


    @Override
    protected void reTry() {
    }
}
