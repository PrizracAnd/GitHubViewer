package ru.demjanov_av.githubviewer.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.demjanov_av.githubviewer.R;
import ru.demjanov_av.githubviewer.presenters.MainView;
import ru.demjanov_av.githubviewer.presenters.MoreUsersPresenter;

public class MoreUsersFragment extends Fragment implements MainView {

    View view;
    MoreUsersPresenter presenter;


    //-----View elements variables begin-----------------
    @BindView(R.id.editText)
    EditText editText;

//    @BindView(R.id.btnLoad)
//    Button buttonLoad;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.tvLoad)
    TextView tvLoad;
    //-----View elements variables end-------------------


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        this.view = inflater.inflate(R.layout.fragment_more_users, viewGroup, false);

        return view;
    }


    /////////////////////////////////////////////////////
    // Methods of onStart
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
        this.presenter = new MoreUsersPresenter(this, view.getContext());

    }



    /////////////////////////////////////////////////////
    // Methods of onClick
    ////////////////////////////////////////////////////
    @OnClick(R.id.btnLoad)
    void onClick(){
        this.presenter.downloadUsers();
    }


    /////////////////////////////////////////////////////
    // Methods of interfaces
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    @Override
    public void startLoad() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void endLoad() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void setError(int number, @Nullable String message) {
        String msg = number + ": " + message;
        tvLoad.setText(msg);
    }

    @Override
    public void setData(int dataType) {
        tvLoad.setText(this.presenter.getUsers());
    }
    //-----End-------------------------------------------
}
