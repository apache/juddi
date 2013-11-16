<%-- 
    Document   : login
    Created on : Feb 24, 2013, 9:08:02 AM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<div class="navbar-form pull-right">

    <%
        if (session.getAttribute("username") != null && session.getAttribute("password") != null
                && ((String) session.getAttribute("username")).length() > 0 && ((String) session.getAttribute("password")).length() > 0) {
            //we're probably logged in

    %>
    <script type="text/javascript">
        loggedin = true;
    </script>
    <a class="btn" title="<%=ResourceLoader.GetResource(session, "navbar.login.logout")%>" href="javascript:logout();">
        <%
                if (!request.isSecure() && UddiHub.getInstance(application, session).isSecure()) {
            %>
            <i class="icon-warning-sign" title="<%=ResourceLoader.GetResource(session, "warning.ssl")%>"></i>
            <%
                }
            %>
        
        <i class="icon-user"></i>
        <%
            out.write(ResourceLoader.GetResource(session, "items.welcome") + " " + StringEscapeUtils.escapeHtml((String) session.getAttribute("username")) + "</a>");

        } else {
        %>

        <script type="text/javascript">
            loggedin = false;
        </script>

        <input class="span2" type="text" placeholder="<%=ResourceLoader.GetResource(session, "navbar.login.username")%>" name="username" id="username">
        <input class="span2" type="password" placeholder="<%=ResourceLoader.GetResource(session, "navbar.login.password")%>" name="password" id="password">
        <button type="button" onclick="javascript:Login();" class="btn" id="loginbutton">
            <%
                if (!request.isSecure()) {
            %>
            <i class="icon-warning-sign" title="<%=ResourceLoader.GetResource(session, "warning.ssl")%>"></i>
            <%
                }
            %>
            <%=ResourceLoader.GetResource(session, "navbar.login.button")%>
        </button>
        <%
            }
        %>


</div>
