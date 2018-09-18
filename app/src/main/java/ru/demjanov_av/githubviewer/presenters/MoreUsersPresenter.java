package ru.demjanov_av.githubviewer.presenters;

import android.content.Context;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.List;

import ru.demjanov_av.githubviewer.models.RetrofitModel;
import ru.demjanov_av.githubviewer.network.Caller;
import ru.demjanov_av.githubviewer.views.MoreUsersFragment;

/**
 * Created by demjanov on 11.09.2018.
 */

public class MoreUsersPresenter  extends MyPresenter {
    //-----Class variables begin-------------------------
    private Context context;
    private MoreUsersFragment moreUsersFragment;
    //-----Class variables end---------------------------


    //-----Other variables begin-------------------------
    private long timeWaite = 10000;
    private boolean isDownload = false;
    private List<RetrofitModel> retrofitModelList;
    private String previous_id = "0";
    //-----Other variables end---------------------------


    /////////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////////
    public MoreUsersPresenter(MoreUsersFragment moreUsersFragment, Context context) {
        this.moreUsersFragment = moreUsersFragment;
        this.context = context;
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
        this.moreUsersFragment.endLoad();
        this.moreUsersFragment.setData(0);

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

        try {
            caller.downloadUsers(previous_id);
        }catch (IOException e){
            onFail(false, Caller.ON_FAILURE, e.getMessage());
        }

//        long t0 = System.currentTimeMillis();
//        while (caller.isDownloads()){
////            if((System.currentTimeMillis() - t0) >= this.timeWaite){
////                break;
////            }
//        }
//
//        moreUsersFragment.endLoad();
//
//        int codeMessage = caller.getCodeMessage();
//        if(codeMessage == Caller.ALL_GUT){
//            this.retrofitModelList = caller.getRetrofitModelList();
//            moreUsersFragment.setData(0);
//        }else {
//            moreUsersFragment.setError(codeMessage, caller.getMessages());
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
    // Method getUsers
    ////////////////////////////////////////////////////
    @Nullable
    public String getUsers(){
//        String result;
        if (this.retrofitModelList.size() < 1){
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for(RetrofitModel item: this.retrofitModelList) {
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
}
