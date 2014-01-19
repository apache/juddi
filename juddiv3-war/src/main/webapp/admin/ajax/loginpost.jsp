<%-- 
    Document   : loginpost
    Created on : Feb 24, 2013, 3:36:37 PM
    Author     : Alex O'Ree
--%>

<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.File"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.util.Properties"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.net.URL"%>
<%@page import="org.apache.juddi.adminconsole.AES"%>
<%@page import="org.apache.juddi.adminconsole.hub.UddiAdminHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include  file="../csrf.jsp" %>
<%        boolean ok = true;
        FileInputStream fis = null;
        Properties p = new Properties();
        try {
                File f = new File(application.getRealPath("/WEB-INF/config.properties"));
                fis = new FileInputStream(f);

                p.load(fis);
                fis.close();
        } catch (Exception ex) {
                ex.printStackTrace();
                out.write("Internal configuration error");
                response.setStatus(406);
                ok = false;
        } finally {
                if (fis != null) {
                        try {
                                fis.close();
                        } catch (Exception x) {
                        }
                }
        }
        session.setAttribute("username", request.getParameter("username"));
        if (request.getParameter("password") == null || request.getParameter("password").length() == 0) {
                response.setStatus(406);
                out.write("Please enter a password");
                ok = false;
                //TODO i18n
        }
        if (request.getParameter("username") == null || request.getParameter("username").length() == 0) {
                response.setStatus(406);
                out.write("Please enter a username");
                ok = false;
                //TODO i18n
        }

        if (ok) {
                try {
                        session.setAttribute("password", AES.Encrypt(request.getParameter("password"), (String) p.get("key")));
                } catch (Exception ex) {
                        response.setStatus(406);
                        out.write(StringEscapeUtils.escapeHtml(ex.getMessage()));

                        UddiAdminHub.reset(request.getSession());
                        UddiAdminHub x = UddiAdminHub.getInstance(application, request.getSession());

                        String msg = x.verifyLogin();
                        if (msg != null) {
                                response.setStatus(406);
                                out.write(msg);
                        }
                }
        }


%>