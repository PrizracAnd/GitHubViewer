package ru.demjanov_av.githubviewer.presenters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import ru.demjanov_av.githubviewer.db.QueryUsers;
import ru.demjanov_av.githubviewer.injector.ContextProvider;
import ru.demjanov_av.githubviewer.injector.db.DaggerInjectorRealm;
import ru.demjanov_av.githubviewer.models.RealmModelUser;
import ru.demjanov_av.githubviewer.models.RetrofitModel;
import ru.demjanov_av.githubviewer.network.Caller;
import ru.demjanov_av.githubviewer.views.MoreUsersFragment;

/**
 * Created by demjanov on 11.09.2018.
 */

public class MoreUsersPresenter  extends MyPresenter {
    //-----Constants begin-------------------------------
    private final static String MORE_USERS_PRESENTER = "MORE_USERS_PRESENTER:";
    //-----Constants end---------------------------------


    //-----Class variables begin-------------------------
    private Context context;
    private MoreUsersFragment moreUsersFragment;
    //-----Class variables end---------------------------


    //-----Other variables begin-------------------------
    private boolean isDownload = false;
    private List<RetrofitModel> retrofitModelList;
    private List<RealmModelUser> realmModelUsers;
    private RealmResults<RealmModelUser> realmResults;
    private String previous_id = "0";
    private QueryUsers queryUsers;
    //-----Other variables end---------------------------


    //-----Injection variables begin---------------------
    @Inject
    Realm realm;

    @Inject
    RealmConfiguration realmConfiguration;
    //-----Injection variables end---------------------------


    /////////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////////
    public MoreUsersPresenter(MoreUsersFragment moreUsersFragment, Context context) {
        this.moreUsersFragment = moreUsersFragment;
        this.context = context;

        DaggerInjectorRealm.builder()
                .contextProvider(new ContextProvider(context))
                .build()
                .injectToMoreUsersPresenter(this);
        this.queryUsers = new QueryUsers(this.realmConfiguration, this);

        this.realmResults = this.realm.where(RealmModelUser.class).findAll();

//        updateLoadedUsers(this.realmResults.get(this.realmResults.size() - 1).getId());
        if(this.realmResults.size() > 0) {
            this.previous_id = this.realmResults.get(this.realmResults.size() - 1).getId();
        }else{
            downloadUsers();
        }
    }


    /////////////////////////////////////////////////////
    // Methods for Callers callings
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    @Override
    public void onResp(List<RetrofitModel> retrofitModelList, Boolean isDownload, @Nullable String message) {
        this.retrofitModelList = retrofitModelList;
        this.isDownload = isDownload;
        //-----Call method, wot may be deprecated!!!----{
        String prev_id = getLastID();
        if(prev_id != null) {
            this.previous_id = prev_id;
        }
        //}----------------------------------------------

        this.queryUsers.insertUsersData(retrofitModelList);

        this.moreUsersFragment.endLoad();

        updateLoadedUsers(this.realmResults.get(this.realmResults.size() - 1).getId());
    }


    @Override
    public void onFail(Boolean isDownload, int codeMessage, @Nullable String message) {
        this.isDownload = isDownload;
        this.moreUsersFragment.endLoad();
        this.moreUsersFragment.setError(codeMessage, message);
    }
    //-----End-------------------------------------------


    /////////////////////////////////////////////////////
    // Method downloadUsers
    ////////////////////////////////////////////////////
    public void downloadUsers(){
        moreUsersFragment.startLoad();
        Caller caller = new Caller(this.context, this);
        this.isDownload = true;

//        try {
          caller.downloadUsers(previous_id);
//        }catch (IOException e){
//            onFail(false, Caller.ON_FAILURE, e.getMessage());
//        }
    }


    /////////////////////////////////////////////////////
    // Method getLastID
    ////////////////////////////////////////////////////
    @Nullable
    private String getLastID() {
        if(this.retrofitModelList.size() > 0) {
            return this.retrofitModelList.get(this.retrofitModelList.size() - 1).getId();
        }else return null;
    }


    /////////////////////////////////////////////////////
    // Method updateLoadedUsers
    ////////////////////////////////////////////////////
    private void updateLoadedUsers(String realmMaxId){
        Long lastRecord = strToLong(realmMaxId);
        if(strToLong(this.previous_id) < lastRecord){
            downloadUsers();
        }
    }


    /////////////////////////////////////////////////////
    // Method strToLong
    ////////////////////////////////////////////////////
    private Long strToLong(String str){
        Long res = -1L;
        try {
            res = new Long(str);
        }catch (NumberFormatException e){
            Log.d(MORE_USERS_PRESENTER, e.getMessage());
        }

        return res;
    }


    /////////////////////////////////////////////////////
    // Method getUsers
    ////////////////////////////////////////////////////
    @Nullable
    public String getUsers(){

//        if (this.retrofitModelList.size() < 1){
//            return null;
//        }
//
//        StringBuilder sb = new StringBuilder();
//        for(RetrofitModel item: this.retrofitModelList) {
//            sb.append(RetrofitModel.LOGIN + ":\t");
//            sb.append(item.getLogin());
//            sb.append("\n");
//            sb.append(RetrofitModel.ID +":\t");
//            sb.append(item.getId());
//            sb.append("\n");
//            sb.append(RetrofitModel.AVATAR_URL +":\t");
//            sb.append(item.getAvatarUrl());
//            sb.append("\n");
//            sb.append("---*****---");
//            sb.append("\n");
//            sb.append("\n");
//        }
//        return sb.toString();


//        **********************************************************

        if(this.realmModelUsers == null){
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (RealmModelUser item: this.realmModelUsers){
            sb.append(RetrofitModel.LOGIN + ":\t");
            sb.append(item.getLogin());
            sb.append("\n");
            sb.append(RetrofitModel.ID +":\t");
            sb.append(item.getId());
            sb.append("\n");
            sb.append(RetrofitModel.AVATAR_URL +":\t");
            sb.append(item.getAvatarUrl());
            sb.append("\n");
            sb.append("---*****---");
            sb.append("\n");
            sb.append("\n");
        }
        return sb.toString();


    }


    /////////////////////////////////////////////////////
    // Method getResults
    ////////////////////////////////////////////////////
    public RealmResults<RealmModelUser> getResults(){
//        return this.realm.where(RealmModelUser.class).findAll();
        return this.realmResults;
    }


    /////////////////////////////////////////////////////
    // Methods for QueryUsers callings
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    @Override
    public void onCompleteQueryUsers(int codeOperation, @Nullable List<RealmModelUser> realmModelUsers) {
        switch (codeOperation){
            case 0:
                this.realmModelUsers = realmModelUsers;
                this.moreUsersFragment.setData(0);
                break;
            case 1:
                this.queryUsers.selectAllUsers();
                break;
            default:
                 break;
        }
    }

    @Override
    public void onErrorQueryUsers(String message) {

    }
    //-----End-------------------------------------------


    /////////////////////////////////////////////////////
    // Method destroy
    ////////////////////////////////////////////////////
    public void destroy(){
        this.queryUsers.destroy();
        this.realm.close();
    }

}
