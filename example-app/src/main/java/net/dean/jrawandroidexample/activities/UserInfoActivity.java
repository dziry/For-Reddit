package net.dean.jrawandroidexample.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import net.dean.jraw.RedditClient;
import net.dean.jraw.auth.AuthenticationManager;
import net.dean.jraw.models.Comment;
import net.dean.jraw.models.CommentNode;
import net.dean.jraw.models.Submission;
import net.dean.jrawandroidexample.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserInfoActivity extends AppCompatActivity {

    static final String LOG_TAG = "UserInfoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

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

            //meSubreddits(redditClient);
            meRandomSubmissions(redditClient);
            //submissionDetails(redditClient, "7iu53s"); // LifeProTips
            //submissionDetails(redditClient, "7irlbb"); // news
            //submissionDetails(redditClient, "7iwp77"); // gifs
            //submissionDetails(redditClient, "7iwk06"); // funny

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

    /* get 10 random submissions from the user's subreddits */
    private static void meRandomSubmissions(RedditClient redditClient) {

        List<String> userSubreddits = Arrays.asList("funny", "LifeProTips", "pics", "gifs", "news");
        final int USER_SUBREDDITS_SIZE = userSubreddits.size();
        final int FEED_SIZE = 10;

        for (int i = 0; i < FEED_SIZE; i++) {
            String subredditName = userSubreddits.get(i % USER_SUBREDDITS_SIZE);
            Submission sub = redditClient.getRandomSubmission(subredditName);

            Log.i(LOG_TAG, "subName   = " + subredditName);
            Log.i(LOG_TAG, "id        = " + sub.getId());
            Log.i(LOG_TAG, "title     = " + sub.getTitle());
            Log.i(LOG_TAG, "thumbnail = " + sub.getThumbnail());
            Log.i(LOG_TAG, "postHint  = " + sub.getPostHint());
            Log.i(LOG_TAG, "url       = " + sub.getUrl());
            Log.i(LOG_TAG, "score     = " + sub.getScore());
            Log.i(LOG_TAG, "vote      = " + sub.getVote());
            Log.i(LOG_TAG, "comments  = " + sub.getCommentCount());
            Log.i(LOG_TAG, "--------------------------------------------------------------");
        }
    }

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

        Log.i(LOG_TAG, "Comments :");
        getComments(sub.getComments());
    }
    
    private static void getComments(CommentNode commentNode) {

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

            expandReplies(aCommentNode.getChildren());
        }
    }

    private static void expandReplies(List<CommentNode> replies) {
        if (replies.size() > 0) {
            for (CommentNode aCommentNode : replies) {
                Comment comment = aCommentNode.getComment();
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
    
    private static void meNotifications() {
        //TODO
    }
    
    private static void meMessages() {
        //TODO
    }
    
    private static void meModMails() {
        //TODO
    }
    
    private static void mePosts() {
        //TODO
    }
    
    private static void meComments() {
        //TODO
    }
    
    /* show data such as name, karma, reddit age */
    private static void mePersonalData() {
        //TODO
    }
    
    private static void meHistory() {
        //TODO
    }
    
    private static void meSaved() {
        //TODO
    }
    
    private static void meUpvoted() {
        //TODO
    }
    
    private static void meHidden() {
        //TODO
    }
    
    private static void upvotePost() {
        //TODO
    }
    
    private static void downvotePost() {
        //TODO
    }
    
    private static void sharePost() {
        //TODO
    }    

    private static void upvoteComment() {
        //TODO
    }
    
    private static void downvoteComment() {
        //TODO
    }
    
    private static void postImageVideo() {
        //TODO
    }
    
    private static void postText() {
        //TODO
    }
    
    private static void postLink() {
        //TODO
    }
    
    private static void addComment() {
        //TODO
    }
    
    private static void replayToComment() {
        //TODO
    }
    
    private static void subscribe() {
        //TODO
    }    
    
    private static void unsubscribe() {
        //TODO
    }
}
