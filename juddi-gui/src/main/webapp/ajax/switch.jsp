<%-- 
    Document   : switch
    Created on : Jan 21, 2014, 4:23:25 PM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<%@page import="org.apache.juddi.api_v3.Node"%>
<%@page import="java.util.List"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include  file="../csrf.jsp" %>
<%

        UddiHub x = UddiHub.getInstance(application, session);
        String node = request.getParameter("node");

        String description=null;
        List<Node> nodes = x.GetJuddiClientConfig().getUDDINodeList();
        boolean found = false;
        for (int i = 0; i < nodes.size(); i++) {
                if (nodes.get(i).getName().equals(node)) {
                        description = nodes.get(i).getDescription();
                        found = true;
                        break;
                }
        }
        if (!found) {
                response.setStatus(406);
                out.write("Not found.");
        } else {
                node=x.switchNodes(node);
                out.write(ResourceLoader.GetResource(session, "items.nowconnectedto") +"<br>");
                out.write(ResourceLoader.GetResource(session, "items.nodeid") + ": " + StringEscapeUtils.escapeHtml(node));
                out.write("<br>");
                out.write(ResourceLoader.GetResource(session, "items.description") + ": " + StringEscapeUtils.escapeHtml(description));
                Cookie cookie = new Cookie("current_node", node);
                cookie.setMaxAge(Integer.MAX_VALUE);
                cookie.setPath("/");
                response.addCookie(cookie);
        }
%>