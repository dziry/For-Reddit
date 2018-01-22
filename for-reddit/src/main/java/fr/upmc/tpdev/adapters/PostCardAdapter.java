package fr.upmc.tpdev.adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import fr.upmc.tpdev.R;
import fr.upmc.tpdev.beans.Post;
import fr.upmc.tpdev.interfaces.OnLoadMoreListener;
import fr.upmc.tpdev.interfaces.OnPostCardClickListener;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;


public class PostCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String LOG_TAG = "PostCardAdapter";
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private OnPostCardClickListener mOnPostCardClickListener;
    private boolean isLoading;
    private SparseArray<ArrayList<Post>> postList;
    private int visibleThreshold = 1;
    private int lastVisibleItem;
    private int totalItemCount;
    private int sectionNumber;

    public PostCardAdapter(RecyclerView recyclerView, SparseArray<ArrayList<Post>> postList,
                           int sectionNumber) {
        this.postList = postList;
        this.sectionNumber = sectionNumber;

        final LinearLayoutManager linearLayoutManager =
                (LinearLayoutManager) recyclerView.getLayoutManager();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount == (lastVisibleItem + visibleThreshold)) {

                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }

                    isLoading = true;
                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.mOnLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        return postList.get(sectionNumber).get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_post_card,
                    parent, false);

            return new PostViewHolder(view);

        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.posts_loading,
                    parent, false);

            return new LoadingViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof PostViewHolder) {
            Post post = postList.get(sectionNumber).get(position);
            PostViewHolder postViewHolder = (PostViewHolder) holder;

            postViewHolder.mSubreddit.setText(post.getSubReddit());
            postViewHolder.mAuthor.setText(post.getAuthor());
            postViewHolder.mTime.setText(post.getTime());
            postViewHolder.mContent.setText(post.getTitle());

            if (post.getContentThumbnail() != null) {
                Picasso.with(postViewHolder.mThumbnail.getContext())
                        .load(post.getContentThumbnail())
                        .transform(new RoundedCornersTransformation(10,0))
                        .into(postViewHolder.mThumbnail);

            } else {
                postViewHolder.mThumbnail.setImageResource(R.drawable.ic_broken_image);
            }

            postViewHolder.mCommentsCount.setText(post.getCommentCount());
            postViewHolder.mVotesCount.setText(post.getScoreCount());

        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.mLoadMore.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return postList.get(sectionNumber) == null ? 0 : postList.get(sectionNumber).size();
    }

    public void setLoaded() {
        isLoading = false;
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar mLoadMore;

        LoadingViewHolder(View view) {
            super(view);
            mLoadMore = view.findViewById(R.id.pb_load_more);
        }
    }

    private class PostViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mLayoutHeader;
        private RelativeLayout mLayoutContent;
        private RelativeLayout mLayoutShare;
        private RelativeLayout mLayoutMessages;

        private TextView mSubreddit;
        private TextView mAuthor;
        private TextView mTime;
        private TextView mContent;
        private TextView mVotesCount;
        private TextView mCommentsCount;

        private ImageView mThumbnail;
        private ImageView mUpvote;
        private ImageView mDownvote;

        PostViewHolder(View view) {
            super(view);

            mLayoutHeader = view.findViewById(R.id.rl_header);
            mLayoutContent = view.findViewById(R.id.rl_content);
            mLayoutShare = view.findViewById(R.id.rl_share);
            mLayoutMessages = view.findViewById(R.id.rl_messages);

            mSubreddit = view.findViewById(R.id.tv_subreddit);
            mAuthor = view.findViewById(R.id.tv_op);
            mTime = view.findViewById(R.id.tv_time);
            mContent = view.findViewById(R.id.tv_content);
            mVotesCount = view.findViewById(R.id.tv_votes);
            mCommentsCount = view.findViewById(R.id.tv_comments);

            mThumbnail = view.findViewById(R.id.iv_thumbnail);
            mUpvote = view.findViewById(R.id.iv_upvote);
            mDownvote = view.findViewById(R.id.iv_downvote);

            mLayoutHeader.setOnClickListener(showDetails);
            mLayoutContent.setOnClickListener(showDetails);
            mLayoutMessages.setOnClickListener(showDetails);
            mLayoutShare.setOnClickListener(share);
            mUpvote.setOnClickListener(upvote);
            mDownvote.setOnClickListener(downvote);

            //view.setTag(view);
            //view.setOnClickListener(this);
        }

       private View.OnClickListener showDetails = new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (view instanceof RelativeLayout && mOnPostCardClickListener != null) {
                    mOnPostCardClickListener.onShowDetails((RelativeLayout) view,
                            getAdapterPosition(), sectionNumber);
                }
            }
        };

        private View.OnClickListener upvote = new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (view instanceof ImageView && mOnPostCardClickListener != null) {
                    mOnPostCardClickListener.onUpvote((ImageView) view, getAdapterPosition());
                }
            }
        };

        private View.OnClickListener downvote = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (view instanceof ImageView && mOnPostCardClickListener != null) {
                    mOnPostCardClickListener.onDownvote((ImageView) view, getAdapterPosition());
                }
            }
        };

        private View.OnClickListener share = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (view instanceof RelativeLayout && mOnPostCardClickListener != null) {
                    mOnPostCardClickListener.onShare((RelativeLayout) view, getAdapterPosition());
                }
            }
        };
    }

    public void setOnPostCardClickListener(OnPostCardClickListener onPostCardClickListener) {
        this.mOnPostCardClickListener = onPostCardClickListener;
    }
}
