package ru.demjanov_av.githubviewer.presenters;

import android.support.annotation.Nullable;

public interface MainView {
     //-----Constants begin-------------------------------
     //-----Constants of setData begin--------------------
     int SET_USER_LIST = 0;
     int SET_USERS_DATA = 1;
     int SET_REPOS_DATA = 2;
     //-----Constants of setData end----------------------
     //-----Constants end---------------------------------

     void startLoad();

     void endLoad();

     void setError(int number, @Nullable String message);

     void setData(int dataType);
}
