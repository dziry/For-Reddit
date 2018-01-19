package fr.upmc.tpdev.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import net.dean.jraw.RedditClient;
import net.dean.jraw.auth.AuthenticationManager;

import java.util.ArrayList;

import fr.upmc.tpdev.R;
import fr.upmc.tpdev.activities.UserInfoActivity;
import fr.upmc.tpdev.adapters.PostCardAdapter;
import fr.upmc.tpdev.beans.Post;
import fr.upmc.tpdev.interfaces.OnLoadMoreListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostCardFragment extends Fragment {

    private static final String LOG_TAG = "PostCardFragment";
    // The fragment argument representing the section number for this fragment.
    private static final String ARG_SECTION_NUMBER = "section_number";

    private RecyclerView recyclerView;
    private PostCardAdapter adapter;
    private ArrayList<Post> postList;

    public PostCardFragment() {
        postList = new ArrayList<>();
    }

    // Returns a new instance of this fragment for the given section number.
    public static PostCardFragment newInstance(int sectionNumber) {

        //int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER)

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
        recyclerView = view.findViewById(R.id.rv_cards_in_fragment);

        LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new PostCardAdapter(recyclerView, postList);
        recyclerView.setAdapter(adapter);

        adapter.fetchPosts(view);

        return view;
    }
}
