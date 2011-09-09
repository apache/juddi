<%--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
--%>
<%@ page import="
    org.apache.juddi.v3.client.config.*,
    org.apache.juddi.v3.client.transport.*,
    org.uddi.v3_service.UDDISecurityPortType,
    org.uddi.api_v3.*" %>
<% pageContext.setAttribute("now", new java.util.Date()); %>

<html>
  
  <head>
    <title>Pluto Portal</title>
  </head>

  <body>
  
    <% 
    String token = (String) session.getAttribute("AuthToken");
    if (token!=null) {
       out.println("Token=" + token);
       session.removeAttribute("AuthToken");
       session.removeAttribute("UserName");
       Transport transport = WebHelper.getTransport(session.getServletContext());
       UDDISecurityPortType securityService = transport.getUDDISecurityService();
       DiscardAuthToken discardAuthToken = new DiscardAuthToken();
       discardAuthToken.setAuthInfo(token);
       securityService.discardAuthToken(discardAuthToken);
       session.invalidate();
    }
    String redirectURL = (String) request.getParameter("urlredirect");
    if (redirectURL==null) redirectURL = "/pluto/Logout";
    response.sendRedirect(redirectURL);
    %>
  </body>
  
</html>


