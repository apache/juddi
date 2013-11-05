<%-- 
    Document   : keyHelpModal.jsp
    Created on : Nov 5, 2013, 7:29:53 AM
    Author     : Alex O'Ree
--%>
<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<script type="text/javascript">
    function ShowKeyHelp()
    {
        $("#aboutKeys").modal('show');
    }
</script>

<div class="modal hide fade container" id="aboutKeys">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h3><%=ResourceLoader.GetResource(session, "navbar.help")%></h3>
    </div>
    <div class="modal-body">
        <%=ResourceLoader.GetResource(session, "pages.uddikeys")%>
    </div>
    <div class="modal-footer">

        <a href="javascript:closeXmlPop('aboutKeys');" class="btn"><%=ResourceLoader.GetResource(session, "modal.close")%></a>
    </div>
</div>
