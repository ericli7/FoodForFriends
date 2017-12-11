package ericli.foodforfriends.models;
/**
 * Created by ericli on 11/29/2017.
 */

public class GroupMessagesModel {


    String message, uid, imageUrl;

    long time;


    public GroupMessagesModel() {

    }

    public GroupMessagesModel(String message, long time, String uid) {
        this.message = message;
        this.time = time;
        this.uid = uid;
    }

    public GroupMessagesModel(String message, long time, String uid, String imageUrl) {
        this.message = message;
        this.time = time;
        this.uid = uid;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
