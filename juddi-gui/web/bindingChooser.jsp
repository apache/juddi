<%-- 
    Document   : bindingChooser
    Created on : Apr 24, 2013, 6:25:00 PM
    Author     : Alex O'Ree
this page is meant to be included via jsp:include
--%>

<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<div class="modal hide fade" id="bindingChooser">
    <div class="modal-header">
        <a href="javascript:$('#bindingChooser').modal('hide');" class="close" data-dismiss="modal" aria-hidden="true">&times;</a>
        <h3>Binding Chooser</h3>
    </div>
    <div class="modal-body">

        <%=ResourceLoader.GetResource(session, "totals.records")%>: <span id="totalrecordsBinding"></span><br>
        <%=ResourceLoader.GetResource(session, "totals.recordsreturned")%>: <span id="displayrecordsBinding"></span><br>
        <%=ResourceLoader.GetResource(session, "totals.offset")%> : <span id="offsetBinding">0</span><br>
        <%=ResourceLoader.GetResource(session, "items.lang")%>: <span id="langBinding" class="edit"></span><br>

        <a href="javascript:pagedownChooserBinding();"><i class="icon-circle-arrow-left disabled icon-2x" id="pageupBinding"></i></a>
        <a href="javascript:reloadBinding();"><i class="icon-refresh icon-2x"></i></a>
        <a href="javascript:pageupChooserBinding();"><i class="icon-circle-arrow-right disabled icon-2x" id="pagedownBinding"></i></a>

        <div id="bindinglist">
            <img src="img/bigrollergreen.gif" title="Loading"/>
        </div>
        <script src="js/bindingChooser.js"></script>
        <script type="text/javascript">
                    
            $('.edit').editable(function(value, settings) { 
                console.log(this);
                console.log(value);
                console.log(settings);
                reloadBindingModal();
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
        <a href="#" class="btn" data-dismiss="modal">Cancel</a>
        <a href="javascript:$('#bindingChooser').modal('hide');" class="btn btn-primary" data-dismiss="modal">Select</a>
    </div>
</div>
<%
//<a href="javascript:tModelCancel();" class="close" class="btn btn-danger">Cancel</a>
//<a href="javascript:tModelModal();" class="btn" >Pick a tModel</a>
%>

<a href="javascript:$('#bindingChooser').modal();" class="btn btn-primary" data-dismiss="modal">Select</a>