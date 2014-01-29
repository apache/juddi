<%-- 
    Document   : Service Browser
    Created on : Feb 24, 2013, 9:14:01 AM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="header-top.jsp" %>
<div class="container">

   <!-- Main hero unit for a primary marketing message or call to action -->
   <div class="well" >
      <h1><%=ResourceLoader.GetResource(session, "navbar.services")%></h1>
   </div>

   <!-- Example row of columns -->
   <div class="row">
      <div class="span12" >
         <div id="businesslist">
            <table class="table-striped table-bordered">
               <tr><td>
                     <table>
                        <tr><td><%=ResourceLoader.GetResource(session, "totals.records")%></td><td><span id="totalrecordsService"></span></td></tr>
                        <tr><td><%=ResourceLoader.GetResource(session, "totals.recordsreturned")%></td><td><span id="displayrecordsService"></span></td></tr>
                        <tr><td><%=ResourceLoader.GetResource(session, "totals.offset")%></td><td><span id="offsetService">0</span></td></tr>
                     </table>
                  </td>
                  <td>
                     <table>
                        <tr><td><%=ResourceLoader.GetResource(session, "items.name")%></td><td><input type="text" id="name_service_search" value="%"></td></tr>
                        <tr><td><%=ResourceLoader.GetResource(session, "items.lang")%></td><td><input type="text" id="lang_service_search" value=""></td></tr>
                     </table>
                  </td>
               </tr>
            </table>

            <a href="javascript:pagedownService();"><i class="icon-circle-arrow-left icon-2x" id="pageupService"></i></a>
            <a href="javascript:refreshServiceList();"><i class="icon-refresh icon-2x" id="refresh"></i></a>
            <a href="javascript:pageupService();"><i class="icon-circle-arrow-right  icon-2x" id="pagedownService"></i></a>

            <div id="serviceBrowserListings">
               <img src="img/bigrollergreen.gif" title="Loading"/>
            </div>
            <script src="js/serviceBrowse.js"></script>
            <script type="text/javascript">

               $('.edit').editable(function(value, settings) {
                 window.console && console.log(value + this + settings);
                  RenderServiceListBySearch('%', offset, maxrecords);
                  return(value);
               }, {
                  type: 'text',
                  submit: i18n_ok
               });
            </script>
         </div>

      </div>
   </div>
   <%@include file="header-bottom.jsp" %>