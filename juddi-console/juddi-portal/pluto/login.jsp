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
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<% pageContext.setAttribute("now", new java.util.Date()); %>

<html>
  
  <head>
    <title>Pluto Portal</title>
    <!--[if lt IE 7]>
        <script type="text/javascript" src="<c:out value="${pageContext.request.contextPath}"/>/unitpngfix.js"></script>
	<![endif]--> 
    <style type="text/css" title="currentStyle" media="screen">
      @import "<c:out value="${pageContext.request.contextPath}"/>/pluto.css";
    </style>
    <script type="text/javascript"
            src="<c:out value="${pagecontext.request.contextpath}"/>/pluto.js">
    </script>
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
      <div id="header">
        <h1>Apache Pluto</h1>
        <p>A Apache Portals Project</p>
      </div>
      <div id="content">
        <c:if test='${param.error == "1"}'>
          <p style="color:red;text-align:center">
            Invalid credentials. Please try again
          </p>
        </c:if>
        <form method="POST" action="j_security_check">
          <fieldset>
            <legend>Login to Pluto</legend>
            <div>
              <label for="j_username">User Name</label>
              <input type="text" name="j_username" id="j_username"/>
            </div>
            <div>
              <label for="j_password">Password</label>
              <input type="password" name="j_password" id="j_password"/>
            </div>
            <div>
              <label for="j_login"></label>
              <input type="submit" value="Login" name="login" id="j_login"/>
            </div>
          </fieldset>
        </form>
      </div>
      
      <div id="footer">
        &copy; 2003-<fmt:formatDate value="${now}" pattern="yyyy"/> Apache Software Foundation
      </div>
      
    </div>
  </div>
  
  </body>
  
</html>


