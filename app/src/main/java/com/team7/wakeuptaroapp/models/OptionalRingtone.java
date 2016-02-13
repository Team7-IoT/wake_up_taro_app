package com.team7.wakeuptaroapp.models;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import com.team7.wakeuptaroapp.utils.AppLog;

/**
 * アラーム音未設定を考慮した{@link android.media.Ringtone}のモデル。
 *
 * @author Naotake.K
 */
public class OptionalRingtone {

    private Ringtone ringtone;

    private OptionalRingtone(Ringtone ringtone) {
        this.ringtone = ringtone;
    }

    public static OptionalRingtone of(Context context, Uri uri) {
        if (uri == null) {
            AppLog.d("Create OptionalRingtone as NullRingtone.");
            return new NullRingtone();
        } else {
            AppLog.d("Create OptionalRingtone.");
            return new OptionalRingtone(RingtoneManager.getRingtone(context, uri));
        }
    }

    /**
     * @see Ringtone#play()
     */
    public void play() {
        ringtone.play();
    }

    /**
     * @see Ringtone#stop()
     */
    public void stop() {
        ringtone.stop();
    }

    /**
     * {@link OptionalRingtone}のヌルオブジェクト。
     */
    private static final class NullRingtone extends OptionalRingtone {

        public NullRingtone() {
            super(null);
        }

        @Override
        public void play() {
            // NOP
        }

        @Override
        public void stop() {
            // NOP
        }
    }
}
