package ru.demjanov_av.githubviewer.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.demjanov_av.githubviewer.R;
import ru.demjanov_av.githubviewer.presenters.MainView;

public class MoreUsersFragment extends Fragment implements MainView {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        this.view = inflater.inflate(R.layout.fragment_more_users, viewGroup, false);

        return view;
    }


    /////////////////////////////////////////////////////
    // Methods of interfaces
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    @Override
    public void startLoad() {

    }

    @Override
    public void endLoad() {

    }

    @Override
    public void setError(int number, @Nullable String message) {

    }

    @Override
    public void setData(int dataType) {

    }
    //-----End-------------------------------------------
}
