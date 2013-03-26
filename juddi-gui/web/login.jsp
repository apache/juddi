<%-- 
    Document   : login
    Created on : Feb 24, 2013, 9:08:02 AM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<div class="navbar-form pull-right">
    <%
        if (session.getAttribute("username") == null || session.getAttribute("password") == null) {
    %>
    <script type="text/javascript">
        loggedin = false;
    </script>
    <input class="span2" type="text" placeholder="Username" name="username" id="username">
    <input class="span2" type="password" placeholder="Password" name="password" id="password">
    <button type="button" onclick="javascript:Login();" class="btn" id="loginbutton">Login</button>

    <%    } else {
    %>
     <script type="text/javascript">
        loggedin = true;
    </script>
    <a class="btn" title="Click to logout" href="javascript:logout();"><i class="icon-user"></i>
        <%
                out.write("Welcome " + StringEscapeUtils.escapeHtml((String) session.getAttribute("username")) + "</a>");

            }
        %>
</div>
