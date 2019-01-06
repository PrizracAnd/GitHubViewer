package ru.demjanov_av.githubviewer.db;

import android.support.annotation.NonNull;
import android.util.Log;

import org.jetbrains.annotations.Contract;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import ru.demjanov_av.githubviewer.models.RealmModelRep;
import ru.demjanov_av.githubviewer.models.RealmModelUser;
import ru.demjanov_av.githubviewer.models.RetrofitModel;
import ru.demjanov_av.githubviewer.models.RetrofitModelRep;
import ru.demjanov_av.githubviewer.presenters.MyPresenter;

public class QueryUsers {
    //-----Constants begin-------------------------------
    private final static String REALM_DB = "REALM_DB:";

    //-----Code operations begin-------------------------
    public final static int SELECT              = 0;
    public final static int INSERT_OR_UPDATE    = 1;
    public final static int DELETE              = 2;
    public final static int DELETE_ALL          = 3;
    //-----Code operations end---------------------------

    //-----Code entities begin---------------------------
    public final static int USERS               = 0;
    public final static int REPOS               = 1;
    //-----Code entities end-----------------------------
    //-----Constants end---------------------------------


    //-----Class variables begin-------------------------
    private MyPresenter presenter;
    private RealmConfiguration realmConfiguration;
    //-----Class variables end---------------------------

    private List<RealmModelUser> modelUsersList;
    private Disposable disposable;
    private boolean isTransact = false;
    private boolean isSuccess = false;


    /////////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////////
    public QueryUsers(RealmConfiguration configuration, MyPresenter target) {
        this.presenter = target;
        this.realmConfiguration = configuration;
    }


    /////////////////////////////////////////////////////
    // Getters and Setters
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------

    public List<RealmModelUser> getModelUsersList() {
        return this.modelUsersList;
    }

    public boolean isTransact() {
        return isTransact;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    //-----End-------------------------------------------


    /////////////////////////////////////////////////////
    // Method insertUsersData
    ////////////////////////////////////////////////////
    public void insertUsersData(List<RetrofitModel> listUsers){
        this.isTransact = true;
        Completable completable = Completable.create(emitter ->{
            String curLogin;
            String curUserID;
            String curAvatarUrl;
            Realm realm = Realm.getInstance(this.realmConfiguration);
            for (RetrofitModel item : listUsers) {
                curLogin = item.getLogin();
                curUserID = item.getId();
                curAvatarUrl = item.getAvatarUrl();
                try {
                    realm.beginTransaction();
                    RealmModelUser realmModelUser = new RealmModelUser();
                    realmModelUser.setLogin(curLogin);
                    realmModelUser.setId(curUserID);
                    realmModelUser.setAvatarUrl(curAvatarUrl);
                    realm.insertOrUpdate(realmModelUser);
                    realm.commitTransaction();
                }catch (Exception e) {
                    realm.cancelTransaction();
                    realm.close();
                    emitter.onError(e);
                }
            }
            realm.close();
            emitter.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        this.disposable = completable.subscribeWith(createObserver(INSERT_OR_UPDATE, USERS));
    }


    /////////////////////////////////////////////////////
    // Method deleteAllUsers
    ////////////////////////////////////////////////////
    public void deleteAllUsers(){
        this.isTransact = true;
        Completable completable = Completable.create(emitter -> {
            Realm realm = Realm.getInstance(this.realmConfiguration);
            try{
                final RealmResults<RealmModelUser> listResults = realm.where(RealmModelUser.class).findAll();
                realm.executeTransaction(realm1 -> listResults.deleteAllFromRealm());
                emitter.onComplete();
            }catch (Exception e) {
                emitter.onError(e);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        this.disposable = completable.subscribeWith(createObserver(DELETE, USERS));
    }


    /////////////////////////////////////////////////////
    // Method insertReposData
    ////////////////////////////////////////////////////
    public void insertReposData(List<RetrofitModelRep> listRepos, String userID){
        this.isTransact = true;

        Completable completable = Completable.create(emitter ->{
            Realm realm = Realm.getInstance(this.realmConfiguration);
            for (RetrofitModelRep item : listRepos) {
                try {
                    realm.beginTransaction();
                    RealmModelRep realmModelRep = new RealmModelRep();
                    realmModelRep.setRepId(item.getId());
                    realmModelRep.setNameRep(item.getName());
//                    realmModelRep.setUserId(item.getUserId());
                    realmModelRep.setUserId(userID);
                    realm.insertOrUpdate(realmModelRep);
                    realm.commitTransaction();
                }catch (Exception e) {
                    realm.cancelTransaction();
                    realm.close();
                    emitter.onError(e);
                }
            }
            realm.close();
            emitter.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        this.disposable = completable.subscribeWith(createObserver(INSERT_OR_UPDATE, REPOS));
    }



    /////////////////////////////////////////////////////
    // Methods selectUsers
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    public void selectAllUsers(){
        this.isTransact = true;
        Single single = Single.create(emitter -> {
            Realm realm = Realm.getInstance(this.realmConfiguration);
            try {
                RealmResults<RealmModelUser> listResult = realm.where(RealmModelUser.class).findAll();
                //------------------------------
                List<RealmModelUser> listUser = realm.copyFromRealm(listResult);
                //------------------------------
                emitter.onSuccess(listUser);
            }catch (Exception e){
                emitter.onError(e);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        this.disposable = (Disposable) single.subscribeWith(createObserverSelectUser());
//        this.disposable = (Disposable) single.subscribeWith(new DisposableSingleObserver<List<RealmModelUser>>() {
//
//
//            @Override
//            public void onSuccess(List<RealmModelUser> list) {
//                modelUsersList = list;
//                isTransact = false;
//                isSuccess = true;
//
//                presenter.onCompleteQueryUsers(SELECT, list);
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                modelUsersList = null;
//                isTransact = false;
//                isSuccess = false;
//                Log.d(REALM_DB, e.getMessage());
//
//                presenter.onErrorQueryUsers(e.getMessage());
//            }
//        });
    }


    public void selectUser(String userId){
        this.isTransact = true;
        Single single = Single.create(emitter -> {
            Realm realm = Realm.getInstance(this.realmConfiguration);
            try {
                RealmResults<RealmModelUser> listResult = realm.where(RealmModelUser.class)
                        .equalTo(RetrofitModel.ID, userId)
                        .findAll();
                //------------------------------
                List<RealmModelUser> listUser = realm.copyFromRealm(listResult);
                //------------------------------
                emitter.onSuccess(listUser);
            }catch (Exception e){
                emitter.onError(e);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        this.disposable = (Disposable) single.subscribeWith(createObserverSelectUser());
//        this.disposable = (Disposable) single.subscribeWith(new DisposableSingleObserver<List<RealmModelUser>>() {
//
//
//            @Override
//            public void onSuccess(List<RealmModelUser> list) {
//                modelUsersList = list;
//                isTransact = false;
//                isSuccess = true;
//
//                presenter.onCompleteQueryUsers(SELECT, list);
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                modelUsersList = null;
//                isTransact = false;
//                isSuccess = false;
//                Log.d(REALM_DB, e.getMessage());
//
//                presenter.onErrorQueryUsers(e.getMessage());
//            }
//        });
    }
    //-----End-------------------------------------------


    /////////////////////////////////////////////////////
    // Methods selectRepos
    ////////////////////////////////////////////////////
    public void selectRepos(String userId) {
        this.isTransact = true;
        Single single = Single.create(emitter -> {
            Realm realm = Realm.getInstance(this.realmConfiguration);
            try {
                RealmResults<RealmModelRep> listResult = realm.where(RealmModelRep.class)
                        .equalTo(RealmModelRep.USER_ID, userId)
                        .findAll();
                //------------------------------
                List<RealmModelRep> listRepos = realm.copyFromRealm(listResult);
                //------------------------------
                emitter.onSuccess(listRepos);
            } catch (Exception e) {
                emitter.onError(e);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        this.disposable = (Disposable) single.subscribeWith(createObserverSelectRepos());
    }


    /////////////////////////////////////////////////////
    // Methods createObserver
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    @Contract(pure = true)
    @NonNull
    private DisposableCompletableObserver createObserver(int codeOperation, int codeEntity){
        return new DisposableCompletableObserver() {
            @Override
            public void onComplete() {
                isTransact = false;
                isSuccess = true;

                switch (codeEntity) {
                    case REPOS:
                        presenter.onCompleteQueryReps(codeOperation, null);
                    default:
                        presenter.onCompleteQueryUsers(codeOperation, null);
                        break;
                }

            }

            @Override
            public void onError(Throwable e) {
                isTransact = false;
                isSuccess = false;
                Log.d(REALM_DB, e.getMessage());

                presenter.onErrorQueryUsers(e.getMessage());
            }
        };
    }


    @NonNull
    @Contract(pure = true)
    private DisposableSingleObserver<List<RealmModelUser>> createObserverSelectUser(){
        return new DisposableSingleObserver<List<RealmModelUser>>() {
            @Override
            public void onSuccess(List<RealmModelUser> list) {
                modelUsersList = list;
                isTransact = false;
                isSuccess = true;

                presenter.onCompleteQueryUsers(SELECT, list);

            }

            @Override
            public void onError(Throwable e) {
                modelUsersList = null;
                isTransact = false;
                isSuccess = false;
                Log.d(REALM_DB, e.getMessage());

                presenter.onErrorQueryUsers(e.getMessage());
            }
        };
    }

    @NonNull
    @Contract(pure = true)
    private DisposableSingleObserver<List<RealmModelRep>> createObserverSelectRepos(){
        return new DisposableSingleObserver<List<RealmModelRep>>() {
            @Override
            public void onSuccess(List<RealmModelRep> list) {
                isTransact = false;
                isSuccess = true;

                presenter.onCompleteQueryReps(SELECT, list);

            }

            @Override
            public void onError(Throwable e) {
                isTransact = false;
                isSuccess = false;
                Log.d(REALM_DB, e.getMessage());

                presenter.onErrorQueryReps(e.getMessage());
            }
        };
    }
    //-----End-------------------------------------------


    /////////////////////////////////////////////////////
    // Method destroy
    ////////////////////////////////////////////////////
    public void destroy() {
        if (this.disposable != null && this.disposable.isDisposed()) {
            this.disposable.dispose();
        }
    }
}
