package com.rba.arctouch.utils;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.rba.arctouch.commons.Constants;
import com.rba.arctouch.listener.SnackbarListener;

public class SnackbarUtils {
    public static void showNoConnectionSnackBar(View view, String message, final SnackbarListener listener) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_INDEFINITE)
                .setAction(Constants.RETRY, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onResponse(true);
                    }
                });

        snackbar.show();
    }

    public static void showSnackBar(View view, String message) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_SHORT);

        snackbar.show();
    }
}
