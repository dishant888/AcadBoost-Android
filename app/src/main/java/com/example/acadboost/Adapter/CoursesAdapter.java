package com.example.acadboost.Adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.acadboost.CourseDetailActivity;
import com.example.acadboost.Model.CourseListModel;
import com.example.acadboost.R;

import java.util.ArrayList;

public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.CoursesViewHolder> {

    public Context context;
    private ArrayList<CourseListModel> courseModelList;

    public CoursesAdapter(Context context, ArrayList<CourseListModel> courseModelList) {
        this.context = context;
        this.courseModelList = courseModelList;
    }

    @NonNull
    @Override
    public CoursesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_list_row,parent,false);

        return new CoursesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CoursesViewHolder holder, int position) {

        final CourseListModel course = courseModelList.get(position);

        holder.title.setText(course.getTitle());
        holder.courseBy.setText(course.getCourseBy());
        holder.ratingBar.setRating(Float.valueOf(String.valueOf(course.getRatings())));
        holder.ratings.setText(String.valueOf(course.getRatings()));

        Glide.with(holder.image.getContext()).load(course.getImageURL()).centerCrop().into(holder.image);

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Pair[] pairs = new Pair[5];
                pairs[0] = new Pair<View,String>(holder.image,holder.image.getTransitionName());
                pairs[1] = new Pair<View,String>(holder.title,holder.title.getTransitionName());
                pairs[2] = new Pair<View,String>(holder.ratingBar,holder.ratingBar.getTransitionName());
                pairs[3] = new Pair<View,String>(holder.ratings,holder.ratings.getTransitionName());
                pairs[4] = new Pair<View,String>(holder.courseBy,holder.courseBy.getTransitionName());

                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation((Activity) v.getContext(),pairs).toBundle();
                Intent i = new Intent(v.getContext(), CourseDetailActivity.class);

                i.putExtra("COURSE",course);
                v.getContext().startActivity(i,bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseModelList.size();
    }

    public class CoursesViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        CardView card;
        TextView title,courseBy,ratings;
        RatingBar ratingBar;
        public CoursesViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.courseListRowImage);
            title = itemView.findViewById(R.id.courseListRowTitle);
            courseBy = itemView.findViewById(R.id.courseListRowCourseBy);
            ratingBar = itemView.findViewById(R.id.courseListRowRatingBar);
            card = itemView.findViewById(R.id.courseRowCard);
            ratings = itemView.findViewById(R.id.courseListRowRatings);
        }
    }
}
