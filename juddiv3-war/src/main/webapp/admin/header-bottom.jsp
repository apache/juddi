<%-- 
    Document   : header-bottom
    Created on : Feb 24, 2013, 9:08:18 AM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.io.IOException"%>
<%@page import="java.util.jar.Attributes"%>
<%@page import="java.util.jar.Manifest"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.util.jar.JarFile"%>
<%@page import="java.net.URL"%>
<%@page import="java.util.Enumeration"%>
<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

</div> <!-- /container -->
</form>
<div style="
     padding: 0px 0px 0px 0px; bottom: 0px; margin: 0px 0px 0px 0px; width:100%; text-align: center; position: fixed; 
     background-color: white; 
     "><center><footer><span style="color: red"><b>BETA</b></span> - v<%
     out.write(StringEscapeUtils.escapeHtml(org.apache.juddi.v3.client.Release.getRegistryVersion()));
        %> - <a href="http://www.apache.org"><%=ResourceLoader.GetResource(session, "footer.apachecopyright")%></a</footer></center></div>
</body>
</html>

