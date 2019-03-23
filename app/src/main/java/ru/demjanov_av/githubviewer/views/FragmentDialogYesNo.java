package ru.demjanov_av.githubviewer.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import ru.demjanov_av.githubviewer.R;

public class FragmentDialogYesNo extends DialogFragment implements DialogInterface.OnClickListener{

    //-----Class variables begin-------------------------
    private PswFragment fragment;
//    private String title;
//    private String message;
    //-----Class variables begin-------------------------


    //---------------------------------------------------
    //---------------------------------------------------

    /////////////////////////////////////////////////////
    // Interface BackDialogYN
    ////////////////////////////////////////////////////
    public interface BackDialogYN{
        void onBackDYN(boolean yes);
    }

    //---------------------------------------------------
    //---------------------------------------------------


    /////////////////////////////////////////////////////
    // Getters and Setters
    ////////////////////////////////////////////////////
    //-----Begin----------------------------------------
    public void setFragment(PswFragment fragment) {
        this.fragment = fragment;
    }
    //-----End------------------------------------------


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.alert)
                .setPositiveButton(R.string.yes, this)
                .setNegativeButton(R.string.no, this)
                .setMessage(R.string.query_reset_psw);
        return adb.create();
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which){
            case Dialog.BUTTON_POSITIVE:
                this.fragment.onBackDYN(true);
                break;
            case Dialog.BUTTON_NEGATIVE:
                this.fragment.onBackDYN(false);
                break;
            default:
                this.fragment.onBackDYN(false);
                break;
        }
        dismiss();
    }


    @Override
    public void onCancel(DialogInterface dialogInterface){
        super.onCancel(dialogInterface);
        this.fragment.onBackDYN(false);
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface){
        super.onDismiss(dialogInterface);
    }
}
