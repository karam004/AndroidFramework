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

import android.service.notification.ZenModeConfig;

import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import java.util.List;
import java.util.Objects;

/**
* ACSPROJECT
* Class which extends Preference fragment, responsible for 
* capturing quing setting and maintaing state
**/
public class QueueSettings extends SettingsPreferenceFragment {

	private static final String TAG = "ACSPROJECT";
    private static final String APP_LIST = "applist";
    private static final String KEY_ENABLE_QUEUING = "enable_queuing";
    private static final String KEY_QUEUE_LIMIT = "queue_limit";
    private final SettingsObserver mSettingsObserver = new SettingsObserver();

    private Context mContext;

    private SwitchPreference mEnableQueuing;
    private PreferenceCategory mtargetCategory;
    private DropDownPreference mQueueLimit;
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

                    if (success) {
                        updateControls();
                    }

                }catch (Exception e) {
                   Log.w(TAG, "Error calling NotificationManagerService", e);
                   return false;
                }
                return success;
            }
        });

        // Queue Limit
        mQueueLimit = (DropDownPreference) findPreference(KEY_QUEUE_LIMIT);
        mQueueLimit.addItem(R.string.zen_mode_queue_limit_unlimited, ZenModeConfig.LIMIT_UNLIMITED);
        mQueueLimit.addItem(R.string.zen_mode_queue_limit_one, ZenModeConfig.LIMIT_ONE);
        mQueueLimit.addItem(R.string.zen_mode_queue_limit_two, ZenModeConfig.LIMIT_TWO);
        mQueueLimit.addItem(R.string.zen_mode_queue_limit_three, ZenModeConfig.LIMIT_THREE);
        mQueueLimit.setCallback(new DropDownPreference.Callback() {
            @Override
            public boolean onItemSelected(int pos, Object newValue) {
                final int val = (Integer) newValue;
                Log.d(TAG, "Limit option " + val);

                try {
                    return nm.setQueueLimit(val);
                }catch (Exception e) {
                   Log.w(TAG, "Error calling NotificationManagerService", e);
                   return false;
                }
        
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
                    checkBoxPreference.setTitle(packageInfo.packageName.
                                                substring(packageInfo.packageName.
                                                lastIndexOf('.')+1));
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
                boolean allowQueuing = nm.getZenModeConfig().allowQueuing;
                mEnableQueuing.setChecked(allowQueuing);

                mQueueLimit.setSelectedValue(nm.getZenModeConfig().queueLimit);
                mQueueLimit.setEnabled(allowQueuing);

                if (mtargetCategory != null) {
                    mtargetCategory.setEnabled(allowQueuing);
                }
            } catch (Exception e) {

                mEnableQueuing.setChecked(false);
            }
        }else {
            if (mtargetCategory != null) {
                mtargetCategory.setEnabled(false);
            }

            if (mQueueLimit != null) {
                mQueueLimit.setEnabled(false);
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
