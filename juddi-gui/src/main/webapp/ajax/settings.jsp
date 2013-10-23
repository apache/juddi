<%-- 
    Document   : settings
    Created on : Apr 6, 2013, 9:45:02 PM
    Author     : Alex O'Ree
--%>

<%@page import="java.io.File"%>
<%@page import="java.io.FileOutputStream"%>
<%@page import="java.util.Properties"%>
<%@page import="java.util.Enumeration"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include  file="../csrf.jsp" %>
<%    
    if (!request.getRemoteHost().equalsIgnoreCase("localhost") && !request.getRemoteHost().equalsIgnoreCase("127.0.0.1")) {
        response.setStatus(403);
    }
    if (!request.isUserInRole("manager")) {
        response.setStatus(403);
    }
    if (request.getMethod().equalsIgnoreCase("post")) {
        UddiHub x = UddiHub.getInstance(application, session);
        Properties p = x.GetRawConfiguration();
        Enumeration it = request.getParameterNames();
        while (it.hasMoreElements()) {
            String key = (String) it.nextElement();
            String value = request.getParameter(key);
            if (key.equals("authtype")) {
                p.setProperty(key, value);
            }
            else if (key.startsWith("client"))
            {
                //its part of the juddi client config file
                x.GetJuddiClientConfig().getConfiguration().setProperty(key, value);
                //this just sets the runtime config
                
            }
        }
        try{
            x.GetJuddiClientConfig().saveConfig();
        }
        catch (Exception ex)
        {
            response.setStatus(500);
            out.write("Error saving Juddi Client Config" + ex.getMessage());
        }
        //attempt to save the properties file
        try {
            FileOutputStream fos = new FileOutputStream(new File(x.GetRawConfigurationPath()));
            String msg = "Edited at " + System.currentTimeMillis() + " by " + request.getRemoteUser();
            if (request.getUserPrincipal() != null) {
                msg += " " + request.getUserPrincipal().toString();
            }
            p.store(fos, "Edited at " + System.currentTimeMillis() + " by " + request.getRemoteUser() + request.getUserPrincipal().getName());
            fos.close();
        } catch (Exception ex) {
            response.setStatus(500);
            out.write("Error saving config.properties (authmode only) " + ex.getMessage());
        }
        
    }
%>