package ru.demjanov_av.githubviewer.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmModelUser extends RealmObject{
    //-----Constants begin-------------------------------
    public final static String LOGIN = "login";
    public final static String ID = "id";
    public final static String AVATAR_URL = "avatarUrl";
    //-----Constants end---------------------------------


    //-----Class variables begin-------------------------
    private String login;
    @PrimaryKey
    private String id;
    private String avatarUrl;
    //-----Class variables end---------------------------


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
