## UDDI Subscriptions

Subscriptions come to play in a multi-registry setup. Within your company you may have the need to run with more then one UDDI, let's say one for each department, where you limit access to the systems in each department to just their own UDDI node. However you may want to share some services cross departments. The subscription API can help you cross registering those services and keeping them up to date by sending out notifications as the registry information in the parent UDDI changes.

There are two type of subscriptions:

* asynchronous - Save a subscription, and the UDDI server sends you alerts according to your search criteria.

* synchronous -  Save a subscription, and then the UDDI client polls periodically for updates.

Save a subscription and invoke the get_Subscription and get a synchronous reply.
The notification can be executed in a synchronous and an asynchronous way. The asynchronous way requires a listener service to be installed on the node to which the notifications should be sent.

### Two node example setup: Sales and Marketing

In this example we are setting up a node for 'sales' and a node for 'marketing'. For this you need to deploy jUDDI to two different services, then you need to do the following setup:

Procedure 10.1. Setup Node 1: Sales

Create juddi_custom_install_data.

````
cd juddiv3/WEB-INF/classes
mv RENAME4SALES_juddi_custom_install_data juddi_custom_install_data
````
edit: webapps/juddiv3/WEB-INF/classes/juddiv3.properties and set the following property values where 'sales' is the DNS name of your server.
````
juddi.server.name=sales
juddi.server.port=8080
````
Start the server (tomcat), which will load the UDDI seed data (since this is the first time you're starting jUDDI, see , Root Seed Data)
````
bin/startup.sh
````
Open your browser to http://sales:8080/juddiv3. You should see:

![""](images/sales_node_installation.png)

Figure Sales Node Installation

Procedure 10.2. Setup Node 2: Marketing

Create juddi_custom_install_data.
````
cd juddiv3/WEB-INF/classes
mv RENAME4MARKETING_juddi_custom_install_data \
	juddi_custom_install_data
````
edit: webapps/juddiv3/WEB-INF/classes/juddiv3.properties and set the following property values where 'marketing' is the DNS name of your server.
````
juddi.server.name=marketing
juddi.server.port=8080
````
Start the server (tomcat), which will load the UDDI seed data (since this is the first time you're starting jUDDI, see Chapter 5, Root Seed Data)
````
bin/startup.sh
````
Open your browser to http://marketing:8080/juddiv3 . You should see:

![""](images/marketing_node_installation.png)

Figure 10.2. Marketing Node Installation

Note that we kept the root partition the same as sales and marketing are in the same company, however the Node Id and Name are different and reflect that this node is in 'sales' or 'marketing'.

Finally you will need to replace the sales server's uddi-portlets.war/WEB-INF/classes/META-INF/uddi.xml with uddi-portlets.war/WEB-INF/classes/META-INF/uddi.xml.sales. Then, edit the uddi-portlets.war/WEB-INF/classes/META-INF/uddi.xml and set the following properties:

````
<name>default</name>
<properties>
    <property name="serverName" value="sales"/>
    <property name="serverPort" value="8080"/>
    <property name="rmiPort" value="1099"/>
</properties>
````
Log into the sales portal: http://sales:8080/pluto with username/password: sales/sales.

![""](images/sales_services.png)

Figure 10.3. Sales Services

Before logging into the marketing portal, replace marketing's uddi-portlet.war/WEB-INF/classes/META-INF/uddi.xml with udd-portlet.war/WEB-INF/classes/META-INF/uddi.xml.marketing. Then you will need to edit the uddi-portlet.war/WEB-INF/classes/META_INF/uddi.xml and set the following properties:

````
<name>default</name>
<properties>
    <property name="serverName" value="marketing"/>
    <property name="serverPort" value="8080"/>
    <property name="rmiPort" value="1099"/>
</properties>
````

Now log into the marketing portal http://marketing:8080/pluto with username/password: marketing/ marketing. In the browser for the marketing node we should now see:

![""](images/marketing_services.png)

Figure 10.4. Marketing Services


Note that the subscriptionlistener is owned by the Marketing Node business (and not the Root Marketing Node). The Marketing Node Business is managed by the marketing publisher.

### Deploy the HelloSales Service

The sales department developed a service called HelloSales. The HelloSales service is provided in the juddiv3-samples.war, and it is annotated so that it will auto-register. Before deploying the war, edit the juddiv3-samples.war/WEB-INF/classes/META-INF/uddi.xml file to set some property values to 'sales'.

````
<?xml version="1.0" encoding="ISO-8859-1" ?>
<uddi>
<reloadDelay>5000</reloadDelay>
<manager name="example-manager">
<nodes>
<node>
	<name>default</name>
	<description>Sales jUDDI node</description>  
	<properties>
		<property name="serverName"  value="sales"/>
		<property name="serverPort"  value="8080"/>
		<property name="keyDomain"   value="sales.apache.org"/>
		<property name="department"  value="sales" />
	</properties>
	<proxyTransport>
		org.apache.juddi.v3.client.transport.InVMTransport
	</proxyTransport>
	<custodyTransferUrl>
		org.apache.juddi.api.impl.UDDICustodyTransferImpl
	</custodyTransferUrl>
	<inquiryUrl>org.apache.juddi.api.impl.UDDIInquiryImpl
	</inquiryUrl>
	<publishUrl>org.apache.juddi.api.impl.UDDIPublicationImpl
	</publishUrl>
	<securityUrl>org.apache.juddi.api.impl.UDDISecurityImpl
	</securityUrl>
	<subscriptionUrl>
		org.apache.juddi.api.impl.UDDISubscriptionImpl
	</subscriptionUrl>
	<subscriptionListenerUrl>
		org.apache.juddi.api.impl.UDDISubscriptionListenerImpl
	</subscriptionListenerUrl>
	<juddiApiUrl>org.apache.juddi.api.impl.JUDDIApiImpl
	</juddiApiUrl>
</node>
</nodes>
</manager>
</uddi>
````

Now deploy the juddiv3-samples.war to the sales registry node, by building the juddiv3-samples.war and deploying. The HelloWorld service should deploy

image::images/registration_by_annotation.png

Figure 10.5. Registration by Annotation, deploying the juddi-samples.war to the sales Node


On the Marketing UDDI we'd like to subscribe to the HelloWord service, in the Sales UDDI Node. As mentioned before there are two ways to do this; synchronously and asynchronously.

#### Configure a user to create Subscriptions

For a user to create and save subscriptions the publisher needs to have a valid login to both the sales and the marketing node. Also if the marketing publisher is going to create registry objects in the marketing node, the marketing publisher needs to own the sales keygenerator tModel. Check the marketing_*.xml files in the root seed data of both the marketing and sales node, if you want to learn more about this. It is important to understand that the 'marketing' publisher in the marketing registry owns the following tModels:

````
<save_tModel xmlns="urn:uddi-org:api_v3">
    <tModel tModelKey="uddi:marketing.apache.org:keygenerator" 
	xmlns="urn:uddi-org:api_v3">
        <name>marketing-apache-org:keyGenerator</name>
        <description>Marketing domain key generator</description>
        <overviewDoc>
            <overviewURL useType="text">
                http://uddi.org/pubs/uddi_v3.htm#keyGen
            </overviewURL>
        </overviewDoc>
        <categoryBag>
            <keyedReference 
			tModelKey="uddi:uddi.org:categorization:types" 
                keyName="uddi-org:types:keyGenerator"
                keyValue="keyGenerator" />
        </categoryBag>
    </tModel>
    
    <tModel 
	tModelKey="uddi:marketing.apache.org:subscription:keygenerator" 
        xmlns="urn:uddi-org:api_v3">
        <name>
		marketing-apache-org:subscription:keyGenerator</name>
        <description>Marketing Subscriptions domain key 
		generator</description>
        <overviewDoc>
            <overviewURL useType="text">
                http://uddi.org/pubs/uddi_v3.htm#keyGen
            </overviewURL>
        </overviewDoc>
        <categoryBag>
            <keyedReference 
			tModelKey="uddi:uddi.org:categorization:types" 
                keyName="uddi-org:types:keyGenerator"
                keyValue="keyGenerator" />
        </categoryBag>
    </tModel>

    <tModel tModelKey="uddi:sales.apache.org:keygenerator" 
	xmlns="urn:uddi-org:api_v3">
        <name>sales-apache-org:keyGenerator</name>
        <description>Sales Root domain key generator</description>
        <overviewDoc>
            <overviewURL useType="text">
                http://uddi.org/pubs/uddi_v3.htm#keyGen
            </overviewURL>
        </overviewDoc>
        <categoryBag>
            <keyedReference 
			tModelKey="uddi:uddi.org:categorization:types" 
                keyName="uddi-org:types:keyGenerator"
                keyValue="keyGenerator" />
        </categoryBag>
    </tModel>
</save_tModel>
````

If we are going to user the marketing publisher to subscribe to updates in the sales registry, then we need to provide this publisher with two clerks in the uddi.xml of the uddi-portlet.war.

````
<clerks registerOnStartup="false">
    <clerk  name="MarketingCratchit"    node="default" 
            publisher="marketing"       password="marketing"/>

    <clerk  name="SalesCratchit"        node="sales-ws"   
            publisher="marketing"       password="marketing"/>
    <!--  optional 
    <xregister>
        <servicebinding 
			entityKey="uddi:marketing.apache.org:servicebindings-\
			subscriptionlistener-ws" 
            fromClerk="MarketingCratchit" toClerk="SalesCratchit"/>
    </xregister>
    -->
</clerks>
````

Here we created two clerks for this publisher called 'MarketingCratchit' and 'SalesCratchit'. This will allow the publisher to check the existing subscriptions owned by this publisher in each of the two systems.

### Synchronous Notifications
While being logged in as the marketing publisher on the marketing portal, we should see the following when selecting the UDDISubscription Portlet.

![""](images/a_bothup_b_sales_down.png)

Figure 10.6. Subscriptions. In (a) both nodes are up while in (b) the sales node is down


When both nodes came up green you can lick on the 'new subscription' icon in the toolbar. Since we are going to use this subscription synchronously only the Binding Key and Notification Interval should be left blank, as shown in Figure 10.7, "Create a New Subscription". Click the save icon to save the subscription.

![""](images/create_new_subscription.png)

Figure 10.7. Create a New Subscription


Make sure that the subscription Key uses the convention of the keyGenerator of the marketing publisher. You should see the orange subscription icon appear under the "sales-ws" UDDI node.

image::images/newly_saved_subscription.png

Figure 10.9. Set the Coverage Period


Click the green arrows icon again to invoke the synchronous subscription request. The example finder request will go out to the sales node and look for updates on the HelloWorld service. The raw XML response will be posted in the UDDISubscriptionNotification Portlet.

![""](images/raw_XML_response_synchronous_subscription_request.png)

Figure 10.10. The Raw XML response of the synchronous Subscription request


The response will also be consumed by the marketing node. The marketing node will import the HelloWorld subscription information, as well as the sales business. So after a successful sync you should now see three businesses in the Browser Portlet of the marketing node, see Figure 10.11, "The registry info of the HelloWorld Service information was imported by the subscription mechanism.".

![""](images/registry_info_helloworld.png)

Figure 10.11. The registry info of the HelloWorld Service information was imported by the subscription mechanism.


### Using the 3.2+ Subscription callback API

Since 3.2, a newer simplified subscription API was included in the juddi-client JAR file. See the sample in the uddi-samples project.