package ru.demjanov_av.githubviewer.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmModelUser extends RealmObject{
    private String login;
    @PrimaryKey
    private String id;
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
