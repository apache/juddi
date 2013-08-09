<%-- 
    Document   : saveconfig
    Created on : Jul 31, 2013, 6:52:50 PM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="org.apache.juddi.config.AppConfig"%>
<%@page import="org.apache.commons.configuration.Configuration"%>
<%@page import="java.util.Enumeration"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@include  file="../csrf.jsp" %>
<%
    if (request.getMethod().equalsIgnoreCase("POST")) {
        try {
            Enumeration it = request.getParameterNames();
            Configuration cfg = AppConfig.getConfiguration();
            while (it.hasMoreElements()) {
                String key = (String) it.nextElement();
                String val = request.getParameter(key);
                if (key != "nonce") {
                    boolean isbool = false;
                    boolean isint = false;
                    boolean boolval = false;
                    int intval = 0;
                    if (val.equalsIgnoreCase("true") || val.equalsIgnoreCase("true")) {
                        isbool = true;
                        boolval = Boolean.parseBoolean(val);
                    }

                    try {
                        intval = Integer.parseInt(val);
                        isint = true;
                    } catch (Exception ex) {
                    }
                    if (isbool) {
                        cfg.setProperty(key, boolval);
                    } else if (isint) {
                        cfg.setProperty(key, intval);
                    } else {
                        cfg.setProperty(key, val);
                    }


                }
            }
            out.write("<i class=\"icon-thumbs-up icon-2x\"> Saved!");
        } catch (Exception ex) {
            out.write("<i class=\"icon-thumbs-down icon-2x\"> Save Failed!<br>" + StringEscapeUtils.escapeHtml(ex.getMessage()));
        }
    }
%>