<%-- 
    Document   : businessChooser
    Created on : Apr 24, 2013, 6:25:00 PM
    Author     : Alex O'Ree
this page is meant to be included via jsp:include
--%>

<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<div class="modal hide fade container " id="businessChooser">
    <div class="modal-header">
        <a href="javascript:$('#businessChooser').modal('hide');" class="close" data-dismiss="modal" aria-hidden="true">&times;</a>
        <h3><%=ResourceLoader.GetResource(session, "items.business.chooser")%> </h3>
    </div>
    <div class="modal-body">

        <%=ResourceLoader.GetResource(session, "totals.records")%>: <span id="totalrecordsBusiness"></span><br>
        <%=ResourceLoader.GetResource(session, "totals.recordsreturned")%>: <span id="displayrecordsBusiness"></span><br>
        <%=ResourceLoader.GetResource(session, "totals.offset")%> : <span id="offsetBusiness">0</span><br>
        <%//ResourceLoader.GetResource(session, "items.lang")%> <span id="langBusiness" class=""></span><br>

        <a href="javascript:pagedownChooserBusiness();"><i class="icon-circle-arrow-left disabled icon-2x" id="pageupBusiness"></i></a>
        <a href="javascript:reloadBusiness();"><i class="icon-refresh icon-2x"></i></a>
        <a href="javascript:pageupChooserBusiness();"><i class="icon-circle-arrow-right disabled icon-2x" id="pagedownBusiness"></i></a>

        <div id="businesslist">
            <img src="img/bigrollergreen.gif" title="Loading"/>
        </div>
        <script src="js/businessChooser.js"></script>
        <script type="text/javascript">
                    
            $('.edit').editable(function(value, settings) { 
                console.log(this);
                console.log(value);
                console.log(settings);
                reloadBusinessModal();
                //  RenderTmodelListBySearch('%', offset, maxrecords);
                return(value);
            }, { 
                type    : 'text',
                submit  : i18n_ok
            });
            //only init the data when required reloadTmodelModal();
        </script>

    </div>
    <div class="modal-footer">
        <a href="#" class="btn" data-dismiss="modal"><%=ResourceLoader.GetResource(session, "actions.cancel")%></a>
        <a href="javascript:$('#businessChooser').modal('hide');" class="btn btn-primary" data-dismiss="modal"><%=ResourceLoader.GetResource(session, "actions.select")%></a>
    </div>
</div>
