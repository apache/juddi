Title: Security Advisories

## Security Advisories for Apache jUDDI

### CVEID [CVE-2021-37578](https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2021-37578)

VERSION:  older than 3.3.10

PROBLEMTYPE: Remote Code Execution

REFERENCES: https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2021-37578

DESCRIPTION: Apache jUDDI uses several classes related to Java's Remote Method Invocation (RMI) which (as an extension to UDDI) provides an alternate transport for accessing UDDI services.

RMI uses the default Java serialization mechanism to pass parameters in RMI invocations. A remote attacker can send a malicious serialized object to the above RMI entries. The objects get deserialized without any check on the incoming data. In the worst case, it may let the attacker run arbitrary code remotely. 

For both jUDDI web service applications and jUDDI clients, the usage of RMI is disabled by default. Since this is an optional feature and an extension to the UDDI protocol, the likelihood of impact is low. Starting with 3.3.10, all RMI related code was removed.

Severity: Low

Mitigation:

jUDDI Clients, disable RMITransports (found in uddi.xml) and use alternate transports such as HTTPS.
jUDDI Server (juddiv3.war/WEB-INF/classes/juddiv3.xml), disable JNDI and RMI settings in juddiv3.xml.
The appropriate settings are located below in xpath style notation.

    juddi/jndi/registration=false
    juddi/rmi/registration=false
	
If the settings are not present, then JNDI and RMI are already disabled. This is the default setting.

Credit:

Artem Smotrakov

### CVEID  [CVE-2018-1307](https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2018-1307)

VERSION:  3.2 through 3.3.4

PROBLEMTYPE: XML Entity Expansion

REFERENCES: https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2009-4267

DISCRIPTION: If using the WADL2Java or WSDL2Java classes, which parse a local or remote XML document and then mediates the data structures into UDDI data structures, there are little protections present against entity expansion and DTD type of attacks. This was fixed with https://issues.apache.org/jira/browse/JUDDI-987

Severity: Moderate

Mitigation:

Update your juddi-client dependencies to 3.3.5 or newer and/or discontinue use of the effected classes.

### CVEID : [CVE-2009-4267](https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2009-4267)

VERSION:  3.0.0

PROBLEMTYPE: Information Disclosure

REFERENCES: https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2009-4267

DISCRIPTION: The jUDDI console doesn't escape line feeds that were passed in the numRows parameter. This affects log integrity, as this allows authenticated users to forge log records.

Severity: Moderate

Mitigation:

3.0.0 users should upgrade to jUDDI 3.0.1 or newer

Credit:

This issue was discovered by ﻿Marc Schoenefeld of Red Hat Software.


### CVEID: [CVE-2015-5241](https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2015-5241)

VERSION: 3.1.2, 3.1.3, 3.1.4, and 3.1.5 that utilize the portlets based user interface also known as 'Pluto', 'jUDDI Portal', 'UDDI Portal' or 'uddi-console'

PROBLEMTYPE: Open Redirect

REFERENCES: http://www.cve.mitre.org/cgi-bin/cvename.cgi?name=2015-5241

DESCRIPTION: After logging into the portal, the logout jsp page redirects the browser back to the login page after. It is feasible for malicious user to redirect the browser to an unintended web page. User session data, credentials, and auth tokens are cleared before the redirect.

Mitigations:

 1) Remove or disable the portlet's based user interface. 
 2) Upgrade to newer versions of jUDDI (v3.2 and newer) which is not affected by this issue
 3) If upgrading or disabling the portlet based user interface is not an option, the following can be used to resolve the issue. Modify the file located at "uddi-portlets/logout.jsp", replacing the following text
 
````
   "String redirectURL = (String) request.getParameter("urlredirect");
   if (redirectURL==null) redirectURL = "/pluto/Logout";
````

with this text

````
    String redirectURL = "/pluto/Logout";
````

No patches or releases are planned for the affected versions since jUDDI v3.2 replaced the user interface.
