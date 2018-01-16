package fr.upmc.tpdev.adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import fr.upmc.tpdev.R;
import fr.upmc.tpdev.beans.Post;
import fr.upmc.tpdev.interfaces.OnLoadMoreListener;

/**
 * Created by Adel on 15/01/18.
 */

public class PostCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean isLoading;
    private ArrayList<Post> postList;
    private int visibleThreshold = 1;
    private int lastVisibleItem;
    private int totalItemCount;

    public PostCardAdapter(RecyclerView recyclerView, ArrayList<Post> postList) {
        this.postList = postList;

        final LinearLayoutManager linearLayoutManager =
                (LinearLayoutManager) recyclerView.getLayoutManager();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                /*Log.i("PPP", "-----T: " + totalItemCount);
                Log.i("PPP", "-----L: " + lastVisibleItem);
                Log.i("PPP", "--------------------------");*/

                if (!isLoading && totalItemCount == (lastVisibleItem + visibleThreshold)) {
                    //Log.i("PPP", "-----------------TRUE");
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }

                    isLoading = true;

                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        return postList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_post_card, parent, false);

        return new MyViewHolder(view);*/
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_post_card, parent,
                    false);
            return new PostViewHolder(view);

        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.posts_loading, parent,
                    false);
            return new LoadingViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof PostViewHolder) {
            Post post = postList.get(position);
            PostViewHolder postViewHolder = (PostViewHolder) holder;

            postViewHolder.mSubreddit.setText(post.getSubReddit());
            postViewHolder.mOp.setText(post.getAuthor());

            postViewHolder.mContent.setText(post.getTitle());
            //postViewHolder.mThumbnail.setImage

            postViewHolder.mCommentsCount.setText(post.getCommentCount());
            postViewHolder.mVotesCount.setText(post.getScoreCount());

        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.mLoadMore.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return postList == null ? 0 : postList.size();
    }

    public void setLoaded() {
        isLoading = false;
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar mLoadMore;

        public LoadingViewHolder(View view) {
            super(view);
            mLoadMore = view.findViewById(R.id.pb_load_more);
        }
    }

    private class PostViewHolder extends RecyclerView.ViewHolder {

        private TextView mSubreddit;
        private TextView mOp;
        private TextView mContent;
        private TextView mVotesCount;
        private TextView mCommentsCount;
        private TextView mShareAction;

        private ImageButton mThumbnail;
        private ImageButton mUpvote;
        private ImageButton mDownvote;
        private ImageButton mComments;
        private ImageButton mShare;

        public PostViewHolder(View view) {
            super(view);

            mSubreddit = view.findViewById(R.id.tv_subreddit);
            mOp = view.findViewById(R.id.tv_op);
            mContent = view.findViewById(R.id.tv_content);
            mVotesCount = view.findViewById(R.id.tv_votes);
            mCommentsCount = view.findViewById(R.id.tv_comments);
            mShareAction = view.findViewById(R.id.tv_share);

            mThumbnail = view.findViewById(R.id.ib_thumbnail);
            mUpvote = view.findViewById(R.id.ib_upvote);
            mDownvote = view.findViewById(R.id.ib_downvote);
            mComments = view.findViewById(R.id.ib_comments);
            mShare = view.findViewById(R.id.ib_share);
        }
    }
}
