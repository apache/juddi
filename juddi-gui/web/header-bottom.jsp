<%-- 
    Document   : header-bottom
    Created on : Feb 24, 2013, 9:08:18 AM
    Author     : Alex O'Ree
--%>

<%@page import="java.io.IOException"%>
<%@page import="java.util.jar.Attributes"%>
<%@page import="java.util.jar.Manifest"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.util.jar.JarFile"%>
<%@page import="java.net.URL"%>
<%@page import="java.util.Enumeration"%>
<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<hr>

<footer>
    <p><%=ResourceLoader.GetResource(session, "footer.apachecopyright")%></p>
</footer>



<div class="modal hide fade" id="viewAsXml">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h3>As XML</h3>
    </div>
    <div class="modal-body" id="viewAsXmlContent">


    </div>
    <div class="modal-footer">
        <a href="#" class="btn"><%=ResourceLoader.GetResource(session, "modal.close")%></a>
    </div>
</div>
</div> <!-- /container -->
</form>
<div style="
     padding: 0px 0px 0px 0px; bottom: 0px; margin: 0px 0px 0px 0px; width:100%; text-align: center; position: fixed; 
     background-color: white; 
     border-width: 4px; border-color: red; border-style: dashed"><center>BETA - v<%

    Enumeration resEnum;
    try {
        resEnum = Thread.currentThread().getContextClassLoader().getResources(JarFile.MANIFEST_NAME);
        while (resEnum.hasMoreElements()) {
            try {
                URL url = (URL) resEnum.nextElement();
                if (url.toString().toLowerCase().contains("juddi-client")) {
                   // out.write(url.toString() + "=");
                    InputStream is = url.openStream();
                    if (is != null) {
                        Manifest manifest = new Manifest(is);
                        Attributes mainAttribs = manifest.getMainAttributes();
                        String version = mainAttribs.getValue("Bundle-Version");
                        if (version != null) {
                            out.write(version);
                     //       out.write("<br>");
                        }
                    }
                }
            } catch (Exception e) {
                // Silently ignore wrong manifests on classpath?
            }
        }
    } catch (IOException e1) {
        // Silently ignore wrong manifests on classpath?
    }




        %></center></div>
</body>
</html>

