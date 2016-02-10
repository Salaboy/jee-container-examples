/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.was.util.core;

import com.ibm.websphere.management.application.AppNotification;
import org.drools.was.util.core.listeners.BaseNotificationListener;

/**
 *
 * @author salaboy
 */
public interface MainApp {

    public static void main(String[] args) throws InterruptedException {
        String appName = "MyApp";
        String earPath = "/Users/salaboy/Projects/jee-container-examples/drools-was-container-java/myear.ear";
        String target = "WebSphere:cell=ubuntuNode01Cell,node=ubuntuNode01,server=server1";
        WASContainerManager container = new WASContainerManager("10.211.55.3", "8880");
        
        container.addNotificationListener(new BaseNotificationListener( "Install: " + appName, AppNotification.INSTALL));
       // container.addNotificationListener(new BaseNotificationListener( "Install: " + appName, AppNotification.UNINSTALL));
        
        if(container.connect()){
            System.out.println(">> Listing Apps: ");
            container.listApps();
            
            System.out.println(">> Deploying App: ");
            container.deployApp(earPath, appName, target);
            Thread.sleep(100000);
            System.out.println(">> Listing Apps: ");
            container.listApps();
            
            System.out.println(">> UnDeploying App: ");
            container.undeployApp(appName);
            Thread.sleep(100000);
            System.out.println(">> Listing Apps: ");
            container.listApps();
        }
    }
}
