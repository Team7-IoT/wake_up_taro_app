package com.team7.wakeuptaroapp.views.dialogs;

import android.app.AlertDialog;
import android.content.Context;

import com.team7.wakeuptaroapp.R;

/**
 * 起こしタロウ内で扱う {@link AlertDialog} を生成するビルダー。
 *
 * @author Naotake.K
 */
public class AlertDialogBuilder {

    /**
     * Bluetooth が無効である旨を通知するダイアログ。
     */
    public static class DisabledBluetooth extends Builder {

        public DisabledBluetooth(Context context) {
            super(context);
        }

        public void show() {
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle(getLabel(R.string.title_dialog_disabled_bluetooth))
                    .setMessage(getLabel(R.string.message_dialog_disabled_bluetooth))
                    .setPositiveButton("OK", null)
                    .create();

            dialog.show();
        }
    }

    /**
     * 疎通検証を行った親機が未設定である旨を通知するダイアログ。
     */
    public static class UnknownDevice extends Builder {

        public UnknownDevice(Context context) {
            super(context);
        }

        public void show() {
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle(getLabel(R.string.title_dialog_usated_device))
                    .setMessage(getLabel(R.string.message_dialog_usated_device))
                    .setPositiveButton("OK", null)
                    .create();

            dialog.show();
        }
    }

    private static abstract class Builder {

        protected Context context;

        public Builder(Context context) {
            this.context = context;
        }

        protected String getLabel(int resId) {
            return context.getString(resId);
        }
    }
}
