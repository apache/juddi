## Using the UDDI Technology Compatibility Kit

Since UDDI is a specification with many complex rules in it, we (the jUDDI team) have had to write test cases to exercise each of the rules and restrictions in UDDI. Knowing that there are a number of open source and commercial UDDI v3 implementations, the jUDDI team took this as an opportunity to create a reusable benchmark for testing the compatibility of UDDI v3 implementations.

IMPORTANT: Although the TCK covers a large number of test cases, the UDDI specification is long and complex. It's more than possible that we missed a few scenarios or test cases. If you run across any, please let us know.

### Using the TCK Runner

The TCK Runner requires a few files to operate:

 - `juddi-tck-runner-<version>.jar` - This is the executable
 - uddi.xml - This file sets the location of the UDDI services
 - tck.properties - This file controls what tests are ran.
 - truststore and keystore.jks - These files are for digital signature tests

#### Configuration

 - Edit the uddi.xml file and update all of the UDDI endpoint locations for all supported endpoints of UDDI server being tested. Ignore all credentials and other settings
 - Edit tck.properties and update the usernames and passwords of the test users. Enable or disable tests based on the whether or not the UDDI server supports the optional listed capabilities. 

TIP: Do not use usernames and passwords that already have data associated with it.

A few of the test cases, such as RMI transport, are not identified by the UDDI specification, therefore the results may be skewed if unsupported tests are attempted. In addition, the UDDI specification identifies a number of APIs and features that are considered optional. 

Although it is possible to run the TCK against a UDDIv2 registry using the UDDIv2 transport adapters, this is not supported. The TCK's test cases and rules apply to the business rules defined in UDDIv3. Unsupported and unmapped functions defined in UDDIv3 that are not supported in UDDIv2 fail ultimately fail.

##### tck.properties

The TCK properties file contains settings for all of the TCK tests.

 - Credentials - You'll need credentials for a number of user accounts
 - jUDDI optional tests - If you're running the tests against jUDDI, there's a number of additional tests ran to exercise things like user accounts.
 - Load testing - These settings enable you to tweak or disable the load testing.
 - Key stores - These are needed to run the digital signature tests
 - Supported transports - jUDDI supports a number of transports, such as RMI and HTTP (for UDDI service interaction) and SMTP and HTTP for subscription callbacks. RMI is actually not in the spec and SMTP is considered optional, so you'll want to adjusted these based on the available documentation from the vendor.

##### uddi.xml

The only parts used from uddi.xml are the following

 - The endpoints of the UDDI services
 - The client subscription callback settings

#### Running the TCK Runner

Executing the TCK runner is simple.

````
java (options) \
	-Duddi.client.xml=uddi.xml -jar \
	juddi-tck-runner-{VERSION}-jar-with-dependencies.jar
````

Optional parameters

 * -Ddebug=true - this turns up the logging output, typically including the XML payloads of each message.
 * -Duddi.client.xml=uddi.xml -  Use this file as the jUDDI Client config file. This specifies where all of the UDDI endpoints are.
 * -Dtck.properties=file.properties - Use this to use an alternate tck properties file.

### Analyzing the Results

There are two ways to identify the result of the tests.

 * Analyze the console output
 * Review the test results in uddi-tck-results-[DateTime].txt

The results are summarized in the uddi-tck-results file along with the specific error conditions and stack traces that will enable you to find out the root cause of the failure. It may be necessary to obtain UDDI server logs to help with root cause identification.
