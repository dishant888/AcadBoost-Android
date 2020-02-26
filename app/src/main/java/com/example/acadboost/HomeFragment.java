package com.example.acadboost;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.acadboost.Model.FeedModel;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    private boolean dataLoadedOnce = false;
    private RecyclerView recyclerView;
    private ArrayList<FeedModel> feedModelArrayList = new ArrayList<>();
    private Adapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!dataLoadedOnce) {
            fetchFeed();
            dataLoadedOnce = true;
        }
    }

    private void fetchFeed() {
        Toast.makeText(getContext(), "Hello", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.home_fragment,container,false);
        //Set Action Bar Title
        getActivity().setTitle("Home");

        return view;
    }

}
