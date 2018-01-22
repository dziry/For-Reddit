package fr.upmc.tpdev.beans;

import android.support.annotation.NonNull;

import net.dean.jraw.models.CommentNode;
import net.dean.jraw.models.VoteDirection;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Comment {

    private long currentTime;
    private String id;
    private String author;
    private String time;
    private String body;
    private String score;
    private String repliesCount;
    private int voteDirection;
    private ArrayList<Comment> replies;
    private List<CommentNode> children;
    private int marginLevelCoefficient;

    public Comment(@NonNull int marginLevelCoefficient) {
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

    public void setScore(int score) {
        this.score = String.valueOf(score);
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
        return repliesCount;
    }

    public String getRepliesCountString() {
        return repliesCount + " more " + (repliesCount.equals("1")? "reply" : "replies");
    }

    public int getRepliesCountInt() {
        return (repliesCount != null)? Integer.parseInt(repliesCount) : 0;
    }

    public void setRepliesCount(int repliesCount) {
        this.repliesCount = String.valueOf(repliesCount);
    }

    public void setRepliesCount(String repliesCount) {
        this.repliesCount = repliesCount;
    }

    public void setReplies(ArrayList<Comment> replies) {
        this.replies = replies;
    }

    public int getMarginLevelCoefficient() {
        return marginLevelCoefficient;
    }

    public void setChildren(List<CommentNode> children) {
        this.children = children;
    }

    public List<CommentNode> getChildren() {
        return children;
    }

    public void setMarginLevelCoefficient(int marginLevelCoefficient) {
        this.marginLevelCoefficient = marginLevelCoefficient;
    }
}
