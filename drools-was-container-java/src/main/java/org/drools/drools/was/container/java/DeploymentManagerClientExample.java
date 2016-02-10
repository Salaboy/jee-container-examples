/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.drools.was.container.java;

/**
 *
 * @author salaboy
 */
import java.util.Properties;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.AdminClientFactory;
import com.ibm.websphere.management.application.AppConstants;
import com.ibm.websphere.management.application.AppManagement;
import com.ibm.websphere.management.application.AppManagementHelper;
import com.ibm.websphere.management.application.AppManagementProxy;
import com.ibm.websphere.management.application.AppNotification;
import com.ibm.websphere.management.application.client.AppDeploymentController;
import com.ibm.websphere.management.application.client.AppDeploymentException;
import com.ibm.websphere.management.application.client.AppDeploymentTask;
import com.ibm.websphere.management.exception.AdminException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import javax.management.Notification;
import javax.management.NotificationFilterSupport;

public class DeploymentManagerClientExample {

    public static void main(String[] args) throws AppDeploymentException, AdminException, InterruptedException, Exception {
//https://www-01.ibm.com/support/knowledgecenter/api/content/nl/en-us/SSAW57_8.5.5/com.ibm.websphere.nd.doc/ae/tjmx_install_app.html
        String appDir = "/Users/salaboy/Projects/jee-container-examples/drools-was-web-app/target";
        String appName = "drools-was-web-app-1.0-SNAPSHOT";
        String warFile = appDir + "/" + appName + ".war";
        String earFileLoc = "myear.ear";
        Hashtable prefs = new Hashtable();
        prefs.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault());

        Properties defaultBnd = new Properties();
        prefs.put(AppConstants.APPDEPL_DFLTBNDG, defaultBnd);
        defaultBnd.put(AppConstants.APPDEPL_DFLTBNDG_VHOST, "default_host");
        
     Hashtable conf = new Hashtable();   
   conf.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault());        
   conf.put(AppConstants.APPDEPL_WEBMODULE_CONTEXTROOT, "/");
   conf.put(AppConstants.APPDEPL_APPNAME, "myear");
   conf.put(AppConstants.APPDEPL_MODULETYPE_WEB,appName);
   conf.put(AppConstants.APPDEPL_MODULE, appName+".war");
   conf.put(AppConstants.APPDEPL_WEB_MODULE, appName+".war");
 
   //Wrap WAR into EAR:
   //http://beyondadmin.blogspot.co.uk/2014/04/jmx-for-was-admins.html
   
//   String out = AppManagementHelper.wrapModule(warFile, earFileLoc, appName+".war", conf);
//        System.out.println("OUT: "+ out);
        
        AppDeploymentController controller = AppDeploymentController
                .readArchive("/Users/salaboy/Projects/jee-container-examples/drools-was-container-java/"+"myear.ear", prefs);
        
        AppDeploymentTask task = controller.getFirstTask();
        while (task != null) {
// Populate the task data.
            String[][] data = task.getTaskData();
// Manipulate task data which is a table of stringtask.
            task.setTaskData(data);
            task = controller.getNextTask();
        }
        controller.saveAndClose();
        
         // Thread.sleep(10000); // Wait so that the program does not end.

        Hashtable options = controller.getAppDeploymentSavedResults();
// The previous options table contains the module-to-server relationship if it was set by
// using tasks.
//Preparation phase: End

// Get a connection to the product.
        String host = "10.211.55.3";
        String port = "8880";
        String target = "WebSphere:cell=ubuntuNode01Cell,node=ubuntuNode01,server=server1";

        Properties config = new Properties();
        config.put(AdminClient.CONNECTOR_HOST, host);
        config.put(AdminClient.CONNECTOR_PORT, port);
        config.put(AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_SOAP);
        System.out.println("Config: " + config);
        AdminClient _soapClient = AdminClientFactory.createAdminClient(config);

// Create the application management proxy, AppManagement.
        AppManagement proxy = AppManagementProxy.getJMXProxyForClient(_soapClient);

        // If code for the preparation phase has been run, then you already have the options table.
// If not, create a new table and add the module-to-server relationship to it by uncommenting
// the next statement.
//Hashtable options = new Hashtable();
        Hashtable module2server = new Hashtable();
        module2server.put ("*", target);
        options.put (AppConstants.APPDEPL_MODULE_TO_SERVER, module2server);
        options.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault());
        options.put(AppConstants.APPDEPL_ARCHIVE_UPLOAD, true);
// Uncomment the following statements to add the module to the server relationship table if 
//  the preparation phase does not collect it.
//Hashtable module2server = new Hashtable();
//module2server.put ("*", target);
//options.put (AppConstants.APPDEPL_MODULE_TO_SERVER, module2server);
//Create the notification filter for listening to installation events.
        NotificationFilterSupport myFilter = new NotificationFilterSupport();
        myFilter.enableType(AppConstants.NotificationType);

//Add the listener.
        NotificationListener listener = new AListener(_soapClient,
                myFilter, "Install: " + appName, AppNotification.INSTALL);

// Install the application.
        proxy.installApplication("/Users/salaboy/Projects/jee-container-examples/drools-was-container-java/"+"myear.ear", "My App", options, null);
        System.out.println("After install App is called..");

// Wait for some timeout. The installation application programming interface (API) is 
//  asynchronous and so returns immediately.
// If the program does not wait here, the program ends.
        Thread.sleep(300000); // Wait so that the program does not end.

    }

}

class AListener implements NotificationListener {

    AdminClient _soapClient;
    NotificationFilterSupport myFilter;
    Object handback;
    ObjectName on;
    String eventTypeToCheck;

    public AListener(AdminClient cl, NotificationFilterSupport fl,
            Object h, String eType) throws Exception {
        _soapClient = cl;
        myFilter = fl;
        handback = h;
        eventTypeToCheck = eType;

        Iterator iter = _soapClient.queryNames(new ObjectName(
                "WebSphere:type=AppManagement,*"), null).iterator();
        on = (ObjectName) iter.next();
        System.out.println("ObjectName: " + on);
        _soapClient.addNotificationListener(on, this, myFilter, handback);
    }

    public void handleNotification(Notification notf, Object handback) {
        AppNotification ev = (AppNotification) notf.getUserData();
        System.out.println("!! JMX event Recd: (handback obj= " + handback + "): " + ev);

        //When the installation is done, remove the listener and quit.
        if (ev.taskName.equals(eventTypeToCheck)
                && (ev.taskStatus.equals(AppNotification.STATUS_COMPLETED)
                || ev.taskStatus.equals(AppNotification.STATUS_FAILED))) {
            try {
                _soapClient.removeNotificationListener(on, this);
            } catch (Throwable th) {
                System.out.println("Error removing listener: " + th);
            }
            System.exit(0);
        }
    }
}
