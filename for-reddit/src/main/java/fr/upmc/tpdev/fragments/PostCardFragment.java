package fr.upmc.tpdev.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.dean.jraw.RedditClient;
import net.dean.jraw.auth.AuthenticationManager;

import java.util.ArrayList;
import java.util.HashMap;

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
    public static final String ARG_SECTION_NUMBER = "section_number";

    private final int MAX_POSTS_TO_LOAD = 150;

    private PostCardAdapter adapter;
    private SparseArray<ArrayList<Post>> postList;

    public PostCardFragment() {
        postList = new SparseArray<>();
        postList.put(1, new ArrayList<Post>());
        postList.put(2, new ArrayList<Post>());
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

        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);

        adapter = new PostCardAdapter(recyclerView, postList, sectionNumber);
        recyclerView.setAdapter(adapter);
        adapter.setOnPostCardClickListener(this);

        fetchPosts(view, sectionNumber);

        return view;
    }

    public void fetchPosts(View view, int sectionNumber) {

        switch (sectionNumber) {
            case 1: // HOME
                new MorePostsTask(view, false, 1).execute();
                break;

            case 2: // GLOBAL
                new MorePostsTask(view, false, 2).execute();
                break;

            default:
                break;
        }
    }

    private int hack;
    private class MorePostsTask extends AsyncTask<Void, Void, Void> {

        private ProgressBar mShowPosts;
        private View mView;
        private boolean isLoadMore;
        private int sectionNumber;

        MorePostsTask(View view, boolean isLoadMore, int sectionNumber) {
            super();
            this.mShowPosts = view.findViewById(R.id.pb_show_posts);
            this.mView = view;
            this.isLoadMore = isLoadMore;
            this.sectionNumber = sectionNumber;
        }

        @Override
        protected void onPreExecute() {
            mShowPosts.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            RedditClient redditClient = AuthenticationManager.get().getRedditClient();
            ArrayList<Post> internPostList = new ArrayList<>();

            if (sectionNumber == 1) { // HOME
                internPostList = UserInfoActivity.meRandomUserSubmissions(redditClient);

            } else if (sectionNumber == 2) { // GLOBAL
                internPostList = UserInfoActivity.meRandomGlobalSubmissions(redditClient);
            }

            postList.get(sectionNumber).addAll(internPostList);

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            adapter.notifyDataSetChanged();
            mShowPosts.setVisibility(View.GONE);
            loadMorePosts(mView, sectionNumber);

            if (isLoadMore) {
                postList.get(sectionNumber).remove(hack);
                adapter.notifyItemRemoved(postList.get(sectionNumber).size());
                adapter.setLoaded();
            }
        }
    }

    private void loadMorePosts(final View view, final int sectionNumber) {
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {

                if (postList.get(sectionNumber).size() <= MAX_POSTS_TO_LOAD) {
                    // do this hack to show a progress bar
                    postList.get(sectionNumber).add(null);
                    hack = postList.get(sectionNumber).size() - 1;
                    adapter.notifyItemInserted(hack);

                    new MorePostsTask(view, true, sectionNumber).execute();

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
        Intent intent = new Intent(PostCardFragment.this.getContext(), PostActivity.class);

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
        Toast.makeText(getContext(), "The wrapper can't handle upVote action yet",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDownvote(ImageView view, int position) {
        Log.i(LOG_TAG, "--------------------onDownvote " + position);
        Toast.makeText(getContext(), "The wrapper can't handle downVote action yet",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShare(RelativeLayout view, int position, int tab) {
        Log.i(LOG_TAG, "--------------------onShare " + position);
        /*Toast.makeText(getContext(), "Share action !",
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
