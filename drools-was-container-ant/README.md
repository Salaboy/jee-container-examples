This project shows how to automate deployments and app lifecycle by using Java Ant integration
against WebSphere 8.5

An ant file is provided to just run the ant tasks without using Java. You can run this wsdeploy.xml ant task file 
locally in your WAS installation by running this:

From

/home/parallels/IBM/WebSphere/AppServer/profiles/AppSrv01/bin

run:

./ws_ant.sh -buildfile /home/parallels/Projects/jee-container-examples/drools-was-container-ant/src/main/resources/wsdeploy.xml deploy

The wsdeploy.xml file contains different target actions:
- listApps
- deploy
- undeploy
- startApplication
- stopApplication
- update
- startServer
- stopServer
- 




