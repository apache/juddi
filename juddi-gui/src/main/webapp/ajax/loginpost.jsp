<%-- 
    Document   : loginpost
    Created on : Feb 24, 2013, 3:36:37 PM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.util.Properties"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.net.URL"%>
<%@page import="org.apache.juddi.webconsole.AES"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include  file="../csrf.jsp" %>
<%  URL prop = application.getResource("/WEB-INF/config.properties");
        boolean ok = true;
        if (prop == null) { 
                prop = application.getResource("WEB-INF/config.properties");

        }
        if (prop == null) {
                prop = application.getResource("META-INF/config.properties");

        }
        if (prop == null) {
                prop = application.getResource("/META-INF/config.properties");

        }
        if (prop == null) {
                response.setStatus(406);

                out.write("Contact the sysadmin. Cannot locate the configuration file.");
                ok = false;
        }

        InputStream in = prop.openStream();
        Properties p = new Properties();
        p.load(in);
        in.close();
        session.setAttribute("username", request.getParameter("username"));
        if (request.getParameter("password") == null || request.getParameter("password").length() == 0) {
                response.setStatus(406);
                ok = false;
                out.write("Please enter a password");
                //TODO i18n
        }
        if (request.getParameter("username") == null || request.getParameter("username").length() == 0) {
                response.setStatus(406);
                ok = false;
                out.write("Please enter a username");
                //TODO i18n
        }
        if (ok) {
                try {
                        session.setAttribute("password", AES.Encrypt(request.getParameter("password"), (String) p.get("key")));
                } catch (Exception ex) {
                        response.setStatus(406);

                        out.write(StringEscapeUtils.escapeHtml(ex.getMessage()));
                }

                UddiHub.reset(request.getSession());
                UddiHub x = UddiHub.getInstance(application, request.getSession());

                String msg = x.verifyLogin();
                if (msg != null) {
                        session.removeAttribute("username");
                        session.removeAttribute("password");
                        response.setStatus(406);

                        out.write(msg);
                }
        }


%>