package ru.demjanov_av.githubviewer.presenters;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.List;

import ru.demjanov_av.githubviewer.models.RetrofitModel;
import ru.demjanov_av.githubviewer.network.Caller;
import ru.demjanov_av.githubviewer.views.MoreUsersFragment;

/**
 * Created by demjanov on 11.09.2018.
 */

public class MoreUsersPresenter {
    //-----Class variables begin-------------------------
    private Context context;
    private MoreUsersFragment moreUsersFragment;
    //-----Class variables end---------------------------


    //-----Other variables begin-------------------------
    private long timeWaite = 10000;

    private List<RetrofitModel> retrofitModelList;
    //-----Other variables end---------------------------


    /////////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////////
    public MoreUsersPresenter(MoreUsersFragment moreUsersFragment, Context context) {
        this.moreUsersFragment = moreUsersFragment;
        this.context = context;
    }

    /////////////////////////////////////////////////////
    // Method downloadUsers
    ////////////////////////////////////////////////////
    public void downloadUsers(){
        moreUsersFragment.startLoad();
        Caller caller = new Caller(this.context);
        caller.downloadUsers();

        long t0 = System.currentTimeMillis();
        while (caller.isDownloads()){
            if((System.currentTimeMillis() - t0) >= this.timeWaite){
                break;
            }
        }

        moreUsersFragment.endLoad();

        int codeMessage = caller.getCodeMessage();
        if(codeMessage == Caller.ALL_GUT){
            this.retrofitModelList = caller.getRetrofitModelList();
            moreUsersFragment.setData(0);
        }else {
            moreUsersFragment.setError(codeMessage, caller.getMessages());
        }
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
            sb.append("<br/>");
            sb.append(RetrofitModel.ID +":\t");
            sb.append(item.getId());
            sb.append("<br/>");
            sb.append(RetrofitModel.AVATAR_URL +":\t");
            sb.append(item.getAvatarUrl());
            sb.append("<br/>");
            sb.append("---*****---");
            sb.append("<br/>");
            sb.append("<br/>");
        }
        return sb.toString();
    }
}
