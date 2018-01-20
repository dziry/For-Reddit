package fr.upmc.tpdev.fragments;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import net.dean.jraw.RedditClient;
import net.dean.jraw.auth.AuthenticationManager;

import java.util.ArrayList;

import fr.upmc.tpdev.R;
import fr.upmc.tpdev.activities.PostActivity;
import fr.upmc.tpdev.activities.UserInfoActivity;
import fr.upmc.tpdev.adapters.PostCardAdapter;
import fr.upmc.tpdev.beans.Post;
import fr.upmc.tpdev.interfaces.OnLoadMoreListener;
import fr.upmc.tpdev.interfaces.OnPostCardClickListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostCardFragment extends Fragment implements OnPostCardClickListener {

    private static final String LOG_TAG = "PostCardFragment";
    // The fragment argument representing the section number for this fragment.
    private static final String ARG_SECTION_NUMBER = "section_number";

    private final int MAX_POSTS_TO_LOAD = 150;

    private PostCardAdapter adapter;
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

        adapter = new PostCardAdapter(recyclerView, postList);
        recyclerView.setAdapter(adapter);
        adapter.setOnPostCardClickListener(this);

        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        fetchPosts(view, sectionNumber);

        return view;
    }

    public void fetchPosts(View view, int sectionNumber) {

        switch (sectionNumber) {
            case 1:
                new FooTask(view, false).execute();
                break;

            case 2:
                //todo
                break;

            case 3:
                //todo
                break;

            default:
                break;
        }
    }

    private int hack;
    private class FooTask extends AsyncTask<Void, Void, Void> {

        private ProgressBar mShowPosts;
        private View mView;
        private boolean isLoadMore;
        private ArrayList<Post> internPostList;

        FooTask(View view, boolean isLoadMore) {
            super();
            this.mShowPosts = view.findViewById(R.id.pb_show_posts);
            this.mView = view;
            this.isLoadMore = isLoadMore;
            this.internPostList = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            mShowPosts.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            RedditClient redditClient = AuthenticationManager.get().getRedditClient();
            internPostList = UserInfoActivity.meRandomSubmissions(redditClient);
            postList.addAll(internPostList);
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            adapter.notifyDataSetChanged();
            mShowPosts.setVisibility(View.GONE);
            loadMorePosts(mView);

            if (isLoadMore) {
                postList.remove(hack);
                adapter.notifyItemRemoved(postList.size());
                adapter.setLoaded();
            }
        }
    }

    private void loadMorePosts(final View view) {
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {

                if (postList.size() <= MAX_POSTS_TO_LOAD) {
                    // do this hack to show a progress bar
                    postList.add(null);
                    hack = postList.size() - 1;
                    adapter.notifyItemInserted(hack);

                    new FooTask(view, true).execute();

                } else {
                    Log.i(LOG_TAG, "Loading data completed.");
                }
            }
        });
    }

    @Override
    public void onShowDetails(RelativeLayout view, int position) {
        Log.i(LOG_TAG, "--------------------onShowDetails " + position);
        final Post post = postList.get(position);
        Intent intent = new Intent(PostCardFragment.this.getContext(), PostActivity.class);

        intent.putExtra("id", post.getId());
        intent.putExtra("subReddit", post.getSubReddit());
        intent.putExtra("author", post.getAuthor());
        intent.putExtra("time", post.getTime());
        intent.putExtra("title", post.getTitle());
        intent.putExtra("contentText", post.getContentText());
        intent.putExtra("url", post.getUrl());
        intent.putExtra("scoreCount", post.getScoreCount());
        intent.putExtra("commentCount", post.getCommentCount());
        intent.putExtra("voteDirection", post.getVoteDirection());

        startActivity(intent);
    }

    @Override
    public void onUpvote(ImageView view, int position) {
        Log.i(LOG_TAG, "--------------------onUpvote " + position);
    }

    @Override
    public void onDownvote(ImageView view, int position) {
        Log.i(LOG_TAG, "--------------------onDownvote " + position);
    }

    @Override
    public void onShare(RelativeLayout view, int position) {
        Log.i(LOG_TAG, "--------------------onShare " + position);
    }
}
