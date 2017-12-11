package ericli.foodforfriends.models;

/**
 * Created by ericli on 11/29/2017.
 */
public class RequestsModel {
    private String User_Name;
    private String User_Status;
    private String User_thumb_Image;

    public RequestsModel(){

    }

    public RequestsModel(String user_Name, String user_Status, String user_thumb_Image) {
        User_Name = user_Name;
        User_Status = user_Status;
        User_thumb_Image = user_thumb_Image;
    }

    public String getUser_Name() {
        return User_Name;
    }

    public void setUser_Name(String user_Name) {
        User_Name = user_Name;
    }

    public String getUser_Status() {
        return User_Status;
    }

    public void setUser_Status(String user_Status) {
        User_Status = user_Status;
    }

    public String getUser_thumb_Image() {
        return User_thumb_Image;
    }

    public void setUser_thumb_Image(String user_thumb_Image) {
        User_thumb_Image = user_thumb_Image;
    }
}
