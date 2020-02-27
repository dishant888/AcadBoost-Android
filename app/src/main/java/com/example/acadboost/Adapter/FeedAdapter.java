package com.example.acadboost.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.acadboost.Model.FeedModel;
import com.example.acadboost.R;

import java.util.ArrayList;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    public Context context;
    private ArrayList<FeedModel> feedModelList = new ArrayList<>();
    private RequestManager glide;

    public FeedAdapter(Context context,ArrayList<FeedModel> feedModelList) {
        this.context = context;
        this.feedModelList = feedModelList;
        glide = Glide.with(context);
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_row,parent,false);
        FeedViewHolder holder = new FeedViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {

        final FeedModel feedModel = feedModelList.get(position);

        holder.feedRowStatus.setText(feedModel.getStatus());
        holder.feedRowComments.setText(feedModel.getComments().toString() + " comments");
        holder.feedRowLikes.setText(feedModel.getLikes().toString());
        holder.feedRowTimeStamp.setText(feedModel.getTimeStamp());
        holder.feedRowUserName.setText(feedModel.getUserName());

        glide.load(feedModel.getUserProfilePictureUrl()).into(holder.feedRowUserProfilePic);
    }

    @Override
    public int getItemCount() {
        return feedModelList.size();
    }

    public class FeedViewHolder extends RecyclerView.ViewHolder {

        TextView feedRowUserName,feedRowTimeStamp,feedRowLikes,feedRowComments,feedRowStatus;
        ImageView feedRowUserProfilePic;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);

            feedRowUserProfilePic = itemView.findViewById(R.id.feedRowProfilePic);
            feedRowUserName = itemView.findViewById(R.id.feedRowProfileName);
            feedRowTimeStamp = itemView.findViewById(R.id.feedRowTimeStamp);
            feedRowLikes = itemView.findViewById(R.id.feedRowUpVotes);
            feedRowComments = itemView.findViewById(R.id.feedRowComments);
            feedRowStatus = itemView.findViewById(R.id.feedRowStatus);
        }
    }
}
