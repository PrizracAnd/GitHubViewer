package ru.demjanov_av.githubviewer.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by demjanov on 06.11.2018.
 */

public class RetrofitModelRep
{
    public static final String LOGIN = "login";
    public static final String ID = "id";
    public static final String AVATAR_URL = "avatar_url";

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    /////////////////////////////////////////////////////
    // Getters and Setters
    ////////////////////////////////////////////////////
    //-----Begin----------------------------------------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //-----End------------------------------------------
}
