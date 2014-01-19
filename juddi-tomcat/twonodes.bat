rem This script will setup a two node example using two tomcat instances with independent databases and containers
rem windows only
rd /q /s .\target\tomcat\apache-tomcat-6.0.26Node2\
rd /q /s .\target\tomcat\apache-tomcat-6.0.26\bin\target
xcopy /E /Y .\target\tomcat\apache-tomcat-6.0.26 .\target\tomcat\apache-tomcat-6.0.26Node2\
copy /Y juddiv3Node2.xml target\tomcat\apache-tomcat-6.0.26Node2\webapps\juddiv3\WEB-INF\classes\juddiv3.xml
copy /Y serverNode2.xml target\tomcat\apache-tomcat-6.0.26Node2\conf\server.xml
copy /Y uddiNode2.xml target\tomcat\apache-tomcat-6.0.26Node2\webapps\juddi-gui\WEB-INF\classes\META-INF\uddi.xml
cd target\tomcat\apache-tomcat-6.0.26\bin
call startup.bat
cd ..\..\apache-tomcat-6.0.26Node2\bin
call startup.bat
