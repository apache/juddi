## Universal Description, Discovery and Integration (UDDI)

### UDDI Protocol, Specification

The Universal Description, Discovery and Integration (UDDI) protocol is one of the major building blocks required for successful Web services. UDDI creates a standard interoperable platform that enables companies and applications to quickly, easily, and dynamically find and use Web services over the Internet (or Intranet). UDDI also allows operational registries to be maintained for different purposes in different contexts. UDDI is a cross-industry effort driven by major platform and software providers, as well as marketplace operators and e-business leaders within the OASIS standards consortium. UDDI has gone through 3 revisions and the latest version is [3.0.2](http://www.uddi.org/pubs/uddi-v3.0.2-20041019.htm). Additional information regarding UDDI can be found at [uddi.xml.org](http://uddi.xml.org).

### UDDI Registry 

The UDDI Registry (((UDDI,registry))) implements the UDDI specification (((UDDI,specification))). UDDI is a Web-based _distributed_ _directory_ (((directory))) that enables businesses to list themselves on the Internet (or Intranet) and discover each other, similar to a traditional phone book's yellow and white pages. The UDDI registry is both a white pages business directory and a technical specifications library. The Registry is designed to store information about Businesses and Services and it holds references to detailed documentation.

!["Invocation Pattern using the UDDI Registry"](images/UDDI_Registry_invocation_pattern.png)

In step 1, it is shown how a business publishes services to the UDDI registry. In step 2, a client looks up the service in the registry and receives service binding information. Finally in step 3, the client then uses the binding information to invoke the service. The UDDI APIs are SOAP based for interoperability reasons. In this example we've three APIs specified in the UDDI v3 specification, Security, Publication and Inquiry. The UDDI v3 specification defines 9 APIs:

. UDDI_Security_PortType, defines the API to obtain a security token. With a valid security token a publisher can publish to the registry. A security token can be used for the entire session.
. UDDI_Publication_PortType, defines the API to publish business and service information to the UDDI registry.
. UDDI_Inquiry_PortType, defines the API to query the UDDI registry. Typically this API does not require a security token.
. UDDI_CustodyTransfer_PortType, this API can be used to transfer the custody of a business from one UDDI node to another.
. UDDI_Subscription_PortType, defines the API to register for updates on a particular business of service.
. UDDI_SubscriptionListener_PortType, defines the API a client must implement to receive subscription notifications from a UDDI node.
. UDDI_Replication_PortType, defines the API to replicate registry data between UDDI nodes.
. UDDI_ValueSetValidation_PortType, by nodes to allow external providers of value set validation. Web services to assess whether keyedReferences or keyedReferenceGroups are valid.
. UDDI_ValueSetCaching_PortType, UDDI nodes may perform validation of publisher references themselves using the cached values obtained from such a Web service.

### jUDDI Project

Apache jUDDI is server and client-side implementation of the UDDI v3 specification. The server side is the UDDI Registry, the client side are the juddi-client libraries. There is a Java as well as a C# version of the client libraries. The jUDDI GUI uses the client libraries to connect to a UDDI Registry. For more details please see the <<GuideGettingStarted#chapter-GettingStarted>>. 

The following is a list of all supported UDDI interfaces provided by this release of jUDDI

Supported UDDI Interfaces

| API																							| Spec 			| Supported 		| Notes 
| --- | --- | --- | --- |
| [Inquiry](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908076)						| Required		| All Methods	|		|
| [Inquiry HTTP GET](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908158)					| Optional		| All Methods	| Plus a number of additional methods 
| [Publication](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908095) 						| Required		| All Methods	| | 
| [Security](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908115) 						| Optional		| All Methods 	| Pluggable authentication 
| [Subscription](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908128)						| Optional		| All Methods 	| HTTP, SMTP delivery implemented, pluggable			
| [Subscription Listener](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908336)			|Optional		| All Methods	| Client and Server side implementations 
| [Value Set Caching](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908141)				| Optional		| Partial		| Scheduled for 3.3 
| [Value Set Validation](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908141)				| Optional		| Implemented	| Scheduled for 3.3 
| [Replication](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908180)						| Optional		| Partial		| Scheduled for 3.3 
| [Custody and Ownership Transfer](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908118)	| Optional		| All Methods	| Only supports user to user transfers on the same node 
| [UDDIv2 Inquiry](http://uddi.org/pubs/ProgrammersAPI-V2.04-Published-20020719.htm#_Toc25137711) | Required | BETA | Supported via API translator
| [UDDIv2 Publish](http://uddi.org/pubs/ProgrammersAPI-V2.04-Published-20020719.htm#_Toc25137722 | Required | BETA | Supported via API translator


The following is a list of other features of interest that was either defined in the UDDI specifications or in technical notes.

jUDDI Features


| API								| Spec	| Supported 	| Notes 
| --- | --- | --- | --- |
| Digital Signatures					| Server req	| Full support | Java and .NET clients and in browser signing	
| Client side Subscription Listener	| Optional		| Full support | Java and .NET clients	
| [WSDL to UDDI](https://www.oasis-open.org/committees/uddi-spec/doc/tn/uddi-spec-tc-tn-wsdl-v2.htm)						| Recommendation| Full support | Java, .NET clients and web GUI
| [WADL to UDDI]https://www.oasis-open.org/committees/uddi-spec/doc/tn/uddi-spec-tc-tn-wsdl-v2.htm)						| Recommendation| Full support | Java, .NET clients and web GUI 
| [BPEL to UDDI](https://www.oasis-open.org/committees/uddi-spec/doc/tn/uddi-spec-tc-tn-bpel-20040725.htm)						| Recommendation| Full support | Java client 
| UDDI Technical Compliance Kit		| -				| Full support | Provides a standalone UDDI testing capability 
| Internationalization				| Recommendation| Yes		   | Both end user interfaces (User and Admin web apps) are supported. Error messages from the server are external and can be overwritten.
| Registration via Annotations          | - | Full support | Provides automated registration of classes via Java/.NET Annotations


UDDI defines a number of http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908080[sorting mechanisms]. 

Supported Sort Orders

| Find Qualifier		| Spec		| Supported 	| Notes 
| --- | --- | --- | --- |
| [binarySort](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#sortOrd)			| Required 	| yes |  
| [caseInsensitiveSort](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#caseInsensSort)	| Required 	| partialy | Only when using caseInsentitiveMatch, https://issues.apache.org/jira/browse/JUDDI-785[JIRA opened] 
| [caseSensitiveSort](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908355)		| Required 	| yes |  |
| [sortByNameAsc](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908356)			| Required 	| yes |  |
| [sortByNameDesc](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908357)		| Required 	| yes |  |
| [sortByDateAsc](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908358)			| Required 	| yes |  |
| [sortByDateDesc](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908359)		| Required 	| yes |  |
| [JIS-X4061](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc42047570)				| Optional 	| no  | Japanese Character Strings
| [UTS-10](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#UCASort)					| Recommended 	| yes | |

Supported Find Qualifiers

| Find Qualifier			| Spec			|Supported 	
| --- | --- | --- | 
| [andAllKeys](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908360)				| Required 		| yes 
| [approximateMatch](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908346)			| Required 		| yes 
| [bindingSubset](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908365)				| Required 		| yes 
| [caseInsensitiveMatch](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908348)		| Required 		| yes  
| [caseSensitiveMatch](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908349)		| Required 		| yes 
| [combineCategoryBags](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908363)		| Required 		| yes 
| [diacriticInsensitiveMatch](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908350)	| Optional 		| yes 
| [diacriticSensitiveMatch](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908351)	| Required 		| yes 
| [exactMatch](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908347)				| Required 		| yes 
| [signaturePresent](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908367)			| Required 		| yes 
| [orAllKeys](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908361)					| Required 		| yes 
| [orLikeKeys](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908362)				| Required 		| yes 
| [serviceSubset](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908364)				| Required 		| yes 
| [suppressProjectedServices](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908366)	| Required 		| yes 


