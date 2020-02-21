package com.example.acadboost;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "Login";
    private static final String IS_USER_LOGGEDIN = "isUserLoggedIn";
    public static final String NAME = "Name";
    public static final String EMAIL = "Email";
    public static final String ID = "Id";
    public static final String SIGNUP_TYPE = "signUpType";
    public static final String PROFILE_PICTURE_URL = "profilePictureURL";

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor = pref.edit();
    }

    public void startSession(String name,String email,String id,String signUpType,String profilePictureURL) {
        editor.putBoolean(IS_USER_LOGGEDIN,true);
        editor.putString(NAME,name);
        editor.putString(EMAIL,email);
        editor.putString(ID,id);
        editor.putString(SIGNUP_TYPE,signUpType);
        editor.putString(PROFILE_PICTURE_URL,profilePictureURL);
        editor.commit();
    }

    public boolean checkLogin() {
        if(!this.isUserLoggedIn()) {
            Intent login = new Intent(context,LoginActivity.class);
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(login);

            return true;
        }
        return false;
    }

    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGGEDIN,false);
    }

    public HashMap<String, String> getSessionData() {
        HashMap<String,String> user = new HashMap<>();
        user.put(NAME,pref.getString(NAME,null));
        user.put(EMAIL,pref.getString(EMAIL,null));
        user.put(ID,pref.getString(ID,null));
        user.put(SIGNUP_TYPE,pref.getString(SIGNUP_TYPE,null));
        user.put(PROFILE_PICTURE_URL,pref.getString(PROFILE_PICTURE_URL,null));

        return user;
    }

    public void endSession() {

        editor.clear();
        editor.commit();

    }
}
