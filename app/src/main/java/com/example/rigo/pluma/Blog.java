package com.example.rigo.pluma;

/**
 * Created by rigo on 10/24/17.
 */

public class Blog {

    private String title;
    private String body;

    public Blog() {}

    public Blog(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
