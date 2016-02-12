/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.was.util.core;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.AdminClientFactory;
import com.ibm.websphere.management.application.AppConstants;
import com.ibm.websphere.management.application.AppManagement;
import com.ibm.websphere.management.application.AppManagementHelper;
import com.ibm.websphere.management.application.AppManagementProxy;
import com.ibm.websphere.management.application.client.AppDeploymentController;
import com.ibm.websphere.management.application.client.AppDeploymentException;
import com.ibm.websphere.management.application.client.AppDeploymentTask;
import com.ibm.websphere.management.exception.AdminException;
import com.ibm.websphere.management.exception.ConnectorException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.drools.was.util.core.listeners.AppManagerNotificationListener;

/**
 *
 * @author salaboy for more info about WAS look here:
 * https://www-01.ibm.com/support/knowledgecenter/#!/SSAW57_8.5.5/com.ibm.websphere.nd.doc/ae/tjmx_updating_app.html?cp=SSAW57_8.5.5
 */
public class WASContainerManager implements ContainerManager {

    private String host; // required
    private String port; // required
    private String target;
    private AdminClient soapClient;
    private AppManagement proxy;
    private List<AppManagerNotificationListener> listeners;

    public WASContainerManager(String host, String port) {
        this.host = host;
        this.port = port;
    }

    public void addNotificationListener(AppManagerNotificationListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<AppManagerNotificationListener>();
        }
        listeners.add(listener);
    }

    public boolean connect() {
        Properties config = new Properties();
        config.put(AdminClient.CONNECTOR_HOST, host);
        config.put(AdminClient.CONNECTOR_PORT, port);
        config.put(AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_SOAP);
        System.out.println("Config: " + config);
        try {
            soapClient = AdminClientFactory.createAdminClient(config);
            proxy = AppManagementProxy.getJMXProxyForClient(soapClient);
            initListeners();
            return true;
        } catch (ConnectorException ex) {
            Logger.getLogger(WASContainerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(WASContainerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private void initListeners() {
        try {
            ObjectName on = getObjectNameFilter();
            for (AppManagerNotificationListener l : listeners) {
                soapClient.addNotificationListener(on, l, l.getNotificationFilter(), l.getFilterString());
            }
        } catch (InstanceNotFoundException ex) {
            Logger.getLogger(WASContainerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConnectorException ex) {
            Logger.getLogger(WASContainerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedObjectNameException ex) {
            Logger.getLogger(WASContainerManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private ObjectName getObjectNameFilter() throws ConnectorException, MalformedObjectNameException {
        Iterator iter = soapClient.queryNames(new ObjectName(
                "WebSphere:type=AppManagement,*"), null).iterator();
        ObjectName on = (ObjectName) iter.next();
        System.out.println("ObjectName: " + on);
        return on;
    }

    public static String wrapWarIntoEar(String warFilePath, String earFileFinalLocation, String appName) {
        Hashtable conf = new Hashtable();
        conf.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault());
        conf.put(AppConstants.APPDEPL_WEBMODULE_CONTEXTROOT, "/");
        conf.put(AppConstants.APPDEPL_APPNAME, appName);
        conf.put(AppConstants.APPDEPL_MODULETYPE_WEB, appName);
        conf.put(AppConstants.APPDEPL_MODULE, appName + ".war");
        conf.put(AppConstants.APPDEPL_WEB_MODULE, appName + ".war");
        // Wrap WAR into EAR:
        //http://beyondadmin.blogspot.co.uk/2014/04/jmx-for-was-admins.html

        String out = null;
        try {
            out = AppManagementHelper.wrapModule(warFilePath, earFileFinalLocation, appName + ".war", conf);
            System.out.println("Wrapperd WAR into EAR stored in: " + out);
        } catch (AppDeploymentException ex) {
            Logger.getLogger(WASContainerManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return out;
    }

    public void listApps() {
        Hashtable options = new Hashtable();
        options.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault());
        Vector appList;
        try {
            appList = proxy.listApplications(options, null);
            for (Object o : appList) {
                System.out.println("App: " + o);
            }
        } catch (AdminException ex) {
            Logger.getLogger(WASContainerManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void undeployApp(String appName) {
        Hashtable options = new Hashtable();
        options.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault());
        try {
            proxy.uninstallApplication(appName, options, null);
        } catch (AdminException ex) {
            Logger.getLogger(WASContainerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void startApp(String appName) {
        Hashtable options = new Hashtable();
        options.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault());
        try {
            proxy.startApplication(appName, options, null);
        } catch (AdminException ex) {
            Logger.getLogger(WASContainerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void stopApp(String appName) {
        Hashtable options = new Hashtable();
        options.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault());
        try {
            proxy.stopApplication(appName, options, null);
        } catch (AdminException ex) {
            Logger.getLogger(WASContainerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void restartApp(String appName) {
        Hashtable options = new Hashtable();
        options.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault());
        try {
            proxy.stopApplication(appName, options, null);
            proxy.startApplication(appName, options, null);
        } catch (AdminException ex) {
            Logger.getLogger(WASContainerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
     * Deploys an application to a WAS instance 
     *   In IBM Docs you should look for Install an App: 
     *   https://www-01.ibm.com/support/knowledgecenter/api/content/nl/en-us/SSAW57_8.5.5/com.ibm.websphere.nd.doc/ae/tjmx_install_app.html
     */
    public void deployApp(String earPath, String appName, String target) {
        Hashtable prefs = new Hashtable();
        prefs.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault());

        Properties defaultBnd = new Properties();
        prefs.put(AppConstants.APPDEPL_DFLTBNDG, defaultBnd);
        defaultBnd.put(AppConstants.APPDEPL_DFLTBNDG_VHOST, "default_host");

        AppDeploymentController controller;
        try {
            controller = AppDeploymentController
                    .readArchive(earPath, prefs);
            AppDeploymentTask task = controller.getFirstTask();
            while (task != null) {
                // Populate the task data.
                String[][] data = task.getTaskData();
                // Manipulate task data which is a table of stringtask.
                task.setTaskData(data);
                task = controller.getNextTask();
            }
            controller.saveAndClose();
            Hashtable options = controller.getAppDeploymentSavedResults();
            Hashtable module2server = new Hashtable();
            module2server.put("*", target);
            options.put(AppConstants.APPDEPL_MODULE_TO_SERVER, module2server);
            options.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault());
            options.put(AppConstants.APPDEPL_ARCHIVE_UPLOAD, true);
            try {
                proxy.installApplication(earPath, appName, options, null);
            } catch (AdminException ex) {
                Logger.getLogger(WASContainerManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (AppDeploymentException ex) {
            Logger.getLogger(WASContainerManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
