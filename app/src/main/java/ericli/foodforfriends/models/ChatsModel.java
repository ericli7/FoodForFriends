package ericli.foodforfriends.models;

/**
 * Created by ericli on 11/29/2017.
 */

public class ChatsModel {
    private String User_Status;

    public ChatsModel() {

    }

    public String getUser_Status() {
        return User_Status;
    }

    public void setUser_Status(String user_Status) {
        User_Status = user_Status;
    }

    public ChatsModel(String user_status) {
        this.User_Status = user_status;
    }
}
