package ru.demjanov_av.githubviewer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ru.demjanov_av.githubviewer.views.MoreUsersFragment;
import ru.demjanov_av.githubviewer.views.OneUsersFragment;
import ru.demjanov_av.githubviewer.views.PswFragment;


public class MainActivity extends AppCompatActivity implements PswFragment.EnterCodeSuccess, MoreUsersFragment.ClickListenerUsers {

    //-----Class variables begin--------------------------
    private FragmentManager fm;
    private byte[] keySpecBytes = new byte[0];
    //-----Class variables end----------------------------


    /////////////////////////////////////////////////////
    // Method onCreate
    ////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeElements();

    }


    /////////////////////////////////////////////////////
    // Method initializeElements
    ////////////////////////////////////////////////////
    private void initializeElements() {
        fm = getSupportFragmentManager();

        //  переписано именно так потому, что при старте приложения ВСЕГДА затавляем вводить ПИН
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if(fragment != null){
            fm.beginTransaction()
                    .remove(fragment)
                    .commit();
        }


        fm.beginTransaction()
                .add(R.id.fragment_container, new PswFragment())
                .commit();
    }


    public byte[] getKeySpecBytes() {
        return keySpecBytes;
    }

    /////////////////////////////////////////////////////
    // Method onDestroy
    ////////////////////////////////////////////////////
    @Override
    public void onDestroy() {


        super.onDestroy();
    }


    /////////////////////////////////////////////////////
    // Methods of interfaces
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    @Override
    public void onEnterCode(byte[] keySpecBytes) {
        this.keySpecBytes = keySpecBytes;

        MoreUsersFragment muf = new MoreUsersFragment();
        fm.beginTransaction()
                .replace(R.id.fragment_container, muf)
                .commit();
    }

    @Override
    public void onClickUsers(String userId) {
        OneUsersFragment oneUsersFragment = new OneUsersFragment();

        fm.beginTransaction()
                .replace(R.id.fragment_container, oneUsersFragment)
                .addToBackStack(null)
                .commit();

        oneUsersFragment.setUserId(userId);
    }
    //-----End-------------------------------------------
}
