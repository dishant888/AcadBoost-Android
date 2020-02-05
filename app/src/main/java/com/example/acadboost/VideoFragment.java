package com.example.acadboost;

import android.content.Context;
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
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class VideoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.video_fragment,container,false);
        //Set Action Bar Title
        getActivity().setTitle("Vidoes");

//        ArrayList<String> items = new ArrayList<>();
//        items.add("dishantsukhwal8@gmail.com");

        String mtitle[] = {"Video1","Video2"};
        String mDescription[] = {"This is example Description for video1.","This is example Description for video1."};
        int mImage[] ={R.color.colorTheme,R.color.colorTheme};

        ListView listView = view.findViewById(R.id.videoList);
        //ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,items);
        videoListAdapter adapter = new videoListAdapter(getContext(),mtitle,mDescription,mImage);
        listView.setAdapter(adapter);

        return view;
    }

    class videoListAdapter extends ArrayAdapter<String> {

        Context context;
        String rTitle[];
        String rDescription[];
        int rImage[];

        videoListAdapter(Context c,String title[], String description[], int image[]) {
            super(c,R.layout.video_list_row,R.id.videoListTitle,title);
            this.context = c;
            this.rTitle = title;
            this.rDescription = description;
            this.rImage = image;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.video_list_row,parent,false);

            ImageView imageView = row.findViewById(R.id.videoListImage);
            TextView titleTextView = row.findViewById(R.id.videoListTitle);
            TextView descriptionTextView = row.findViewById(R.id.videoListDescription);

            imageView.setImageResource(rImage[position]);
            titleTextView.setText(rTitle[position]);
            descriptionTextView.setText(rDescription[position]);


            return row;
        }
    }
}
