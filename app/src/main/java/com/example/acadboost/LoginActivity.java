package com.example.acadboost;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.util.Patterns;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.ListUsersQuery;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import javax.annotation.Nonnull;

import type.ModelStringInput;
import type.ModelUserFilterInput;


public class LoginActivity extends AppCompatActivity {

    EditText emailEditText,passwordEditText;
    private AWSAppSyncClient awsAppSyncClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailLoginEditText);
        passwordEditText = findViewById(R.id.passwordLoginEditText);

        awsAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

    }

    public void loginButtonClick(View view) {

        if(logInValid()) {
            //login
            String emailStr,passwordStr;
            emailStr = emailEditText.getText().toString();
            passwordStr = passwordEditText.getText().toString();


            //Todo:- before user log in check if email is verified

            ModelStringInput emailStringInput = ModelStringInput.builder().eq(emailStr).build();
            ModelStringInput passwordStringInput = ModelStringInput.builder().eq(passwordStr).build();
            ModelUserFilterInput emailUserFilterInput = ModelUserFilterInput.builder().email(emailStringInput).build();
            ModelUserFilterInput passwordUserFilterInput = ModelUserFilterInput.builder().password(passwordStringInput).build();

            ListUsersQuery listUsersQuery = ListUsersQuery.builder().filter(emailUserFilterInput).filter(passwordUserFilterInput).build();
            awsAppSyncClient.query(listUsersQuery)
                    .responseFetcher(AppSyncResponseFetchers.NETWORK_ONLY)
                    .enqueue(new GraphQLCall.Callback<ListUsersQuery.Data>() {
                        @Override
                        public void onResponse(@Nonnull Response<ListUsersQuery.Data> response) {
                            //Log.i("Found",response.data().listUsers().items().toString());
                            if(!response.data().listUsers().items().isEmpty()){
                                Log.i("Valid","User found");
                                Intent login = new Intent(getApplicationContext(),HomeActivity.class);
                                startActivity(login);
                                finish();
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
}
