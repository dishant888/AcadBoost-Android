package com.example.acadboost.Fragment;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.acadboost.R;

public class CourseDescriptionFragment extends Fragment {

    TextView desc;
    String descriptionString;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedInstanceState = this.getArguments();
        descriptionString = savedInstanceState.getString("DESCRIPTION");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.course_description_fragment,container,false);

        desc = view.findViewById(R.id.courseDetailDescription);
        desc.setText(Html.fromHtml(descriptionString));

        return view;
    }
}
