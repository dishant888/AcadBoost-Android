package com.example.acadboost;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.util.Patterns;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.CreateUserMutation;
import com.amazonaws.amplify.generated.graphql.ListUsersQuery;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.UUID;

import javax.annotation.Nonnull;

import type.CreateUserInput;
import type.ModelStringInput;
import type.ModelUserFilterInput;


public class LoginActivity extends AppCompatActivity {

    EditText emailEditText,passwordEditText;
    private AWSAppSyncClient awsAppSyncClient;
    SessionManager session;
    ImageView googleLoginImageView,fbLoginImageView,liLoginImageView;
    GoogleSignInClient mGoogleSignInClient;
    int RC_GOOGLE_SIGN_IN = 0;
    CallbackManager callbackManager;
    private static String DEFAULT_PROFILE_IMAGE = "https://acadboost-courses-videos.s3.ap-south-1.amazonaws.com/ProfilePicture/Default/defaultProfilePicture.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Google SignIn Options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //Google SignIn Client
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //FaceBook SignIn
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("FB",loginResult.getAccessToken().getUserId());
                String userId = loginResult.getAccessToken().getUserId();

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        saveFbUser(object);
                    }
                });
                Bundle bundle = new Bundle();
                bundle.putString("fields","first_name,last_name,email,id");
                request.setParameters(bundle);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.i("HJHJ",error.toString());
            }
        });

        emailEditText = findViewById(R.id.emailLoginEditText);
        passwordEditText = findViewById(R.id.passwordLoginEditText);

        googleLoginImageView = findViewById(R.id.googleLoginImageView2);
        fbLoginImageView = findViewById(R.id.facebookLoginImageView2);
        liLoginImageView = findViewById(R.id.linkedinLoginImageView2);

        awsAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();


        //Sign In With Google
        googleLoginImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mGoogleSignInClient != null) {
                    mGoogleSignInClient.signOut();
                }
                Intent googleSigninIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(googleSigninIntent, RC_GOOGLE_SIGN_IN);
            }
        });

        //Sign In With FB
        fbLoginImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean loggedIn = AccessToken.getCurrentAccessToken() != null;
                if(loggedIn) {
                    LoginManager.getInstance().logOut();
                }
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("user_photos","email","public_profile"));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //google SignIn
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        }
        //FaceBook SignIn
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            // Save in DB
            ModelStringInput emailStringInput = ModelStringInput.builder().eq(account.getEmail()).build();
            ModelStringInput signUpTypeInput = ModelStringInput.builder().eq("Google").build();
            ModelUserFilterInput filter = ModelUserFilterInput.builder().email(emailStringInput).sign_up_type(signUpTypeInput).build();
            ListUsersQuery query = ListUsersQuery.builder().filter(filter).build();

            checkGoogleAccountExist(query,account);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.i("GoogleSignInError", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void checkGoogleAccountExist(ListUsersQuery query, GoogleSignInAccount account) {
        awsAppSyncClient.query(query)
                .responseFetcher(AppSyncResponseFetchers.NETWORK_ONLY)
                .enqueue(new GraphQLCall.Callback<ListUsersQuery.Data>() {
                    @Override
                    public void onResponse(@Nonnull Response<ListUsersQuery.Data> response) {

                        if(!response.data().listUsers().items().isEmpty()) {
//                            Log.i("Call",response.data().listUsers().items().toString());
                            //User is already Saved
                            //Start new Session
                            SessionManager session = new SessionManager(getApplicationContext());

                            String imageUrl,name,email,id="",signUpType;

                            for(ListUsersQuery.Item row : response.data().listUsers().items()) {
                                id = row.id();
                            }

                            if(account.getPhotoUrl() == null) {
                                imageUrl = DEFAULT_PROFILE_IMAGE;
                            }else {
                                imageUrl = account.getPhotoUrl().toString();
                            }
                            name = account.getDisplayName();
                            email = account.getEmail();
                            signUpType = "Google";
                            session.startSession(name,email,id,signUpType,imageUrl);
                            goToHome();

                        }else {
                            //Save New User
//                            Log.i("Call","Empty");
                            String imageUrl;

                            if(account.getPhotoUrl() == null) {
                                imageUrl = DEFAULT_PROFILE_IMAGE;
                            }else {
                                imageUrl = account.getPhotoUrl().toString();
                            }

                            String id = UUID.randomUUID().toString();
                            CreateUserInput input = CreateUserInput.builder()
                                    .id(id).email(account.getEmail()).name(account.getDisplayName())
                                    .sign_up_type("Google").profile_picture_url(imageUrl).build();
                            CreateUserMutation query = CreateUserMutation.builder().input(input).build();

                            awsAppSyncClient.mutate(query).enqueue(new GraphQLCall.Callback<CreateUserMutation.Data>() {
                                @Override
                                public void onResponse(@Nonnull Response<CreateUserMutation.Data> response) {

                                    //Start Session
                                    SessionManager session = new SessionManager(getApplicationContext());

                                    String imageUrl,name,email,id,signUpType;

                                    if(account.getPhotoUrl() == null) {
                                        imageUrl = DEFAULT_PROFILE_IMAGE;
                                    }else {
                                        imageUrl = account.getPhotoUrl().toString();
                                    }
                                    name = account.getDisplayName();
                                    email = account.getEmail();
                                    id = response.data().createUser().id();
                                    signUpType = "Google";
                                    session.startSession(name,email,id,signUpType,imageUrl);
                                    goToHome();
                                }

                                @Override
                                public void onFailure(@Nonnull ApolloException e) {

                                }
                            });
                        }

                    }

                    @Override
                    public void onFailure(@Nonnull ApolloException e) {

                    }
                });
    }

    public void saveFbUser(JSONObject object) {
        String email="";
        try {
            email = object.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Check if user already exist
        ModelStringInput emailStringInput = ModelStringInput.builder().eq(email).build();
        ModelStringInput signUpTypeInput = ModelStringInput.builder().eq("Facebook").build();
        ModelUserFilterInput filter = ModelUserFilterInput.builder().email(emailStringInput).sign_up_type(signUpTypeInput).build();
        ListUsersQuery query = ListUsersQuery.builder().filter(filter).build();

        awsAppSyncClient.query(query)
                .responseFetcher(AppSyncResponseFetchers.NETWORK_ONLY)
                .enqueue(new GraphQLCall.Callback<ListUsersQuery.Data>() {
                    @Override
                    public void onResponse(@Nonnull Response<ListUsersQuery.Data> response) {

                        String f_name = "",l_name = "",email = "",id = "",full_name = "",imageUrl = "";
                        try {
                            f_name = object.getString("first_name");
                            l_name = object.getString("last_name");
                            email = object.getString("email");
                            id = object.getString("id");
                            full_name = f_name + " " + l_name;
                            imageUrl = "https://graph.facebook.com/"+id+"/picture?return_ssl_resources=1";
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(!response.data().listUsers().items().isEmpty()) {
//                            Log.i("Call",response.data().listUsers().items().toString());
                            //User is already Saved
                            //Start new Session
                            String sessionId = "";
                            for(ListUsersQuery.Item row: response.data().listUsers().items()) {
                                sessionId = row.id();
                            }

                            SessionManager session = new SessionManager(getApplicationContext());
                            session.startSession(full_name,email,sessionId,"Facebook",imageUrl);
                            goToHome();

                        } else{

                            String uID = UUID.randomUUID().toString();
                            CreateUserInput input = CreateUserInput.builder()
                                    .id(uID).email(email).name(full_name)
                                    .sign_up_type("Facebook").profile_picture_url(imageUrl).build();
                            CreateUserMutation query = CreateUserMutation.builder().input(input).build();

                            awsAppSyncClient.mutate(query).enqueue(new GraphQLCall.Callback<CreateUserMutation.Data>() {
                                @Override
                                public void onResponse(@Nonnull Response<CreateUserMutation.Data> response) {
                                    //Log.i("FB",response.data().createUser().email());

                                    String f_name = "",l_name = "",email = "",id = "",full_name = "",imageUrl = "";
                                    String sessionId = response.data().createUser().id();
                                    try {
                                        f_name = object.getString("first_name");
                                        l_name = object.getString("last_name");
                                        email = object.getString("email");
                                        id = object.getString("id");
                                        full_name = f_name + " " + l_name;
                                        imageUrl = "https://graph.facebook.com/"+id+"/picture?return_ssl_resources=1";
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    SessionManager session = new SessionManager(getApplicationContext());
                                    session.startSession(full_name,email,sessionId,"Facebook",imageUrl);
                                    goToHome();
                                }

                                @Override
                                public void onFailure(@Nonnull ApolloException e) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@Nonnull ApolloException e) {

                    }
                });

    }

    public void loginButtonClick(View view) {

        if(logInValid()) {
            //login
            String emailStr,passwordStr;
            emailStr = emailEditText.getText().toString();
            passwordStr = passwordEditText.getText().toString();

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);


            //Todo:- before user log in check if email is verified

            ModelStringInput emailStringInput = ModelStringInput.builder().eq(emailStr).build();
            ModelStringInput passwordStringInput = ModelStringInput.builder().eq(passwordStr).build();
            ModelUserFilterInput emailUserFilterInput = ModelUserFilterInput.builder().email(emailStringInput).build();
            ModelUserFilterInput passwordUserFilterInput = ModelUserFilterInput.builder().password(passwordStringInput).build();
            ModelStringInput signUpType = ModelStringInput.builder().eq("Email/Password").build();
            ModelUserFilterInput signUpTypeFilter = ModelUserFilterInput.builder().sign_up_type(signUpType).build();

            ListUsersQuery listUsersQuery = ListUsersQuery.builder().filter(emailUserFilterInput).filter(passwordUserFilterInput).filter(signUpTypeFilter).build();
            awsAppSyncClient.query(listUsersQuery)
                    .responseFetcher(AppSyncResponseFetchers.NETWORK_ONLY)
                    .enqueue(new GraphQLCall.Callback<ListUsersQuery.Data>() {
                        @Override
                        public void onResponse(@Nonnull Response<ListUsersQuery.Data> response) {
                            //Log.i("Found",response.data().listUsers().items().toString());
                            if(!response.data().listUsers().items().isEmpty()){
                                Log.i("Valid","User found");

                                //Session Start
                                session = new SessionManager(getApplicationContext());

                                for(ListUsersQuery.Item data : response.data().listUsers().items()) {
                                    String name,email,id,signUpType;
                                    String profilePictureURL;
                                    name = data.name();
                                    email = data.email();
                                    id = data.id();
                                    signUpType = data.sign_up_type();
                                    profilePictureURL = data.profile_picture_url();
                                    session.startSession(name,email,id,signUpType,profilePictureURL);
                                }

                                goToHome();

                            }else{
                                Log.i("Invalid","User not found");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(@Nonnull ApolloException e) {
                            Log.i("Error",e.toString());
                        }
                    });

        }

    }

    public boolean logInValid() {

        boolean valid = true;
        String emailString,passwordString;
        emailString = emailEditText.getText().toString();
        passwordString = passwordEditText.getText().toString();

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if(emailString.matches("")) {
            emailEditText.setError("Enter Email address");
            emailEditText.requestFocus();
            inputMethodManager.showSoftInput(emailEditText,InputMethodManager.SHOW_IMPLICIT);
            valid = false;
        }

        if(passwordString.matches("") && !emailString.matches("")) {
            passwordEditText.setError("Enter password");
            passwordEditText.requestFocus();
            inputMethodManager.showSoftInput(passwordEditText,InputMethodManager.SHOW_IMPLICIT);
            valid = false;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(emailString).matches() && !passwordString.matches("")) {
            emailEditText.setError("Enter Valid Email address");
            emailEditText.requestFocus();
            inputMethodManager.showSoftInput(emailEditText,InputMethodManager.SHOW_IMPLICIT);
            valid = false;
        }

        return valid;
    }

    public void goToHome() {
        Intent login = new Intent(getApplicationContext(),HomeActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(login);
        finish();
    }

}
