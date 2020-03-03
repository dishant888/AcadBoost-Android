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
import com.example.acadboost.Model.PostModel;
import com.example.acadboost.R;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    public Context context;
    private ArrayList<PostModel> postModelList;

    public PostAdapter(Context context, ArrayList<PostModel> postModelList) {
        this.context = context;
        this.postModelList = postModelList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_row,parent,false);
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {

        final PostModel postModel = postModelList.get(position);

        holder.feedRowStatus.setText(postModel.getStatus());
        holder.feedRowComments.setText(postModel.getComments().toString() + " comments");
        holder.feedRowLikes.setText(postModel.getLikes().toString());
        holder.feedRowTimeStamp.setText(postModel.getTimeStamp());
        Glide.with(holder.feedRowUserProfilePic.getContext()).load(postModel.getUserProfilePictureUrl()).into(holder.feedRowUserProfilePic);
        holder.feedRowUserName.setText(postModel.getUserName());
    }

    @Override
    public int getItemCount() {
        return postModelList.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        TextView feedRowUserName,feedRowTimeStamp,feedRowLikes,feedRowComments,feedRowStatus;
        ImageView feedRowUserProfilePic;

        public PostViewHolder(@NonNull View itemView) {
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
