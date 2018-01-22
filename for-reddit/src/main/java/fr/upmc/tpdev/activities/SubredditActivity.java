package fr.upmc.tpdev.activities;


import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.dean.jraw.RedditClient;
import net.dean.jraw.auth.AuthenticationManager;

import java.util.ArrayList;

import fr.upmc.tpdev.R;
import fr.upmc.tpdev.adapters.PostCardAdapter;
import fr.upmc.tpdev.beans.Post;
import fr.upmc.tpdev.fragments.PostCardFragment;
import fr.upmc.tpdev.interfaces.OnLoadMoreListener;
import fr.upmc.tpdev.interfaces.OnPostCardClickListener;

public class SubredditActivity extends AppCompatActivity implements OnPostCardClickListener {

    private static final String LOG_TAG = "SubredditActivity";

    private final int MAX_POSTS_TO_LOAD = 150;
    private final int POSTS_LIST_INDEX = 3;

    private PostCardAdapter adapter;
    private SparseArray<ArrayList<Post>> postList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subreddit);

        postList = new SparseArray<>();
        postList.put(POSTS_LIST_INDEX, new ArrayList<Post>());

        RecyclerView recyclerView = findViewById(R.id.rv_cards_in_fragment);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new PostCardAdapter(recyclerView, postList, POSTS_LIST_INDEX);
        recyclerView.setAdapter(adapter);
        adapter.setOnPostCardClickListener(this);

        Intent intent = getIntent();
        String id = intent.getStringExtra("sub");

        setTitle("r/" + id);
        fetchPosts(id);
    }

    private void fetchPosts(String subredditName) {
        new MorePostsTask(false, subredditName).execute();
    }

    private int hack;
    private class MorePostsTask extends AsyncTask<Void, Void, Void> {

        private ProgressBar mShowPosts;
        private boolean isLoadMore;
        private String subredditName;

        MorePostsTask(boolean isLoadMore, String subredditName) {
            super();
            this.mShowPosts = findViewById(R.id.pb_show_posts);
            this.isLoadMore = isLoadMore;
            this.subredditName = subredditName;
        }

        @Override
        protected void onPreExecute() {
            mShowPosts.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            RedditClient redditClient = AuthenticationManager.get().getRedditClient();
            ArrayList<Post> internPostList = UserInfoActivity.meRandomSpecificSubmissions(
                    redditClient, subredditName);

            postList.get(POSTS_LIST_INDEX).addAll(internPostList);

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            adapter.notifyDataSetChanged();
            mShowPosts.setVisibility(View.GONE);

            loadMorePosts(subredditName);

            if (isLoadMore) {
                postList.get(POSTS_LIST_INDEX).remove(hack);
                adapter.notifyItemRemoved(postList.get(3).size());
                adapter.setLoaded();
            }
        }
    }

    private void loadMorePosts(final String subredditName) {
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {

                if (postList.get(POSTS_LIST_INDEX).size() <= MAX_POSTS_TO_LOAD) {
                    // do this hack to show a progress bar
                    postList.get(POSTS_LIST_INDEX).add(null);
                    hack = postList.get(POSTS_LIST_INDEX).size() - 1;
                    adapter.notifyItemInserted(hack);

                    new MorePostsTask(true, subredditName).execute();

                } else {
                    Log.i(LOG_TAG, "Loading data completed.");
                }
            }
        });
    }

    @Override
    public void onShowDetails(RelativeLayout view, int position, int sectionNumber) {
        Log.i(LOG_TAG, "--------------------onShowDetails " + position);
        final Post post = postList.get(sectionNumber).get(position);
        Intent intent = new Intent(getApplicationContext(), PostActivity.class);

        intent.putExtra("id", post.getId());
        intent.putExtra("url", post.getUrl());
        intent.putExtra("subReddit", post.getSubReddit());
        intent.putExtra("author", post.getAuthor());
        intent.putExtra("time", post.getTime());
        intent.putExtra("title", post.getTitle());
        intent.putExtra("contentText", post.getContentText());
        intent.putExtra("scoreCount", post.getScoreCount());
        intent.putExtra("commentCount", post.getCommentCount());
        intent.putExtra("voteDirection", post.getVoteDirection());

        startActivity(intent);
    }

    @Override
    public void onUpvote(ImageView view, int position) {
        Log.i(LOG_TAG, "--------------------onUpvote " + position);
        Toast.makeText(getApplicationContext(), "The wrapper can't handle upVote action yet",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDownvote(ImageView view, int position) {
        Log.i(LOG_TAG, "--------------------onDownvote " + position);
        Toast.makeText(getApplicationContext(), "The wrapper can't handle downVote action yet",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShare(RelativeLayout view, int position, int tab) {
        Log.i(LOG_TAG, "--------------------onShare " + position);
        /*Toast.makeText(getApplicationContext(), "Share action !",
                Toast.LENGTH_SHORT).show();*/

        shareText(postList.get(tab).get(position).getTitle()
                + postList.get(tab).get(position).getUrl());
    }

    public void shareText(String bodyText) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject/Title");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, bodyText);
        startActivity(Intent.createChooser(intent, "Choose sharing method"));
    }
}
