package com.example.acadboost.Model;

import com.amazonaws.amplify.generated.graphql.GetUserQuery;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import javax.annotation.Nonnull;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FeedModel {

    private String id,userID,timeStamp,userName,userProfilePictureUrl,status;
    private Integer likes,comments;
    private AWSAppSyncClient awsAppSyncClient;

    public FeedModel(String id, String userID, String timeStamp, String status, Integer likes, Integer comments) {
        this.id = id;
        this.userID = userID;
        this.timeStamp = timeStamp;
        this.status = status;
        this.likes = likes;
        this.comments = comments;

        fetchUser(userID);
    }

    private void fetchUser(String userID) {
        awsAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

        GetUserQuery query = GetUserQuery.builder().id(userID).build();
        awsAppSyncClient.query(query)
                .responseFetcher(AppSyncResponseFetchers.NETWORK_ONLY)
                .enqueue(new GraphQLCall.Callback<GetUserQuery.Data>() {
                    @Override
                    public void onResponse(@Nonnull Response<GetUserQuery.Data> response) {
                        setUserName(response.data().getUser().name());
                        setUserProfilePictureUrl(response.data().getUser().profile_picture_url());
                    }

                    @Override
                    public void onFailure(@Nonnull ApolloException e) {

                    }
                });
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserProfilePictureUrl() {
        return userProfilePictureUrl;
    }

    public void setUserProfilePictureUrl(String userProfilePictureUrl) {
        this.userProfilePictureUrl = userProfilePictureUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }
}
