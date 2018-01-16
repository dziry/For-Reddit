package fr.upmc.tpdev.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.dean.jraw.RedditClient;
import net.dean.jraw.auth.AuthenticationManager;

import java.util.ArrayList;
import java.util.List;

import fr.upmc.tpdev.R;
import fr.upmc.tpdev.activities.DrawerActivity;
import fr.upmc.tpdev.activities.UserInfoActivity;
import fr.upmc.tpdev.adapters.PostCardAdapter;
import fr.upmc.tpdev.beans.Post;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostCardFragment extends Fragment {

    private static RecyclerView recyclerView;
    private static PostCardAdapter adapter;
    private ArrayList<Post> postList;

    public PostCardFragment() {
        // Required empty public constructor
        this.postList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /*View view = inflater.inflate(R.layout.fragment_post_card, container, false);

        TextView mSubreddit = view.findViewById(R.id.tv_subreddit);
        TextView mOp = view.findViewById(R.id.tv_op);
        mSubreddit.setText("Hello");
        mOp.setText("world !");*/

        View view = inflater.inflate(R.layout.content_tabbed, container, false);
        recyclerView = view.findViewById(R.id.rv_cards_in_fragment);
        LayoutManager layoutManager = new LinearLayoutManager(view.getContext());

        adapter = new PostCardAdapter(postList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        postList = fetchPosts(view);
        /*for (int i = 0; i < 3; i++) {
            Post post = new Post();
            post.setSubReddit("sss");
            post.setAuthor("aaa");
            post.setTitle("ttt");
            post.setCommentCount(i);
            post.setScoreCount(i*2);
            postList.add(post);
        }*/

        //adapter.notifyDataSetChanged();
        //adapter.notifyDataSetChanged();

        //postList = fetchPosts(view);
        //Log.i("AAA","postList.S---------" + postList.size());
        //adapter.notifyDataSetChanged(); // FIXME

        // Inflate the layout for this fragment
        return view;
    }

    private ArrayList<Post> fetchPosts(View view) {

        RedditInfoTask redditInfoTask = new RedditInfoTask(view);
        redditInfoTask.execute();

        return redditInfoTask.getPostList();
    }

    private static class RedditInfoTask extends AsyncTask<Void, Void, Void> {

        private ArrayList<Post> postList;
        @SuppressLint("StaticFieldLeak")
        private ProgressBar mShowPosts;

        RedditInfoTask(View view) {
            super();
            this.postList = new ArrayList<>();
            this.mShowPosts = view.findViewById(R.id.pb_show_posts);
        }

        @Override
        protected void onPreExecute() {
            this.mShowPosts.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            RedditClient redditClient = AuthenticationManager.get().getRedditClient();
            this.postList = UserInfoActivity.meRandomSubmissions(redditClient);

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            this.mShowPosts.setVisibility(View.GONE);

            PostCardFragment.adapter = new PostCardAdapter(postList);
            PostCardFragment.recyclerView.setAdapter(adapter);

            //PostCardFragment.adapter.notifyDataSetChanged();
        }

        public ArrayList<Post> getPostList() {
            return postList;
        }
    }
}
