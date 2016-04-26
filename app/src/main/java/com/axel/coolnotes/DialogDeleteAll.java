package com.axel.coolnotes;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

public class DialogDeleteAll extends DialogFragment implements DialogInterface.OnClickListener {


    public Dialog onCreateDialog(Bundle SavedInstance)
    {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle("Clearing").setPositiveButton(R.string.yes, this)
                .setNegativeButton(R.string.no, this)
                .setMessage(R.string.AreYouSure);
        return adb.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
            {
                DBHelper.deleteAll(getContext());
                UpdateData upd = new UpdateData(NotesListFragment.DELETE_ALL_TAG);
                EventBus.getDefault().post(upd);
                UpdateData upd2 = new UpdateData(Settings.SETTING_CLOSE);
                EventBus.getDefault().post(upd2);
                break;
            }
            case Dialog.BUTTON_NEGATIVE:
            {
                onCancel(null);
                break;
            }
        }
        onDismiss(null);
    }
}
