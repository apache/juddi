<%-- 
    Document   : assertions
    Created on : Apr 13, 2013, 7:44:30 PM
    Author     : Alex O'Ree
--%>

<%@page import="org.uddi.api_v3.AssertionStatusItem"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.util.concurrent.atomic.AtomicReference"%>
<%@page import="javax.xml.ws.Holder"%>
<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<%@page import="org.apache.juddi.webconsole.PostBackConstants"%>
<%@page import="java.util.List"%>
<%@page import="org.uddi.api_v3.PublisherAssertion"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    UddiHub x = UddiHub.getInstance(application, session);
    if (request.getMethod().equalsIgnoreCase("post")) {
        if (request.getParameter("action") != null) {
            String action = request.getParameter("action");
            if (action.equalsIgnoreCase("delete")) {
                String msg = x.DeletePublisherAssertion(request.getParameter("tokey"), request.getParameter("fromkey"), request.getParameter("tmodelkey"), request.getParameter("keyname"), request.getParameter("keyvalue"));
                if (!msg.equals(ResourceLoader.GetResource(session, "actions.saved"))) {
                    response.setStatus(500);
                }
                out.write(msg);
            }
        } else {
            String msg = x.AddPublisherAssertion(request.getParameter("tokey"), request.getParameter("fromkey"), request.getParameter("tmodelkey"), request.getParameter("keyname"), request.getParameter("keyvalue"));
            if (!msg.equals(ResourceLoader.GetResource(session, "actions.saved"))) {
                response.setStatus(500);
            }
            out.write(msg);
        }
    } else {

        List<AssertionStatusItem> data = null;
        AtomicReference<String> msg = new AtomicReference<String>();
        data = x.GetPublisherAssertions(msg);
        if (msg != null && msg.get() != null) {
            out.write(msg.get());
        } else if (data == null || data.isEmpty())
            out.write(ResourceLoader.GetResource(session, "errors.nodatareturned"));
        else {

%>
<table class="table table-hover">
    <tr><th><%=ResourceLoader.GetResource(session, "items.publisherassertions.from")%></th>
        <th><%=ResourceLoader.GetResource(session, "items.publisherassertions.to")%></th>
        <th><%=ResourceLoader.GetResource(session, "items.publisherassertions.relationship")%></th>
        <th><%=ResourceLoader.GetResource(session, "items.actions")%></th>
        <th><%=ResourceLoader.GetResource(session, "items.status")%></th>
    </tr>
        <%
    //TODO i18n

            for (int i = 0; i < data.size(); i++) {
                out.write("<tr><td>");
                out.write(data.get(i).getFromKey());
                out.write("</td><td>");
                out.write(data.get(i).getToKey());
                out.write("</td><td>");
                if (data.get(i).getKeyedReference() != null) {
                    out.write("<div style=\"float:left\">Key :</div><div id=\"" + PostBackConstants.VALUE + "\" class=\"edit\">" + data.get(i).getKeyedReference().getTModelKey());
                    out.write("<div style=\"float:left\">Name :</div><div id=\"" + PostBackConstants.KEYNAME + "\" class=\"edit\">" + data.get(i).getKeyedReference().getKeyName());
                    out.write("<div style=\"float:left\">Value :</div><div id=\"" + PostBackConstants.KEYVALUE + "\" class=\"edit\">" + data.get(i).getKeyedReference().getKeyValue());
                }
                out.write("</td><td>");
                out.write("<a class=\"btn btn-primary\" href=\"javascript:removeAssertion('"
                        + StringEscapeUtils.escapeJavaScript(data.get(i).getFromKey())
                        + "','"
                        + StringEscapeUtils.escapeJavaScript(data.get(i).getToKey())
                        + "','"
                        + StringEscapeUtils.escapeJavaScript(data.get(i).getKeyedReference().getTModelKey())
                        + "','"
                        + StringEscapeUtils.escapeJavaScript(data.get(i).getKeyedReference().getKeyName())
                        + "','"
                        + StringEscapeUtils.escapeJavaScript(data.get(i).getKeyedReference().getKeyValue())
                        + "');"
                        + "\">Delete</a>");
                out.write("</td><td>");
                out.write(data.get(i).getCompletionStatus().toString());
                out.write("</td></tr>");
                
            }

        %>
</table>
<%
        }
    }
%>