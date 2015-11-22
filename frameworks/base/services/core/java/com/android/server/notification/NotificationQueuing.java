package com.android.server.notification;


import java.util.Queue;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;

import android.content.Context;
import android.util.Log;
import android.app.NotificationManager;
import android.app.Notification;



/**
*   Class to stores the notifications when Queuing is enabled
*
*/
public class NotificationQueuing {

    private static String TAG = "ACSPROJECT";
    // queue to hold notification
    private Queue<NotificationElements> notificationQueue;
    // Application specific preference  --key as package name of APP
    private Set<String> preferenceSet;


    public NotificationQueuing() {
        Log.d(TAG, "initializing notification queue");
        notificationQueue = new LinkedList<>();
        preferenceSet = new HashSet<>();
    }

    public static class NotificationElements {
        String pkg;
        String opPkg;
        int callingUid;
        int callingPid;
        String tag;
        int id;
        Notification notification;
        int[] idOut;
        int incomingUserId;

    }

    public void add(String pkg, String opPkg, int callingUid,
            int callingPid, String tag, int id, Notification notification,
            int[] idOut, int incomingUserId) {

        Log.d(TAG, "Enquing request for " + pkg);
        if (preferenceSet.contains(pkg)) {
            Log.d(TAG, "Enquing disabled for " + pkg);
            return;
        }

        NotificationElements notificationElements = new NotificationElements();
        notificationElements.pkg = pkg;
        notificationElements.opPkg = opPkg;
        notificationElements.callingUid = callingUid;
        notificationElements.callingPid = callingPid;
        notificationElements.tag = tag;
        notificationElements.id = id;
        notificationElements.notification = notification;
        notificationElements.idOut = idOut;
        notificationElements.incomingUserId = incomingUserId;

        notificationQueue.add(notificationElements);

    }

    public Queue<NotificationElements> getQueue() {
        return notificationQueue;
    }

    public boolean setAppPreference(String key , boolean val) {
        if (key == null) {
            return false;
        }
        Log.d(TAG, "app preference for "+ key + " as " + val);

        if (val) {
            preferenceSet.remove(key);    
        }else {
            preferenceSet.add(key);
        }

        return true;
    }

    public boolean isQueuingEnabledForApp(String key){
        if (preferenceSet.contains(key)) {
            return false;
        }
        return true;
    }

}
