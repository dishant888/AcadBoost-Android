package com.example.acadboost.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.acadboost.Model.CourseListModel;
import com.example.acadboost.R;

import java.util.ArrayList;
import java.util.List;

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

        Glide.with(holder.image.getContext()).load(course.getImageURL()).centerCrop().into(holder.image);
    }

    @Override
    public int getItemCount() {
        return courseModelList.size();
    }

    public class CoursesViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title,courseBy;
        RatingBar ratingBar;
        public CoursesViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.courseListRowImage);
            title = itemView.findViewById(R.id.courseListRowTitle);
            courseBy = itemView.findViewById(R.id.courseListRowCourseBy);
            ratingBar = itemView.findViewById(R.id.courseListRowRatings);
        }
    }
}
