package fr.upmc.tpdev.adapters;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.upmc.tpdev.R;
import fr.upmc.tpdev.beans.Post;

/**
 * Created by Adel on 15/01/18.
 */

public class PostCardAdapter extends RecyclerView.Adapter<PostCardAdapter.MyViewHolder> {

    private ArrayList<Post> postList;

    public PostCardAdapter(ArrayList<Post> postList) {
        this.postList = postList;
    }

    class MyViewHolder extends ViewHolder {
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

        private MyViewHolder(View view) {
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

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_post_card, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Post post = postList.get(position);
        holder.mSubreddit.setText(post.getSubReddit());
        holder.mOp.setText(post.getAuthor());

        holder.mContent.setText(post.getTitle());
        //holder.mThumbnail.setImage

        holder.mCommentsCount.setText(post.getCommentCount());
        holder.mVotesCount.setText(post.getScoreCount());
    }

    @Override
    public int getItemCount() {

        return postList.size();
    }
}
