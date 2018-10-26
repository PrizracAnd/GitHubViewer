package ru.demjanov_av.githubviewer.db;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

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
import ru.demjanov_av.githubviewer.models.RealmModelUser;
import ru.demjanov_av.githubviewer.models.RetrofitModel;
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
    //-----Constants end---------------------------------


    //-----Class variables begin-------------------------
//    private Realm realm;
    private Context context;
    private MyPresenter presenter;
    //-----Class variables end---------------------------

    private RealmConfiguration realmConfiguration;
    private RealmResults<RealmModelUser> modelUsersList;
    private Disposable disposable;
    private boolean isTransact = false;
    private boolean isSuccess = false;


    /////////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////////

    public QueryUsers(Context context, MyPresenter target) {
//        this.realm = realm;
        this.context = context;
        this.presenter = target;

        Realm.init(context);//FIXME !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        this.realmConfiguration = new RealmConfiguration.Builder().build();
    }


    /////////////////////////////////////////////////////
    // Getters and Setters
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------

    public RealmResults<RealmModelUser> getModelUsersList() {
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
    //-----Begin-----------------------------------------
    public void insertUsersData(List<RetrofitModel> listUsers){
        this.isTransact = true;
        Completable completable = Completable.create(emitter ->{
            String curLogin;
            String curUserID;
            String curAvatarUrl;
//            Realm realm = this.realm;
            Realm realm = Realm.getInstance(this.realmConfiguration); //Fixme!!!
            for (RetrofitModel item : listUsers) {
                curLogin = item.getLogin();
                curUserID = item.getId();
                curAvatarUrl = item.getAvatarUrl();
                try {
                    realm.beginTransaction();
//                    RealmModelUser realmModelUser = realm.createObject(RealmModelUser.class);
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
        this.disposable = completable.subscribeWith(createObserver(INSERT_OR_UPDATE));
    }


    /////////////////////////////////////////////////////
    // Method deleteAllUsers
    ////////////////////////////////////////////////////
    public void deleteAllUsers(){
        this.isTransact = true;
        Completable completable = Completable.create(emitter -> {
//            Realm realm = this.realm;
            Realm realm = Realm.getInstance(this.realmConfiguration); //Fixme!!!
            try{
                final RealmResults<RealmModelUser> listResults = realm.where(RealmModelUser.class).findAll();
                realm.executeTransaction(realm1 -> listResults.deleteAllFromRealm());
                emitter.onComplete();
            }catch (Exception e) {
                emitter.onError(e);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        this.disposable = completable.subscribeWith(createObserver(DELETE));
    }


    /////////////////////////////////////////////////////
    // Methods selectAllUsers
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    public void selectAllUsers(){
        this.isTransact = true;
        Single single = Single.create(emitter -> {
//            Realm realm = this.realm;
            Realm realm = Realm.getInstance(this.realmConfiguration); //Fixme!!!
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

        this.disposable = (Disposable) single.subscribeWith(new DisposableSingleObserver<RealmResults<RealmModelUser>>() {


            @Override
            public void onSuccess(RealmResults<RealmModelUser> list) {
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
        });
    }
    //-----End-------------------------------------------


    /////////////////////////////////////////////////////
    // Method createObserver
    ////////////////////////////////////////////////////
    @NonNull
    @org.jetbrains.annotations.Contract(value = " -> !null", pure = true)
    private DisposableCompletableObserver createObserver(int codeOperation){
        return new DisposableCompletableObserver() {
            @Override
            public void onComplete() {
                isTransact = false;
                isSuccess = true;

                presenter.onCompleteQueryUsers(codeOperation, null);

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


    /////////////////////////////////////////////////////
    // Method destroy
    ////////////////////////////////////////////////////
    public void destroy() {
        if (this.disposable != null && this.disposable.isDisposed()) {
            this.disposable.dispose();
        }
    }
}
