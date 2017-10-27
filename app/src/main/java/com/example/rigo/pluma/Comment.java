package com.example.rigo.pluma;

/**
 * Created by rigo on 10/26/17.
 */

public class Comment {

    String body;
    String author;
    long date;

    public Comment()  {}

    public Comment(String body, String author, long date) {
        this.body = body;
        this.author = author;
        this.date = date;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
