package com.example.acadboost;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class CourseDetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView titleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        imageView = findViewById(R.id.courseDetailImage);
        titleTextView = findViewById(R.id.courseDetailTitle);

        String URl = getIntent().getStringExtra("URL");
        String title = getIntent().getStringExtra("Title");

        Glide.with(this).load(URl).fitCenter().into(imageView);
        titleTextView.setText(title);
    }
}
