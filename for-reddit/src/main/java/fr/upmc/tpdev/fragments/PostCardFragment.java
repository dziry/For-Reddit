package fr.upmc.tpdev.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import fr.upmc.tpdev.R;
import fr.upmc.tpdev.adapters.PostCardAdapter;
import fr.upmc.tpdev.beans.Post;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostCardFragment extends Fragment {

    private static final String LOG_TAG = "PostCardFragment";
    // The fragment argument representing the section number for this fragment.
    private static final String ARG_SECTION_NUMBER = "section_number";

    private ArrayList<Post> postList;

    public PostCardFragment() {
        postList = new ArrayList<>();
    }

    // Returns a new instance of this fragment for the given section number.
    public static PostCardFragment newInstance(int sectionNumber) {

        PostCardFragment fragment = new PostCardFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.content_tabbed, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.rv_cards_in_fragment);

        LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        PostCardAdapter adapter = new PostCardAdapter(recyclerView, postList);
        recyclerView.setAdapter(adapter);

        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        adapter.fetchPosts(getActivity(), view, sectionNumber);

        return view;
    }

    // Our Broadcast Receiver. We get notified that the data is ready this way.
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            boolean isLoaded = intent.getBooleanExtra("isLoaded", false);

            Log.i(LOG_TAG, "************* isLoaded : " + isLoaded);

            if (isLoaded) {
                Log.i(LOG_TAG, "************* YES.");
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, new IntentFilter("loadPosts"));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }
}
