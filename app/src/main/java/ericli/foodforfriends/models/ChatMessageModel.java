package ericli.foodforfriends.models;
/**
 * Created by ericli on 11/29/2017.
 */


public class ChatMessageModel {

    private String message, type,deletekey;
    private long  time;
    private boolean seen;

    private String from;

    private String imageUrl;

    public ChatMessageModel(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public ChatMessageModel(String message, String type, long time, boolean seen) {
        this.message = message;
        this.type = type;
        this.time = time;
        this.seen = seen;
    }

    public ChatMessageModel(String message, String type, String deletekey, long time, boolean seen, String from, String imageUrl) {
        this.message = message;
        this.type = type;
        this.deletekey = deletekey;
        this.time = time;
        this.seen = seen;
        this.from = from;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDeletekey() {
        return deletekey;
    }

    public void setDeletekey(String deletekey) {
        this.deletekey = deletekey;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public ChatMessageModel(){

    }

}
