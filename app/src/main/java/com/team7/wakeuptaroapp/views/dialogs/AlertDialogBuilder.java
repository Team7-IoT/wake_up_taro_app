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
     * Used by {@code new AlertDialogBuilder.ConcreteDialog(context).show();}
     */
    private AlertDialogBuilder() {
        // NOP
    }

    /**
     * Bluetooth が無効である旨を通知するダイアログ。
     */
    public static class DisabledBluetooth extends Builder {

        public DisabledBluetooth(Context context) {
            super(context);
        }

        public void show() {
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle(R.string.title_dialog_disabled_bluetooth)
                    .setMessage(R.string.message_dialog_disabled_bluetooth)
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
                    .setTitle(R.string.title_dialog_usated_device)
                    .setMessage(R.string.message_dialog_usated_device)
                    .setPositiveButton("OK", null)
                    .create();

            dialog.show();
        }
    }

    /**
     * アラーム登録時にバリデーションチェックに失敗した旨を通知するダイアログ。
     */
    public static class ValidationFailureDialog extends Builder {

        private int messageId;

        public ValidationFailureDialog(Context context) {
            super(context);
        }

        public ValidationFailureDialog cause(int messageId) {
            this.messageId = messageId;
            return this;
        }

        public void show() {
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle(R.string.title_dialog_alarm_validation_failure)
                    .setMessage(messageId)
                    .setPositiveButton("OK", null)
                    .create();

            dialog.show();
        }
    }

    private static abstract class Builder {

        protected final Context context;

        public Builder(Context context) {
            this.context = context;
        }
    }
}
