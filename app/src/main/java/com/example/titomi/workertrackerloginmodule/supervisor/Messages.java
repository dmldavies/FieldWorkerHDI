package com.example.titomi.workertrackerloginmodule.supervisor;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by NeonTetras on 13-Feb-18.
 */
public class Messages extends Entity implements Serializable {

    public Messages() {

    }

    public Messages(long id,String title, String body) {
        this.title = title;
        this.body = body;
        setId(id);
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


    public User getPoster() {
        return poster;
    }

    public void setPoster(User poster) {
        this.poster = poster;
    }

    public Date getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(Date datePosted) {
        this.datePosted = datePosted;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipients) {
        this.recipient = recipients;
    }

    public String[] getRecipients() {
        return recipients;
    }

    public void setRecipients(String[] recipients) {
        this.recipients = recipients;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    private String title;
    private String body;
    private User poster;
    private User receiver;
    private Date datePosted;
    private String priority;
    private String recipient;
    private String[] recipients;
    private boolean read;

    protected static final long serialVersionUID = 1l;
}
