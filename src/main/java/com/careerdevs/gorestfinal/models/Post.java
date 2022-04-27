package com.careerdevs.gorestfinal.models;

import javax.persistence.*;

@Entity
public class Post {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private long id;
    private String title;

    @Column(length = 512)
    private String body;


    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public void setTitle() {
        this.title = title;
    }

    public void setBody() {
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
