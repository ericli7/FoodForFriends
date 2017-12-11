package ericli.foodforfriends.models;

/**
 * Created by ericli on 11/29/2017.
 */

public class UsersModel {

    public String User_Name;
    public String User_Image;
    public String User_Status;
    public String User_thumb_Image;
    public boolean selected;


    public UsersModel() {
    }

    public UsersModel(String name, String image, String status, String thumb_image) {
        this.User_Name = name;
        this.User_Image = image;
        this.User_Status = status;
        this.User_thumb_Image = thumb_image;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getName() {
        return User_Name;
    }

    public void setName(String name) {
        this.User_Name = name;
    }

    public String getImage() {
        return User_Image;
    }

    public void setImage(String image) {
        this.User_Image = image;
    }

    public String getStatus() {
        return User_Status;
    }

    public void setStatus(String status) {
        this.User_Status = status;
    }

    public String getThumb_image() {
        return User_thumb_Image;
    }

    public void setThumb_image(String thumb_image) {
        this.User_thumb_Image = thumb_image;
    }

}
