<%-- 
    Document   : publisherBrowse
    Created on : Feb 28, 2013, 6:44:04 AM
    Author     : Alex O'Ree
--%>


<%@page import="org.apache.juddi.webconsole.UddiHub"%>
<%@page import="org.apache.juddi.v3.client.transport.Transport"%>

<%@page import="org.apache.juddi.ClassUtil"%>
<%@page import="org.apache.juddi.query.util.FindQualifiers"%>
<%@page import="org.apache.juddi.v3.client.config.UDDIClientContainer"%>
<%@page import="org.apache.juddi.v3.client.transport.Transport"%>
<%@page import="org.apache.juddi.v3_service.JUDDIApiPortType"%>
<%@page import="org.uddi.api_v3.AuthToken"%>
<%@page import="org.uddi.api_v3.BindingTemplates"%>
<%@page import="org.uddi.api_v3.BusinessDetail"%>
<%@page import="org.uddi.api_v3.BusinessInfos"%>
<%@page import="org.uddi.api_v3.BusinessList"%>
<%@page import="org.uddi.api_v3.BusinessService"%>
<%@page import="org.uddi.api_v3.CategoryBag"%>
<%@page import="org.uddi.api_v3.Contacts"%>
<%@page import="org.uddi.api_v3.Description"%>
<%@page import="org.uddi.api_v3.FindBusiness"%>
<%@page import="org.uddi.api_v3.GetAuthToken"%>
<%@page import="org.uddi.api_v3.GetBusinessDetail"%>
<%@page import="org.uddi.api_v3.GetServiceDetail"%>
<%@page import="org.uddi.api_v3.KeyedReference"%>
<%@page import="org.uddi.api_v3.Name"%>
<%@page import="org.uddi.api_v3.ServiceDetail"%>
<%@page import="org.uddi.api_v3.ServiceInfos"%>
<%@page import="org.uddi.v3_service.UDDIInquiryPortType"%>
<%@page import="org.uddi.v3_service.UDDISecurityPortType"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="header-top.jsp" %>
<div class="container">

    <!-- Main hero unit for a primary marketing message or call to action -->
    <div class="well" >
        <h1>Publishers</h1>
    </div>

    <!-- Example row of columns -->
    <div class="row">
        <div class="span12" >
            <div id="publisherlist">
                <%
                    UddiHub x = UddiHub.getInstance(application, request.getSession());

                    out.write(x.GetPublisherListAsHtml());
                %>
            </div>
            <script type="text/javascript">
                
                $('.edit').editable(function(value, settings) { 
                    console.log(this);
                    console.log(value);
                    console.log(settings);
                    return(value);
                }, { 
                    type    : 'text',
                    submit  : 'OK'
                });
            </script>
        </div>
    </div>
    <%@include file="header-bottom.jsp" %>
