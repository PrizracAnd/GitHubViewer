package ru.demjanov_av.githubviewer.models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RetrofitModel {
    public static final String LOGIN = "login";
    public static final String ID = "id";
    public static final String AVATAR_URL = "avatar_url";

    @SerializedName("login")
    @Expose
    private String login;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("avatar_url")
    @Expose
    private String avatarUrl;

    /////////////////////////////////////////////////////
    // Getters and Setters
    ////////////////////////////////////////////////////
    //-----Begin----------------------------------------
    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getAvatarUrl() {
        return avatarUrl;
    }
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    //-----End------------------------------------------

}
