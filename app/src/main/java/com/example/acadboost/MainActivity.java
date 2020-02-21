package com.example.acadboost;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.amplify.generated.graphql.CreateUserMutation;
import com.amazonaws.amplify.generated.graphql.GetUserQuery;
import com.amazonaws.amplify.generated.graphql.ListUsersQuery;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.interceptor.ApolloInterceptor;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

import javax.annotation.Nonnull;

import type.CreateUserInput;
import type.ModelStringInput;
import type.ModelUserFilterInput;


public class MainActivity extends AppCompatActivity {

    ImageView imageView,googleLoginImageView,facebookLoginImageView,linkedinLoginImageView;
    TextView textView,appName,textView3,loginTextView;
    Button getStartedbutton;
    View bottomSheetView;
    private AWSAppSyncClient awsAppSyncClient;
    String uuid;
    EditText nameEditText,passwordEditText,emailEditText;
    SessionManager session;
    GoogleSignInClient mGoogleSignInClient;
    int RC_GOOGLE_SIGN_IN = 0;
    private static String DEFAULT_PROFLE_IMAGE = "https://acadboost-courses-videos.s3.ap-south-1.amazonaws.com/ProfilePicture/Default/defaultProfilePicture.png";

    @Override
    protected void onStart() {
        super.onStart();

        if(session.isUserLoggedIn()) {
            goToHome();
        }

        if(mGoogleSignInClient != null) {
            mGoogleSignInClient.signOut();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(getApplicationContext());

        imageView = findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.pencilimage);

        textView = findViewById(R.id.textView);
        appName = findViewById(R.id.appName);
        textView3 = findViewById(R.id.textView3);
        loginTextView = findViewById(R.id.loginTextView);
        getStartedbutton = findViewById(R.id.getStartedButton);

        //Initialize AWSAppSync Client
        awsAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

        //Animations

        imageView.animate().alpha(1).setDuration(800).setStartDelay(200);
        appName.animate().translationY(0).setDuration(800).setStartDelay(400);
        textView.animate().translationY(0).setDuration(800).setStartDelay(400);
        getStartedbutton.animate().translationY(0).setDuration(800).setStartDelay(400);
        textView3.animate().translationY(0).setDuration(800).setStartDelay(700);
        loginTextView.animate().translationY(0).setDuration(800).setStartDelay(700);

        //Google SignIn Options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //Google SignIn Client
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Bottom Sheet Show

        getStartedbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog getStartedBottomSheetDialog = new BottomSheetDialog(
                  MainActivity.this,R.style.getStartedModal
                );

                bottomSheetView = LayoutInflater.from(getApplication()).inflate(
                        R.layout.get_started_bottom_sheet,
                        (LinearLayout)findViewById(R.id.getStartedBottomSheet)
                );

                googleLoginImageView = bottomSheetView.findViewById(R.id.googleLoginImageView);
                facebookLoginImageView = bottomSheetView.findViewById(R.id.facebookLoginImageView);
                linkedinLoginImageView = bottomSheetView.findViewById(R.id.linkedinLoginImageView);
                googleLoginImageView.setImageResource(R.drawable.google);
                facebookLoginImageView.setImageResource(R.drawable.facebook);
                linkedinLoginImageView.setImageResource(R.drawable.linkedin);

                //SignUp with email and password
                bottomSheetView.findViewById(R.id.signUpButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(emailSignUpValid()) {
                            userNotExists();
                        }
                    }
                });

                //SignIn with Google
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

                getStartedBottomSheetDialog.setContentView(bottomSheetView);
                getStartedBottomSheetDialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        }
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

    public void checkGoogleAccountExist(ListUsersQuery query,GoogleSignInAccount account) {

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

                            String imageUrl,name,email,id,signUpType;

                            if(account.getPhotoUrl() == null) {
                                imageUrl = DEFAULT_PROFLE_IMAGE;
                            }else {
                                imageUrl = account.getPhotoUrl().toString();
                            }
                            name = account.getDisplayName();
                            email = account.getEmail();
                            id = account.getId();
                            signUpType = "Google";
                            session.startSession(name,email,id,signUpType,imageUrl);
                            goToHome();

                        }else {
                            //Save New User
//                            Log.i("Call","Empty");
                            String imageUrl;

                            if(account.getPhotoUrl() == null) {
                                imageUrl = DEFAULT_PROFLE_IMAGE;
                            }else {
                                imageUrl = account.getPhotoUrl().toString();
                            }

                            CreateUserInput input = CreateUserInput.builder()
                                    .id(account.getId()).email(account.getEmail()).name(account.getDisplayName())
                                    .sign_up_type("Google").profile_picture_url(imageUrl).build();
                            CreateUserMutation query = CreateUserMutation.builder().input(input).build();

                            awsAppSyncClient.mutate(query).enqueue(new GraphQLCall.Callback<CreateUserMutation.Data>() {
                                @Override
                                public void onResponse(@Nonnull Response<CreateUserMutation.Data> response) {

                                    //Start Session
                                    SessionManager session = new SessionManager(getApplicationContext());

                                    String imageUrl,name,email,id,signUpType;

                                    if(account.getPhotoUrl() == null) {
                                        imageUrl = DEFAULT_PROFLE_IMAGE;
                                    }else {
                                        imageUrl = account.getPhotoUrl().toString();
                                    }
                                    name = account.getDisplayName();
                                    email = account.getEmail();
                                    id = account.getId();
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


    public boolean emailSignUpValid() {
        boolean valid = true;

        nameEditText = bottomSheetView.findViewById(R.id.nameEditText);
        passwordEditText = bottomSheetView.findViewById(R.id.passwordEditText);
        emailEditText = bottomSheetView.findViewById(R.id.emailEditText);

        String name,password,email;
        name = nameEditText.getText().toString();
        password = passwordEditText.getText().toString();
        email = emailEditText.getText().toString();

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if(name.matches("")) {
            nameEditText.setError("Name is required");
            nameEditText.requestFocus();
            inputMethodManager.showSoftInput(nameEditText,InputMethodManager.SHOW_IMPLICIT);
            valid = false;
        }

        if(password.matches("") && !name.matches("")) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            inputMethodManager.showSoftInput(passwordEditText,InputMethodManager.SHOW_IMPLICIT);
            valid = false;
        }

        if(email.matches("") && !password.matches("")) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            inputMethodManager.showSoftInput(emailEditText,InputMethodManager.SHOW_IMPLICIT);
            valid = false;
        }

        if(password.length() < 8 && password.length() > 0 && !email.matches("")) {
            passwordEditText.setError("Password must be atleast 8 character long");
            passwordEditText.requestFocus();
            inputMethodManager.showSoftInput(passwordEditText,InputMethodManager.SHOW_IMPLICIT);
            valid = false;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.length() > 0 && !password.matches("") && password.length() >= 8) {
            emailEditText.setError("Enter valid Email address");
            emailEditText.requestFocus();
            inputMethodManager.showSoftInput(emailEditText,InputMethodManager.SHOW_IMPLICIT);
            valid = false;
        }

        return valid;
    }

    public void userNotExists() {
        String emailStr;
        emailStr = emailEditText.getText().toString();

        ModelStringInput emailModelInput = ModelStringInput.builder().eq(emailStr).build();
        ModelStringInput signUpInput = ModelStringInput.builder().eq("Email/Password").build();
        ModelUserFilterInput modelUserFilterInput = ModelUserFilterInput.builder().email(emailModelInput).sign_up_type(signUpInput).build();

        awsAppSyncClient.query(ListUsersQuery.builder().filter(modelUserFilterInput).build())
                .responseFetcher(AppSyncResponseFetchers.NETWORK_ONLY)
                .enqueue(new GraphQLCall.Callback<ListUsersQuery.Data>() {
                    @Override
                    public void onResponse(@Nonnull Response<ListUsersQuery.Data> response) {
                        Log.i("Response",response.data().listUsers().items().toString());
                        if(response.data().listUsers().items().isEmpty()) {
                            Log.i("UserExist","This email is not registered");
                            saveUser();
                        }else {
                            Log.i("UserExist","This email is already registered");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "This email is already registered", Toast.LENGTH_SHORT).show();
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

    public void saveUser() {

        nameEditText = bottomSheetView.findViewById(R.id.nameEditText);
        passwordEditText = bottomSheetView.findViewById(R.id.passwordEditText);
        emailEditText = bottomSheetView.findViewById(R.id.emailEditText);

        String nameStr,passwordStr,emailStr;
        nameStr = nameEditText.getText().toString();
        passwordStr = passwordEditText.getText().toString();
        emailStr = emailEditText.getText().toString();


        //Todo:- save user in dynamoDB table and send confirmation email (user can login only after email verification) after sending email redirect to login

        //Save User
        String uuid = UUID.randomUUID().toString();
        CreateUserInput user = CreateUserInput.builder()
                .id(uuid).name(nameStr).email(emailStr).password(passwordStr)
                .profile_picture_url(DEFAULT_PROFLE_IMAGE)
                .sign_up_type("Email/Password")
                .build();

        awsAppSyncClient.mutate(CreateUserMutation.builder().input(user).build()).enqueue(createUserCallback);
    }


    //Callback for saving user
    private GraphQLCall.Callback<CreateUserMutation.Data> createUserCallback = new GraphQLCall.Callback<CreateUserMutation.Data>() {
        @Override
        public void onResponse(@Nonnull Response<CreateUserMutation.Data> response) {
            //Log.i("Success",response.toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                }
            });
            goToLogin();
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.i("Error",e.toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Please try after some time", Toast.LENGTH_SHORT).show();
                }
            });

        }
    };

    public void goToLogin() {
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void goToHome() {
        Intent login = new Intent(getApplicationContext(),HomeActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(login);
        finish();
    }

    public void loginClick(View view) {
        goToLogin();
    }

}
