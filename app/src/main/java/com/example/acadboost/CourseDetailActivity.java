package com.example.acadboost;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.acadboost.Adapter.CourseViewPagerAdapter;
import com.example.acadboost.Fragment.CourseContentFragment;
import com.example.acadboost.Fragment.CourseDescriptionFragment;
import com.example.acadboost.Model.CourseListModel;
import com.google.android.material.tabs.TabLayout;

public class CourseDetailActivity extends AppCompatActivity {

     ImageView imageView;
     TextView titleTextView,ratingsTextView,courseByTextView,validityTextView,langTextView;
     RatingBar ratingBar;
     TabLayout tabLayout;
     ViewPager viewPager;
     Fragment descriptionFragment;
     Fragment contentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        getSupportActionBar().setTitle("Course");

        imageView = findViewById(R.id.courseDetailImage);
        titleTextView = findViewById(R.id.courseDetailTitle);
        ratingsTextView = findViewById(R.id.courseDetailRatings);
        courseByTextView = findViewById(R.id.courseDetailCourseBy);
        validityTextView = findViewById(R.id.courseDetailValidity);
        langTextView = findViewById(R.id.courseDetailLanguage);
        ratingBar = findViewById(R.id.courseDetailRatingBar);
        tabLayout = findViewById(R.id.courseDetailTabLayout);
        viewPager = findViewById(R.id.courseDetailViewPager);
        contentFragment = new CourseContentFragment();
        descriptionFragment = new CourseDescriptionFragment();

        Intent intent = getIntent();
        CourseListModel course = intent.getParcelableExtra("COURSE");
        Bundle descriptionFragmentArgs = new Bundle();
        descriptionFragmentArgs.putString("DESCRIPTION",course.getDescription());
        descriptionFragment.setArguments(descriptionFragmentArgs);
        tabLayout.setupWithViewPager(viewPager);
        CourseViewPagerAdapter adapter = new CourseViewPagerAdapter(getSupportFragmentManager(),0);
        adapter.addFragment(descriptionFragment,"Description");
        adapter.addFragment(new CourseContentFragment(),"Content");
        viewPager.setAdapter(adapter);

        setData(course);
    }

    private void setData(CourseListModel course) {
        Float rating = Float.valueOf(String.valueOf(course.getRatings()));
        Log.i("COURSE",rating.toString());
        Glide.with(this).load(course.getImageURL()).centerCrop().into(imageView);
        titleTextView.setText(course.getTitle());
        ratingBar.setRating(rating);
        ratingsTextView.setText(String.valueOf(rating));
        courseByTextView.setText(course.getCourseBy());
        validityTextView.setText(course.getValidity());
        langTextView.setText(course.getLang());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
