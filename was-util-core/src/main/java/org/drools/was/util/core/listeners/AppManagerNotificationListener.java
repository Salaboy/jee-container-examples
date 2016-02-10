/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.was.util.core.listeners;

import javax.management.NotificationFilterSupport;
import javax.management.NotificationListener;

/**
 *
 * @author salaboy
 */
public interface AppManagerNotificationListener extends NotificationListener {
    
    public NotificationFilterSupport getNotificationFilter();

    public String getFilterString();

}
