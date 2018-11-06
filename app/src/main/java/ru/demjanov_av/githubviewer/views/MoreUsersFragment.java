package ru.demjanov_av.githubviewer.views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.demjanov_av.githubviewer.R;
import ru.demjanov_av.githubviewer.presenters.MainView;
import ru.demjanov_av.githubviewer.presenters.MoreUsersPresenter;


public class MoreUsersFragment extends Fragment implements MoreUsersAdapter.MoreUsersCall, MainView {

    private View view;
    private MoreUsersPresenter presenter;
    private MoreUsersAdapter myAdapter;
    private ClickListenerUsers mainActivity;

    //-----View elements variables begin-----------------
//    @BindView(R.id.progressBarMore)
//    ProgressBar progressBar;

    @BindView(R.id.more_users_recycle)
    RecyclerView recyclerView;
    //-----View elements variables end-------------------


    //-----Flags variables begin-------------------------
    private boolean isLoad = false;
    private boolean isInitializeRecycler = false;
    //-----Flags variables End---------------------------


    /////////////////////////////////////////////////////
    // Interface ClickListenerUsers
    ////////////////////////////////////////////////////
    public interface ClickListenerUsers{
        void onClickUsers(String userName);
    }


    /////////////////////////////////////////////////////
    // Method onAttach
    ////////////////////////////////////////////////////
    @Override
    public void onAttach(Context context) {
        mainActivity = (ClickListenerUsers) context;
        super.onAttach(context);
    }


    /////////////////////////////////////////////////////
    // Method onCreateView
    ////////////////////////////////////////////////////
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        this.view = inflater.inflate(R.layout.fragment_more_users, viewGroup, false);

        return view;
    }


    /////////////////////////////////////////////////////
    // Method onStart
    ////////////////////////////////////////////////////
    @Override
    public void onStart() {
        super.onStart();

        initializeElements(this.view);
    }



    /////////////////////////////////////////////////////
    // Method initializeElements
    ////////////////////////////////////////////////////
    private void initializeElements(View view) {
        ButterKnife.bind(this, view);


        //---Presenter_begin---
        this.presenter = new MoreUsersPresenter(this, view.getContext());
        //---Presenter_end---


        //---RecyclerView_begin---
        initializeRecycler(view);
        //---RecyclerView_end---

    }


    /////////////////////////////////////////////////////
    // Method initializeRecycler
    ////////////////////////////////////////////////////
    private void initializeRecycler(View view){
        this.isInitializeRecycler = true;
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        //передаем в адаптер RealmResults для автообновления и наш MoreUsersFragment для обратной связи
        myAdapter = new MoreUsersAdapter(this.presenter.getResults(), this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(myAdapter);

        //запливаем листенер, чтоб вызывать подгрузку записей когда юзер долистал до конца
        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int itemCountVisible = layoutManager.getChildCount();
                int itemCountTotal = layoutManager.getItemCount();
                int itemFirstVisible =layoutManager.findFirstVisibleItemPosition();

                if((itemFirstVisible + itemCountVisible) >= itemCountTotal){
                    presenter.downloadUsers();
                }

            }
        };

        //назначаем наш листенер
        //метод set теперь deprecated, вместо него есть add и remove
        recyclerView.addOnScrollListener(scrollListener);
    }


    /////////////////////////////////////////////////////
    // Method onClickBtnLoad
    ////////////////////////////////////////////////////
//    @OnClick(R.id.btnLoad)
//    public void onClickBtnLoad(){
//        this.presenter.downloadUsers();
//    }


    /////////////////////////////////////////////////////
    // Methods of interfaces
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    @Override
    public void onCallUser(String userName) {
        mainActivity.onClickUsers(userName);
    }

    @Override
    public void startLoad() {
        this.isLoad = true;
//        progressBar.setVisibility(View.VISIBLE);
//        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void endLoad() {
        this.isLoad = false;
//        if(!this.isInitializeRecycler) initializeRecycler(this.view);

//        progressBar.setVisibility(View.GONE);
//        recyclerView.setVisibility(View.VISIBLE);

    }

    @Override
    public void setError(int number, @Nullable String message) {

    }

    @Override
    public void setData(int dataType) {

    }
    //-----End-------------------------------------------


    /////////////////////////////////////////////////////
    // Method onDestroy
    ////////////////////////////////////////////////////
    @Override
    public void onDestroy() {

        this.presenter.destroy();

        super.onDestroy();
    }
}
