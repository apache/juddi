Title: Security Advisories

## Security Advisories for Apache jUDDI

### CVEID:CVE-2015-5241

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
