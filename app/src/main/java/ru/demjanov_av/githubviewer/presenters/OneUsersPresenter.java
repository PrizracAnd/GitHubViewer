package ru.demjanov_av.githubviewer.presenters;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import ru.demjanov_av.githubviewer.db.QueryUsers;
import ru.demjanov_av.githubviewer.injector.ContextProvider;
import ru.demjanov_av.githubviewer.injector.db.DaggerInjectorRealm;
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
    // Constructor
    ////////////////////////////////////////////////////
    public OneUsersPresenter(OneUsersFragment oneUsersFragment, Context context) {
        this.context = context;
        this.oneUsersFragment = oneUsersFragment;

        DaggerInjectorRealm.builder()
                .contextProvider(new ContextProvider(context))
                .build()
                .injectToOneUsersPresenter(this);

        this.queryUsers = new QueryUsers(this.realmConfiguration, this);
    }

    public OneUsersPresenter(OneUsersFragment oneUsersFragment, Context context, String userID) {
        this.context = context;
        this.oneUsersFragment = oneUsersFragment;
        this.userID = userID;

        DaggerInjectorRealm.builder()
                .contextProvider(new ContextProvider(context))
                .build()
                .injectToOneUsersPresenter(this);

        this.queryUsers = new QueryUsers(this.realmConfiguration, this);

        this.isDownloadNeed = true;
        this.queryUsers.selectRepos(userID);
        this.queryUsers.selectRepos(userID);
    }


    /////////////////////////////////////////////////////
    // Getters and Setters
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------

    public List<RealmModelUser> getRealmModelUsers() {
        return realmModelUsers;
    }

    public List<RealmModelRep> getRealmModelReps() {
        return realmModelReps;
    }

    public void setUserID(String userID) {
        this.userID = userID;

//        this.isDownloadNeed = true;
//        this.isSelectUser = true;
//        this.isSelectRepos = true;
//        this.queryUsers.selectUser(userID);
//        this.queryUsers.selectRepos(userID);

        //---Actualise data by download from net-begin---
        downloadData();
    }

    //-----End-------------------------------------------


    /////////////////////////////////////////////////////
    // Method downloadData
    ////////////////////////////////////////////////////
    public void downloadData(){
        if(realmModelUsers == null){
            this.isDownloadNeed = true;
            this.isSelectUser = true;
            this.queryUsers.selectUser(this.userID);
        }else {
            if (!isDownloadUser && !isDownloadRepos && realmModelUsers != null && realmModelUsers.size() > 0) {
                this.oneUsersFragment.startLoad();
                this.isDownloadRepos = true;
                this.isDownloadUser = true;
                this.isSelectRepos = true;
                this.isSelectUser = true;

                Caller caller = new Caller(this.context, this);

                String userName = this.realmModelUsers.get(0).getLogin();

                caller.downloadUser(userName);
                caller.downloadRepos(userName);

//            this.isDownloadNeed = false;
            }
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

//        onDownloadComplete();
    }


    @Override
    public void onRespRepos(List<RetrofitModelRep> retrofitModelRepList, Boolean isDownload, @Nullable String message) {
        this.retrofitModelRepList = retrofitModelRepList;
        this.isDownloadRepos = isDownload;

        this.isSelectRepos = true;
        this.queryUsers.insertReposData(retrofitModelRepList, this.userID);

//        this.oneUsersFragment.endLoad();
//        onDownloadComplete();
    }


    @Override
    public void onFail(Boolean isDownload, int codeMessage, @Nullable String message) {
        this.isDownloadUser = isDownload;
        this.isDownloadRepos = isDownload;

        this.oneUsersFragment.endLoad();
        this.oneUsersFragment.setError(codeMessage, message); // FIXME!!!!!!!!!!!!!!*****!!!!!!!!!!!!!!!!!!!!!!!!!!!!


        this.isSelectUser = true;
        this.isSelectRepos = true;
        this.queryUsers.selectUser(this.userID);
        this.queryUsers.selectRepos(this.userID);
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
                this.oneUsersFragment.setData(MainView.SET_USERS_DATA);
//                onSelectedData();
                onSelectDataComplete();
                //---------------+++------------------------
                if(isDownloadNeed) {
                    isDownloadNeed = false;
                    downloadData();
                }
                //---------------+++------------------------

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
                this.oneUsersFragment.setData(MainView.SET_REPOS_DATA);
//                onSelectedData();
                onSelectDataComplete();
                break;
            case QueryUsers.INSERT_OR_UPDATE:
                this.queryUsers.selectRepos(this.userID);
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
    // Method onSelectedData
    ////////////////////////////////////////////////////
    private void onSelectedData() {
        if(!this.isSelectUser && !this.isSelectRepos && this.isDownloadNeed){   //--Проверяем, получены ли все данные из БД и требуется ли их актуализация
//            this.isDownloadNeed = false;                                        //--сбрасываем флаг требования автоактуализации, чтоб избежать возможного зацикливания
            downloadData();                                                     //--запрашиваем данные из сети
//            this.oneUsersFragment.endLoad();
//            this.oneUsersFragment.setData(0);
//            if(this.isDownloadNeed){
//                downloadData();
//            }
        }
    }



    /////////////////////////////////////////////////////
    // Method onDownloadComplete
    ////////////////////////////////////////////////////
//    private void onDownloadComplete(){
//        if(!isDownloadUser && !isDownloadRepos){
//            this.oneUsersFragment.endLoad();
//        }
//    }

    private boolean onDownloadComplete(){
        return (!isDownloadUser && !isDownloadRepos);
    }

    /////////////////////////////////////////////////////
    // Method onSelectDataComplete
    ////////////////////////////////////////////////////
    private void onSelectDataComplete(){
        if(onDownloadComplete() && !isSelectUser && !isSelectRepos){
            this.oneUsersFragment.endLoad();
        }
    }

}
