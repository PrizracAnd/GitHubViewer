package ru.demjanov_av.githubviewer.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by demjanov on 06.11.2018.
 */

public class RealmModelRep extends RealmObject {
    //-----Constants begin-------------------------------
    public final static String REP_ID = "repId";
    public final static String USER_ID = "userId";
    public final static String NAME_REP = "nameRep";
    //-----Constants end---------------------------------


    //-----Class variables begin-------------------------
    @PrimaryKey
    private String repId;
    private String userId;
    private String nameRep;
    //-----Class variables end---------------------------


    /////////////////////////////////////////////////////
    // Getters and Setters
    ////////////////////////////////////////////////////
    //-----Begin----------------------------------------
    public String getRepId() {
        return repId;
    }

    public void setRepId(String repId) {
        this.repId = repId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNameRep() {
        return nameRep;
    }

    public void setNameRep(String nameRep) {
        this.nameRep = nameRep;
    }
    //-----End------------------------------------------

}
