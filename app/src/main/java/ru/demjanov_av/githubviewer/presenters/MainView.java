package ru.demjanov_av.githubviewer.presenters;

import android.support.annotation.Nullable;

public interface MainView {
     void startLoad();

     void endLoad();

     void setError(int number, @Nullable String message);

     void setData(int dataType);
}
