package com.example.acadboost;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;


public class VideoFragment extends Fragment {

    private ListView listView;
    private VideoFragment.videoListAdapter adapter;
    private ArrayList<String> mTitleList;
    private ArrayList<String> mDescriptionList;
    private ArrayList<Integer> mImageList;
    private ArrayList<String> mObjectUrl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitleList = new ArrayList<>();
        mDescriptionList = new ArrayList<>();
        mImageList = new ArrayList<>();
        mObjectUrl = new ArrayList<>();

        savedInstanceState = this.getArguments();
        mTitleList = savedInstanceState.getStringArrayList("titleArrayList");
        mDescriptionList = savedInstanceState.getStringArrayList("descriptionArrayList");
        mImageList = savedInstanceState.getIntegerArrayList("imageArrayList");
        mObjectUrl = savedInstanceState.getStringArrayList("objectUrlArrayList");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.video_fragment,container,false);
        //Set Action Bar Title
        getActivity().setTitle("Videos");

        listView = view.findViewById(R.id.videoList);
        adapter = new videoListAdapter(getContext(),mTitleList,mDescriptionList,mImageList,mObjectUrl);
        listView.setAdapter(adapter);

        return view;
    }


    class videoListAdapter extends ArrayAdapter<String> {

        Context context;
        ArrayList<String> rTitle;
        ArrayList<String> rDescription;
        ArrayList<Integer> rImage;
        ArrayList<String> rObjectUrl;

        videoListAdapter(Context c,ArrayList<String> title, ArrayList<String> description, ArrayList<Integer> image,ArrayList<String> objectUrl) {
            super(c,R.layout.video_list_row,R.id.videoListTitle,title);
            this.context = c;
            this.rTitle = title;
            this.rDescription = description;
            this.rImage = image;
            this.rObjectUrl = objectUrl;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.video_list_row,parent,false);

            ImageView imageView = row.findViewById(R.id.videoListImage);
            TextView titleTextView = row.findViewById(R.id.videoListTitle);
            TextView descriptionTextView = row.findViewById(R.id.videoListDescription);

            imageView.setImageResource(rImage.get(position));
            titleTextView.setText(rTitle.get(position));
            descriptionTextView.setText(rDescription.get(position));

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent videoPlayer = new Intent(getContext(),VideoPlayerActivity.class);
                    videoPlayer.putExtra("objectUrl",rObjectUrl.get(position));
                    startActivity(videoPlayer);
                }
            });

            return row;
        }
    }
}
