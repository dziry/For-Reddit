package fr.upmc.tpdev.adapters;

import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import fr.upmc.tpdev.R;
import fr.upmc.tpdev.beans.Comment;
import fr.upmc.tpdev.beans.Post;
import fr.upmc.tpdev.interfaces.OnCommentClickListener;

import fr.upmc.tpdev.interfaces.OnPostDetailsClickListener;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class PostDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String LOG_TAG = "PostDetailsAdapter";
    private final int VIEW_TYPE_POST = 0;
    private final int VIEW_TYPE_COMMENTS = 1;

    private OnCommentClickListener mOnCommentClickListener;
    private static OnPostDetailsClickListener mOnPostDetailsClickListener;

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

        if (holder instanceof PostDetailsAdapter.PostContentViewHolder) {
            PostDetailsAdapter.PostContentViewHolder postContentViewHolder =
                    (PostDetailsAdapter.PostContentViewHolder) holder;

            postContentViewHolder.mSubreddit.setText(post.getSubReddit());
            postContentViewHolder.mAuthor.setText(post.getAuthor());
            postContentViewHolder.mTime.setText(post.getTime());
            postContentViewHolder.mTitle.setText(post.getTitle());
            postContentViewHolder.mScoreCount.setText(post.getScoreCount());
            postContentViewHolder.mCommentsCount.setText(post.getCommentCount());

            if (post.getContentText() == null
                    && !(boolean)PostContentViewHolder.mContentImage.getTag(R.id.is_gif_ok)
                    && !(boolean)PostContentViewHolder.mContentImage.getTag(R.id.is_image_ok)) {

                new PostContentViewHolder.PostContentTask(post.getUrl()).execute();

            } else {
                postContentViewHolder.mContentText.setVisibility(View.VISIBLE);
                postContentViewHolder.mContentText.setText(post.getContentText());
            }

        } else if (holder instanceof PostDetailsAdapter.CommentsViewHolder) {
            Comment comment = comments.get(position);

            PostDetailsAdapter.CommentsViewHolder commentsViewHolder =
                    (PostDetailsAdapter.CommentsViewHolder) holder;

            commentsViewHolder.mAuthor.setText(comment.getAuthorString());
            commentsViewHolder.mTime.setText(comment.getTimeString());
            commentsViewHolder.mBody.setText(comment.getBody());
            commentsViewHolder.mScore.setText(comment.getScoreString());

            if (comment.getRepliesCountInt() <= 0) {
                commentsViewHolder.mlayoutReplies.setVisibility(View.GONE);

            } else {
                if (!comment.isExpanded()) {
                    commentsViewHolder.mlayoutReplies.setVisibility(View.VISIBLE);
                    commentsViewHolder.mlayoutReplies.setTag(comment.getId());
                    commentsViewHolder.mRepliesCount.setText(comment.getRepliesCountString());

                } else {
                    commentsViewHolder.mlayoutReplies.setVisibility(View.GONE);
                }
            }

            int level = comment.getMarginLevelCoefficient();
            if (level > CommentsViewHolder.MAX_REPLIES_COUNT) {
                level = CommentsViewHolder.MAX_REPLIES_COUNT;
            }
            // clean because it's a recycler view
            for (int i = 0; i < CommentsViewHolder.MAX_REPLIES_COUNT; i++) {
                commentsViewHolder.mHorizontalSeparator.get(i).setVisibility(View.GONE);
            }
            // update
            for (int i = 0; i < level; i++) {
                commentsViewHolder.mHorizontalSeparator.get(i).setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return comments == null ? 0 : comments.size();
    }

    private static class PostContentViewHolder extends RecyclerView.ViewHolder {

        private TextView mSubreddit;
        private TextView mAuthor;
        private TextView mTime;
        private TextView mTitle;
        private TextView mContentText;
        private static TextView mUrl;
        private TextView mScoreCount;
        private TextView mCommentsCount;

        private static ProgressBar mLoadPost;
        private static ImageView mContentImage;
        private static RelativeLayout mLayoutLink;
        private RelativeLayout mLayoutShare;

        private ImageView mUpvote;
        private ImageView mDownvote;

        PostContentViewHolder(View view) {
            super(view);

            mSubreddit = view.findViewById(R.id.tv_subreddit);
            mAuthor = view.findViewById(R.id.tv_op);
            mTime = view.findViewById(R.id.tv_time);
            mTitle = view.findViewById(R.id.tv_title);
            mContentText = view.findViewById(R.id.tv_self_text);
            mUrl = view.findViewById(R.id.tv_link);
            mScoreCount = view.findViewById(R.id.tv_votes);
            mCommentsCount = view.findViewById(R.id.tv_comments);
            mLoadPost = view.findViewById(R.id.pb_load_post);
            mContentImage = view.findViewById(R.id.iv_content);
            mLayoutLink = view.findViewById(R.id.rl_link);
            mLayoutShare = view.findViewById(R.id.rl_share);
            mUpvote = view.findViewById(R.id.iv_upvote);
            mDownvote = view.findViewById(R.id.iv_downvote);

            // to check if resources are ready or not
            mContentImage.setTag(R.id.is_gif_ok, false);
            mContentImage.setTag(R.id.is_image_ok, false);

            mLayoutLink.setOnClickListener(onUrl);
            mUpvote.setOnClickListener(upvote);
            mDownvote.setOnClickListener(downvote);
            mLayoutShare.setOnClickListener(share);
        }

        private View.OnClickListener onUrl = new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (view instanceof RelativeLayout && mOnPostDetailsClickListener != null) {
                    mOnPostDetailsClickListener.onUrl((RelativeLayout) view, getAdapterPosition());
                }
            }
        };
        private View.OnClickListener upvote = new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (view instanceof ImageView && mOnPostDetailsClickListener != null) {
                    mOnPostDetailsClickListener.onUpvote((ImageView) view, getAdapterPosition());
                }
            }
        };
        private View.OnClickListener downvote = new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (view instanceof ImageView && mOnPostDetailsClickListener != null) {
                    mOnPostDetailsClickListener.onDownvote((ImageView) view, getAdapterPosition());
                }
            }
        };
        private View.OnClickListener share = new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (view instanceof RelativeLayout && mOnPostDetailsClickListener != null) {
                    mOnPostDetailsClickListener.onShare((RelativeLayout) view, getAdapterPosition());
                }
            }
        };
        public static class PostContentTask extends AsyncTask<Integer, Void, Integer> {
            private String url;

            PostContentTask(String url) {
                super();
                this.url = url;
            }

            @Override
            protected void onPreExecute() {
                mLoadPost.setVisibility(View.VISIBLE);
            }

            @Override
            protected Integer doInBackground(Integer... params) {
                if (isGif(url)) {
                    return 1;

                } else if (isImage(url)) {
                    return 2;
                }

                return 0;
            }

            @Override
            protected void onPostExecute(Integer content) {

                if (content == 1) {
                    Log.i(LOG_TAG, "--------GIF-------");
                    mContentImage.setVisibility(View.VISIBLE);
                    mContentImage.setTag(R.id.is_gif_ok, true);
                    Glide.with(mContentImage.getContext())
                            .load(url)
                            .listener(new RequestListener<String,GlideDrawable>() {

                                @Override
                                public boolean onException(Exception e, String model,
                                                           Target<GlideDrawable> target,
                                                           boolean isFirstResource) {

                                    mLoadPost.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model,
                                                               Target<GlideDrawable> target,
                                                               boolean isFromMemoryCache,
                                                               boolean isFirstResource) {

                                    mLoadPost.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(mContentImage);

                } else if (content == 2) {
                    Log.i(LOG_TAG, "--------IMAGE-------");
                    mContentImage.setVisibility(View.VISIBLE);
                    mContentImage.setTag(R.id.is_image_ok, true);
                    Picasso.with(mContentImage.getContext())
                            .load(url)
                            .transform(new RoundedCornersTransformation(10,0))
                            .into(mContentImage);
                    mLoadPost.setVisibility(View.GONE);

                } else {
                    mUrl.setText(url);
                    mLoadPost.setVisibility(View.GONE);
                    mLayoutLink.setVisibility(View.VISIBLE);
                }
            }

            private boolean isGif(String url) {
                try {
                    InputStream input = new URL(url).openStream();
                    byte[] bytes = IOUtils.toByteArray(input);

                    // All GIF files must start with a header block.
                    // The header takes up the first six bytes of the file.
                    // These bytes should all correspond to ASCII character codes.
                    // The first three bytes are called the signature.
                    // These should always be "GIF" (ie 71="G", 73="I", 70="F")
                    return bytes.length > 2 && bytes[0] == 71 && bytes[1] == 73 && bytes[2] == 70;

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return false;
            }

            private boolean isImage(String url) {
                try {
                    URLConnection connection = new URL(url).openConnection();
                    String contentType = connection.getHeaderField("Content-Type");
                    return contentType.startsWith("image/");

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return false;
            }
        }
    }

    private class CommentsViewHolder extends RecyclerView.ViewHolder {

        private static final int MAX_REPLIES_COUNT = 8;
        private TextView mAuthor;
        private TextView mTime;
        private TextView mBody;
        private TextView mScore;
        private TextView mRepliesCount;

        private RelativeLayout mlayoutReplies;

        private SparseArray<View> mHorizontalSeparator;

        CommentsViewHolder(View view) {
            super(view);

            mAuthor = view.findViewById(R.id.tv_author);
            mTime = view.findViewById(R.id.tv_time);
            mBody = view.findViewById(R.id.tv_body);
            mScore = view.findViewById(R.id.tv_score);
            mRepliesCount = view.findViewById(R.id.tv_replies_count);
            mlayoutReplies = view.findViewById(R.id.rl_comment_replies);

            mHorizontalSeparator = new SparseArray<>(MAX_REPLIES_COUNT);

            mHorizontalSeparator.put(0, view.findViewById(R.id.vi_horizontal_separator1));
            mHorizontalSeparator.put(1, view.findViewById(R.id.vi_horizontal_separator2));
            mHorizontalSeparator.put(2, view.findViewById(R.id.vi_horizontal_separator3));
            mHorizontalSeparator.put(3, view.findViewById(R.id.vi_horizontal_separator4));
            mHorizontalSeparator.put(4, view.findViewById(R.id.vi_horizontal_separator5));
            mHorizontalSeparator.put(5, view.findViewById(R.id.vi_horizontal_separator6));
            mHorizontalSeparator.put(6, view.findViewById(R.id.vi_horizontal_separator7));
            mHorizontalSeparator.put(7, view.findViewById(R.id.vi_horizontal_separator8));

            mlayoutReplies.setOnClickListener(showReplies);
        }

        private View.OnClickListener showReplies = new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (view instanceof RelativeLayout && mOnCommentClickListener != null) {
                    mOnCommentClickListener.onShowReplies((RelativeLayout) view, getAdapterPosition());
                }
            }
        };
    }

    public void setOnCommentClickListener(OnCommentClickListener onCommentClickListener) {
        this.mOnCommentClickListener = onCommentClickListener;
    }

    public void setOnPostDetailsClickListener(OnPostDetailsClickListener onPostDetailsClickListener) {
        mOnPostDetailsClickListener = onPostDetailsClickListener;
    }
}
