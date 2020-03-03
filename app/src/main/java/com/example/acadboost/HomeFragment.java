package com.example.acadboost;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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
import com.example.acadboost.Adapter.PostAdapter;
import com.example.acadboost.Model.PostModel;

import java.util.ArrayList;

import javax.annotation.Nonnull;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<PostModel> postModelArrayList = new ArrayList<>();
    private PostAdapter adapter;
    private AWSAppSyncClient awsAppSyncClient;
    private ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //AWS App Sync Client
        awsAppSyncClient = AWSAppSyncClient.builder()
                .context(getActivity().getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getActivity().getApplicationContext()))
                .build();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.home_fragment,container,false);
        //Set Action Bar Title
        getActivity().setTitle("Home");

        recyclerView = view.findViewById(R.id.feedRecyclerView);
        progressBar = view.findViewById(R.id.feedLoadProgressbar);
        adapter = new PostAdapter(getContext(), postModelArrayList);
        recyclerView.setAdapter(adapter);
        fetchFeed();

        return view;
    }

    private void fetchFeed() {

        ListPostsQuery query = ListPostsQuery.builder().build();
        awsAppSyncClient.query(query)
                .responseFetcher(AppSyncResponseFetchers.NETWORK_ONLY)
                .enqueue(new GraphQLCall.Callback<ListPostsQuery.Data>() {
                    @Override
                    public void onResponse(@Nonnull Response<ListPostsQuery.Data> response) {
                        for(ListPostsQuery.Item row : response.data().listPosts().items()) {
                            Log.i("RESPONSE",row.user_id());
                            PostModel postModel = new PostModel(row.id(),row.user_id(),row.timestamp(),row.status(),row.likes(),row.comments());
                            postModelArrayList.add(postModel);
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void onFailure(@Nonnull ApolloException e) {

                    }
                });
    }

}
