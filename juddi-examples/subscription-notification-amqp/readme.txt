To run this demo, first go here
http://qpid.apache.org/

1) Download the following:
 - Java broker
2) Start Qpid Java broker (qpid-server)
	- Reconfigure Qpid. The goal here is to have Qpid's http management interface rerouted to another port that 8080
	- Login with admin/admin, add a new HTTP port and delete the old one
	- Restart Qpid
3) juddi-examples/subscription-notification-amqp> mvn clean install
4) copy juddi-qpid-notifier/target/juddi-qpid-notifier-3.2-SNAPSHOT.jar to tomcat/webapps/juddiv3.war/WEB-INF/lib
5) copy qpid-dependencies/target/qpid-with-dependencies-jar-with-dependencies.jar to tomcat/webapps/juddiv3.war/WEB-INF/lib
6) Start Tomcat with jUDDI
7) run juddi-qpid-notifier> mvn clean install -Pdemo

At this point, our AMQP client will sit and listen for changes to business, services and tModels.

It's pretty each to change something using the juddi-gui.
	http://localhost:8080/juddi-gui

	
Notes: this example is very basic and does not consider cases such as authentication and SSL key information for connecting to Qpid.