package ru.demjanov_av.githubviewer.presenters;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import ru.demjanov_av.githubviewer.db.QueryUsers;
import ru.demjanov_av.githubviewer.models.RealmModelRep;
import ru.demjanov_av.githubviewer.models.RealmModelUser;
import ru.demjanov_av.githubviewer.models.RetrofitModel;
import ru.demjanov_av.githubviewer.models.RetrofitModelRep;
import ru.demjanov_av.githubviewer.network.Caller;
import ru.demjanov_av.githubviewer.views.OneUsersFragment;

/**
 * Created by demjanov on 08.11.2018.
 */

public class OneUsersPresenter extends MyPresenter {
    //-----Constants begin-------------------------------
    private final static String ONE_USERS_PRESENTER = "ONE_USERS_PRESENTER:";
    //-----Constants end---------------------------------


    //-----Class variables begin-------------------------
    private Context context;
    private OneUsersFragment oneUsersFragment;
    private String userID;
    //-----Class variables end---------------------------


    //-----Other variables begin-------------------------
    private List<RetrofitModel> retrofitModelList;
    private List<RetrofitModelRep> retrofitModelRepList;
    private List<RealmModelUser> realmModelUsers;
    private List<RealmModelRep> realmModelReps;
    private QueryUsers queryUsers;
    //-----Other variables end---------------------------


    //-----Boolean variables begin-----------------------
    private boolean isDownloadUser = false;
    private boolean isDownloadRepos = false;

    private boolean isSelectUser = false;
    private boolean isSelectRepos = false;

    private boolean isDownloadNeed = true;
    //-----Boolean variables end-------------------------


    //-----Injection variables begin---------------------
    @Inject
    Realm realm;

    @Inject
    RealmConfiguration realmConfiguration;
    //-----Injection variables end---------------------------


    /////////////////////////////////////////////////////
    // Method downloadData
    ////////////////////////////////////////////////////
    public void downloadData(){
        if (realmModelUsers != null && realmModelUsers.size() > 0) {
            this.oneUsersFragment.startLoad();
            this.isDownloadRepos = true;
            this.isSelectUser = true;

            Caller caller = new Caller(this.context,this);

            String userName = this.realmModelUsers.get(0).getLogin();

            caller.downloadUser(userName);
            caller.downloadRepos(userName);

            this.isDownloadNeed = false;
        }
    }


    /////////////////////////////////////////////////////
    // Methods for Callers callings
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    @Override
    public void onResp(List<RetrofitModel> retrofitModelList, Boolean isDownload, @Nullable String message) {
        this.retrofitModelList = retrofitModelList;
        this.isDownloadUser = isDownload;
        this.isSelectUser = true;
        this.queryUsers.insertUsersData(retrofitModelList);
    }


    @Override
    public void onRespRepos(List<RetrofitModelRep> retrofitModelRepList, Boolean isDownload, @Nullable String message) {
        this.retrofitModelRepList = retrofitModelRepList;
        this.isDownloadRepos = isDownload;

        this.isSelectRepos = true;
        this.queryUsers.insertReposData(retrofitModelRepList);
    }


    @Override
    public void onFail(Boolean isDownload, int codeMessage, @Nullable String message) {
        this.isDownloadUser = isDownload;
        this.isDownloadRepos = isDownload;
        this.oneUsersFragment.endLoad();
        this.oneUsersFragment.setError(codeMessage, message); //FIXME!!!!!!!!!!!!!!*****!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    }
    //-----End-------------------------------------------


    /////////////////////////////////////////////////////
    // Methods for QueryUsers callings
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    @Override
    public void onCompleteQueryUsers(int codeOperation, @Nullable List<RealmModelUser> realmModelUsers) {
        switch (codeOperation){
            case QueryUsers.SELECT:
                this.realmModelUsers = realmModelUsers;
                this.isSelectUser = false;
                onSelectedData();
                break;
            case QueryUsers.INSERT_OR_UPDATE:
                this.queryUsers.selectUser(this.userID);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCompleteQueryReps(int codeOperation, @Nullable List<RealmModelRep> realmModelReps) {
        switch (codeOperation){
            case QueryUsers.SELECT:
                this.realmModelReps = realmModelReps;
                this.isSelectRepos = false;
                onSelectedData();
                break;
            case QueryUsers.INSERT_OR_UPDATE:
                this.queryUsers.selectUser(this.userID);
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
    // Method isSelectedData
    ////////////////////////////////////////////////////
    private void onSelectedData() {
        if(!this.isSelectUser && !this.isSelectRepos ){
            this.oneUsersFragment.setData(0);
            if(this.isDownloadNeed){
                downloadData();
            }
        }
    }

}
