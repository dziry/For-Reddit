package fr.upmc.tpdev.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import net.dean.jraw.RedditClient;
import net.dean.jraw.auth.AuthenticationManager;
import net.dean.jraw.http.HttpRequest;
import net.dean.jraw.http.RestResponse;
import net.dean.jraw.models.Comment;
import net.dean.jraw.models.CommentNode;
import net.dean.jraw.models.Submission;
import fr.upmc.tpdev.R;
import fr.upmc.tpdev.beans.Post;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static net.dean.jraw.util.JrawUtils.mapOf;

public class UserInfoActivity extends AppCompatActivity {

    static final String LOG_TAG = "UserInfoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        /*RedditClient redditClient = AuthenticationManager.get().getRedditClient();
        if (redditClient.me() != null) {
            Log.i(LOG_TAG, "CONNECTED !");

        } else {
            Log.i(LOG_TAG, "oups");
        }*/

        new RedditInfoTask().execute();

        /*new AsyncTask<Void, Void, LoggedInAccount>() {
            @Override
            protected LoggedInAccount doInBackground(Void... params) {
                return AuthenticationManager.get().getRedditClient().me();
                //return AuthenticationManager.get().getRedditClient().me().getDataNode().toString();
            }

            @Override
            protected void onPostExecute(LoggedInAccount data) {
                ((TextView) findViewById(R.id.user_created)).setText("Created: " + data.getCreated());
                ((TextView) findViewById(R.id.user_link_karma)).setText("Link karma: " + data.getLinkKarma());
                ((TextView) findViewById(R.id.user_comment_karma)).setText("Comment karma: " + data.getCommentKarma());
                ((TextView) findViewById(R.id.user_has_mail)).setText("Has mail? " + (data.getInboxCount() > 0));
                ((TextView) findViewById(R.id.user_inbox_count)).setText("Inbox count: " + data.getInboxCount());
                ((TextView) findViewById(R.id.user_is_mod)).setText("Is mod? " + data.isMod());
            }
        }.execute();*/
    }

    private static class RedditInfoTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            RedditClient redditClient = AuthenticationManager.get().getRedditClient();

            Log.i(LOG_TAG, "N !" + redditClient.me().getFullName());
            Log.i(LOG_TAG, "N !" + redditClient.me().getCommentKarma());

            //meSubreddits(redditClient);

            //meRandomSubmissions(redditClient);
            //getComments(redditClient, "7qel3h"); // IAmA
            //submissionDetails(redditClient, "7iu53s"); // LifeProTips
            //submissionDetails(redditClient, "7irlbb"); // news
            //submissionDetails(redditClient, "7iwp77"); // gifs
            //submissionDetails(redditClient, "7iwk06"); // funny

            /*try {
                subscribe(redditClient);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }*/

            return null;
        }
    }

    /* get all subs for a user */
    private static void meSubreddits(RedditClient redditClient) {

        final String[] SUBREDDIT_TAB = new String[]{"funny", "AskReddit", "todayilearned", "science", "worldnews", "pics", "IAmA", "gaming", "videos", "movies", "aww", "Music", "blog", "gifs", "news", "explainlikeimfive", "askscience", "EarthPorn", "books", "television", "mildlyinteresting", "LifeProTips"};
        List<String> userSubs = new ArrayList<>();
        for (String aSubreddit : SUBREDDIT_TAB) {
            if (redditClient.getSubreddit(aSubreddit).isUserSubscriber()) {
                userSubs.add(aSubreddit);
            }
        }

        Log.i(LOG_TAG, "All subs for a user :");
        for (String aSubreddit : userSubs) {
            Log.i(LOG_TAG, aSubreddit);
        }
    }

    private static final int FEED_SIZE = 5;
    //private static List<String> userSubreddits = Arrays.asList("funny", "LifeProTips", "pics", "gifs", "news");
    private static List<String> userSubreddits = Arrays.asList("IAmA", "nosleep");
    //private static List<String> userSubreddits = Arrays.asList("gifs", "pics");
    private static final int USER_SUBREDDITS_SIZE = userSubreddits.size();

    /* get 5 random submissions from the user's subreddits */
    public static ArrayList<Post> meRandomUserSubmissions(RedditClient redditClient) {

        if (redditClient == null) {
            return new ArrayList<>();
        }

        ArrayList<Post> posts = new ArrayList<>();

        for (int i = 0; i < FEED_SIZE; i++) {

            int randomIndex = (int)(Math.random() * USER_SUBREDDITS_SIZE);
            String subredditName = userSubreddits.get(randomIndex);

            // API bug : this may throws a null exception
            Submission sub = redditClient.getRandomSubmission(subredditName);
            Post post = preparePost(sub);
            posts.add(post);
        }

        return posts;
    }

    /* get 5 random submissions from global subreddits */
    public static ArrayList<Post> meRandomGlobalSubmissions(RedditClient redditClient) {

        if (redditClient == null) {
            return new ArrayList<>();
        }

        ArrayList<Post> posts = new ArrayList<>();

        for (int i = 0; i < FEED_SIZE; i++) {

            // API bug : this may throws a null exception
            Submission sub = redditClient.getRandomSubmission();
            Post post = preparePost(sub);
            posts.add(post);
        }

        return posts;
    }

    /* get 5 random submissions from a specific subreddits */
    public static ArrayList<Post> meRandomSpecificSubmissions(RedditClient redditClient
            , String subredditName) {

        if (redditClient == null) {
            return new ArrayList<>();
        }

        ArrayList<Post> posts = new ArrayList<>();

        for (int i = 0; i < FEED_SIZE; i++) {

            // API bug : this may throws a null exception
            Submission sub = redditClient.getRandomSubmission(subredditName);
            Post post = preparePost(sub);
            posts.add(post);
        }

        return posts;
    }

    private static Post preparePost(Submission sub) {
        Post post = new Post();

        post.setId(sub.getId());
        post.setUrl(sub.getUrl());
        post.setSubReddit(sub.getSubredditName());
        post.setAuthor(sub.getAuthor());
        post.setTime(sub.getCreated());
        post.setTitle(sub.getTitle());
        post.setContentText(sub.getSelftext());
        post.setContentThumbnail(sub.getThumbnail());
        post.setScoreCount(sub.getScore());
        post.setVoteDirection(sub.getVote());
        post.setCommentCount(sub.getCommentCount());

        return post;
    }

    /*static int index = 0;
    public static ArrayList<Post> meRandomSubmissions(RedditClient redditClient) {

        if (redditClient == null) {
            return new ArrayList<>();
        }

        ArrayList<Post> posts = new ArrayList<>();
        List<String> userSubreddits = Arrays.asList("funny", "LifeProTips", "pics", "gifs", "news");
        final int USER_SUBREDDITS_SIZE = userSubreddits.size();
        final int FEED_SIZE = 10;

        for (int i = index; i < FEED_SIZE + index; i++) {
            String subredditName = userSubreddits.get(i % USER_SUBREDDITS_SIZE);
            // API bug : this may throws a null exception
            Submission sub = redditClient.getRandomSubmission(subredditName);

            Post post = new Post();

            post.setId("" + i);
            post.setUrl("" + i);
            post.setSubReddit("" + i);
            post.setAuthor("" + i);
            post.setTime(sub.getCreated());
            post.setTitle("" + i);
            post.setContentThumbnail(sub.getThumbnail());
            post.setScoreCount(i);
            post.setVoteDirection(sub.getVote());
            post.setCommentCount(i);

            posts.add(post);
        }

        index += 10;

        return posts;
    }*/

    /* get submission details */
    private static void submissionDetails(RedditClient redditClient, String id) {
        Submission sub = redditClient.getSubmission(id);

        Log.i(LOG_TAG, "subName    = " + sub.getSubredditName());
        Log.i(LOG_TAG, "id         = " + sub.getId());
        Log.i(LOG_TAG, "title      = " + sub.getTitle());
        Log.i(LOG_TAG, "thumbnail  = " + sub.getThumbnail());
        Log.i(LOG_TAG, "postHint   = " + sub.getPostHint());
        Log.i(LOG_TAG, "url        = " + sub.getUrl());
        Log.i(LOG_TAG, "score      = " + sub.getScore());
        Log.i(LOG_TAG, "vote      = " + sub.getVote().getValue());
        Log.i(LOG_TAG, "commentsC  = " + sub.getCommentCount());

        //Log.i(LOG_TAG, "Comments :");
        //getComments(sub.getComments());
    }
    
    public static ArrayList<fr.upmc.tpdev.beans.Comment> getComments(RedditClient redditClient,
                                                                     String id) {

        Submission sub = redditClient.getSubmission(id);
        CommentNode commentNode = sub.getComments();
        ArrayList<fr.upmc.tpdev.beans.Comment> comments = new ArrayList<>();

        for (CommentNode aCommentNode : commentNode) {
            net.dean.jraw.models.Comment commentJraw = aCommentNode.getComment();
            Log.i(LOG_TAG, "body         = " + commentJraw.getBody());
            Log.i(LOG_TAG, "author       = " + commentJraw.getAuthor());
            Log.i(LOG_TAG, "created      = " + commentJraw.getCreated());
            Log.i(LOG_TAG, "score        = " + commentJraw.getScore());
            Log.i(LOG_TAG, "vote         = " + commentJraw.getVote().getValue());
            Log.i(LOG_TAG, "--------------------------------------------------------");
            Log.i(LOG_TAG, aCommentNode.getChildren().size() + " replay(ies)");
            Log.i(LOG_TAG, "--------------------------------------------------------");

            //expandReplies(aCommentNode.getChildren());

            fr.upmc.tpdev.beans.Comment comment = new fr.upmc.tpdev.beans.Comment(0);
            comment.setId(commentJraw.getId());
            comment.setAuthor(commentJraw.getAuthor());
            comment.setTime(commentJraw.getCreated());
            comment.setScore(commentJraw.getScore());
            comment.setBody(commentJraw.getBody());
            comment.setRepliesCount(aCommentNode.getChildren().size());
            comment.setChildren(aCommentNode.getChildren());

            comments.add(comment);
        }

        return comments;
    }

    private static void expandReplies(List<CommentNode> replies) {
        if (replies.size() > 0) {
            for (CommentNode aCommentNode : replies) {
                net.dean.jraw.models.Comment comment = aCommentNode.getComment();
                Log.i(LOG_TAG, "rep-body = " + comment.getBody());
                Log.i(LOG_TAG, "*****************************************************");
                Log.i(LOG_TAG, aCommentNode.getChildren().size() + " replay(ies)");
                Log.i(LOG_TAG, "*****************************************************");
            }
        }
    }

    /* get comments of a submission */
    /*private static void getComments(CommentNode commentNode) {
        if (commentNode.getDepth() > 0) {
            for (CommentNode aCommentNode : commentNode) {
                showComments(aCommentNode);
            }
        } else {
            Log.i(LOG_TAG, "No comment found");
        }
    }*/

    /* get replies of a comment */
    /*private static void expandReplies(List<CommentNode> replies) {
        if (replies.size() > 0) {
            for (CommentNode aCommentNode : replies) {
                showComments(aCommentNode);
            }
        }
    }*/

    /*private static void showComments(CommentNode commentNode) {
        if (commentNode.getDepth() > 0) {
            for (CommentNode aCommentNode : commentNode) {
                Comment comment = aCommentNode.getComment();
                Log.i(LOG_TAG, "body         = " + comment.getBody());
                Log.i(LOG_TAG, "author       = " + comment.getAuthor());
                Log.i(LOG_TAG, "created      = " + comment.getCreated());
                Log.i(LOG_TAG, "score        = " + comment.getScore());
                Log.i(LOG_TAG, "vote         = " + comment.getVote().getValue());
                Log.i(LOG_TAG, "--------------------------------------------------------");
                Log.i(LOG_TAG, aCommentNode.getChildren().size() + " replay(ies)");
                Log.i(LOG_TAG, "--------------------------------------------------------");
                //expandReplies(aCommentNode.getChildren());
            }
        }
    }*/
}
