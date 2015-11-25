package com.android.settings.notification;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.ServiceManager;
import android.os.Handler;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.telephony.TelephonyManager;
import android.app.INotificationManager;
import android.util.Log;

import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.CheckBoxPreference;
import android.preference.SwitchPreference;
import android.preference.Preference.OnPreferenceChangeListener;

import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import java.util.List;
import java.util.Objects;

public class QueueSettings extends SettingsPreferenceFragment {

	private static final String TAG = "ACSPROJECT";
    private static final String APP_LIST = "applist";
    private static final String KEY_ENABLE_QUEUING = "enable_queuing";
    private final SettingsObserver mSettingsObserver = new SettingsObserver();

    private Context mContext;

    private SwitchPreference mEnableQueuing;
    private PreferenceCategory mtargetCategory;
    private INotificationManager nm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "QueueSettings on create");

        addPreferencesFromResource(R.xml.queuing_setting);

        mContext = getActivity();
        nm = INotificationManager.Stub.asInterface(
                        ServiceManager.getService(Context.NOTIFICATION_SERVICE));

        // Enable/Disable button
        mEnableQueuing = (SwitchPreference) findPreference(KEY_ENABLE_QUEUING);
        mEnableQueuing.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final boolean val = (Boolean) newValue;
                Log.d(TAG, "onPrefChange allowQueuing=" + val);

                boolean success = false;
                try {

                    if (val) {
                        success = nm.setQueingTrue();
                    }else {
                        success = nm.setQueingFalse();
                    }

                }catch (Exception e) {
                   Log.w(TAG, "Error calling NotificationManagerService", e);
                   return false;
                }
                return success;
            }
        });




        // List of Apps
        
        final PackageManager pm = mContext.getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        // find catagory to add preference
        mtargetCategory = (PreferenceCategory)findPreference(APP_LIST);

        for (ApplicationInfo packageInfo : packages) {

            CheckBoxPreference checkBoxPreference = null;

            // try to find the checkBoxPreference
            checkBoxPreference = (CheckBoxPreference)mtargetCategory.findPreference(packageInfo.packageName);

            if (checkBoxPreference == null) {
                //create one check box for each app
                checkBoxPreference = new CheckBoxPreference(mContext);
     
                if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {

                    Log.d(TAG, "Installed package :" + packageInfo.packageName);
                    //make sure each key is unique 
                    checkBoxPreference.setKey(packageInfo.packageName);
                    checkBoxPreference.setTitle(packageInfo.applicationInfo.processName);
                    checkBoxPreference.setIcon(packageInfo.icon);
                    checkBoxPreference.setChecked(true);

                    mtargetCategory.addPreference(checkBoxPreference);
                    
                }

                checkBoxPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                        final boolean val = (Boolean) newValue;

                        Log.d(TAG, "checkBoxPreference =" + val);
                        boolean success = false;
                        try {

                            success = nm.setQueuingPreference(preference.getKey(), val);

                        }catch (Exception e) {
                           Log.w(TAG, "Error calling NotificationManagerService", e);
                           return false;
                        }
                        return success;
                    }
                });
            }
        }

        // persist controls
        updateControls();

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


    private void updateControls(){
        if(mEnableQueuing != null)
        {   
            try {
               mEnableQueuing.setChecked(nm.getZenModeConfig().allowQueuing);
            } catch (Exception e) {

                mEnableQueuing.setChecked(false);
            }
        }
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
