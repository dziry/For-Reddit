package fr.upmc.tpdev.beans;

/**
 * TODO
 * Created by Adel on 11/01/18.
 */

public class Post {

    // Header
    private String subReddit;
    private String op;
    private String time;
    private String headerThumbnail;
    // Content
    private String title;
    private String contentThumbnail;
    // Footer
    private String upvotesCount;
    private String commentsCount;

    public Post(String subReddit, String op, String time, String headerThumbnail, String title,
                String contentThumbnail, String upvotesCount, String commentsCount) {

        this.subReddit = subReddit;
        this.op = op;
        this.time = time;
        this.headerThumbnail = headerThumbnail;
        this.title = title;
        this.contentThumbnail = contentThumbnail;
        this.upvotesCount = upvotesCount;
        this.commentsCount = commentsCount;
    }

    public String getSubReddit() {
        return subReddit;
    }

    public void setSubReddit(String subReddit) {
        this.subReddit = subReddit;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHeaderThumbnail() {
        return headerThumbnail;
    }

    public void setHeaderThumbnail(String headerThumbnail) {
        this.headerThumbnail = headerThumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentThumbnail() {
        return contentThumbnail;
    }

    public void setContentThumbnail(String contentThumbnail) {
        this.contentThumbnail = contentThumbnail;
    }

    public String getUpvotesCount() {
        return upvotesCount;
    }

    public void setUpvotesCount(String upvotesCount) {
        this.upvotesCount = upvotesCount;
    }

    public String getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(String commentsCount) {
        this.commentsCount = commentsCount;
    }
}
