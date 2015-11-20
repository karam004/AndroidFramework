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

import android.preference.PreferenceCategory;
import android.preference.CheckBoxPreference;

import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import java.util.List;

public class QueueSettings extends SettingsPreferenceFragment {

	private static final String TAG = "ACSPROJECT";
    private static final String APP_LIST = "applist";
    private final SettingsObserver mSettingsObserver = new SettingsObserver();

    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "QueueSettings on create");

        addPreferencesFromResource(R.xml.queuing_setting);

        mContext = getActivity();

        final PackageManager pm = mContext.getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        // find catagory to add preference
        PreferenceCategory targetCategory = (PreferenceCategory)findPreference(APP_LIST);

        for (ApplicationInfo packageInfo : packages) {
            //create one check box for each app
            CheckBoxPreference checkBoxPreference = new CheckBoxPreference(mContext);
 
            if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {

                Log.d(TAG, "Installed package :" + packageInfo.packageName);
                //make sure each key is unique 
                checkBoxPreference.setKey(packageInfo.packageName);
                checkBoxPreference.setTitle(packageInfo.packageName);
                checkBoxPreference.setIcon(packageInfo.icon);
                checkBoxPreference.setChecked(true);

                targetCategory.addPreference(checkBoxPreference);
            }
            
        }

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
