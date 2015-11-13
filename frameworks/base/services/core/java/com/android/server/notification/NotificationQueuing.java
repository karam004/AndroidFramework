package com.android.server.notification;


import java.util.Queue;
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
    private static int limit = 1000;

    private Queue<NotificationElements> notificationQueue;


    public NotificationQueuing() {
        Log.d(TAG, "initializing notification queue");
        notificationQueue = new LinkedList<>();
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

        if (notificationQueue.size() == limit) {
            Log.d(TAG, "Notification Queue full, drooping last one");
            notificationQueue.remove();
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

    public void setLimit(int lt) {
        limit = lt;
        Log.d(TAG, "setting queue limit to " + lt);
        if (notificationQueue.size() > lt) {
            Log.d(TAG,"limit less than size, dropping last " + (notificationQueue.size() - lt) + " notification");
            while(notificationQueue.size() != lt){
                notificationQueue.remove();
            }
        }
    }

}
