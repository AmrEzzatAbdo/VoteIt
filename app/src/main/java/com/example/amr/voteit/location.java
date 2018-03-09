package com.example.amr.voteit;

/**
 * Created by Amr on 3/5/2018.
 */

public class location {
    private String lname;
    private long ratingnum;
    private String image;
    private String comment;
    private String email;


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private String location;

    public location(String lname, long ratingnum, String image) {
        this.lname = lname;
        this.ratingnum = ratingnum;
        this.image = image;
    }

    public location() {
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getRatingnum() {
        return ratingnum;
    }

    public void setRatingnum(long ratingnum) {
        this.ratingnum = ratingnum;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
