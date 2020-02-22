package com.example.acadboost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.ListVideoDetailListsQuery;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nonnull;



public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    SessionManager session;
    Fragment homeFragment = new HomeFragment();
    Fragment videoFragment = new VideoFragment();
    Fragment userFragment = new UserFragment();

    private AWSAppSyncClient awsAppSyncClient;
    private ArrayList<String> mTitle;
    private ArrayList<String> mDescription;
    private ArrayList<Integer> mImage;
    private ArrayList<String> mObjectUrl;
    Bundle videoBundle;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mTitle = new ArrayList<>();
        mDescription = new ArrayList<>();
        mImage = new ArrayList<>();
        mObjectUrl = new ArrayList<>();

        //AWS App Sync Cilent
        awsAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();
        //Google SignIn Options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //Google SignIn Client
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //NavBarDrawer Toggler
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Bottom Menu and Fragments
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListner);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new HomeFragment()).commit();

        //Navigation Drawer
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(drawerListner);

        //Session
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> sessionData = session.getSessionData();

        //Set session data to nav header
        View navigationHeaderView = navigationView.getHeaderView(0);
        TextView navBarHeaderUserName = navigationHeaderView.findViewById(R.id.navBarHeaderUserName);
        TextView navBarHeaderUserEmail = navigationHeaderView.findViewById(R.id.navBarHeaderUserEmail);
        ImageView navBarHeaderImage = navigationHeaderView.findViewById(R.id.navBarHeaderImage);
        navBarHeaderUserName.setText(sessionData.get(SessionManager.NAME));
        navBarHeaderUserEmail.setText(sessionData.get(SessionManager.EMAIL));

        Glide.with(getApplicationContext()).load(Uri.parse(sessionData.get(SessionManager.PROFILE_PICTURE_URL))).apply(RequestOptions.circleCropTransform()).into(navBarHeaderImage);

        //VideoList
        fetchVideoList();

    }


    private NavigationView.OnNavigationItemSelectedListener drawerListner = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.myCourses:
                    Toast.makeText(HomeActivity.this, "My Progress", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.progress:
                    Toast.makeText(HomeActivity.this,"Progress",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.helpAndSupport:
                    Toast.makeText(HomeActivity.this, "Help and Support", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.logout:
                    logout();
                    break;
            }
            return true;
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener navListner = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.homeMenu :
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,homeFragment).commit();
                    break;

                case R.id.videoMenu :
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,videoFragment).commit();
                    break;

                case R.id.userMenu :
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,userFragment).commit();
                    break;
            }
            return true;
        }
    };

    public void logout() {

        session.endSession();

        if(mGoogleSignInClient != null) {
            mGoogleSignInClient.signOut();
        }

        boolean loggedIn = AccessToken.getCurrentAccessToken() != null;
        if(loggedIn) {
            LoginManager.getInstance().logOut();
        }

        Intent login = new Intent(getApplicationContext(),LoginActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(login);
        finish();
    }

    //Drawer toggler
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void fetchVideoList() {

        ListVideoDetailListsQuery query = ListVideoDetailListsQuery.builder().build();
        awsAppSyncClient.query(query)
                .responseFetcher(AppSyncResponseFetchers.NETWORK_ONLY)
                .enqueue(queryCallback);
    }

    private GraphQLCall.Callback<ListVideoDetailListsQuery.Data> queryCallback = new GraphQLCall.Callback<ListVideoDetailListsQuery.Data>() {
        @Override
        public void onResponse(@Nonnull Response<ListVideoDetailListsQuery.Data> response) {
                runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!response.data().listVideoDetailLists().items().isEmpty()){
                        for(ListVideoDetailListsQuery.Item row : response.data().listVideoDetailLists().items()) {
                            mTitle.add(row.video_title());
                            mDescription.add(row.video_description());
                            mObjectUrl.add(row.object_url());
                            mImage.add(R.color.colorPrimaryDark);
                        }
                        videoBundle = new Bundle();
                        videoBundle.putStringArrayList("titleArrayList",mTitle);
                        videoBundle.putStringArrayList("descriptionArrayList",mDescription);
                        videoBundle.putIntegerArrayList("imageArrayList",mImage);
                        videoBundle.putStringArrayList("objectUrlArrayList",mObjectUrl);
                        videoFragment.setArguments(videoBundle);
                    }
                }
            });

        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {

        }
    };
}
