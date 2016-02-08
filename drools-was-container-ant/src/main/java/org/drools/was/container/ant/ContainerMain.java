package org.drools.was.container.ant;

import com.ibm.websphere.ant.tasks.InstallApplication;

/**
 *
 * @author salaboy
 */
public class ContainerMain {

    public static void main(String[] args) {
        String appDir = "/home/parallels/Projects/jee-container-examples/drools-was-web-app/target";
        String appName = "drools-was-web-app-1.0-SNAPSHOT";
        InstallApplication installApp = new InstallApplication();
        installApp.setEar(appDir + "/" + appName + ".war");
        installApp.setWasHome("/home/parallels/IBM/WebSphere/AppServer");
        installApp.setOptions("-appname "+appName);
        installApp.setDebug(true);
       
        installApp.execute();
    }

}
