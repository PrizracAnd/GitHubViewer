package ru.demjanov_av.githubviewer.network;

import android.support.annotation.Nullable;

/**
 * Created by demjanov on 11.09.2018.
 */

public interface CallerInterface {
    void onResp();

    void onResp(Boolean isDownload, @Nullable String message);

    void onFail();

    void onFail(Boolean isDownload, @Nullable String message);
}
