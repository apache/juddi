Title: Supported UDDI APIs and Functions

The following is a list of all supported UDDI interfaces.

| API																							| Spec 			| Supported 		| Notes | 
| --- 																							| --- 			|--- 			| ---	|
|[Inquiry](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908076) 							| Required		| All Methods	|		|
|[Inquiry HTTP GET](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908158)					| Optional		| All Methods	| Plus a number of additional methods |
|[Publication](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908095) 						| Required		| All Methods	| |
|[Security](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908115) 						| Optional		| All Methods 	| Pluggable authentication |
|[Subscription](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908128)						| Optional		| All Methods 	| HTTP, SMTP delivery implemented, pluggable			|
|[Subscription Listener](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908336)			| Optional		| All Methods	| Client and Server side implementations |
|[Value Set Caching](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908141)				| Optional		| Partial		| Since 3.3 |
|[Value Set Validation](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908141)				| Optional		| Implemented	| Since 3.3 |
|[Replication](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908180)						| Optional		| All Methods	| Since 3.3 |
|[Custody and Ownership Transfer](http://uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908118)	| Optional		| All Methods	| Since 3.3, full support |


The following is a list of other features of interest

| API								| Spec	| Supported 	| Notes | 
| --- 								| --- 			| --- 		| ---	|
|Digital Signatures					| Server req	| Full support | Java and .NET clients and in browser signing	|
|Client side Subscription Listener	| Optional		| Full support | Java and .NET clients	|
|WSDL to UDDI						| Recommendation| Full support | [1](https://www.oasis-open.org/committees/uddi-spec/doc/tn/uddi-spec-tc-tn-wsdl-v2.htm) Java, .NET clients and web GUI |
|WADL to UDDI						| Recommendation| Full support | [1](https://www.oasis-open.org/committees/uddi-spec/doc/tn/uddi-spec-tc-tn-wsdl-v2.htm) Java, .NET clients and web GUI |
|BPEL to UDDI						| Recommendation| Full support | [2](https://www.oasis-open.org/committees/uddi-spec/doc/tn/uddi-spec-tc-tn-bpel-20040725.htm) Java client | 
|UDDI Technical Compliance Kit		| -				| Full support | Provides a standalone UDDI testing capability |
|Internationalization				| Recommendation| Yes		   | Both end user interfaces (User and Admin web apps) are supported. Error messages from the server are externaliws and can be overwritten.
|Replication Two Stage Commit 		| Server Optional | No | The [spec](http://www.uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Ref8979261) talks about a Conditional New Data message.
|UDDI Policy						| Recommendation| No | The [spec](http://www.uddi.org/pubs/uddi-v3.0.2-20041019.htm#_Toc85908203) 

Supported Sort Orders

| Find Qualifier		| Spec		| Supported 	| Notes | 
| --- 					| ---			| --- 		| ---	|
| binarySort			| Required 	| yes |  |
| caseInsensitiveSort	| Required 	| no | [JIRA discussion](https://issues.apache.org/jira/browse/JUDDI-785) |
| caseSensitiveSort		| Required 	| yes | |
| sortByNameAsc			| Required 	| yes | |
| sortByNameDesc		| Required 	| yes | |
| sortByDateAsc			| Required 	| yes | |
| sortByDateDesc		| Required 	| yes | |
| JIS-X4061				| Optional 	| no  | Japanese Character Strings|

Supported Find Qualifiers

| Find Qualifier			| Spec			|Supported 	| Notes | 
| --- 						| --- 			| --- 		| ---	|
| andAllKeys				| Required 		| yes | |
| approximateMatch			| Required 		| yes | |
| bindingSubset				| Required 		| yes | |
| caseInsensitiveMatch		| Required 		| yes | |
| caseSensitiveMatch		| Required 		| yes | |
| combineCategoryBags		| Required 		| yes | |
| diacriticInsensitiveMatch	| Optional 		| yes | |
| diacriticSensitiveMatch	| Required 		| yes | |
| exactMatch				| Required 		| yes | |
| signaturePresent			| Required 		| yes | |
| orAllKeys					| Required 		| yes | |
| orLikeKeys				| Required 		| yes | |
| serviceSubset				| Required 		| yes | |
| suppressProjectedServices	| Required 		| yes | |
| UTS-10					| Recommended 	| yes | |


Supported Authentication Mechanisms

 - Username/Password via Security API/Auth Token
	- LDAP
	- Clear text password file
	- Encrypted password file
	- JBoss container based
