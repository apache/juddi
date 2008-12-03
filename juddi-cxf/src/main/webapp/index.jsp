<%@ page session="false" %>
<%@ page import="java.util.List,
                 org.apache.juddi.util.Install,
                 javax.xml.bind.JAXBException,
                 org.uddi.v3_service.DispositionReportFaultMessage,
                 org.apache.juddi.config.AppConfig,
                 org.apache.juddi.config.Property,
                 org.uddi.api_v3.RegisteredInfo,
                 org.uddi.api_v3.BusinessInfo,
                 org.uddi.api_v3.Name,
                 org.uddi.api_v3.Description"
%>

<!-- index.jsp -->
<%

String errMsg = "";
if (request.getParameter("install") != null) {
    try {
        Install.install(request.getRealPath("WEB-INF\\install"));
    }
    catch (JAXBException je) {
        errMsg = "JAXBException occurred attempting to install jUDDI:  " + je.getMessage();
        if (je.getLinkedException() != null)
            errMsg = errMsg + "; linkedException=" + je.getLinkedException().getMessage();
    }
    catch (DispositionReportFaultMessage drfm) {
        errMsg = "An error occurred attempting to install jUDDI:  " + drfm.getMessage();
    }
}
%>
<html>
<head>
<title>Apache jUDDI Registry</title>
<link rel="stylesheet" href="juddi.css" />
</head>
<body>
<div class="header" align="right"><a href="http://ws.apache.org/juddi/" target="_top">jUDDI@Apache</a></div>
<h1>Apache jUDDI version [add version info here!]</h1>

<% 
if (errMsg != null && errMsg.length() > 0) {
%> 
<div class="error"><%= errMsg %></div>
<%	
}
%>

<h3><em>Welcome</em> to Apache jUDDI!</h3>
<ul>
    <li><a href="services">View</a> service listing</li>
    <li><a href="http://ws.apache.org/juddi/">Visit</a> the Apache-jUDDI Home Page</li>
</ul>

<%
if (Install.alreadyInstalled())  {
    String nodeKey = "";
    String nodeName = "";
    String nodeDescription = "";
    
    RegisteredInfo ri = Install.getRootRegisteredInfo();
    if (ri != null) {
        List biList = ri.getBusinessInfos().getBusinessInfo();
        if (biList != null && biList.size() > 0) {
            BusinessInfo bi = (BusinessInfo) biList.get(0);
            nodeKey = bi.getBusinessKey();
            Name n = (Name) bi.getName().get(0);
            if (n != null)
                nodeName = n.getValue();
            
            List descList = bi.getDescription();
            if (descList != null && descList.size() > 0) {
                Description d = (Description) descList.get(0);
                if (d != null)
                    nodeDescription = d.getValue();
            }
                
            
        }
    }
%>
    <div>jUDDI has been successfully installed!</div>
    <p />
    <h3>Node Information</h3>
    <table>
        <tr>
            <td><b>Root Domain:</b></td>
            <td><%= AppConfig.getConfiguration().getString(Property.JUDDI_ROOT_DOMAIN) %></td>
        </tr>
        <tr>
            <td><b>Business Key:</b></td>
            <td><%= nodeKey %></td>
        </tr>
        <tr>
            <td><b>Name:</b></td>
            <td><%= nodeName %></td>
        </tr>
        <tr>
            <td><b>Description:</b></td>
            <td><%= nodeDescription %></td>
        </tr>
    </table>
<%
} else { 
%>
    <div>
      jUDDI does not appear to have been installed.  In order for jUDDI to function properly, certain entities must be installed into the registry.
      Please read the setup documentation for more information.
    </div>
    <form action="index.jsp" method="post">
      <input type="submit" value="Install" name = "install" />
    </form>
<% 
} 
%>

<hr />
<table width="100%" border="0">
    <tr>
        <td height="50" align="center" valign="bottom" nowrap>
        <div class="footer">&nbsp;</div>
        </td>
    </tr>
</table>

</body>
</html>