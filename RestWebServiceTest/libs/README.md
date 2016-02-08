# WebSphere 8.5 libs directory. 

If you want to compile this project locally you will need to: 


1) You will need to copy these libraries to this libs/ directory:
IBM/WebSphere/AppServer/plugins/ -> com.ibm.ws.runtime.jar

2) and then install them with Maven:

mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file  
-Dfile=com.ibm.ws.runtime.jar -DgroupId=com.ibm.ws -DartifactId=runtime -Dversion=8.5.0 
-Dpackaging=jar -DlocalRepositoryPath=<your directory here>/jee-container-examples/drools-was-container-ant/libs/



