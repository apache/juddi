## Extending UDDI

jUDDI has extensively uses the Interface/Factory pattern to enable configuration runtime options and to provide you, the developer easy insertion points to customize the behavior of jUDDI. The remaining sections of this chapter outline the different technology insertion points.

### Authentication modules

Authentication modules are used when the UDDI's AuthToken is utilized on the Security web service. It's function is to point to some kind of user credential store to validate users. See the User Guide for details on what's available out of the box.

All of the provided classes implement the interface .org.apache.juddi.v3.auth.Authenticator. So, if you wanted something a bit more functional than what's provided out of the box. you'll need to implement your own Authenticator.  To wire it in, edit the `juddiv3.xml` file, specifying your class name as the value to the property `juddi/auth/authenticator/class` and then add the class or jar containing your implementation to `juddiv3.war/WEB-INF/classes` or `judiv3.war/WEB-INF/lib` respectively. 

### Subscription Notification Handlers

Subscription Notification Handlers are used to asynchronously notify users that something has changed in UDDI. In order to do this, a UDDI Subscription is created that references a specific Binding Template key which represents the service that will be called whens something changes. jUDDI comes with support for Email delivery and the UDDI Subscription Listener Web Service (HTTP) delivery. In addition, jUDDI comes with an example for publishing to an Apache Qpid AMQP pub/sub server, which can be used to further disseminate the change. The following is an exert from the jUDDI Blog posting on this.

1. Make a new Java library projects in your IDE of choice. Reference the juddi-core, and uddi-ws projects or JAR files or the Maven dependency equivalent
2. Create a class of your own within the following package name: `org.apache.juddi.subscription.notify`
3. The class name MUST follow this pattern: PROTOCOLNotifier Where PROTOCOL is the prefix of whatever URL you want users to be able to use. Here's an example using Apache Qpid. Example URL: amqp://guest:guest@client1/development?brokerlist='tcp://localhost:5672' Class Name: AMQPNotifier. The Notification class basically takes the protocol of the Access Point's value, splits it on the character ":" and then grabs the first token "amqp" and converts to upper case. Using this pattern you should be able to insert anything you want.
4. Our new shinny class, AMQPNotifier, must implement the interface org.apache.juddi.subscription.notify.Notifier. From there, all you need to do is to add in the jars necessary for your transport mechanism and wire in your own code.
5. Deployment is simple. Add your PROTOCOLNotifier jar and its dependencies to the juddiv3.war/WEB-INF/lib folder.

Note: be careful and watch for conflicting jar file versions. In general, usually moving up a version is ok, but moving down may cause the services to fail unexpectedly.

To test, create a Service with the BindingTemplate's Access Point's value equal to whatever you need.
Next, setup a subscription and reference the BindingTemplate key that represents your call back handler's end point. Finally,   change an item that is covered by the subscription's filter and monitor the log files. Hopefully, you won't see an unexpected errors.

### KeyedReference Value Set Validation Services

Since jUDDI 3.2.1, we now have support for the Value Set Validation Service. This allows you to define a validator that will check when a user saves a UDDI entity that references a given tModel that contains a keyed reference to uddi:uddi.org:identifier:validatedby (which points to the VSV service).

To defined your own validator, use the following steps

1. Create you tModel with a named key
2. Implement the org.apache.juddi.validation.vsv.ValueSetValidator interface
3. Name your implementation class using the naming schema defined in the ConvertKeyToClass function of UDDIValueSetValidationImpl (first letter is upper, all else is lower. Numbers and letters only. Class must be in the package org.apache.juddi.validation.vsv
4. Update your saved tModel and add a keyed reference for uddi:uddi.org:identifier:validatedby using the value of `uddi:juddi.apache.org:servicebindings-valueset-cp`
5. Get your class in the class path of jUDDI and give it a shot

### Cryptographic Providers

jUDDI provides cryptographic functions via (Java) juddi-client.jar/org.apache.juddi.v3.client.cryptor and implement the Cryptor interface which provides two simple functions, encrypt and decrypt. (Note: .NET has similar functionality).

### jUDDI Client Transport

The juddi-client's Transport class is an abstract class that you can you alter the transport mechanism used by jUDDI's client APIs. Included is what would be used in most cases, such as JAXWS, RMI, and InVM (Embedded mode). This can be extended to use whatever you may need.
