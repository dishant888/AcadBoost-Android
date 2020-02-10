package com.example.acadboost;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

        return view;
    }
}
