The jUDDI TCK Runner is a standalone application used for performing live tests on a UDDI server.

Features
-supports UDDI and HTTP style authentication
-has test cases for the majority of the UDDI endpoints
-has test cases for jUDDI specific endpoints

Notes: 
-This application will start a web service using Jetty.
-The hostname of the machine running the test must be resolvable by the UDDI server
-This application requires a number of usernames and passwords for users accounts used on the UDDI server. You'll have to create them ahead of time
-This application will optionally access a SMTP mail server and scan for subscription alerts send by a UDDI server, if supported
-Endpoint URLs are loaded from uddi.xml
-If you are running this against jUDDI and want to setup a temporary mail server, try hMail. A sample server config is provided


Exexcute using the following command

java -Duddi.client.xml=uddi.xml -jar uddi-tck-runner-{version}-jar-with-dependencies.jar

You can also specify which test cases to run (i.e. a focused test). This will reduce run time by only running the test classes selected by giving the option -Dtests=(comma separated list)
Example: 
java -Dtests=org.apache.juddi.v3.tck.UDDI_090_SubscriptionListenerExternalTest -Duddi.client.xml=uddi.xml -jar uddi-tck-runner-{version}-jar-with-dependencies.jar

This application SHOULD have a net zero effect on the registry, however it is recommended to make a backup of your registry server, database and/or content before proceeding. 

SMTP Delivery - Provided the UDDI server supports and delivers SMTP subscription updates, this tool will delete the specific email from the test case, however there's a very small chance could we delete the wrong email, therefore it's recommended to use a temporary mail account.