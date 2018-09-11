package ru.demjanov_av.githubviewer.presenters;

import android.content.Context;

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
    public MoreUsersPresenter(MoreUsersFragment moreUsersFragment) {
        this.moreUsersFragment = moreUsersFragment;
        this.context = moreUsersFragment.getContext();
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
}
