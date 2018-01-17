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
    private static final int MAX_POSTS_TO_LOAD = 150;
    private static final int POSTS_TO_LOAD_EACH_TIME = 10;

    private RecyclerView recyclerView;
    private PostCardAdapter adapter;
    private ArrayList<Post> postList;

    public PostCardFragment() {
        postList = new ArrayList<>();
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

        //new FooTask(view).execute();
        adapter.fetchPosts(view);

        /*final ProgressBar mShowPosts = view.findViewById(R.id.pb_show_posts);
        mShowPosts.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                for (int i = 0; i < 5; i++) {
                    Post post = new Post();
                    post.setSubReddit("feffef");
                    post.setAuthor("zdzdzdzd");
                    post.setTitle("lkln");
                    post.setScoreCount(i);
                    post.setCommentCount(i*2);

                    postList.add(post);
                }

                PostCardFragment.adapter.notifyDataSetChanged();
                mShowPosts.setVisibility(View.GONE);

            }
        }, 5000);*/

        //postList = fetchPosts(view);
        //adapter.notifyDataSetChanged();

        return view;
    }

    /*private ArrayList<Post> fetchPosts(View view) {

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

            PostCardFragment.adapter = new PostCardAdapter(recyclerView, postList);
            PostCardFragment.recyclerView.setAdapter(adapter);
            //loadMorePosts();
        }

        //set load more listener for the RecyclerView adapter
        private void loadMorePosts() {
            PostCardFragment.adapter.setOnLoadMoreListener(new OnLoadMoreListener() {

                @Override
                public void onLoadMore() {

                    if (postList.size() <= MAX_POSTS_TO_LOAD) {
                        // do this hack to show a progress bar
                        postList.add(null);
                        PostCardFragment.adapter.notifyItemInserted(postList.size() - 1);

                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                postList.remove(postList.size() - 1);
                                PostCardFragment.adapter.notifyItemRemoved(postList.size());

                                //Generating more data
                                int index = postList.size();
                                int end = index + POSTS_TO_LOAD_EACH_TIME;

                                for (int i = index; i < end; i++) {
                                    Post post = new Post();
                                    post.setSubReddit("feffef");
                                    post.setAuthor("zdzdzdzd");
                                    post.setTitle("lkln");
                                    post.setScoreCount(5);
                                    post.setCommentCount(6);

                                    postList.add(post);
                                }

                                PostCardFragment.adapter.notifyDataSetChanged();
                                PostCardFragment.adapter.setLoaded();
                            }
                        }, 5000);
                    } else {
                        Log.i(PostCardFragment.LOG_TAG, "Loading data completed.");
                    }
                }
            });
        }

        private ArrayList<Post> getPostList() {
            return postList;
        }
    }

    private class FooTask extends AsyncTask<Void, Void, Void> {

        @SuppressLint("StaticFieldLeak")
        private ProgressBar mShowPosts;

        FooTask(View view) {
            super();

            this.mShowPosts = view.findViewById(R.id.pb_show_posts);
        }

        @Override
        protected void onPreExecute() {
            mShowPosts.setVisibility(View.VISIBLE);
            Log.i(LOG_TAG, "size(1) : " + postList.size());
        }

        @Override
        protected Void doInBackground(Void... params) {

            Log.i(LOG_TAG, "size(2) : " + postList.size());

            RedditClient redditClient = AuthenticationManager.get().getRedditClient();
            postList = UserInfoActivity.meRandomSubmissions(redditClient);

            Log.i(LOG_TAG, "size(3) : " + postList.size());

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            Log.i(LOG_TAG, "size(4) : " + postList.size());

            adapter.notifyDataSetChanged();
            mShowPosts.setVisibility(View.GONE);
        }
    }*/
}
