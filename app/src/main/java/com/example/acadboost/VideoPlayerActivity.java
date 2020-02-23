package com.example.acadboost;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView videoView;
    private Intent intent;
    private View layer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        intent = getIntent();
        getSupportActionBar().hide();

        videoView = findViewById(R.id.videoView);
        layer = findViewById(R.id.videoViewLayer);
        layer.setForegroundGravity(100);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        playVideo();

        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Clicked","Clicked");
                layer.setVisibility(View.VISIBLE);
            }
        });

        layer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layer.setVisibility(View.GONE);
            }
        });
    }

    public void playVideo(){
        MediaController mediaController = new MediaController(this);
        Uri uri = Uri.parse(intent.getStringExtra("objectUrl"));
        //mediaController.setAnchorView(videoView);
        //videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
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
}
