set MVN_HOME=C:\Prog\apache-maven-3.3.9
set JBOSS_PATH=C:\Prog\jboss-as-7.1.1.Final
cd /d %~dp0
"%MVN_HOME%\bin\mvn" deploy:deploy-file -Dfile="%JBOSS_PATH%\bin\client\jboss-client.jar" -DpomFile=jboss-client.pom    -DrepositoryId=local    -Durl=file:///C:\Users\didier\.m2\repository
pause