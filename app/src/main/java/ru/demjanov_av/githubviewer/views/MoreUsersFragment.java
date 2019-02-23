package ru.demjanov_av.githubviewer.views;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.demjanov_av.githubviewer.R;
import ru.demjanov_av.githubviewer.presenters.MainView;
import ru.demjanov_av.githubviewer.presenters.MoreUsersPresenter;


public class MoreUsersFragment extends Fragment implements MoreUsersAdapter.MoreUsersCall, MainView {
    //-----Constants begin-------------------------------
    private final static String KEY_PARCELABLE = "key_parcelable";
    //-----Constants end---------------------------------


    //-----Class variables begin-------------------------
    private View view;
    private MoreUsersPresenter presenter;
    private ClickListenerUsers mainActivity;
    private static Bundle bundleState = null;
    //-----Class variables end---------------------------


    //-----View elements variables begin-----------------
    @BindView(R.id.more_users_recycle)
    RecyclerView recyclerView;
    //-----View elements variables end-------------------


    /////////////////////////////////////////////////////
    // Interface ClickListenerUsers
    ////////////////////////////////////////////////////
    public interface ClickListenerUsers{
        void onClickUsers(String userId);
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        //передаем в адаптер RealmResults для автообновления и наш MoreUsersFragment для обратной связи
        MoreUsersAdapter myAdapter = new MoreUsersAdapter(this.presenter.getResults(), this);
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


    // сохраняем прокрутку рециклера
    /////////////////////////////////////////////////////
    // Method onPause
    ////////////////////////////////////////////////////
    @Override
    public void onPause() {
        bundleState = new Bundle(); // !!!обратите внимание на объявление переменной bundleState в разделе переменных класса (выше по тексту)
        Parcelable state = recyclerView.getLayoutManager().onSaveInstanceState();
        bundleState.putParcelable(KEY_PARCELABLE, state);
        super.onPause();
    }


    // восстанавливаем прокрутку рециклера (да, именно здесь!)
    /////////////////////////////////////////////////////
    // Method onResume
    ////////////////////////////////////////////////////
    @Override
    public void onResume() {

        super.onResume();

        if(bundleState != null){
            Parcelable state = bundleState.getParcelable(KEY_PARCELABLE);
            recyclerView.getLayoutManager().onRestoreInstanceState(state);
        }

    }


    /////////////////////////////////////////////////////
    // Methods of interfaces
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    @Override
    public void onCallUser(String userId) {
        mainActivity.onClickUsers(userId);
    }

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


    /////////////////////////////////////////////////////
    // Method onDestroy
    ////////////////////////////////////////////////////
    @Override
    public void onDestroy() {

        this.presenter.destroy();

        super.onDestroy();
    }
}
