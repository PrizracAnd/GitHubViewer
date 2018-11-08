package ru.demjanov_av.githubviewer.presenters;


import android.support.annotation.Nullable;

import java.util.List;


import io.realm.RealmResults;
import ru.demjanov_av.githubviewer.db.QueryUsersInterface;
import ru.demjanov_av.githubviewer.models.RealmModelRep;
import ru.demjanov_av.githubviewer.models.RealmModelUser;
import ru.demjanov_av.githubviewer.models.RetrofitModel;
import ru.demjanov_av.githubviewer.models.RetrofitModelRep;
import ru.demjanov_av.githubviewer.network.CallerInterface;

/**
 * Created by demjanov on 17.09.2018.
 */

public abstract class MyPresenter implements CallerInterface, QueryUsersInterface {
//    @Inject
//    Realm realm;


    /////////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////////
//    public MyPresenter(Context context){
//        DaggerInjectorRealm.builder()
//                .contextProvider(new ContextProvider(context))
//                .build()
//                .injectToPresenter(this);
//    }


    /////////////////////////////////////////////////////
    // Methods of CallerInterface
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    @Override
    public void onResp(List<RetrofitModel> retrofitModelList) {

    }

    @Override
    public void onResp(RetrofitModel retrofitModel) {

    }

    @Override
    public void onResp(List<RetrofitModel> retrofitModelList, Boolean isDownload, @Nullable String message) {

    }

    @Override
    public void onResp(RetrofitModel retrofitModel, Boolean isDownload, @Nullable String message) {

    }

    @Override
    public void onFail() {

    }

    @Override
    public void onFail(Boolean isDownload, int codeMessage, @Nullable String message) {

    }

    @Override
    public void onRespRepos(List<RetrofitModelRep> retrofitModelRepList) {

    }

    @Override
    public void onRespRepos(List<RetrofitModelRep> retrofitModelRepList, Boolean isDownload, @Nullable String message) {

    }

    //-----End-------------------------------------------


    /////////////////////////////////////////////////////
    // Methods of CallerInterface
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    @Override
    public void onCompleteQueryUsers(int codeOperation, @Nullable List<RealmModelUser> realmModelUsers) {

    }

    @Override
    public void onCompleteQueryReps(int codeOperation, @Nullable List<RealmModelRep> realmModelReps) {

    }

    @Override
    public void onErrorQueryUsers(String message) {

    }

    @Override
    public void onErrorQueryReps(String message) {

    }

    //-----End-------------------------------------------
}
