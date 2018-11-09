package ru.demjanov_av.githubviewer.network;

import android.content.Context;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.util.Log;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.demjanov_av.githubviewer.injector.ContextProvider;
import ru.demjanov_av.githubviewer.injector.network.DaggerInjectorToCaller;
import ru.demjanov_av.githubviewer.models.RetrofitModel;
import ru.demjanov_av.githubviewer.models.RetrofitModelRep;
import ru.demjanov_av.githubviewer.presenters.MyPresenter;

public class Caller {
    //-----Constants begin-------------------------------
    //-----Messages constants begin----------------------
    public final static int NOT_MESSAGE = 0;
    public final static int ALL_GUT = 200;
    public final static int NO_RETROFIT = 1;
    public final static int NO_CALL = 2;
    public final static int NO_CONNECTED = 3;
    public final static int ON_FAILURE = 4;
    public final static int RESPONSE_ERROR = 5;
    public final static int NOT_LOADING_DATA = 6;

    public final static String[] titleMessage = {
            "NOT_MESSAGE: "
            , "NO_RETROFIT: "
            , "NO_CALL: "
            , "NO_CONNECTED: "
            , "ON_FAILURE: "
            , "RESPONSE_ERROR: "
            , "NOT_LOADING_DATA: "
    };

    public final static String CALLER = "CALLER: ";
    //-----Messages constants end------------------------

    //-----Queries types constants begin-----------------
    public final static int MORE_USERS = 0;
    public final static int ONE_USER = 1;
    //-----Queries types constants end-------------------
    //-----Constants end---------------------------------


    //-----Messages variables begin----------------------
    private int codeMessage = NOT_MESSAGE;      //-- code of message
    private String message;                     //-- string of message
    //-----Messages variables end------------------------


    //-----Class variables begin-------------------------
//    private List<RetrofitModel> retrofitModelList;
    private MyPresenter presenter;
    //-----Class variables end---------------------------


    //-----Other variables begin-------------------------
    private boolean isDownloads;

    @Inject
    RestAPI restAPI;
    @Inject
    NetworkInfo networkInfo;
    //-----Other variables end---------------------------


    /////////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////////
    public Caller(Context context, MyPresenter target) {

        this.presenter = target;

        DaggerInjectorToCaller.builder()
                .contextProvider(new ContextProvider(context))
                .build()
                .injectToCaller(this);
    }


    /////////////////////////////////////////////////////
    // Getters and Setters
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    public int getCodeMessage() {
        return codeMessage;
    }

    public String getMessages(){
        String messageString = titleMessage[this.codeMessage];
        if (this.message != null){
            messageString += (": " + this.message);
        }
        return messageString;
    }

//    public List<RetrofitModel> getRetrofitModelList() {
//        return retrofitModelList;
//    }

    public boolean isDownloads() {
        return isDownloads;
    }

    //-----End-------------------------------------------

    /////////////////////////////////////////////////////
    // Method setMessageInfo
    ////////////////////////////////////////////////////
    private void setMessageInfo(int codeMessage, @Nullable String message){
        this.codeMessage = codeMessage;
        this.message = message;

        if(codeMessage != NOT_MESSAGE && codeMessage != ALL_GUT){
            Log.d(CALLER, message);
        }
    }

    /////////////////////////////////////////////////////
    // Method createCall
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    @Nullable
    private Call createCallMoreUsers(String previous_id){
        Call call;
        try {
            call = this.restAPI.loadUsers(previous_id);
        }catch (Exception e){
            setMessageInfo(NO_RETROFIT, e.getMessage());
            return null;
        }
        return call;
    }

    @Nullable
    private Call createCallOneUser(String userName){
        Call call;
        try {
            call = this.restAPI.loadUser(userName);
        }catch (Exception e){
            setMessageInfo(NO_RETROFIT, e.getMessage());
            return null;
        }
        return call;
    }

    @Nullable
    private Call createCallRepos(String userName){
        Call call;
        try {
            call = this.restAPI.loadRepos(userName);
        }catch (Exception e){
            setMessageInfo(NO_RETROFIT, e.getMessage());
            return null;
        }
        return call;
    }
    //-----End-------------------------------------------

    private boolean isConnected(){
        return (networkInfo != null && networkInfo.isConnected());
    }


    /////////////////////////////////////////////////////
    // Methods download
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    public void downloadUsers(String previous_id) {
        resetCaller();

        this.isDownloads = true;
        Call call = createCallMoreUsers(previous_id);

        if (call != null) {
            if(isConnected()) {
                call.enqueue(new Callback<List<RetrofitModel>>(){

                    @Override
                    public void onResponse(Call<List<RetrofitModel>> call, Response<List<RetrofitModel>> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {


                                List<RetrofitModel> retrofitModelList = new ArrayList<RetrofitModel>();

                                for (int i = 0; i < response.body().size(); i++) {
                                    retrofitModelList.add(response.body().get(i));
                                }

                                setMessageInfo(ALL_GUT, null);
                                presenter.onResp(retrofitModelList, false, message);
                            }else {
                                setMessageInfo(NOT_LOADING_DATA, null);
                                presenter.onFail(false, NOT_LOADING_DATA, message);
                            }
                        } else {
                            setMessageInfo(RESPONSE_ERROR, "" + response.code());
                            presenter.onFail(false, RESPONSE_ERROR, message);
                        }
                        isDownloads = false;
                    }

                    @Override
                    public void onFailure(Call<List<RetrofitModel>> call, Throwable t) {
                        setMessageInfo(ON_FAILURE, t.getMessage());
                        isDownloads = false;
                        presenter.onFail(false, ON_FAILURE, message);
                    }
                });
            }else {
                setMessageInfo(NO_CONNECTED, null);
                isDownloads = false;
                presenter.onFail(false, NO_CONNECTED, message);
            }

        }else{
            setMessageInfo(NO_CALL, null);
            isDownloads = false;
            presenter.onFail(false, NO_CALL, null);
        }

    }


    public void downloadUser(String userName){
        resetCaller();
        this.isDownloads = true;
        Call call = createCallOneUser(userName);

        if(isConnected()){
            if(call != null) {
                call.enqueue(new Callback<RetrofitModel>() {
                    @Override
                    public void onResponse(Call<RetrofitModel> call, Response<RetrofitModel> response) {
                        if(response.isSuccessful()){
                            if(response.body() != null){
                                RetrofitModel rm = response.body();
                                List<RetrofitModel> listRetrofitModel = new ArrayList<RetrofitModel>();
                                listRetrofitModel.add(rm);

                                setMessageInfo(ALL_GUT, null);
                                presenter.onResp(listRetrofitModel, false, message);
                            }else {
                                setMessageInfo(NOT_LOADING_DATA, null);
                                presenter.onFail(false, NOT_LOADING_DATA, message);
                            }
                        }else {
                            setMessageInfo(RESPONSE_ERROR, "" + response.code());
                            presenter.onFail(false, RESPONSE_ERROR, message);
                        }

                        isDownloads = false;
                    }

                    @Override
                    public void onFailure(Call<RetrofitModel> call, Throwable t) {
                        setMessageInfo(ON_FAILURE, t.getMessage());
                        presenter.onFail(false, ON_FAILURE, message);
                        isDownloads = false;
                    }
                });
            }else isDownloads = false;
        }else {
            setMessageInfo(NO_CONNECTED, null);
            presenter.onFail(false, NO_CONNECTED, message);
            isDownloads = false;
        }

    }


    public void downloadRepos(String userName) {
        resetCaller();

        this.isDownloads = true;
        Call call = createCallRepos(userName);

        if (call != null) {
            if(isConnected()) {
                call.enqueue(new Callback<List<RetrofitModelRep>>(){

                    @Override
                    public void onResponse(Call<List<RetrofitModelRep>> call, Response<List<RetrofitModelRep>> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {

                                List<RetrofitModelRep> retrofitModelRepList = new ArrayList<RetrofitModelRep>();

                                for (int i = 0; i < response.body().size(); i++) {
                                    retrofitModelRepList.add(response.body().get(i));
                                }

                                setMessageInfo(ALL_GUT, null);
                                presenter.onRespRepos(retrofitModelRepList, false, message);
                            }else {
                                setMessageInfo(NOT_LOADING_DATA, null);
                                presenter.onFail(false, NOT_LOADING_DATA, message);
                            }
                        } else {
                            setMessageInfo(RESPONSE_ERROR, "" + response.code());
                            presenter.onFail(false, RESPONSE_ERROR, message);
                        }
                        isDownloads = false;
                    }

                    @Override
                    public void onFailure(Call<List<RetrofitModelRep>> call, Throwable t) {
                        setMessageInfo(ON_FAILURE, t.getMessage());
                        isDownloads = false;
                        presenter.onFail(false, ON_FAILURE, message);
                    }
                });
            }else {
                setMessageInfo(NO_CONNECTED, null);
                isDownloads = false;
                presenter.onFail(false, NO_CONNECTED, message);
            }

        }else{
            setMessageInfo(NO_CALL, null);
            isDownloads = false;
            presenter.onFail(false, NO_CALL, null);
        }

    }
    //-----End-------------------------------------------


    /////////////////////////////////////////////////////
    // Method resetCaller
    ////////////////////////////////////////////////////
    public void resetCaller(){
//        if(this.retrofitModelList.size() > 0) {
//            this.retrofitModelList.removeAll(this.retrofitModelList);
//        }
//        this.retrofitModelList = new ArrayList<RetrofitModel>();
        this.codeMessage = NOT_MESSAGE;
        this.message = null;
    }
}
