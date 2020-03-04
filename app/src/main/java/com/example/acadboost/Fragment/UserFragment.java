package com.example.acadboost.Fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.acadboost.R;
import com.example.acadboost.SessionManager;

import java.util.HashMap;

public class UserFragment extends Fragment {

    SessionManager session;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_fragment,container,false);
        getActivity().setTitle("Profile");

        session = new SessionManager(getContext());
        HashMap<String, String> sessionData = session.getSessionData();
        TextView userFragmentUserName = view.findViewById(R.id.userFragmentUserName);
        userFragmentUserName.setText(sessionData.get(SessionManager.NAME));
        ImageView userFragmentImage = view.findViewById(R.id.userFragmentImage);

        Glide.with(getContext()).load(Uri.parse(sessionData.get(SessionManager.PROFILE_PICTURE_URL))).apply(RequestOptions.circleCropTransform()).into(userFragmentImage);

        return view;
    }
}
