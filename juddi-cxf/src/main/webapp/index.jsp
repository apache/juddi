<%@ page session="false" %>
<%@ page import="java.util.List,
                 org.apache.juddi.config.Install,
                 javax.xml.bind.JAXBException,
                 org.uddi.v3_service.DispositionReportFaultMessage,
                 org.apache.juddi.config.AppConfig,
                 org.apache.juddi.config.Property,
                 org.uddi.api_v3.BusinessEntity,
                 org.uddi.api_v3.Name,
                 org.uddi.api_v3.Description,
                 java.io.IOException,
                 org.apache.juddi.config.Release"
%>

<!-- index.jsp -->
<%

String errMsg = "";
if (request.getParameter("install") != null) {
    try {
        Install.install(request.getRealPath("WEB-INF" + java.io.File.separator + "install"), request.getParameter("rootPartition"), true);
    }
    catch (JAXBException je) {
        errMsg = "JAXBException occurred attempting to install jUDDI:  " + je.getMessage();
        if (je.getLinkedException() != null)
            errMsg = errMsg + "; linkedException=" + je.getLinkedException().getMessage();
    }
    catch (IOException ioe) {
        errMsg = "An IOException occurred attempting to install jUDDI:  " + ioe.getMessage();
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
<h1>Apache jUDDI version <%= Release.getRegistryVersion() %></h1>


<h3><em>Welcome</em> to Apache jUDDI!</h3>
<ul>
    <li><a href="/pluto/portal/jUDDI">jUDDI Portal</a></li>
    <li><a href="services">View</a> service listing</li>
    <li><a href="http://ws.apache.org/juddi/">Visit</a> the Apache-jUDDI Home Page</li>
</ul>

<div class="install">
  <h4>jUDDI Installation</h4>
    <div class="content">
<%
if (Install.alreadyInstalled())  {
    String rootPartition = AppConfig.getConfiguration().getString(Property.JUDDI_ROOT_PARTITION);
	String nodeId = AppConfig.getConfiguration().getString(Property.JUDDI_NODE_ID);
    String nodeName = "";
    String nodeDescription = "";
    
    BusinessEntity be = Install.getNodeBusinessEntity(nodeId);
    if (be != null) {
        Name n = (Name) be.getName().get(0);
        if (n != null)
            nodeName = n.getValue();

        List descList = be.getDescription();
        if (descList != null && descList.size() > 0) {
            Description d = (Description) descList.get(0);
            if (d != null)
                nodeDescription = d.getValue();
        }
    }
%>
    <div>jUDDI has been successfully installed!</div>
    <p />
    <h3>Node Information</h3>
    <table>
        <tr>
            <td><b>Root Partition:</b></td>
            <td><%= rootPartition %></td>
        </tr>
        <tr>
            <td><b>Node Id:</b></td>
            <td><%= nodeId %></td>
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
      You can chose to edit the entities located in the <i>WEB-INF/install</i> directory and install manually by clicking the install button below.  
      Or, you can leave everything as is, and the entities will automatically be installed with the default values.  Please read the setup documentation 
      for more information.
    </div>
<% 
if (errMsg != null && errMsg.length() > 0) {
%> 
    <br/>
    <div class="error"><%= errMsg %></div>
<%	
}
%>
    <br/>
    <div>
      <form action="index.jsp" method="post">
        Please enter the root partition for this node.  The root partition will serve as the prefix to all identifiers created in the node.<br/><br/>
        uddi:<input type="text" value="" name="rootPartition" size="40" /> (Ex. juddi.apache.org, www.mycompany.com:registry)<br/><br/>
        <input type="submit" value="Install" name = "install" />
      </form>
    </div>
<% 
} 
%>
  </div>
</div>

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
