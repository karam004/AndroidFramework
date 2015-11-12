package com.android.settings.notification;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class QueueSettings extends SettingsPreferenceFragment {

	private static final String TAG = "ACSPROJECT";

    private final SettingsObserver mSettingsObserver = new SettingsObserver();

    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "QueueSettings on create");
        addPreferencesFromResource(R.xml.queuing_setting);

        mContext = getActivity();

    }

    @Override
    public void onResume() {
        super.onResume();
        mSettingsObserver.register(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSettingsObserver.register(false);
    }


    // === Callbacks ===

    private final class SettingsObserver extends ContentObserver {
        public SettingsObserver() {
            super(new Handler());
        }

        public void register(boolean register) {
            final ContentResolver cr = getContentResolver();
            if (register) {
            } else {
                cr.unregisterContentObserver(this);
            }
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
        }
    }

}
