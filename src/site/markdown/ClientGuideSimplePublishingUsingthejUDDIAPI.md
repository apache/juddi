## Simple Publishing Using the jUDDI API

One of the most common requests we get on the message board is "How do I publish a service using jUDDI?" This question holds a wide berth, as it can result anywhere from not understanding the UDDI data model, to confusion around how jUDDI is set up, to the order of steps required to publish artifacts in the registry, to general use of the API - and everything in between. This article will attempt to answer this "loaded" question and, while not going into too much detail, will hopefully clear some of the confusion about publishing into the jUDDI registry.

### UDDI Data Model

Before you begin publishing artifacts, you need to know exactly how to break down your data into the UDDI model. This topic is covered extensively in the specification, particularly in section 3, so I only want to gloss over some for details. Readers interested in more extensive coverage should most definitely take a look at the UDDI specification.

Below is a great diagram of the UDDI data model (taken directly from the specification): 
http://juddi.apache.org/docs/3.x/userguide/html/images/uddi_core_datastructures.gif
As you can see, data is organized into a hierarchical pattern. Business Entities are at the top of the pyramid, they contain Business Services and those services in turn contain Binding Templates. TModels (or technical models) are a catch-all structure that can do anything from categorize one of the main entities, describe the technical details of a binding (ex. protocols, transports, etc), to registering a key partition. TModels won't be covered too much in this article as I want to focus on the three main UDDI entities.

The hierarchy defined in the diagram is self-explanatory. You must first have a Business Entity before you can publish any services. And you must have a Business Service before you can publish a Binding Template. There is no getting around this structure; this is the way UDDI works.

Business Entities describe the organizational unit responsible for the services it publishes. It generally consist of a description and contact information. How one chooses to use the Business Entity is really dependent on the particular case. If you're one small company, you will likely just have one Business Entity. If you are a larger company with multiple departments, you may want to have a Business Entity per department. (The question may arise if you can have one uber-Business Entity and multiple child Business Entities representing the departments. The answer is yes, you can relate Business Entities using Publisher Assertions, but that is beyond the scope of this article.)

Business Services are the cogs of the SOA landscape. They represent units of functionality that are consumed by clients. In UDDI, there's not much to a service structure; mainly descriptive information like name, description and categories. The meat of the technical details about the service is contained in its child Binding Templates.

Binding Templates, as mentioned above, give the details about the technical specification of the service. This can be as simple as just providing the service's access point, to providing the location of the service WSDL to more complicated scenarios to breaking down the technical details of the WSDL (when used in concert with tModels). Once again, getting into these scenarios is beyond the scope of this article but may be the subject of future articles.

### jUDDI Additions to the Model

Out of the box, jUDDI provides some additional structure to the data model described in the specification. Primarily, this is the concept of the Publisher.

The UDDI specification talks about ownership of the entities that are published within the registry, but makes no mention about how ownership should be handled. Basically, it is left up to the particular implementation to decide how to handle "users" that have publishing rights in the registry.

Enter the jUDDI Publisher. The Publisher is essentially an out-of-the-box implementation of an identity management system. Per the specification, before assets can be published into the registry, a "publisher" must authenticate with the registry by retrieving an authorization token. This authorization token is then attached to future publish calls to assign ownership to the published entities.

jUDDI's Publisher concept is really quite simple, particularly when using the default authentication. You can save a Publisher to the registry using jUDDI's custom API and then use that Publisher to publish your assets into the registry. jUDDI allows for integration into your own identity management system, circumventing the Publisher entirely if desired. This is discussed in more detail in the documentation, but for purposes of this article, we will be using the simple out-of-the-box Publisher solution.

TIP: In UDDI, ownership is essentially assigned to a given registry entity by using its "authorizedName" field. The "authorizedName" field is defined in the specification in the operationalInfo structure which keeps track of operational info for each entity. In jUDDI, the authorizedName field translates to the person's username, also know as the publisher id, 


### UDDI and jUDDI API

Knowing the UDDI data model is all well and good. But to truly interact with the registry, you need to know how the UDDI API is structured and how jUDDI implements this API. The UDDI API is covered in great detail in chapter 5 of the specification but will be summarized here.

UDDI divides their API into several "sets" - each representing a specific area of functionality. The API sets are listed below:

* Inquiry - deals with querying the registry to return details on entities within
* Publication - handles publishing entities into the registry
* Security - open-ended specification that handles authentication
* Custody and Ownership Transfer - deals with transferring ownership and custody of entities
* Subscription - allows clients to retrieve information on entities in a timely manner using a subscription format
* Subscription Listener - client API that accepts subscription results
* Value Set (Validation and Caching)- validates keyed reference values (not implemented by jUDDI)
* Replication - deals with federation of data between registry nodes (not implemented by jUDDI)
The most commonly used APIs are the Inquiry, Publication and Security APIs. These APIs provide the standard functions for interacting with the registry.

The jUDDI server implements each of these API sets as a JAX-WS compliant web service and each method defined in the API set is simply a method in the corresponding web service. The client module provided by jUDDI uses a "transport" class that defines how the call is to be made. The default transport uses JAX-WS but there are several alternative ways to make calls to the API. Please refer to the documentation for more information.

One final note, jUDDI defines its own API set. This API set contains methods that deal with handling Publishers as well as other useful maintenance functions (mostly related to jUDDI's subscription model). This API set is obviously proprietary to jUDDI and therefore doesn't conform to the UDDI specification.

### Getting Started

Now that we've covered the basics of the data model and API sets, it's time to get started with the publishing sample. The first thing that must happen is to get the jUDDI server up and running. Please refer to this http://apachejuddi.blogspot.com/2010/02/getting-started-with-juddi-v3.html article that explains how to start the jUDDI server.

#### Simple Publishing Example

We will now go over the "simple-publish" examples. These examples expand upon the HelloWorld example in that after retrieving an authentication token, a BusinessEntity and BusinessService are published to the registry. There are two examples:

 - simple-publish-portal - This is how to perform the publish operations in a way that's portable, meaning that the code logic should apply to any UDDIv3 client application library.
 - simple-publish-clerk - This shows you how to perform the same actions using the helper functions in jUDDI's Client library, which greatly reduces the code required and makes things simple. This uses the UDDIClerk's functions.

##### Simple Publishing using Portable Code

The complete source for this example can be found here: 
 - Portable http://svn.apache.org/repos/asf/juddi/trunk/juddi-examples/simple-publish-portable/

````
public SimplePublishPortable() {
try {
	// create a client and read the config in the archive; 
	// you can use your config file name
	UDDIClient uddiClient = new UDDIClient("META-INF/uddi.xml");
	// a UddiClient can be a client to multiple UDDI nodes, so 
	// supply the nodeName (defined in your uddi.xml.
	// The transport can be WS, inVM, RMI etc which is defined in the uddi.xml
	Transport transport = uddiClient.getTransport("default");
	// Now you create a reference to the UDDI API
	security = transport.getUDDISecurityService();
	publish = transport.getUDDIPublishService();
} catch (Exception e) {
		e.printStackTrace();
}
}
````

The constructor uses the jUDDI client API to retrieve the transport from the default node. You can refer to the documentation if you're confused about how clerks and nodes work. Suffice it to say, we are simply retrieving the default client transport class which is designed to make UDDI calls out using JAX-WS web services.

Once the transport is instantiated, we grab the two API sets we need for this demo: 1) the Security API set so we can get authorization tokens and 2) the Publication API set so we can actually publish entities to the registry.

All the magic happens in the publish method. We will look at that next.

Here are the first few lines of the publish method:

````
	// Login aka retrieve its authentication token
	GetAuthToken getAuthTokenMyPub = new GetAuthToken();
	getAuthTokenMyPub.setUserID("bob");                    //your username
	getAuthTokenMyPub.setCred("bob");                          //your password
	AuthToken myPubAuthToken = security.getAuthToken(getAuthTokenMyPub);
	System.out.println(getAuthTokenMyPub.getUserID() + "'s AUTHTOKEN = " + "******* never log auth tokens!");
````

IMPORTANT: Don't log authentication tokens. In addition, whenever you're done with it, it should be 'discarded'. Think of it as a logout function.

This code simply gets the authorization token for the 'bob' user. 

TIP: jUDDI includes two reserved usernames, 'uddi' and 'root'. Root acts as the "administrator" for jUDDI API calls. Additionally, the 'root' user is the owning publisher for all the initial services installed with jUDDI. You may be wondering what those "initial services" are. Well, since the UDDI API sets are all implemented as web services by jUDDI, every jUDDI node actually registers those services inside itself. This is done per the specification. The user 'uddi' owns the remaining preinstalled data.

Now that we have Bob's authorization, we can start publishing.

TIP: You'll note that no credentials have been set on both authorization calls. This is because we're using the default authenticator (which is for testing purposes doesn't require credentials). Most UDDI servers will require authentication. 

````
// Creating the parent business entity that will contain our service.
BusinessEntity myBusEntity = new BusinessEntity();
Name myBusName = new Name();
myBusName.setValue("My Business");
myBusEntity.getName().add(myBusName);

// Adding the business entity to the "save" structure, using our publisher's authentication info 
// and saving away.
SaveBusiness sb = new SaveBusiness();
sb.getBusinessEntity().add(myBusEntity);
sb.setAuthInfo(myPubAuthToken.getAuthInfo());
BusinessDetail bd = publish.saveBusiness(sb);
String myBusKey = bd.getBusinessEntity().get(0).getBusinessKey();
System.out.println("myBusiness key:  " + myBusKey);

// Creating a service to save.  Only adding the minimum data: the parent business key retrieved 
//from saving the business above and a single name.
BusinessService myService = new BusinessService();
myService.setBusinessKey(myBusKey);
Name myServName = new Name();
myServName.setValue("My Service");
myService.getName().add(myServName);
// Add binding templates, etc...
// <snip> We removed some stuff here to make the example shorter, check out the source for more info</snip>

// Adding the service to the "save" structure, using our publisher's authentication info and 
// saving away.
SaveService ss = new SaveService();
ss.getBusinessService().add(myService);
ss.setAuthInfo(myPubAuthToken.getAuthInfo());
ServiceDetail sd = publish.saveService(ss);
String myServKey = sd.getBusinessService().get(0).getServiceKey();
System.out.println("myService key:  " + myServKey);

//and we're done, don't forget to logout!
security.discardAuthToken(new DiscardAuthToken(myPubAuthToken.getAuthInfo()));
````

To summarize, here we have created and saved a BusinessEntity and then created and saved a BusinessService. We've just added the bare minimum data to each entity. Obviously, you would want to fill out each structure with greater information, particularly with services. However, this is beyond the scope of this article, which aims to simply show you how to programmatically publish entities.

##### Simple Publishing using Clerks

The complete source for this example can be found here: 
 - Clerk http://svn.apache.org/repos/asf/juddi/trunk/juddi-examples/simple-publish-clerk/

The sample consists of only one class: SimplePublishPortable. Let's start by taking a look at the constructor:


````
public SimplePublishClerk() {
	try {
		// create a client and read the config in the archive; 
		// you can use your config file name
		UDDIClient uddiClient = new UDDIClient("META-INF/uddi.xml");
		//get the clerk
		clerk = uddiClient.getClerk("default");
		if (clerk==null)
				throw new Exception("the clerk wasn't found, check the config file!");
	} catch (Exception e) {
			e.printStackTrace();
	}
}
````

Notice that this is already much more streamlined than the previous example. In this scenario, all configuration settings and credentials are stored in "META-INF/uddi.xml". 

TIP: The configuration file used by clients can be overridden via the system property "uddi.client.xml". E.g. java -Duddi.client.xml=/usr/local/uddi.xml -jar MyCoolProgram.jar

UDDIClient's job is to read the configuration file and initialize the data structures for working with 1 or more UDDI nodes (or servers). It also handles automatic registration of endpoints using WSDL documents or using class annotations. UDDIClerk's job is to manage credentials and to perform a number of common tasks. Feel free to use them in your programs and help you simplify things.

The UDDIClerk also handle credentials and authentication to UDDI for you. If you didn't want to store credentials (it can be encrypted) then you can specify them at runtime very easily. 

Moving on, the next function is Publish. Here's the short short version.

````
public void publish() {
  try {
	// Creating the parent business entity that will contain our service.
	BusinessEntity myBusEntity = new BusinessEntity();
	Name myBusName = new Name();
	myBusName.setValue("My Business");
	myBusEntity.getName().add(myBusName);
	//<snip>we removed a bunch of useful stuff here, see the full example for the rest of it</snip>

	//register the business, if the return value is null, something went wrong!
	BusinessEntity register = clerk.register(myBusEntity);
	//don't forget to log out!
	clerk.discardAuthToken();
	if (register ## null) {
			System.out.println("Save failed!");
			System.exit(1);
	}
	// Now you have a business and service via 
	// the jUDDI API!
	System.out.println("Success!");

	} catch (Exception e) {
			e.printStackTrace();
	}
}

````

The UDDIClerk has a register and unregister function for nearly everything for UDDI. Between the UDDIClient and UDDIClerk, there's enough helper functions to significantly reduce the amount of code needed to work with UDDI. Here's a quick list of things they can do for you:

 * Create a tModel Partition, also know as a Key Generator
 * Resolve endpoints from WSDLs, Hosting directors, and other binding template references from Access Points http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908385
 * Get Bindings by Version
 * Add REST or SOAP tModels to a binding template
 * Setup asynchronous callbacks for subscriptions
 * Compare the values of a tModel Instance Info, such as Quality of Service Metrics
 * Create and register services using a WADL or WSDL document
 * And more...

We're also looking for the next thing to add to the client library. Have an idea? Please open a ticket on jUDDI's Issue Tracker at https://issues.apache.org/jira/browse/JUDDI.

#### About UDDI Entity Keys

There are a couple important notes regarding the use of entity keys. Version 3 of the specification allows for publishers to create their own keys but also instructs implementers to have a default method. Here we have gone with the default implementation by leaving each entity's "key" field blank in the save call. jUDDI's default key generator simply takes the node's partition and appends a GUID. In a default installation, it will look something like this:

uddi:juddi.apache.org:(generated GUID/UUID)

You can, of course, customize all of this, but that is left for another article. The second important point is that when the BusinessService is saved, we have to explicitly set its parent business key (retrieved from previous call saving the business). This is a necessary step when the service is saved in an independent call like this. Otherwise you would get an error because jUDDI won't know where to find the parent entity. I could have added this service to the BusinessEntity's service collection and saved it with the call to saveBusiness. In that scenario we would not have to set the parent business key.


### A few tips on adding Binding Templates

Arguably, the most useful useful part of UDDI is advertising your services similar to a phone book or directory. The important part really isn't that Bob's Business exists (BusinessEntity), it's that Bob provides a service (BusinessService) and it's located at this address. This is where the Binding Template comes it. It identifies an instance of a service, its location and any other metadata associated with the endpoint that someone may want to know.

This article skips the binding Template data because it can be lengthy, but the full source for these examples shows you exactly how to build and add binding templates.

### Conclusion

Hopefully this added clarity to the question, "How do I publish a service using jUDDI?".
