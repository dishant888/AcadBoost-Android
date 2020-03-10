package com.example.acadboost.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.acadboost.Model.SubjectModel;
import com.example.acadboost.Model.VideoModel;
import com.example.acadboost.R;
import com.example.acadboost.VideoPlayerActivity;

import java.util.HashMap;
import java.util.List;

public class CourseContentAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<SubjectModel> subjectGroups;
    private HashMap<String,List<VideoModel>> videoLists;

    public CourseContentAdapter(Context context, List<SubjectModel> subjectGroups, HashMap<String, List<VideoModel>> videoLists) {
        this.context = context;
        this.subjectGroups = subjectGroups;
        this.videoLists = videoLists;
    }

    @Override
    public int getGroupCount() {
        return subjectGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        SubjectModel subject = getGroup(groupPosition);
        List<VideoModel> videos = videoLists.get(subject.getID());
        int count = videos.size();

        return count;
    }

    @Override
    public SubjectModel getGroup(int groupPosition) {
        return this.subjectGroups.get(groupPosition);
    }

    @Override
    public VideoModel getChild(int groupPosition, int childPosition) {
        SubjectModel subject = subjectGroups.get(groupPosition);
        return this.videoLists.get(subject.getID()).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        SubjectModel subject = getGroup(groupPosition);
        String name = subject.getName();

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.subject_group_row,null);
        }

        TextView nameTextView = convertView.findViewById(R.id.subjectGroupRowName);
        nameTextView.setText(name);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        VideoModel video = getChild(groupPosition,childPosition);
        String title = video.getTitle();

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.subject_group_child_row,null);
        }

        LinearLayout row = convertView.findViewById(R.id.subjectGroupChildRow);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra("objectUrl",video.getObjectURL());
                intent.putExtra("actionBarTitle",video.getTitle());
                context.startActivity(intent);
            }
        });

        TextView name = convertView.findViewById(R.id.videoListRowTitle);
        name.setText(title);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
