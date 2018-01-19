package fr.upmc.tpdev.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.dean.jraw.RedditClient;
import net.dean.jraw.auth.AuthenticationManager;

import java.util.ArrayList;

import fr.upmc.tpdev.R;
import fr.upmc.tpdev.activities.UserInfoActivity;
import fr.upmc.tpdev.beans.Post;
import fr.upmc.tpdev.interfaces.OnLoadMoreListener;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;


public class PostCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String LOG_TAG = "PostCardAdapter";
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

    private void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
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

            if (post.getContentThumbnail() != null) {
                Picasso.with(postViewHolder.mThumbnail.getContext())
                        .load(post.getContentThumbnail())
                        .transform(new RoundedCornersTransformation(10,10))
                        .into(postViewHolder.mThumbnail);

            } else {
                postViewHolder.mThumbnail.setVisibility(View.GONE);
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
        return postList == null ? 0 : postList.size();
    }

    private void setLoaded() {
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
        private TextView mOp;
        private TextView mContent;
        private TextView mVotesCount;
        private TextView mCommentsCount;
        private TextView mShareAction;

        private ImageView mThumbnail;
        private ImageButton mUpvote;
        private ImageButton mDownvote;
        private ImageButton mComments;
        private ImageButton mShare;

        PostViewHolder(View view) {
            super(view);

            mLayoutHeader = view.findViewById(R.id.rl_header);
            mLayoutContent = view.findViewById(R.id.rl_content);
            mLayoutShare = view.findViewById(R.id.rl_share);
            mLayoutMessages = view.findViewById(R.id.rl_messages);

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

            mLayoutHeader.setOnClickListener(showDetails);
            mLayoutContent.setOnClickListener(showDetails);
            mLayoutMessages.setOnClickListener(showDetails);
            mLayoutShare.setOnClickListener(share);
            mUpvote.setOnClickListener(upvote);
            mDownvote.setOnClickListener(downvote);
        }

        private View.OnClickListener showDetails = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Details !", Toast.LENGTH_SHORT).show();
            }
        };

        private View.OnClickListener upvote = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Upvote !", Toast.LENGTH_SHORT).show();
            }
        };

        private View.OnClickListener downvote = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Downvote !", Toast.LENGTH_SHORT).show();
            }
        };

        private View.OnClickListener share = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Share !", Toast.LENGTH_SHORT).show();
            }
        };
    }

    public void fetchPosts(Context context, View view, int sectionNumber) {

        switch (sectionNumber) {
            case 1:
                new PostCardAdapter.FooTask(context, view, false).execute();
                break;

            case 2:
                new PostCardAdapter.FooTask(context, view, false).execute();
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

        private final int MAX_POSTS_TO_LOAD = 150;
        private ProgressBar mShowPosts;
        private Context mContext;
        private View mView;
        private boolean isLoadMore;
        private ArrayList<Post> internPostList;

        FooTask(Context context, View view, boolean isLoadMore) {
            super();
            this.mShowPosts = view.findViewById(R.id.pb_show_posts);
            this.mContext = context;
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
            PostCardAdapter.this.notifyDataSetChanged();
            mShowPosts.setVisibility(View.GONE);
            loadMorePosts(mView);

            if (isLoadMore) {
                postList.remove(hack);
                PostCardAdapter.this.notifyItemRemoved(postList.size());
                setLoaded();

            } else {
                Intent intent = new Intent("loadPosts");
                intent.putExtra("isLoaded", true);

                // broadcast the completion
                mContext.sendBroadcast(intent);
            }
        }

        private void loadMorePosts(final View view) {
            PostCardAdapter.this.setOnLoadMoreListener(new OnLoadMoreListener() {

                @Override
                public void onLoadMore() {

                    if (postList.size() <= MAX_POSTS_TO_LOAD) {
                        // do this hack to show a progress bar
                        postList.add(null);
                        hack = postList.size() - 1;
                        PostCardAdapter.this.notifyItemInserted(hack);

                        new PostCardAdapter.FooTask(null, view, true).execute();

                    } else {
                        Log.i(LOG_TAG, "Loading data completed.");
                    }
                }
            });
        }
    }
}
