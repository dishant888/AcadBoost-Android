package com.example.acadboost;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView videoView;
    private Intent intent;
    private View layer;
    private ImageButton playPauseToggle;
    boolean pauseIconShowing = true;
    private SeekBar seekBar;
    Runnable runnable;
    Handler handler;
    TextView startTime,endtime,videoViewtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        intent = getIntent();
        getSupportActionBar().hide();

        handler = new Handler();
        videoView = findViewById(R.id.videoView);
        playPauseToggle = findViewById(R.id.videoViewPlayPauseToggle);
        startTime = findViewById(R.id.videoStartTime);
        endtime = findViewById(R.id.videoEndTime);
        videoViewtitle = findViewById(R.id.videoViewTitle);
        seekBar = findViewById(R.id.seekBar);
        layer = findViewById(R.id.videoViewLayer);
        layer.setForegroundGravity(100);
        videoViewtitle.setText(intent.getStringExtra("actionBarTitle"));

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        playVideo();

        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLayer();
            }
        });

        layer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideLayer();
            }
        });


        seekBar.setOnSeekBarChangeListener(seekBarChangeListner);
    }

    private void playCycle() {

        runnable = new Runnable() {
            @Override
            public void run() {

                if(videoView.getDuration() > 0) {
                    int currentVideoDuration = videoView.getCurrentPosition();
                    seekBar.setProgress(currentVideoDuration);
                    startTime.setText("" + convertToTime(currentVideoDuration));
                    endtime.setText("- " + convertToTime(videoView.getDuration() - currentVideoDuration));
                }
                handler.postDelayed(this,0);
            }
        };
        handler.postDelayed(runnable,1000);
    }

    public void forwardClick(View v) {
        int increment;
        increment = videoView.getCurrentPosition() + 10000;
        videoView.seekTo(increment);
    }

    public void replayClick(View v) {
        int decrement;
        decrement = videoView.getCurrentPosition() - 10000;
        videoView.seekTo(decrement);
    }

    private String convertToTime(int ms) {
        String time;
        int x,seconds,min,hr;
        x = (int) (ms /1000);

        seconds = x % 60;
        x /= 60;

        min = x % 60;
        x /= 60;

        hr = x % 24;

        if(hr != 0){
            time = String.format("%02d",hr) + ":" + String.format("%02d",min) + ":" + String.format("%02d",seconds);
        } else {
            time = String.format("%02d",min) + ":" + String.format("%02d",seconds);
        }

        return time;
    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListner = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) {
                videoView.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    public void playVideo(){
        Uri uri = Uri.parse(intent.getStringExtra("objectUrl"));
        videoView.setVideoURI(uri);
        videoView.requestFocus();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekBar.setMax(videoView.getDuration());
                playCycle();
                videoView.start();
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
               onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        videoView.stopPlayback();
    }

    public void backClick(View v) {
        onBackPressed();
    }

    public void showLayer() {
        layer.setVisibility(View.VISIBLE);
        layer.setAlpha(0);
        layer.animate().setDuration(400).alpha(1).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                layer.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }

    public void hideLayer() {
        layer.animate().setDuration(400).alpha(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                layer.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }

    public void playPauseClick(View v) {
        if(pauseIconShowing) {
            videoView.pause();
            v.setBackground(getDrawable(R.drawable.ic_play));
            pauseIconShowing = false;
        } else {
            videoView.start();
            v.setBackground(getDrawable(R.drawable.ic_pause));
            pauseIconShowing = true;
            hideLayer();
        }
    }
}
