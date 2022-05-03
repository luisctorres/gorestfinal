package com.careerdevs.gorestfinal.models;

import javax.persistence.*;

@Entity
public class Post {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private long id;

    private long user_id;

    private String title;

    @Column(length = 512)
    private String body;

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
