package com.indexyear.jd.dispatch.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

public class CrisisUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: Does this need to be 'exported'?
        // the logic here will decide what to do with a new Crisis event. for now, simply throw an
        // alertdialog

        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Crisis Alert");
        alertDialog.setMessage("A crisis has been received! \n ");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
