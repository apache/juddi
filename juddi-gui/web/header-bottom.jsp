<%-- 
    Document   : header-bottom
    Created on : Feb 24, 2013, 9:08:18 AM
    Author     : Alex O'Ree
--%>

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
</body>
</html>

