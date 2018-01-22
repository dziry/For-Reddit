package fr.upmc.tpdev.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import net.dean.jraw.RedditClient;
import net.dean.jraw.auth.AuthenticationManager;
import net.dean.jraw.models.CommentNode;

import java.util.ArrayList;

import fr.upmc.tpdev.R;
import fr.upmc.tpdev.adapters.PostDetailsAdapter;
import fr.upmc.tpdev.beans.Comment;
import fr.upmc.tpdev.beans.Post;
import fr.upmc.tpdev.interfaces.OnCommentClickListener;

public class PostActivity extends AppCompatActivity implements OnCommentClickListener {

    private final String LOG_TAG = "PostActivity";

    private PostDetailsAdapter adapter;
    private ArrayList<Comment> comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        RecyclerView recyclerView = findViewById(R.id.rv_post_and_comments);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        this.comments = new ArrayList<>();
        comments.add(null);

        /*for (int i = 0; i < 10; i++) {
            Comment comment = new Comment(0);
            comment.setAuthor("aa" + i);
            //comment.setTime("aa" + i);
            comment.setBody("bb" + i);
            comment.setScore(""+ i);

            if (i != 3) {
                comment.setReplies(new ArrayList<Comment>());
            } else {
                Comment c = new Comment(1);
                c.setAuthor("RRaa" + i);
                //c.setTime("RRaa" + i);
                c.setBody("RRbb" + i);
                c.setScore(""+ i);
                c.setReplies(new ArrayList<Comment>());

                ArrayList<Comment> cs = new ArrayList<>();
                cs.add(c);

                comment.setReplies(cs);
            }

            comments.add(comment);
        }*/

        Post post = preparePost();
        adapter = new PostDetailsAdapter(recyclerView, post, comments);
        recyclerView.setAdapter(adapter);
        adapter.setOnCommentClickListener(this);

        loadComments();
    }

    private Post preparePost() {
        Intent intent = getIntent();

        String id = intent.getStringExtra("id");
        String url = intent.getStringExtra("url");
        String subReddit = "r/" + intent.getStringExtra("subReddit");
        String author = "u/" + intent.getStringExtra("author");
        String time = intent.getStringExtra("time");
        String title = intent.getStringExtra("title");
        String scoreCount = intent.getStringExtra("scoreCount");
        String commentCount = intent.getStringExtra("commentCount");
        int voteDirection = intent.getIntExtra("voteDirection", -1);

        Post post = new Post();

        post.setId(id);
        post.setUrl(url);
        post.setSubReddit(subReddit);
        post.setAuthor(author);
        post.setTitle(title);
        post.setTime(time);
        post.setContentText(getPostBody());
        post.setScoreCount(scoreCount);
        post.setCommentCount(commentCount);
        post.setVoteDirection(voteDirection);

        return post;
    }

    private String getPostBody() {
        Intent intent = getIntent();

        String url = intent.getStringExtra("url");
        String contentText = intent.getStringExtra("contentText");

        if (url != null && !isSelfLink(url) || contentText == null) {
            return null;
        }

        return contentText;
    }

    private boolean isSelfLink(String url) {

        return url.contains("www.reddit.com");
    }

    private class LoadCommentsTask extends AsyncTask<Void, Void, Void> {

        //private ProgressBar mShowComments;
        private ArrayList<Comment> internCommentList;
        private String postId;

        LoadCommentsTask(String postId) {
            super();
            //this.mShowComments = findViewById(R.id.pb_show_comments);
            this.internCommentList = new ArrayList<>();
            this.postId = postId;
        }

        @Override
        protected void onPreExecute() {
            //mShowComments.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            RedditClient redditClient = AuthenticationManager.get().getRedditClient();
            internCommentList = UserInfoActivity.getComments(redditClient, postId); //"7qel3h"
            comments.addAll(internCommentList);
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            adapter.notifyDataSetChanged();
            //mShowComments.setVisibility(View.GONE);
        }
    }

    private void loadComments() {
        Intent intent = getIntent();
        String postId = intent.getStringExtra("id");

        new LoadCommentsTask(postId).execute();
    }

    @Override
    public void onShowReplies(RelativeLayout layoutReplies, int position) {
        Log.i(LOG_TAG, "--------position : " + position);
        Log.i(LOG_TAG, "--------id : " + layoutReplies.getTag());

        String commentId = (String) layoutReplies.getTag();
        layoutReplies.setVisibility(View.GONE);
        expandReplies(commentId, position);

        /*Comment comment = new Comment(1);
        comment.setAuthor("xx");
        //comment.setTime("xx");
        comment.setBody("xx");
        comment.setScore(""+ 22);
        comment.setReplies(new ArrayList<Comment>());
        comments.add(4, comment);

        Comment comment1 = new Comment(2);
        comment1.setAuthor("xxx");
        //comment.setTime("xxx");
        comment1.setBody("xxx");
        comment1.setScore(""+ 221);
        comment1.setReplies(new ArrayList<Comment>());
        comments.add(5, comment1);

        Comment comment2 = new Comment(1);
        comment2.setAuthor("yy");
        //comment2.setTime("yy");
        comment2.setBody("yy");
        comment2.setScore(""+ 23);
        comment2.setReplies(new ArrayList<Comment>());
        comments.add(6, comment2);

        adapter.notifyDataSetChanged();*/
    }

    private void expandReplies(String commentId, int parentPosition) {
        Comment comment = getCommentById(commentId);

        if (comment != null) {
            if (comment.getChildren().size() > 0) {

                int position = 0;
                for (CommentNode aCommentNode : comment.getChildren()) {
                    Comment reply = new Comment(comment.getMarginLevelCoefficient() + 1);
                    net.dean.jraw.models.Comment commentJraw = aCommentNode.getComment();

                    reply.setId(commentJraw.getId());
                    reply.setAuthor(commentJraw.getAuthor());
                    reply.setTime(commentJraw.getCreated());
                    reply.setScore(commentJraw.getScore());
                    reply.setBody(commentJraw.getBody());
                    reply.setRepliesCount(aCommentNode.getChildren().size());
                    reply.setChildren(aCommentNode.getChildren());

                    Log.i(LOG_TAG, "--------RA : " + reply.getAuthor());

                    comments.add(parentPosition + 1 + position, reply);
                    position++;
                }

                adapter.notifyDataSetChanged();
            }
        }
    }

    private Comment getCommentById(String commentId) {

        for (Comment comment : comments) {
            if (comment != null && comment.getId().equals(commentId)) {
                return comment;
            }
        }

        return null;
    }
}
