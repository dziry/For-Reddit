package fr.upmc.tpdev.adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import fr.upmc.tpdev.beans.Comment;
import fr.upmc.tpdev.beans.Post;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class PostDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String LOG_TAG = "PostDetailsAdapter";
    private final int VIEW_TYPE_POST = 0;
    private final int VIEW_TYPE_COMMENTS = 1;

    private Post post;
    private ArrayList<Comment> comments;

    public PostDetailsAdapter(RecyclerView recyclerView, Post post, ArrayList<Comment> comments) {
        this.post = post;
        this.comments = comments;

        final LinearLayoutManager linearLayoutManager =
                (LinearLayoutManager) recyclerView.getLayoutManager();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);

                /*totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount == (lastVisibleItem + visibleThreshold)) {

                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }

                    isLoading = true;
                }*/
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return comments.get(position) == null ? VIEW_TYPE_POST : VIEW_TYPE_COMMENTS;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_POST) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_content_layout,
                    parent, false);
            return new PostDetailsAdapter.PostContentViewHolder(view);

        } else if (viewType == VIEW_TYPE_COMMENTS) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout,
                    parent, false);
            return new PostDetailsAdapter.CommentsViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Log.i(LOG_TAG, "position : " + position);

        if (holder instanceof PostDetailsAdapter.PostContentViewHolder) {
            PostDetailsAdapter.PostContentViewHolder postContentViewHolder =
                    (PostDetailsAdapter.PostContentViewHolder) holder;

            postContentViewHolder.mAuthor.setText(post.getAuthor());
            postContentViewHolder.mTitle.setText(post.getTitle());
            postContentViewHolder.mScoreCount.setText(post.getScoreCount());
            postContentViewHolder.mCommentsCount.setText(post.getCommentCount());

        } else if (holder instanceof PostDetailsAdapter.CommentsViewHolder) {
            Comment comment = comments.get(position);

            PostDetailsAdapter.CommentsViewHolder commentsViewHolder =
                    (PostDetailsAdapter.CommentsViewHolder) holder;

            commentsViewHolder.mAuthor.setText(comment.getAuthor());
            commentsViewHolder.mTime.setText(comment.getTime());
            commentsViewHolder.mBody.setText(comment.getBody());
            commentsViewHolder.mScore.setText(comment.getScore());
            //commentsViewHolder.mRepliesCount.setText(comment.getRepliesCount());
        }
    }

    @Override
    public int getItemCount() {
        Log.i(LOG_TAG, "size : " + (comments == null ? 0 : comments.size()));
        return comments == null ? 0 : comments.size();
    }

    private class PostContentViewHolder extends RecyclerView.ViewHolder {

        private TextView mSubreddit;
        private TextView mAuthor;
        private TextView mTime;
        private TextView mTitle;
        private TextView mContentText;
        private TextView mUrl;
        private TextView mScoreCount;
        private TextView mCommentsCount;

        private ImageView mContentImage;

        private RelativeLayout mLayoutLink;

        PostContentViewHolder(View view) {
            super(view);

            mSubreddit = view.findViewById(R.id.tv_subreddit);
            mAuthor = view.findViewById(R.id.tv_op);
            //mTime = view.findViewById(R.id.tv_time);
            mTitle = view.findViewById(R.id.tv_title);
            mContentText = view.findViewById(R.id.tv_self_text);
            mUrl = view.findViewById(R.id.tv_link);
            mScoreCount = view.findViewById(R.id.tv_votes);
            mCommentsCount = view.findViewById(R.id.tv_comments);
            mContentImage = view.findViewById(R.id.iv_content);
            mLayoutLink = view.findViewById(R.id.rl_link);
        }
    }

    private class CommentsViewHolder extends RecyclerView.ViewHolder {

        private TextView mAuthor;
        private TextView mTime;
        private TextView mBody;
        private TextView mScore;
        private TextView mRepliesCount;

        CommentsViewHolder(View view) {
            super(view);

            mAuthor = view.findViewById(R.id.tv_author);
            mTime = view.findViewById(R.id.tv_time);
            mBody = view.findViewById(R.id.tv_body);
            mScore = view.findViewById(R.id.tv_score);
            mRepliesCount = view.findViewById(R.id.tv_replies_count);
        }
    }
}
