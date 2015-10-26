package com.android.server.notification;


import java.util.Queue;
import java.util.LinkedList;


import android.util.Log;



/**
*   Class to stores the notifications when Queuing is enabled
*   
*/
public class NotificationQueuing {

    private static String TAG = "NotificationQueuing";
    
    private Queue<NotificationElements> notificationQueue;  


    public NotificationQueuing() {
        Log.d(TAG, "initializing notification queue");
        notificationQueue = new LinkedList<>();
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
}
