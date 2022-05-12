package com.careerdevs.gorestfinal.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Todos {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private long user_id;

    private String title;

    private Date due_on;

    private String status;

    public long getId() {
        return id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDue_on() {
        return due_on;
    }

    public void setDue_on(Date due_on) {
        this.due_on = due_on;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
