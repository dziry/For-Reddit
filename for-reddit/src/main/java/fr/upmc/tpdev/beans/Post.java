package fr.upmc.tpdev.beans;

import android.support.annotation.NonNull;

import net.dean.jraw.models.VoteDirection;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * TODO
 * Created by Adel on 11/01/18.
 */

public class Post {

    private long currentTime;
    private String id;
    private String url;
    // Header
    private String subReddit;
    private String author;
    private String time;
    // Content
    private String title;
    private String contentText;
    private String contentThumbnail;
    // Footer
    private String scoreCount;
    private int voteDirection;
    private String commentCount;

    public Post() {
        Calendar c = Calendar.getInstance();
        this.currentTime = c.getTime().getTime();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSubReddit() {
        return subReddit;
    }

    public void setSubReddit(String subReddit) {
        this.subReddit = subReddit;
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

        if (this.time.startsWith("0")) {
            this.time = this.time.substring(1);
        }

        this.time += "h";
    }
    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getContentThumbnail() {
        return contentThumbnail;
    }

    public void setContentThumbnail(String contentThumbnail) {
        this.contentThumbnail = contentThumbnail;
    }

    public String getScoreCount() {
        return scoreCount;
    }

    public void setScoreCount(int scoreCount) {
        this.scoreCount = String.valueOf(scoreCount);
    }

    public void setScoreCount(String  scoreCount) {
        this.scoreCount = scoreCount;
    }

    public int getVoteDirection() {
        return voteDirection;
    }

    public void setVoteDirection(VoteDirection voteDirection) {
        this.voteDirection = voteDirection.getValue();
    }
    public void setVoteDirection(int voteDirection) {
        this.voteDirection = voteDirection;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = String.valueOf(commentCount);
    }

    public void setCommentCount(String  commentCount) {
        this.commentCount = commentCount;
    }
}
