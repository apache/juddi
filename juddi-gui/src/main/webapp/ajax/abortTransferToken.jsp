<%-- 
    Document   : abortTransferToken
    Created on : Apr 27, 2013, 1:20:37 PM
    Author     : Alex O'Ree
/*
 * Copyright 2001-2008 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
--%>

<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<%@page import="org.apache.juddi.jaxb.PrintUDDI"%>
<%@page import="org.uddi.custody_v3.TransferToken"%>
<%@page import="javax.xml.datatype.DatatypeFactory"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="javax.xml.datatype.XMLGregorianCalendar"%>
<%@page import="javax.xml.ws.Holder"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:include page="../csrf.jsp" />
<!DOCTYPE html>
<%
    if (request.getMethod().equalsIgnoreCase("POST")) {
        String tokenxml = request.getParameter("tokenxml");
 

        UddiHub x = UddiHub.getInstance(application, session);
        try{
            String msg = x.DiscardToken(tokenxml);
            if (msg != null) {
                if (msg.contains(ResourceLoader.GetResource(session, "errors.generic")))
                    response.setStatus(406);
                out.write(msg);
            } else {
                out.write(ResourceLoader.GetResource(session, "actions.canceled"));

            }
        } catch (Exception ex) {
            response.sendError(400);
            return;
        }
    }


%>