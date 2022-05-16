## Troubleshooting jUDDI

Here are some tips to help you troubleshoot problems with jUDDI, jUDDI-GUI, jUDDI Client and more.

### jUDDI Web Services, juddiv3.war

#### Enable debugging logging

You can adjust the logging level to provide additional output for troubleshooting purposes. To do so, see the Administration Guide, Logging.

### jUDDI GUI, juddi-gui.war

Problem: Can't authentication from juddi-gui's top right hand side login box to juddiv3.war services
Solutions:

* Check the server's log files for both juddi, juddi-gui and the server itself for error messages. This can sometimes be caused by the lack of Java Crypto Extensions (Oracle/Sun JRE/JDK only).
* Check juddi-gui's configuration page at http://localhost:8080/juddi-gui/settings.jsp, confirm that the URL's that are referenced for the UDDI services are correct and accessible from the server hosting juddi-gui.
* Make sure you're using a valid username/password ;)
* Increase the logging level of jUDDI by changing the commons-logging.properties file
* If you're having problems with Email delivery of subscription updates, enable debug logging by setting config/uddi/mail/debug=true in juddiv3.xml

### jUDDI Client Java
 
#### Enable debugging logging

You can adjust the logging level to provide additional output for troubleshooting purposes. To do so, see the Administration Guide, Logging.

### jUDDI Client .NET

Components based on jUDDI's Client for the .NET Framework can configure logging from their application's config file. This is usually app.config or web.config. To configure logging, the following three settings must appear in the `configuration/appSetttings` section.

````
	<!-- DEBUG, INFO, WARN, ERROR -->
	<add key="org.apache.juddi.v3.client.log.level" value="INFO" />
    <!-- options are CONSOLE, EVENTLOG, FILE multiple values can be specified, comma delimited.
		Notes for EVENTLOG, you must run the juddi-installer as admin before running-->
    <add key="org.apache.juddi.v3.client.log.target" value="CONSOLE" />
	<!-- only used when target=FILE -->
    <add key="org.apache.juddi.v3.client.log.logger.file" value="pathToOutputFile" />
````

If nothing is defined, the default log level is "WARN" and the target is "CONSOLE" which is standard out. 


### Getting help

There are many different ways to get help with your jUDDI instance. Please refer to the following URLs for more information.

* jUDDI Home Page http://juddi.apache.org/ 
* User Guide http://juddi.apache.org/docs/3.x/userguide/html/index.htmlnavbar.help.userguide 
* Developer Guide http://juddi.apache.org/docs/3.x/devguide/html/index.htmlnavbar.help.devguide 
* Developer API Documentation http://juddi.apache.org/docs.html 
* jUDDI Wiki http://wiki.apache.org/juddi 
* jUDDI Issue/Bug Tracker http://juddi.apache.org/issue-tracking.html 
* jUDDI User and Developer Mailing List http://juddi.apache.org/mailing-list.html
* jUDDI Source Code http://svn.apache.org/viewvc/juddi/ 

