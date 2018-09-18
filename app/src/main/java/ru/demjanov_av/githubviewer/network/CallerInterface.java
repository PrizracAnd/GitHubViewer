package ru.demjanov_av.githubviewer.network;

import android.support.annotation.Nullable;

import java.util.List;

import ru.demjanov_av.githubviewer.models.RetrofitModel;

/**
 * Created by demjanov on 11.09.2018.
 */

public interface CallerInterface {
    void onResp(List<RetrofitModel> retrofitModelList);

    void onResp(RetrofitModel retrofitModel);


    void onResp(List<RetrofitModel> retrofitModelList, Boolean isDownload, @Nullable String message);

    void onResp(RetrofitModel retrofitModel, Boolean isDownload, @Nullable String message);


    void onFail();

    void onFail(Boolean isDownload, int codeMessage, @Nullable String message);
}
