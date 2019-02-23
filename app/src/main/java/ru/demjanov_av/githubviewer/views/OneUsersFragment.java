package ru.demjanov_av.githubviewer.views;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.demjanov_av.githubviewer.R;
import ru.demjanov_av.githubviewer.models.RealmModelRep;
import ru.demjanov_av.githubviewer.models.RealmModelUser;
import ru.demjanov_av.githubviewer.network.Caller;
import ru.demjanov_av.githubviewer.presenters.MainView;
import ru.demjanov_av.githubviewer.presenters.OneUsersPresenter;


public class OneUsersFragment extends Fragment implements MainView {
    //-----Constants begin-------------------------------
    private final static String KEY_USER_ID = "key_user_id";
    //-----Constants end---------------------------------


    //-----View elements variables begin-----------------
    @BindView(R.id.login_text)
    TextView loginText;

    @BindView(R.id.avatar_view)
    ImageView avatarImage;

    @BindView(R.id.info_text)
    TextView infoText;

    @BindView(R.id.repos_text)
    TextView reposText;

    @BindView(R.id.sr_layout_ouf)
    SwipeRefreshLayout swipeRefreshLayout;
    //-----View elements variables end-------------------


    //-----Class variables begin-------------------------
    private String userId = null;
    private OneUsersPresenter presenter;
    private View rootView;
    private static Bundle bundleState = null;
    //-----Class variables begin-------------------------


    /////////////////////////////////////////////////////
    // Method onCreateView
    ////////////////////////////////////////////////////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        this.rootView = inflater.inflate(R.layout.fragment_one_users, viewGroup, false);

        return this.rootView;
    }


    /////////////////////////////////////////////////////
    // Method onStart
    ////////////////////////////////////////////////////
    @Override
    public void onStart() {
        super.onStart();

        initializeElements(this.rootView);
    }


    /////////////////////////////////////////////////////
    // Method onPause
    ////////////////////////////////////////////////////
    @Override
    public void onPause() {
        bundleState = new Bundle();
        bundleState.putString(KEY_USER_ID, this.userId);
        super.onPause();
    }


    // восстанавливаем прокрутку рециклера (да, именно здесь!)
    /////////////////////////////////////////////////////
    // Method onResume
    ////////////////////////////////////////////////////
    @Override
    public void onResume() {

        super.onResume();

        if(this.userId == null && bundleState != null){
            String user_id = bundleState.getString(KEY_USER_ID);
            setUserId(user_id);
        }
    }


    /////////////////////////////////////////////////////
    // Method initializeElements
    ////////////////////////////////////////////////////
    private void initializeElements(View view) {
        ButterKnife.bind(this, view);

        //---SwipeRefreshLayout begin-------------------
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.downloadData();
            }
        });
        swipeRefreshLayout.setColorSchemeColors(
                Color.RED,
                Color.BLUE,
                Color.GREEN,
                Color.CYAN
        );
        //---SwipeRefreshLayout end---------------------



        presenter = new OneUsersPresenter(this, view.getContext());
        if(this.userId != null){
            presenter.setUserID(this.userId);
        }
    }



    /////////////////////////////////////////////////////
    // Methods of interfaces
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    @Override
    public void startLoad() {
        swipeRefreshLayout.setRefreshing(true);
    }


    @Override
    public void endLoad() {
        swipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void setError(int number, @Nullable String message) {

        Context context = this.rootView.getContext().getApplicationContext();

        switch (number){
            case Caller.NO_CALL:

                Toast.makeText(context, context.getResources().getString(R.string.no_call), Toast.LENGTH_LONG).show();
                break;
            case Caller.NO_CONNECTED:
                Toast.makeText(context, context.getResources().getString(R.string.no_connect),Toast.LENGTH_LONG).show();
                break;
            case Caller.NOT_LOADING_DATA:
//                infoText.setText(getResources().getString(R.string.not_loading_data));
                break;
            default:
                Log.d(Caller.titleMessage[number], message);
                break;
        }

    }


    @Override
    public void setData(int dataType) {
        switch (dataType){
            case MainView.SET_USERS_DATA:
                RealmModelUser realmModelUser = (presenter.getRealmModelUsers()).get(0);
                if (realmModelUser != null) {
                    loginText.setText(realmModelUser.getLogin());
                    Glide.with(this)
                            .load(realmModelUser.getAvatarUrl())
                            .into(avatarImage);
                    avatarImage.setVisibility(View.VISIBLE);

                    infoText.setText(getUserInfo(realmModelUser));
                }
                break;
            case MainView.SET_REPOS_DATA:
                List<RealmModelRep> realmModelRepList = presenter.getRealmModelReps();
                if(realmModelRepList != null && realmModelRepList.size() > 0){
                    reposText.setText(getReposInfo(realmModelRepList));
//                    reposText.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }
    //-----End-------------------------------------------


    /////////////////////////////////////////////////////
    // Methods getInfo
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    @NonNull
    private String getUserInfo(RealmModelUser realmModelUser){

        StringBuilder sb = new StringBuilder();
        sb
                .append(getResources().getString(R.string.user_id))
                .append(":\t")
                .append(realmModelUser.getId())
                .append("\n");

        return sb.toString();
    }


    @NonNull
    private String getReposInfo(List<RealmModelRep> realmModelRepList){

        StringBuilder sb = new StringBuilder();
        for (RealmModelRep item: realmModelRepList) {
            sb
                    .append(item.getNameRep())
                    .append(";")
                    .append("\n");
        }
        sb
                .delete((sb.length() - 2), (sb.length()))
                .append(".");


        return sb.toString();
    }
    //-----End-------------------------------------------


    /////////////////////////////////////////////////////
    // Getters and Setters
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------

    public void setUserId(String userId) {
        this.userId = userId;
        if(this.presenter != null) {
            this.presenter.setUserID(userId);
        }
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
