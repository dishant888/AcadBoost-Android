package com.example.acadboost;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.amplify.generated.graphql.ListVideoDetailListsQuery;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.util.ArrayList;

import javax.annotation.Nonnull;

public class VideoFragment extends Fragment {

    private AWSAppSyncClient awsAppSyncClient;
    private ArrayList<String> mTitle;
    private ArrayList<String> mDescription;
    private ArrayList<Integer> mImage;
    private ListView listView;
    private videoListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle = new ArrayList<>();
        mDescription = new ArrayList<>();
        mImage = new ArrayList<>();

        awsAppSyncClient = AWSAppSyncClient.builder()
                .context(getContext())
                .awsConfiguration(new AWSConfiguration(getContext()))
                .build();

        ListVideoDetailListsQuery query = ListVideoDetailListsQuery.builder().build();
        awsAppSyncClient.query(query)
                .responseFetcher(AppSyncResponseFetchers.NETWORK_ONLY)
                .enqueue(queryCallback);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.video_fragment,container,false);
        //Set Action Bar Title
        getActivity().setTitle("Vidoes");

        listView = view.findViewById(R.id.videoList);
        adapter = new videoListAdapter(getContext(),mTitle,mDescription,mImage);

        return view;
    }

    private GraphQLCall.Callback<ListVideoDetailListsQuery.Data> queryCallback = new GraphQLCall.Callback<ListVideoDetailListsQuery.Data>() {
        @Override
        public void onResponse(@Nonnull Response<ListVideoDetailListsQuery.Data> response) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!response.data().listVideoDetailLists().items().isEmpty()){
                        for(ListVideoDetailListsQuery.Item row : response.data().listVideoDetailLists().items()) {
                            mTitle.add(row.video_title());
                            mDescription.add(row.video_description());
                            mImage.add(R.color.colorTheme);
                        }
                        listView.setAdapter(adapter);
                    }
                }
            });

        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {

        }
    };

    class videoListAdapter extends ArrayAdapter<String> {

        Context context;
        ArrayList<String> rTitle;
        ArrayList<String> rDescription;
        ArrayList<Integer> rImage;

        videoListAdapter(Context c,ArrayList<String> title, ArrayList<String> description, ArrayList<Integer> image) {
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

            imageView.setImageResource(rImage.get(position));
            titleTextView.setText(rTitle.get(position));
            descriptionTextView.setText(rDescription.get(position));


            return row;
        }
    }
}
