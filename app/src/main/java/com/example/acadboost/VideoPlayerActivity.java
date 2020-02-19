package com.example.acadboost;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        Intent intent = getIntent();
        getSupportActionBar().setTitle(intent.getStringExtra("actionBarTitle"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        videoView = findViewById(R.id.videoView);
        MediaController mediaController = new MediaController(this);
        Uri uri = Uri.parse(intent.getStringExtra("objectUrl").toString());
        videoView.setVideoURI(uri);
        videoView.setMediaController(mediaController);
        videoView.start();
    }
}
