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
    private Context context;
    
    private Queue<NotificationElements> notificationQueue;  


    public NotificationQueuing(Context context) {
        Log.d(TAG, "initializing notification queue");
        notificationQueue = new LinkedList<>();
        this.context = context;
    }

    private static class NotificationElements {
        private Notification notification;
        private String tag;
        private int id;

        public Notification getNotification() {
            return notification;
        }

        public String getTag() {
            return tag;
        }

        public int getId() {
            return id;
        }

        public void setNotification(Notification notification) {
            this.notification = notification;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public void add(String tag, int id, Notification notification ) {
        NotificationElements notificationElements = new NotificationElements();
        notificationElements.setTag(tag);
        notificationElements.setId(id);
        notificationElements.setNotification(notification);

        notificationQueue.add(notificationElements);
    }

    public Queue<NotificationElements> getQueue() {
        return notificationQueue;
    }


    public void pollNotification() {

        NotificationManager mNotificationManager = 
       (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        while(!notificationQueue.isEmpty()) {
            NotificationElements mNotificationElements = notificationQueue.poll();
            mNotificationManager.notify(mNotificationElements.getTag, 
                                        mNotificationElements.getId,
                                        mNotificationElements.getNotification);
        }
    }
}
