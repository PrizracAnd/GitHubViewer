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
import android.widget.ProgressBar;
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

    ////http://developer.alexanderklimov.ru/android/layout/swiperefreshlayout.php FIXME !!!!!!!!!!!
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

//    @BindView(R.id.progressBarOne)
//    ProgressBar progressBar;

    private String userId;
    private OneUsersPresenter presenter;
    private View rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        this.rootView = inflater.inflate(R.layout.fragment_one_users, viewGroup, false);

        return this.rootView;
    }


    @Override
    public void onStart() {
        super.onStart();

        initializeElements(this.rootView);
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
//        presenter = new PresenterOneUser(this, view.getContext());
//        presenter.startLoadData(this.userName);

    }





    /////////////////////////////////////////////////////
    // Methods of interfaces
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    @Override
    public void startLoad() {
        swipeRefreshLayout.setRefreshing(true);
//        loginText.setVisibility(View.GONE);
//        infoText.setVisibility(View.GONE);
//        avatarImage.setVisibility(View.GONE);
    }

    @Override
    public void endLoad() {
        swipeRefreshLayout.setRefreshing(false);
//        loginText.setVisibility(View.VISIBLE);
//        infoText.setVisibility(View.VISIBLE);
//        avatarImage.setVisibility(View.VISIBLE);
    }

    @Override
    public void setError(int number, @Nullable String message) {
//        loginText.setText(this.userName);

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
        RealmModelUser realmModelUser = (presenter.getRealmModelUsers()).get(0);
        List<RealmModelRep> realmModelRepList = presenter.getRealmModelReps();

        if (realmModelUser != null) {
            loginText.setText(realmModelUser.getLogin());
            Glide.with(this)
                    .load(realmModelUser.getAvatarUrl())
                    .into(avatarImage);
            avatarImage.setVisibility(View.VISIBLE);

            infoText.setText(getUserInfo(realmModelUser));
        }

        if(realmModelRepList != null && realmModelRepList.size() > 0){
            reposText.setText(getReposInfo(realmModelRepList));
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
                .delete((sb.length() - 3), (sb.length()-1))
                .append(".");


        return sb.toString();
    }
    //-----End-------------------------------------------


    /////////////////////////////////////////////////////
    // Getters and Setters
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------

    public void setUserId(String userId) {
//        this.userId = userId;
        this.presenter.setUserID(userId);
    }

    //-----End-------------------------------------------
}
