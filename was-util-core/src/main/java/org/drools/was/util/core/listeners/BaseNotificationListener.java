/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.was.util.core.listeners;

import com.ibm.websphere.management.application.AppConstants;
import com.ibm.websphere.management.application.AppNotification;
import javax.management.Notification;
import javax.management.NotificationFilterSupport;

/**
 *
 * @author salaboy
 */
public class BaseNotificationListener implements AppManagerNotificationListener {

    private NotificationFilterSupport myFilter;
    private String filter;
    private String eventTypeToCheck;

    public BaseNotificationListener(
            String filter, String eventType) {
        myFilter = new NotificationFilterSupport();
        myFilter.enableType(AppConstants.NotificationType);
        this.filter = filter;
        this.eventTypeToCheck = eventType;

    }

    @Override
    public NotificationFilterSupport getNotificationFilter() {
        return myFilter;
    }

    @Override
    public String getFilterString() {
        return filter;
    }

    @Override
    public void handleNotification(Notification notf, Object handback) {
        AppNotification ev = (AppNotification) notf.getUserData();
        System.out.println("!! JMX event Recd: (handback obj= " + handback + "): " + ev);
    }
}
