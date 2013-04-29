<%-- 
    Document   : tmodelChooser
    Created on : Apr 17, 2013, 6:25:00 PM
    Author     : Alex O'Ree
this page is meant to be included via jsp:include
--%>

<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<div class="modal hide fade container" id="tmodelChooser">
    <div class="modal-header">
        <a href="javascript:$('#tmodelChooser').modal('hide');" class="close" data-dismiss="modal" aria-hidden="true">&times;</a>
        <h3><%=ResourceLoader.GetResource(session, "items.tmodel.chooser")%></h3>
    </div>
    <div class="modal-body">

        <%=ResourceLoader.GetResource(session, "totals.records")%>: <span id="totalrecords"></span><br>
        <%=ResourceLoader.GetResource(session, "totals.recordsreturned")%>: <span id="displayrecords"></span><br>
        <%=ResourceLoader.GetResource(session, "totals.offset")%> : <span id="offset">0</span><br>
        <%=ResourceLoader.GetResource(session, "items.lang")%>: <span id="lang" class="edit"></span><br>

        <a href="javascript:pagedownChooserTmodel();"><i class="icon-circle-arrow-left disabled icon-large" id="pageup"></i></a>
        <a href="javascript:reload();"><i class="icon-refresh icon-large"></i></a>
        <a href="javascript:pageupChooserTmodel();"><i class="icon-circle-arrow-right disabled icon-large" id="pagedown"></i></a>

        <div id="tmodellist">
            <img src="img/bigrollergreen.gif" title="Loading"/>
        </div>
        <script src="js/tmodelsearch.js"></script>
        <script src="js/tmodelChooser.js"></script>
        <script type="text/javascript">
                    
            $('.edit').editable(function(value, settings) { 
                console.log(this);
                console.log(value);
                console.log(settings);
                reloadTmodelModal();
                //  RenderTmodelListBySearch('%', offset, maxrecords);
                return(value);
            }, { 
                type    : 'text',
                submit  : 'OK'
            });
           //only init the data when required reloadTmodelModal();
        </script>

    </div>
    <div class="modal-footer">
        <a href="#" class="btn" data-dismiss="modal"><%=ResourceLoader.GetResource(session, "actions.cancel")%></a>
        <a href="javascript:$('#tmodelChooser').modal('hide');" class="btn btn-primary" data-dismiss="modal"><%=ResourceLoader.GetResource(session, "actions.select")%></a>
    </div>
</div>
<%
//<a href="javascript:tModelCancel();" class="close" class="btn btn-danger">Cancel</a>
//<a href="javascript:tModelModal();" class="btn" >Pick a tModel</a>
%>