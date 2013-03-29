<%-- 
    Document   : csrf Provides basic Cross site request forgery protection
    Created on : Feb 27, 2013, 8:42:07 PM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="org.apache.log4j.Level"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String current = null;

    if (request.getMethod().equalsIgnoreCase("post")) {

        if ((request.getParameter("nonce") == null || request.getParameter("nonce").isEmpty())) {
            //reject it
            session.removeAttribute("nonce");
            response.sendRedirect("index.jsp");
            UddiHub.log.log(Level.WARN, "CSRF Test failed, no nonce guid." + request.getRemoteAddr() + request.getRemoteUser());
            throw new SecurityException("Cross Site Request Forgery");
        } else {

            String noncestr = (String) session.getAttribute("nonce");
            if (noncestr == null) {
                //no session variable to test against, reject it
                UddiHub.log.log(Level.WARN, "CSRF Test failed, no session guid." + request.getRemoteAddr() + request.getRemoteUser());
                session.removeAttribute("nonce");
                throw new SecurityException("Cross Site Request Forgery");
            }
            String postedstr = request.getParameter("nonce");

            //check session against existing nonce, if match
            //generate new one, add to page and session
            //else redirect to index page
            if (noncestr.equals(postedstr)) {
                current = noncestr;
                //OK
                // current = UUID.randomUUID();
                //session.removeAttribute("nonce");
                // session.setAttribute("nonce", current.toString());
                UddiHub.log.log(Level.INFO, "CSRF Test passed.");
            } else {
                //mismatch, reject it
                UddiHub.log.log(Level.WARN, "CSRF Test failed, session did not match nonce guid." + request.getRemoteAddr() + request.getRemoteUser());
                session.removeAttribute("nonce");
                throw new SecurityException("Cross Site Request Forgery");
            }
        }
    } else {
        //HTTP GET or otherwise message
        if ((current == null) || current.isEmpty()) {
            current = (String)session.getAttribute("nonce");
            if (current == null) {
                current = java.util.UUID.randomUUID().toString();
            }
            session.setAttribute("nonce", current);
        }

    }

%>
