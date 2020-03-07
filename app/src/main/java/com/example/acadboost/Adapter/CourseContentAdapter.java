package com.example.acadboost.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.acadboost.Model.SubjectModel;
import com.example.acadboost.Model.VideoModel;
import com.example.acadboost.R;

import java.util.HashMap;
import java.util.List;

public class CourseContentAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<SubjectModel> subjectGroups;
    private HashMap<SubjectModel,List<VideoModel>> videoLists;

    public CourseContentAdapter(Context context, List<SubjectModel> subjectGroups, HashMap<SubjectModel, List<VideoModel>> videoLists) {
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
//        return this.videoLists.get(this.subjectGroups.get(groupPosition)).size();
        return 1;
    }

    @Override
    public SubjectModel getGroup(int groupPosition) {
        return this.subjectGroups.get(groupPosition);
    }

    @Override
    public VideoModel getChild(int groupPosition, int childPosition) {
        return this.videoLists.get(this.subjectGroups.get(groupPosition)).get(childPosition);
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

        TextView name = convertView.findViewById(R.id.videoListRowTitle);
        name.setText(title);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
