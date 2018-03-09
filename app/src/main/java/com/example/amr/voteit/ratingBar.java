package com.example.amr.voteit;

import android.media.Rating;

/**
 * Created by Amr on 3/3/2018.
 */

public class ratingBar {
    private String userEmail;
    private String userComment;
    private String userLocation;

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public String getUserComment() {
        return userComment;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void ating(String userEmail,String userComment,String userLocation){
        this.userEmail=userEmail;
        this.userComment=userComment;
        this.userLocation=userLocation;
    }

    public String getUserEmail(){
        return userEmail;
    }
    public void setUserEmail(String userEmail){
        this.userEmail=userEmail;
    }
}
