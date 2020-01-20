Title: Security Advisories

## Security Advisories for Apache jUDDI

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
