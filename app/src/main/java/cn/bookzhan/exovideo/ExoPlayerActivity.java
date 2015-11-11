/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.bookzhan.exovideo;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.accessibility.CaptioningManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.drm.UnsupportedDrmException;
import com.google.android.exoplayer.metadata.GeobMetadata;
import com.google.android.exoplayer.metadata.PrivMetadata;
import com.google.android.exoplayer.metadata.TxxxMetadata;
import com.google.android.exoplayer.text.CaptionStyleCompat;
import com.google.android.exoplayer.text.Cue;
import com.google.android.exoplayer.text.SubtitleLayout;
import com.google.android.exoplayer.util.Util;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.List;
import java.util.Map;

import cn.bookzhan.bases.BaseActivity;
import cn.bookzhan.library.R;
import cn.bookzhan.utils.LogUtil;

/**
 * An activity that plays media using {@link PlayerManager}.
 */
public class ExoPlayerActivity extends BaseActivity implements SurfaceHolder.Callback,
        PlayerManager.Listener, PlayerManager.CaptionListener, PlayerManager.Id3MetadataListener,
        AudioCapabilitiesReceiver.Listener {

    // For use within demo app code.
    public static final int TYPE_DASH = 0;
    public static final int TYPE_SS = 1;
    public static final int TYPE_HLS = 2;
    public static final int TYPE_OTHER = 3;

    private final int SHOW_FIRST_ANIMATION = 25;
    private final int ANIMATION_TIME = 400;

    private static final String TAG = "ExoPlayerActivity";

    private static final CookieManager defaultCookieManager;

    static {
        defaultCookieManager = new CookieManager();
        defaultCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    private EventLogger eventLogger;
    private View shutterView;
    private AspectRatioFrameLayout videoFrame;
    private SurfaceView surfaceView;
    private SubtitleLayout subtitleLayout;

    private PlayerManager player;
    private boolean playerNeedsPrepare;

    private long playerPosition;

    private Uri contentUri;
    private int contentType;
    private TranslateAnimation showControlAnimation;
    private TranslateAnimation hideControlAnimation;
    private TranslateAnimation hideExitAnimation;
    private TranslateAnimation showExitAnimation;

    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;
    private ImageButton ibPlay;
    private View rlController;
    private SeekBar seekBar;
    private Button btBuy;
    private ImageView ivExit;
    private AudioManager audioService;
    private boolean isPlaying;
    private String sku;
    private boolean isHanding;
    private boolean isInited;
    private boolean controllerIsShow = true;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_FIRST_ANIMATION:
                    handler.removeMessages(SHOW_FIRST_ANIMATION);
                    hideController();
                    break;
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exoplayer);
        Intent intent = getIntent();
        contentUri = Uri.parse(intent.getStringExtra("url"));
        sku = intent.getStringExtra("sku");
        contentType = TYPE_HLS;//写死
        shutterView = findViewById(R.id.shutter);

        videoFrame = (AspectRatioFrameLayout) findViewById(R.id.video_frame);
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        surfaceView.getHolder().addCallback(this);
        surfaceView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    LogUtil.d(TAG, "ACTION_UP");
                    handAnimation();
                }
                return false;
            }
        });

        subtitleLayout = (SubtitleLayout) findViewById(R.id.subtitles);

        CookieHandler currentHandler = CookieHandler.getDefault();
        if (currentHandler != defaultCookieManager) {
            CookieHandler.setDefault(defaultCookieManager);
        }

        audioCapabilitiesReceiver = new AudioCapabilitiesReceiver(this, this);
        audioCapabilitiesReceiver.register();
        ibPlay = (ImageButton) findViewById(R.id.ib_play);
        rlController = findViewById(R.id.rl_controller);

        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        audioService = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        seekBar.setMax(audioService.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekBar.setProgress(audioService.getStreamVolume(AudioManager.STREAM_MUSIC));
        btBuy = (Button) findViewById(R.id.bt_buy);
        ivExit = (ImageView) findViewById(R.id.iv_exit);
    }

    @Override
    protected void reTry() {

    }


    private void initListener() {
        ibPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    preparePlayer(true);
                } else {
                    releasePlayer();
                }
                isPlaying = !isPlaying;
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
    public void onNewIntent(Intent intent) {
        releasePlayer();
        playerPosition = 0;
        setIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        configureSubtitleView();
        if (player == null) {
            preparePlayer(true);
        } else {
            player.setBackgrounded(false);
        }
        if (isInited) {
            preparePlayer(true);
        }
        showLoading("玩命加载中...", true, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        shutterView.setVisibility(View.VISIBLE);
        releasePlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        audioCapabilitiesReceiver.unregister();
    }

    @Override
    public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
        if (player == null) {
            return;
        }
        boolean backgrounded = player.getBackgrounded();
        boolean playWhenReady = player.getPlayWhenReady();
        releasePlayer();
        preparePlayer(playWhenReady);
        player.setBackgrounded(backgrounded);
    }

    // Internal methods

    private PlayerManager.RendererBuilder getRendererBuilder() {
        String userAgent = Util.getUserAgent(this, "ExoPlayerDemo");
        switch (contentType) {
            case TYPE_SS:
                return new SmoothStreamingRendererBuilder(this, userAgent, contentUri.toString(),
                        new SmoothStreamingTestMediaDrmCallback());
            case TYPE_DASH:
                return new DashRendererBuilder(this, userAgent, contentUri.toString(),
                        new WidevineTestMediaDrmCallback("contentId"));
            case TYPE_HLS:
                return new HlsRendererBuilder(this, userAgent, contentUri.toString());
            case TYPE_OTHER:
                return new ExtractorRendererBuilder(this, userAgent, contentUri);
            default:
                throw new IllegalStateException("Unsupported type: " + contentType);
        }
    }

    private void preparePlayer(boolean playWhenReady) {
        if (player == null) {
            player = new PlayerManager(getRendererBuilder());
            player.addListener(this);
            player.setCaptionListener(this);
            player.setMetadataListener(this);
            player.seekTo(playerPosition);
            playerNeedsPrepare = true;
            eventLogger = new EventLogger();
            eventLogger.startSession();
            player.addListener(eventLogger);
            player.setInfoListener(eventLogger);
            player.setInternalErrorListener(eventLogger);
        }
        if (playerNeedsPrepare) {
            player.prepare();
            playerNeedsPrepare = false;
//            updateButtonVisibilities(); TODO
        }
        player.setSurface(surfaceView.getHolder().getSurface());
        player.setPlayWhenReady(playWhenReady);
        ibPlay.setImageResource(R.mipmap.pause_icon);
    }

    private void releasePlayer() {
        if (player != null) {
            playerPosition = player.getCurrentPosition();
            player.release();
            player = null;
            eventLogger.endSession();
            eventLogger = null;
            ibPlay.setImageResource(R.mipmap.play_icon);
        }
    }

    // PlayerManager.Listener implementation

    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {

        String text = "playWhenReady=" + playWhenReady + ", playbackState=";
        switch (playbackState) {
            case ExoPlayer.STATE_BUFFERING:
                showLoading("玩命加载中...", true, false);
                text += "buffering";
                break;
            case ExoPlayer.STATE_ENDED:
                text += "ended";
                break;
            case ExoPlayer.STATE_IDLE:
                preparePlayer(true);
                text += "idle";
                break;
            case ExoPlayer.STATE_PREPARING:
                text += "preparing";
                break;
            case ExoPlayer.STATE_READY:
                hiddenLoadingView();
                if (!isInited) {
                    handler.sendEmptyMessageDelayed(SHOW_FIRST_ANIMATION, 5000);
                }
                isInited = true;
                text += "ready";
                break;
            default:
                text += "unknown";
                break;
        }
        Log.d(TAG, "text=" + text);

    }

    @Override
    public void onError(Exception e) {
        if (e instanceof UnsupportedDrmException) {
            // Special case DRM failures.
            UnsupportedDrmException unsupportedDrmException = (UnsupportedDrmException) e;
            int stringId = Util.SDK_INT < 18 ? R.string.drm_error_not_supported
                    : unsupportedDrmException.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME
                    ? R.string.drm_error_unsupported_scheme : R.string.drm_error_unknown;
            Toast.makeText(getApplicationContext(), stringId, Toast.LENGTH_LONG).show();
        }
        playerNeedsPrepare = true;
        LogUtil.d(TAG, "onError" + e.toString());
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees,
                                   float pixelWidthAspectRatio) {
        shutterView.setVisibility(View.GONE);
        videoFrame.setAspectRatio(
                height == 0 ? 1 : (width * pixelWidthAspectRatio) / height);
    }


    @Override
    public void onCues(List<Cue> cues) {
        subtitleLayout.setCues(cues);
    }

    // PlayerManager.MetadataListener implementation

    @Override
    public void onId3Metadata(Map<String, Object> metadata) {
        for (Map.Entry<String, Object> entry : metadata.entrySet()) {
            if (TxxxMetadata.TYPE.equals(entry.getKey())) {
                TxxxMetadata txxxMetadata = (TxxxMetadata) entry.getValue();
                Log.i(TAG, String.format("ID3 TimedMetadata %s: description=%s, value=%s",
                        TxxxMetadata.TYPE, txxxMetadata.description, txxxMetadata.value));
            } else if (PrivMetadata.TYPE.equals(entry.getKey())) {
                PrivMetadata privMetadata = (PrivMetadata) entry.getValue();
                Log.i(TAG, String.format("ID3 TimedMetadata %s: owner=%s",
                        PrivMetadata.TYPE, privMetadata.owner));
            } else if (GeobMetadata.TYPE.equals(entry.getKey())) {
                GeobMetadata geobMetadata = (GeobMetadata) entry.getValue();
                Log.i(TAG, String.format("ID3 TimedMetadata %s: mimeType=%s, filename=%s, description=%s",
                        GeobMetadata.TYPE, geobMetadata.mimeType, geobMetadata.filename,
                        geobMetadata.description));
            } else {
                Log.i(TAG, String.format("ID3 TimedMetadata %s", entry.getKey()));
            }
        }
    }

    // SurfaceHolder.Callback implementation

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (player != null) {
            player.setSurface(holder.getSurface());
            initListener();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Do nothing.
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (player != null) {
            player.blockingClearSurface();
        }
    }

    private void configureSubtitleView() {
        CaptionStyleCompat style;
        float fontScale;
        if (Util.SDK_INT >= 19) {
            style = getUserCaptionStyleV19();
            fontScale = getUserCaptionFontScaleV19();
        } else {
            style = CaptionStyleCompat.DEFAULT;
            fontScale = 1.0f;
        }
        subtitleLayout.setStyle(style);
        subtitleLayout.setFractionalTextSize(SubtitleLayout.DEFAULT_TEXT_SIZE_FRACTION * fontScale);
    }

    @TargetApi(19)
    private float getUserCaptionFontScaleV19() {
        CaptioningManager captioningManager =
                (CaptioningManager) getSystemService(Context.CAPTIONING_SERVICE);
        return captioningManager.getFontScale();
    }

    @TargetApi(19)
    private CaptionStyleCompat getUserCaptionStyleV19() {
        CaptioningManager captioningManager =
                (CaptioningManager) getSystemService(Context.CAPTIONING_SERVICE);
        return CaptionStyleCompat.createFromCaptionStyle(captioningManager.getUserStyle());
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


}
