package fr.upmc.tpdev.beans;

import android.support.annotation.NonNull;

import net.dean.jraw.models.VoteDirection;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Comment {

    private long currentTime;
    private String id;
    private String author;
    private String time;
    private String body;
    private String score;
    private int voteDirection;
    private ArrayList<Comment> replies;
    private int marginLevelCoefficient;

    public Comment(int marginLevelCoefficient) {
        Calendar c = Calendar.getInstance();
        this.currentTime = c.getTime().getTime();
        this.replies = new ArrayList<>();
        this.marginLevelCoefficient = marginLevelCoefficient;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTime() {
        return time;
    }

    public void setTime(@NonNull Date time) {
        long tmpTime = currentTime - time.getTime();
        DateFormat formatter = new SimpleDateFormat("HH", Locale.FRANCE);
        this.time = formatter.format(tmpTime);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public int getVoteDirection() {
        return voteDirection;
    }

    public void setVoteDirection(VoteDirection voteDirection) {
        this.voteDirection = voteDirection.getValue();
    }

    public ArrayList<Comment> getReplies() {
        return replies;
    }

    public String getRepliesCount() {
        return String.valueOf(replies.size());
    }

    public void setReplies(ArrayList<Comment> replies) {
        this.replies = replies;
    }

    public int getMarginLevelCoefficient() {
        return marginLevelCoefficient;
    }

    public void setMarginLevelCoefficient(int marginLevelCoefficient) {
        this.marginLevelCoefficient = marginLevelCoefficient;
    }
}
