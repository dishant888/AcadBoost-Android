package com.example.acadboost.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amazonaws.amplify.generated.graphql.ListSubjectsQuery;
import com.amazonaws.amplify.generated.graphql.ListVideosQuery;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.acadboost.Adapter.CourseContentAdapter;
import com.example.acadboost.Model.SubjectModel;
import com.example.acadboost.Model.VideoModel;
import com.example.acadboost.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nonnull;

import type.ModelStringInput;
import type.ModelsubjectFilterInput;
import type.ModelvideoFilterInput;

public class CourseContentFragment extends Fragment {

    private ExpandableListView expandableListView;
    private List<SubjectModel> subjectGroup;
    private List<VideoModel> videoList;
    private HashMap<SubjectModel,List<VideoModel>> hashMap;
    private CourseContentAdapter adapter;
    private String course_id;
    private AWSAppSyncClient awsAppSyncClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        savedInstanceState = this.getArguments();
        course_id = savedInstanceState.getString("course_id");

        awsAppSyncClient = AWSAppSyncClient.builder()
                .context(getContext())
                .awsConfiguration(new AWSConfiguration(getContext()))
                .build();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.course_content_fragment,container,false);

        expandableListView = view.findViewById(R.id.courseExpandableListView);
        subjectGroup = new ArrayList<>();
        videoList = new ArrayList<>();
        hashMap = new HashMap<>();
        adapter = new CourseContentAdapter(getContext(),subjectGroup,hashMap);
        expandableListView.setAdapter(adapter);

        fetchSubjects(course_id);
        Log.i("CONTENT COURSE_ID",course_id);
        return  view;
    }

    private void fetchSubjects(String course_id) {

        ModelStringInput StringInput = ModelStringInput.builder().eq(course_id).build();
        ModelsubjectFilterInput filter = ModelsubjectFilterInput.builder().course_id(StringInput).build();
        ListSubjectsQuery query = ListSubjectsQuery.builder().filter(filter).build();

        awsAppSyncClient.query(query)
                .responseFetcher(AppSyncResponseFetchers.NETWORK_ONLY)
                .enqueue(new GraphQLCall.Callback<ListSubjectsQuery.Data>() {
                    @Override
                    public void onResponse(@Nonnull Response<ListSubjectsQuery.Data> response) {
                        if(!response.data().listSubjects().items().isEmpty()) {

                            for(ListSubjectsQuery.Item row : response.data().listSubjects().items()) {

                                final SubjectModel subject = new SubjectModel(row.id(),row.name(),row.description());
                                subjectGroup.add(subject);
                                fetchVideos(subject);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@Nonnull ApolloException e) {

                    }
                });
    }

    private void fetchVideos(SubjectModel subject) {

        ModelStringInput StringInput = ModelStringInput.builder().eq(subject.getID()).build();
        ModelvideoFilterInput filter = ModelvideoFilterInput.builder().subject_id(StringInput).build();
        ListVideosQuery query = ListVideosQuery.builder().filter(filter).build();

        awsAppSyncClient.query(query)
                .responseFetcher(AppSyncResponseFetchers.NETWORK_ONLY)
                .enqueue(new GraphQLCall.Callback<ListVideosQuery.Data>() {
                    @Override
                    public void onResponse(@Nonnull Response<ListVideosQuery.Data> response) {
                        if(!response.data().listVideos().items().isEmpty()) {

                            for(ListVideosQuery.Item row : response.data().listVideos().items()) {

                                final VideoModel video = new VideoModel(row.id(),row.title(),row.object_url());
                                videoList.add(video);
                                Log.i("CONTENT SUBJECT_NAME",subject.getName());
                                Log.i("CONTENT VIDEO_TITLE",video.getTitle());
                            }

                            hashMap.put(subject,videoList);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@Nonnull ApolloException e) {

                    }
                });
    }

}
