package fr.upmc.tpdev.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import fr.upmc.tpdev.R;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class PostActivity extends AppCompatActivity {

    private final String LOG_TAG = "PostActivity";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mSubreddit = findViewById(R.id.tv_subreddit);
        mAuthor = findViewById(R.id.tv_op);
        //mTime = findViewById(R.id.tv_time);
        mTitle = findViewById(R.id.tv_title);
        mContentText = findViewById(R.id.tv_self_text);
        mUrl = findViewById(R.id.tv_link);
        mScoreCount = findViewById(R.id.tv_votes);
        mCommentsCount = findViewById(R.id.tv_comments);
        mContentImage = findViewById(R.id.iv_content);
        mLayoutLink = findViewById(R.id.rl_link);

        preparePost();
    }

    private void preparePost() {
        Intent intent = getIntent();

        String id = intent.getStringExtra("id");
        String subReddit = "r/" + intent.getStringExtra("subReddit");
        String author = "u/" + intent.getStringExtra("author");
        String time = intent.getStringExtra("time");

        String title = intent.getStringExtra("title");

        String scoreCount = intent.getStringExtra("scoreCount");
        String commentCount = intent.getStringExtra("commentCount");
        int voteDirection = intent.getIntExtra("voteDirection", -1);

        mSubreddit.setText(subReddit);
        mAuthor.setText(author);
        mTitle.setText(title);
        //mTime.setText(time);

        setPostContent();

        mScoreCount.setText(scoreCount);
        mCommentsCount.setText(commentCount);

        /*Log.i(LOG_TAG, "id                  = " + id);
        Log.i(LOG_TAG, "subReddit           = " + subReddit);
        Log.i(LOG_TAG, "author              = " + author);
        Log.i(LOG_TAG, "time                = " + time);
        Log.i(LOG_TAG, "title               = " + title);
        Log.i(LOG_TAG, "contentText         = " + contentText);
        Log.i(LOG_TAG, "url                 = " + url);
        Log.i(LOG_TAG, "subscoreCountReddit = " + scoreCount);
        Log.i(LOG_TAG, "commentCount        = " + commentCount);
        Log.i(LOG_TAG, "voteDirection       = " + voteDirection);*/
    }

    private void setPostContent() {
        Intent intent = getIntent();

        String url = intent.getStringExtra("url");
        String contentText = intent.getStringExtra("contentText");

        if (url != null && !isSelfLink(url)) {
            new PostContentTask(url).execute();

        } else if (contentText != null) {
            mContentText.setText(contentText);
            mContentText.setVisibility(View.VISIBLE);
            //mLayoutLink.setVisibility(View.GONE);

        }/* else {
            mLayoutLink.setVisibility(View.GONE);
        }*/
    }

    private boolean isSelfLink(String url) {

        return url.contains("www.reddit.com");
    }

    private class PostContentTask extends AsyncTask<Integer, Void, Integer> {

        private ProgressBar mLoadPost;
        private String url;

        PostContentTask(String url) {
            super();
            this.mLoadPost = findViewById(R.id.pb_load_post);
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            this.mLoadPost.setVisibility(View.VISIBLE);
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
                Glide.with(mContentImage.getContext())
                        .load(url)
                        .asGif()
                        .error(R.drawable.ic_broken_image)
                        .into(mContentImage);
                mContentImage.setVisibility(View.VISIBLE);

            } else if (content == 2) {
                Log.i(LOG_TAG, "--------IMAGE-------");
                Picasso.with(mContentImage.getContext())
                        .load(url)
                        .transform(new RoundedCornersTransformation(10,0))
                        .into(mContentImage);
                mContentImage.setVisibility(View.VISIBLE);

            } else {
                mUrl.setText(url);
                mLayoutLink.setVisibility(View.VISIBLE);
            }

            this.mLoadPost.setVisibility(View.GONE);
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
