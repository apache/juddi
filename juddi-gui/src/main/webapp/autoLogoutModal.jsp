<%-- 
    Document   : autoLogoutModal
    Created on : Nov 5, 2013, 6:25:56 PM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<%
    UddiHub internal_ = UddiHub.getInstance(application, session);
    if (internal_.isAutoLogoutEnabled()) {
        
%>
<script type="text/javascript">

    var timer1, timer2;
    document.onkeypress = resetTimer;
    document.onmousemove = resetTimer;
    var counterzxy=0;
    function resetTimer()
    {
         window.console && console.log("time reset " + counterzxy++);
        $("#autologout").modal('hide');
        clearTimeout(timer1);
        clearTimeout(timer2);

        // waiting time in minutes
        var wait = <%=internal_.GetAutoLogoutDuration()%>;

        // alert user one minute before
        timer1 = setTimeout("alertUser()", (wait) - 30000);

        // logout user
        timer2 = setTimeout("logout()", wait);
    }

    function alertUser()
    {
        $("#autologout").modal('hide');
        $("#autologout").modal('show');
        // document.getElementById('timeoutPopup').style.display = 'block';
    }

    function logout()
    {
        window.location.href = 'logoutredir.jsp';
    }

</script>


<div class="modal hide fade container" id="autologout">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h3><%=ResourceLoader.GetResource(session, "pages.serviceimport.content.sslwarning")%></h3>
    </div>
    <div class="modal-body">
        <%=ResourceLoader.GetResource(session, "pages.session.expire")%>
    </div>
    <div class="modal-footer">

        <a href="javascript:closeXmlPop('autologout');" class="btn"><%=ResourceLoader.GetResource(session, "modal.close")%></a>
    </div>
</div>

<%
    }

%>