package fr.upmc.tpdev.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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
import fr.upmc.tpdev.adapters.PostCardAdapter;
import fr.upmc.tpdev.adapters.PostDetailsAdapter;
import fr.upmc.tpdev.beans.Comment;
import fr.upmc.tpdev.beans.Post;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class PostActivity extends AppCompatActivity {

    private final String LOG_TAG = "PostActivity";

    private PostDetailsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        RecyclerView recyclerView = findViewById(R.id.rv_post_and_comments);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<Comment> comments = new ArrayList<>();
        comments.add(null);

        for (int i = 0; i < 10; i++) {
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
        }

        Post post = preparePost();
        adapter = new PostDetailsAdapter(recyclerView, post, comments);
        recyclerView.setAdapter(adapter);

        Comment comment = new Comment(1);
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

        adapter.notifyDataSetChanged();

        /*adapter.setOnPostCardClickListener(this);

        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        fetchPosts(view, sectionNumber);*/
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
}
