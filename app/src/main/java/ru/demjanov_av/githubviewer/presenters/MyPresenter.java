package ru.demjanov_av.githubviewer.presenters;

import android.support.annotation.Nullable;

import java.util.List;

import ru.demjanov_av.githubviewer.models.RetrofitModel;
import ru.demjanov_av.githubviewer.network.CallerInterface;

/**
 * Created by demjanov on 17.09.2018.
 */

public abstract class MyPresenter implements CallerInterface {

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
    //-----End-------------------------------------------
}
