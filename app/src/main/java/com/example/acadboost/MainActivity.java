package com.example.acadboost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaCodec;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.core.auth.providers.userpassword.UserPasswordAuthProviderClient;


public class MainActivity extends AppCompatActivity {

    ImageView imageView,googleLoginImageView,facebookLoginImageView,linkedinLoginImageView;
    TextView textView,appName,textView3,loginTextView;
    Button getStartedbutton;
    View bottomSheetView;
    StitchUser user;

    @Override
    protected void onStart() {
        super.onStart();
        user = Stitch.getAppClient("acadboost-rauqg").getAuth().getUser();
        if(user != null) {
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.pencilimage);

        textView = findViewById(R.id.textView);
        appName = findViewById(R.id.appName);
        textView3 = findViewById(R.id.textView3);
        loginTextView = findViewById(R.id.loginTextView);
        getStartedbutton = findViewById(R.id.getStartedButton);

        //Connection

        Stitch.initializeDefaultAppClient(getResources().getString(R.string.my_app_id));

        //Animations

        imageView.animate().alpha(1).setDuration(800).setStartDelay(200);
        appName.animate().translationY(0).setDuration(800).setStartDelay(400);
        textView.animate().translationY(0).setDuration(800).setStartDelay(400);
        getStartedbutton.animate().translationY(0).setDuration(800).setStartDelay(400);
        textView3.animate().translationY(0).setDuration(800).setStartDelay(700);
        loginTextView.animate().translationY(0).setDuration(800).setStartDelay(700);

        //Bottom Sheet Show

        getStartedbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog getStartedBottomSheetDialog = new BottomSheetDialog(
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

                bottomSheetView.findViewById(R.id.signUpButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(emailSignUpValid()) {
                            emailSignUp();
                            getStartedBottomSheetDialog.dismiss();
                        }
                    }
                });

                getStartedBottomSheetDialog.setContentView(bottomSheetView);
                getStartedBottomSheetDialog.show();
            }
        });
    }

    public boolean emailSignUpValid() {
        boolean valid = true;

        EditText nameEditText,passwordEditText,emailEditText;
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

    public void emailSignUp() {

        EditText nameEditText,passwordEditText,emailEditText;
        nameEditText = bottomSheetView.findViewById(R.id.nameEditText);
        passwordEditText = bottomSheetView.findViewById(R.id.passwordEditText);
        emailEditText = bottomSheetView.findViewById(R.id.emailEditText);

        String name,password,email;
        name = nameEditText.getText().toString();
        password = passwordEditText.getText().toString();
        email = emailEditText.getText().toString();


        UserPasswordAuthProviderClient newUser = Stitch.getDefaultAppClient().getAuth().getProviderClient(UserPasswordAuthProviderClient.factory);

        newUser.registerWithEmail(email,password).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Email Sent", Toast.LENGTH_SHORT).show();
                    goToLogin();
                }else {
                    Toast.makeText(MainActivity.this, "This Email is already registered", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void goToLogin() {
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
    }

    public void loginClick(View view) {
        goToLogin();
    }

}
