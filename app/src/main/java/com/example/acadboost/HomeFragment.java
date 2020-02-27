package com.example.acadboost;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.amplify.generated.graphql.ListPostsQuery;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.acadboost.Adapter.FeedAdapter;
import com.example.acadboost.Model.FeedModel;

import java.util.ArrayList;

import javax.annotation.Nonnull;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<FeedModel> feedModelArrayList = new ArrayList<>();
    private FeedAdapter adapter;
    private AWSAppSyncClient awsAppSyncClient;
    private ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //AWS App Sync Cilent
        awsAppSyncClient = AWSAppSyncClient.builder()
                .context(getActivity().getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getActivity().getApplicationContext()))
                .build();
        fetchFeed();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.home_fragment,container,false);
        //Set Action Bar Title
        getActivity().setTitle("Home");

        recyclerView = view.findViewById(R.id.feedRecyclerView);
        progressBar = view.findViewById(R.id.feedLoadProgressbar);
        adapter = new FeedAdapter(getContext(),feedModelArrayList);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void fetchFeed() {
        Log.i("Fetch","asca");
        ListPostsQuery query = ListPostsQuery.builder().limit(10).build();
        awsAppSyncClient.query(query)
                .responseFetcher(AppSyncResponseFetchers.NETWORK_ONLY)
                .enqueue(new GraphQLCall.Callback<ListPostsQuery.Data>() {
                    @Override
                    public void onResponse(@Nonnull Response<ListPostsQuery.Data> response) {
                        for(ListPostsQuery.Item row : response.data().listPosts().items()) {
                            FeedModel feedModel = new FeedModel(row.id(),row.user_id().toString(),row.timestamp(),row.status(),row.likes(),row.comments());
                            feedModelArrayList.add(feedModel);
                        }
                    }

                    @Override
                    public void onFailure(@Nonnull ApolloException e) {

                    }
                });
    }

}
