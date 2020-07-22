<%-- 
    Document   : browse
    Created on : Feb 24, 2013, 9:14:01 AM
    Author     : Alex O'Ree
/*
 * Copyright 2001-2008 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
--%>

<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="header-top.jsp" %>
<div class="container">

   
   <div class="well" >
      <h1><%=ResourceLoader.GetResource(session, "navbar.businesses")%></h1>
   </div>

   
   <div class="row">
      <div class="span12" >
         <table class="table-bordered table-striped">
            <tr>
               <td>
                  <table>
                     <tr><td><%=ResourceLoader.GetResource(session, "totals.records")%></td><td><span id="totalrecordsBusiness"></span></td></tr>
                     <tr><td><%=ResourceLoader.GetResource(session, "totals.recordsreturned")%></td><td><span id="displayrecordsBusiness"></span></td></tr>
                     <tr><td title="<%=ResourceLoader.GetResource(session, "totals.offset.v2disclaimer")%>">
                                <%=ResourceLoader.GetResource(session, "totals.offset")%></td><td><input id="offsetBusiness" value="0"></td></tr>
                  </table>
               </td>
               <td>
                  <table>
                     <tr><td><%=ResourceLoader.GetResource(session, "items.name")%></td><td><input type="text" id="name_business" value="%" tabindex="1"></td></tr>
                     <tr><td><%=ResourceLoader.GetResource(session, "items.lang")%></td><td><input type="text" id="lang_business" tabindex="2"></td></tr>
                     <tr><td><%=ResourceLoader.GetResource(session, "totals.fetch")%></td><td><input type="number" id="fetch" value="40"></td></tr>

                  </table>
               </td>
            </tr>
         </table>
         <a href="javascript:pagedownBusiness();"><i class="icon-circle-arrow-left disabled icon-2x" id="pageupBusiness"></i></a>
         <a href="javascript:refreshBusinessList();"><i class="icon-refresh icon-2x" id="refresh"></i></a>
         <a href="javascript:pageupBusiness();"><i class="icon-circle-arrow-right disabled icon-2x" id="pagedownBusiness"></i></a>

         <div id="businesslist">
            <img src="img/bigrollergreen.gif" title="<%=ResourceLoader.GetResource(session, "items.loading")%>"/>
         </div>

         <script src="js/businesssearch.js"></script>
         <script type="text/javascript">

            $('.edit').editable(function(value, settings) {
               window.console && console.log(value + this + settings);
               RenderBusinessListBySearch('%', offset, maxrecords);
               return(value);
            }, {
               type: 'text',
               submit: i18n_ok
            });
            /*
            var now=+new Date();
            $("name_business").keydown(function(e) {
               
               var newnow = + new Date();
               window.console && console.log(newnow + " " + now + " refreshing business list (search as you type)");
               if (newnow-now > 500 )
               {
                  refreshBusinessList();
                  window.console && console.log(newnow + " " + now + " refreshing business list (search as you type)");
               }
               return false;
            });*/
         </script>
      </div>
   </div>
   <%@include file="header-bottom.jsp" %>