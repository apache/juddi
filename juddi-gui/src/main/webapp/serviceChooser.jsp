<%-- 
    Document   : serviceChooser
    Created on : Apr 28, 2013, 7:11:19 PM
    Author     : Alex O'Ree
--%>


<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<div class="modal hide fade container " id="serviceChooser">
    <div class="modal-header">
        <a href="javascript:$('#serviceChooser').modal('hide');" class="close" data-dismiss="modal" aria-hidden="true">&times;</a>
        <h3><%=ResourceLoader.GetResource(session, "items.service.chooser")%> </h3>
    </div>
    <div class="modal-body">

        <%=ResourceLoader.GetResource(session, "totals.records")%>: <span id="totalrecordsService"></span><br>
        <%=ResourceLoader.GetResource(session, "totals.recordsreturned")%>: <span id="displayrecordsService"></span><br>
        <%=ResourceLoader.GetResource(session, "totals.offset")%> : <span id="offsetService">0</span><br>
        <%=ResourceLoader.GetResource(session, "items.lang")%>: <span id="langService" class="edit"></span><br>
        <%=ResourceLoader.GetResource(session, "items.name")%>: <span id="nameService" class="edit">%</span><br>

        <a href="javascript:pagedownChooserService();"><i class="icon-circle-arrow-left disabled icon-2x" id="pageupService"></i></a>
        <a href="javascript:reloadService();"><i class="icon-refresh icon-2x"></i></a>
        <a href="javascript:pageupChooserService();"><i class="icon-circle-arrow-right disabled icon-2x" id="pagedownService"></i></a>

        <div id="servicelist">
            <img src="img/bigrollergreen.gif" title="Loading"/>
        </div>
        <script src="js/serviceChooser.js"></script>
        <script type="text/javascript">
                    
            $('.edit').editable(function(value, settings) { 
                console.log(this);
                console.log(value);
                console.log(settings);
                reloadServiceModal();
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
        <a href="javascript:$('#serviceChooser').modal('hide');" class="btn btn-primary" data-dismiss="modal"><%=ResourceLoader.GetResource(session, "actions.select")%></a>
    </div>
</div>
<%
//<a href="javascript:tModelCancel();" class="close" class="btn btn-danger">Cancel</a>
//<a href="javascript:tModelModal();" class="btn" >Pick a tModel</a>


//<a href="javascript:$('#bindingChooser').modal();" class="btn btn-primary" data-dismiss="modal">< %=ResourceLoader.GetResource(session, "actions.select")% ></a>
%>

