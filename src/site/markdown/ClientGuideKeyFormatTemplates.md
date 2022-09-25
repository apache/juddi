## Key Format Templates

### UDDIv3 key format
The UDDI v3 keys are formatted such that they are human readable. The short story is that UDDI v3 keys are formatted like: `_uddi:<domain>:name_`. For example, if you wanted a tModel defined as `uddi:www.mycompany.com:serviceauthenticationmethod`, you would first have to create a tModel key generator with value `uddi:www.mycompany.com:keygenerator`. 


### jUDDI key format templates

The jUDDI client has taken the key format approach one step further so the name part of the key actually helps you understand what the key is for, or even better in the case of a binding template what server registered the key. 

#### Advantages of using a template

Using a binding Key with format `uddi:${keyDomain}:binding_${serverName}_${serviceName}_${portName}_${serverPort}` contains valuable information for two reasons

- you can tell from the bindingKey what server registered it. This is helpful information if you want to debug connectivity issues between a client and the UDDI server.
- if a server goes to register a bindingTemplate it registered before it won't create a second bindingTemplate, so it won't leave behind dead or duplicate bindingTemplates.

#### Default UDDIKeyConvention Key Templates

The default templates setup by the jUDDI client are:

````
uddi:${keyDomain}:business_${businessName}
uddi:${keyDomain}:service_${serviceName}
uddi:${keyDomain}:service_cache_${serverName}
uddi:${keyDomain}:\
	binding_${serverName}_${serviceName}_${portName}_${serverPort}
````

where tokens are expressed using `${}`. The templates are defined in the UDDIKeyConvention class.

#### How to use the templates?

At runtime a serviceKey can be obtained using

````
String serviceKey = \
	UDDIKeyConvention.getServiceKey(properties, serviceName);
````

The serviceName can be specified in as a property in the first argument, or it can explicitly passed as the second argument. Using the second argument overrides what's specified in the properties. By default it will use the service template `uddi:${keyDomain}:service_${serviceName}`, but if you wish to use a different template you can simple specify that as a property, for example

````
String myCustomServiceFormat = \
	uddi:${keyDomain}:s_${serviceName};
properties.add(Property.SERVICE_KEY_FORMAT, \
	myCustomServiceFormat);
String myCustomFormattedServiceKey = \
	UDDIKeyConvention.getServiceKey(properties, serviceName);
````

#### Where to define to properties?

You can define the properties in your code, or you can obtain and pass in the properties defined in your `uddi.xml`. For an exmaple of this you can check out the `META-INF/wsdl2uddi-uddi.xml` of the `wsdl2uddi` example where for the `default` node we set

````
...
<nodes>
	<node>
		<name>default</name>
		<properties>
			<property name="serverName" value="localhost"/>
			<property name="serverPort" value="8080"/>
			<property name="keyDomain" value="uddi.joepublisher.com"/>
			<property name="businessName" value="WSDL-Business"/>
		</properties>
		...
	</node>
	...
</nodes>
...
````

and you can obtain the properties like

````
UDDIClient uddiClient = \
	new UDDIClient(`META-INF/wsdl2uddi-uddi.xml`);
Properties properties = \
	uddiClient.getClientConfig().getUDDINode(`default`)\
	.getProperties();
````

This is exactly what the WSDL2UDDI implementation does, and it the reason the class requires the properties in the constructor.
