<%--
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed  under the  License is distributed on an "AS IS" BASIS,
WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
implied.

See the License for the specific language governing permissions and
limitations under the License.
--%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://portals.apache.org/pluto" prefix="pluto" %>
<% pageContext.setAttribute("now", new java.util.Date()); %>

<!--
Portal page template for default theme used by the Pluto Portal Driver.
This template divides all portlets into two groups (div blocks): the first
group (the left column) displays portlets with odd IDs, while the second group
(the right column) displays portlets with even IDs.
-->

<html>

<head>
    <title>Pluto Portal</title>
    <!--[if lt IE 7]>
        <script type="text/javascript" src="<c:out value="${pageContext.request.contextPath}"/>/unitpngfix.js"></script>
	<![endif]--> 
    <style type="text/css" title="currentStyle" media="screen">
        @import "<c:out value="${pageContext.request.contextPath}"/>/pluto.css";
        @import "<c:out value="${pageContext.request.contextPath}"/>/portlet-spec-1.0.css";
    </style>
    <script type="text/javascript" src="<c:out value="${pageContext.request.contextPath}"/>/pluto.js"></script>
     <script type="text/javascript" >
	 	// writeCookie("myCookie", "my name", 24);
		// Stores the string "my name" in the cookie "myCookie" which expires after 24 hours.
		// The hours parameter is optional; if hours is left out, the cookie value expires at the end of the visitor's browser session.
		function writeCookie(name, value, hours)
		{
		  var expire = "";
		  if(hours != null)
		  {
			expire = new Date((new Date()).getTime() + hours * 3600000);
			expire = "; expires=" + expire.toGMTString();
		  }
		  document.cookie = name + "=" + escape(value) + expire + "; path=/";
		}
		
		// Example:
		// alert( readCookie("myCookie") );
		
		function readCookie(name)
		{
		  var cookieValue = "";
		  var search = name + "=";
		  if(document.cookie.length > 0)
		  { 
			offset = document.cookie.indexOf(search);
			if (offset != -1)
			{ 
			  offset += search.length;
			  end = document.cookie.indexOf(";", offset);
			  if (end == -1) end = document.cookie.length;
			  cookieValue = unescape(document.cookie.substring(offset, end))
			}
		  }
		  return cookieValue;
		}
		function getTheme () {
			var myTheme = readCookie("Theme");
			
			if (myTheme != null && myTheme != ''){
				//  alert(myTheme);
				document.body.style.backgroundColor= myTheme
			}
			
		} 
		function changeTheme (color) {
			if (color != "#") {
				document.body.style.backgroundColor=color;
				writeCookie("Theme", color, 24);
			}	
		}
		
	</script>

</head>

<body>
<script type="text/javascript">getTheme();</script>
<div id="wrapper">
<div id="portal">

    <!-- Header block: the Apache Pluto banner image and description -->
    <div id="header">
        <h1>Apache Pluto</h1>
        <p>An Apache Portals Project</p>
        
        <!-- Logout link -->
        <div id="logout" style="float:right;">
            <a href="/uddi-portlets/logout.jsp?urlredirect=%2fpluto%2fLogout">Logout</a>
            <SELECT onChange="changeTheme(this.options[this.selectedIndex].value);">
           	 <OPTION value="#">-- Select Theme --</OPTION>
           	 <OPTION value="midnightblue">Deep Atlantic</OPTION>
           	 <OPTION value="5d3207">Dark Chocolate</OPTION>
           	 <OPTION value="maroon">Crimson Tide</OPTION>
           	 <OPTION value="darkgreen">Forest Green</OPTION>
           	 <OPTION value="dimgrey">Neutral Grey</OPTION>
            </SELECT>
        </div>
    </div>


    <!-- Navigation block: links to portal pages -->
    <jsp:include page="navigation.jsp"/>

    <!-- Content block: portlets are divided into two columns/groups -->
    <div id="content">
        <pluto:isMaximized var="isMax"/>

        <!-- Left column -->
        <c:choose>
            <c:when test="${isMax}">
                    <c:forEach var="portlet" varStatus="status"
                               items="${currentPage.portletIds}">
                        <c:set var="portlet" value="${portlet}" scope="request"/>
                        <jsp:include page="portlet-skin.jsp"/>
                    </c:forEach>
             </c:when>

            <c:otherwise>
                <div id="portlets-left-column">
                    <c:forEach var="portlet" varStatus="status"
                               items="${currentPage.portletIds}" step="2">
                        <c:set var="portlet" value="${portlet}" scope="request"/>
                        <jsp:include page="portlet-skin.jsp"/>
                    </c:forEach>
                </div>

                <!-- Right column -->
                <div id="portlets-right-column">
                    <c:forEach var="portlet" varStatus="status"
                               items="${currentPage.portletIds}" begin="1" step="2">
                        <c:set var="portlet" value="${portlet}" scope="request"/>
                        <jsp:include page="portlet-skin.jsp"/>
                    </c:forEach>
                </div>

            </c:otherwise>
        </c:choose>

    </div>

    <!-- Footer block: copyright -->
    <div id="footer">
       &copy; 2003-<fmt:formatDate value="${now}" pattern="yyyy"/> Apache Software Foundation
    </div>

</div>
</div>
</body>

</html>


