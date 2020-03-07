package com.example.acadboost.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.amplify.generated.graphql.ListCourseCategorysQuery;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.acadboost.Adapter.CoursesAdapter;
import com.example.acadboost.Model.CourseListModel;
import com.example.acadboost.R;

import java.util.ArrayList;

import javax.annotation.Nonnull;



public class CoursesFragment extends Fragment {

//    private ListView listView;
//    private CoursesFragment.videoListAdapter adapter;
//    private ArrayList<String> mTitleList;
//    private ArrayList<String> mDescriptionList;
//    private ArrayList<Integer> mImageList;
//    private ArrayList<String> mObjectUrl;

    private AWSAppSyncClient awsAppSyncClient;
    private ArrayList<CourseListModel> courseList;
    private CoursesAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mTitleList = new ArrayList<>();
//        mDescriptionList = new ArrayList<>();
//        mImageList = new ArrayList<>();
//        mObjectUrl = new ArrayList<>();

        //AWS App Sync Cilent
        awsAppSyncClient = AWSAppSyncClient.builder()
                .context(getContext())
                .awsConfiguration(new AWSConfiguration(getContext()))
                .build();

        courseList = new ArrayList<>();

        savedInstanceState = this.getArguments();
//        mTitleList = savedInstanceState.getStringArrayList("titleArrayList");
//        mDescriptionList = savedInstanceState.getStringArrayList("descriptionArrayList");
//        mImageList = savedInstanceState.getIntegerArrayList("imageArrayList");
//        mObjectUrl = savedInstanceState.getStringArrayList("objectUrlArrayList");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.courses_fragment,container,false);
        //Set Action Bar Title
        getActivity().setTitle("Categories");

//        listView = view.findViewById(R.id.videoList);
//        adapter = new videoListAdapter(getContext(),mTitleList,mDescriptionList,mImageList,mObjectUrl);
//        listView.setAdapter(adapter);

        recyclerView = view.findViewById(R.id.courseListRecyclerView);

        adapter = new CoursesAdapter(getContext(),courseList);
        recyclerView.setAdapter(adapter);

        fetchCourse();
        return view;
    }

    private void fetchCourse() {

        ListCourseCategorysQuery query = ListCourseCategorysQuery.builder().build();

        awsAppSyncClient.query(query)
                .responseFetcher(AppSyncResponseFetchers.NETWORK_ONLY)
                .enqueue(new GraphQLCall.Callback<ListCourseCategorysQuery.Data>() {
                    @Override
                    public void onResponse(@Nonnull Response<ListCourseCategorysQuery.Data> response) {
                        if(!response.data().listCourseCategorys().items().isEmpty()) {

                            for(ListCourseCategorysQuery.Item row : response.data().listCourseCategorys().items()) {

                                CourseListModel course = new CourseListModel(row.id(),row.title(),row.course_by(),row.image_url(),row.description(),row.language(),row.validity(),row.ratings());
                                courseList.add(course);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onFailure(@Nonnull ApolloException e) {

                    }
                });
    }

//    class videoListAdapter extends ArrayAdapter<String> {
//
//        Context context;
//        ArrayList<String> rTitle;
//        ArrayList<String> rDescription;
//        ArrayList<Integer> rImage;
//        ArrayList<String> rObjectUrl;
//
//        videoListAdapter(Context c,ArrayList<String> title, ArrayList<String> description, ArrayList<Integer> image,ArrayList<String> objectUrl) {
//            super(c,R.layout.video_list_row,R.id.videoListTitle,title);
//            this.context = c;
//            this.rTitle = title;
//            this.rDescription = description;
//            this.rImage = image;
//            this.rObjectUrl = objectUrl;
//        }
//
//        @NonNull
//        @Override
//        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            View row = layoutInflater.inflate(R.layout.video_list_row,parent,false);
//
//            ImageView imageView = row.findViewById(R.id.videoListImage);
//            TextView titleTextView = row.findViewById(R.id.videoListTitle);
//            TextView descriptionTextView = row.findViewById(R.id.videoListDescription);
//
//            imageView.setImageResource(rImage.get(position));
//            titleTextView.setText(rTitle.get(position));
//            descriptionTextView.setText(rDescription.get(position));
//
//            row.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent videoPlayer = new Intent(getContext(),VideoPlayerActivity.class);
//                    videoPlayer.putExtra("objectUrl",rObjectUrl.get(position));
//                    videoPlayer.putExtra("actionBarTitle",rTitle.get(position));
//                    startActivity(videoPlayer);
//                }
//            });
//
//            return row;
//        }
//    }
}
