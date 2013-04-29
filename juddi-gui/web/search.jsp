<%-- 
    Document   : search
    Created on : Feb 24, 2013, 9:14:01 AM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.v3.client.UDDIConstants"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="header-top.jsp" %>
<div class="container">

    <!-- Main hero unit for a primary marketing message or call to action -->
    <div class="well" >
        <h1><%= ResourceLoader.GetResource(session, "navbar.search")%> </h1>
    </div>

    <!-- Example row of columns -->
    <div class="row">
        <div class="span12" >


            <%
                UddiHub x = UddiHub.getInstance(application, request.getSession());

            %>
            <div class="accordion" id="accordion2">
                <div class="accordion-group">
                    <div class="accordion-heading">
                        <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapseOne">
                            <b><%= ResourceLoader.GetResource(session, "navbar.search")%> !</b>
                        </a>
                    </div>
                    <div id="collapseOne" class="accordion-body collapse in">
                        <div class="accordion-inner">
                            <%= ResourceLoader.GetResource(session, "search.wrylf")%><Br>
                            <div class="btn-group" id="searchfor" data-toggle="buttons-radio">
                                <button type="button" class="btn active" value="business"><%= ResourceLoader.GetResource(session, "items.business")%></button>
                                <!--<button type="button" class="btn " >Related Business</button>-->
                                <button type="button" class="btn " value="service"><%= ResourceLoader.GetResource(session, "items.service")%></button>
                                <button type="button" class="btn " value="bindingTemplate"><%= ResourceLoader.GetResource(session, "items.bindingtemplate")%></button>
                                <button type="button" class="btn " value="tModel"><%= ResourceLoader.GetResource(session, "items.tmodel")%></button>
                                <% 
                                //if (x.IsJuddiRegistry()) {
                              
                                //<button type="button" class="btn " ><%= ResourceLoader.GetResource(session, "items.publisher")%></button>

                                //}
                                %>
                            </div><br><Br>
                            <%= ResourceLoader.GetResource(session, "search.criteria")%><br>
                            <div class="btn-group" id="searchcriteria" data-toggle="buttons-radio">
                                <button type="button" class="btn active" active value="name"><%= ResourceLoader.GetResource(session, "search.criteria.byname")%></button>
                                <button type="button" class="btn " value="category"><%= ResourceLoader.GetResource(session, "search.criteria.bycategory")%></button>
                                <button type="button" class="btn " value="key"><%= ResourceLoader.GetResource(session, "search.criteria.bykey")%></button>
                                <button type="button" class="btn " value="tmodel"><%= ResourceLoader.GetResource(session, "search.criteria.bytmodel")%></button>
                            </div><br>
                            Find Qualifiers<br>

                            <div style=" float:left; padding: 2px">

                            <div ><input type="checkbox" name="<%=UDDIConstants.AND_ALL_KEYS%>" value="<%=UDDIConstants.AND_ALL_KEYS%>"> <%=UDDIConstants.AND_ALL_KEYS%></div>
                            <div ><input type="checkbox" name="<%=UDDIConstants.APPROXIMATE_MATCH%>" value="<%=UDDIConstants.APPROXIMATE_MATCH%>"> <%=UDDIConstants.APPROXIMATE_MATCH%></div>
                            <div ><input type="checkbox" name="<%=UDDIConstants.BINARY_SORT%>" value="<%=UDDIConstants.BINARY_SORT%>"> <%=UDDIConstants.BINARY_SORT%></div>
                            <div ><input type="checkbox" name="<%=UDDIConstants.BINDING_SUBSET%>" value="<%=UDDIConstants.BINDING_SUBSET%>"> <%=UDDIConstants.BINDING_SUBSET%></div>
                            <div ><input type="checkbox" name="<%=UDDIConstants.CASE_INSENSITIVE_MATCH%>" value="<%=UDDIConstants.CASE_INSENSITIVE_MATCH%>"> <%=UDDIConstants.CASE_INSENSITIVE_MATCH%></div>
                            </div>
                            <div style=" float:left; padding: 2px">
                            <div ><input type="checkbox" name="<%=UDDIConstants.CASE_INSENSITIVE_SORT%>" value="<%=UDDIConstants.CASE_INSENSITIVE_SORT%>"> <%=UDDIConstants.CASE_INSENSITIVE_SORT%></div>
                            <div ><input type="checkbox" name="<%=UDDIConstants.CASE_SENSITIVE_MATCH%>" value="<%=UDDIConstants.CASE_SENSITIVE_MATCH%>"> <%=UDDIConstants.CASE_SENSITIVE_MATCH%></div>
                            <div ><input type="checkbox" name="<%=UDDIConstants.CASE_SENSITIVE_SORT%>" value="<%=UDDIConstants.CASE_SENSITIVE_SORT%>"> <%=UDDIConstants.CASE_SENSITIVE_SORT%></div>
                            <div ><input type="checkbox" name="<%=UDDIConstants.COMBINE_CATEGORY_BAGS%>" value="<%=UDDIConstants.COMBINE_CATEGORY_BAGS%>"> <%=UDDIConstants.COMBINE_CATEGORY_BAGS%></div>
                            <div ><input type="checkbox" name="<%=UDDIConstants.DIACRITIC_INSENSITIVE_MATCH%>" value="<%=UDDIConstants.DIACRITIC_INSENSITIVE_MATCH%>"> <%=UDDIConstants.DIACRITIC_INSENSITIVE_MATCH%></div>
                            </div>
                            <div style=" float:left; padding: 2px">
                            <div ><input type="checkbox" name="<%=UDDIConstants.DIACRITIC_SENSITIVE_MATCH%>" value="<%=UDDIConstants.DIACRITIC_SENSITIVE_MATCH%>"> <%=UDDIConstants.DIACRITIC_SENSITIVE_MATCH%></div>
                            <div ><input type="checkbox" name="<%=UDDIConstants.EXACT_MATCH%>" value="<%=UDDIConstants.EXACT_MATCH%>"> <%=UDDIConstants.EXACT_MATCH%></div>
                            <div ><input type="checkbox" name="<%=UDDIConstants.OR_ALL_KEYS%>" value="<%=UDDIConstants.OR_ALL_KEYS%>"> <%=UDDIConstants.OR_ALL_KEYS%></div>
                            <div ><input type="checkbox" name="<%=UDDIConstants.OR_LIKE_KEYS%>" value="<%=UDDIConstants.OR_LIKE_KEYS%>"> <%=UDDIConstants.OR_LIKE_KEYS%></div>
                            <div ><input type="checkbox" name="<%=UDDIConstants.SERVICE_SUBSET%>" value="<%=UDDIConstants.SERVICE_SUBSET%>"> <%=UDDIConstants.SERVICE_SUBSET%></div>
                            </div>
                            <div style=" float:left; padding: 2px">
                            <div ><input type="checkbox" name="<%=UDDIConstants.SIGNATURE_PRESENT%>" value="<%=UDDIConstants.SIGNATURE_PRESENT%>"> <%=UDDIConstants.SIGNATURE_PRESENT%></div>
                            <div ><input type="checkbox" name="<%=UDDIConstants.SORT_BY_DATE_ASC%>" value="<%=UDDIConstants.SORT_BY_DATE_ASC%>"> <%=UDDIConstants.SORT_BY_DATE_ASC%></div>
                            <div ><input type="checkbox" name="<%=UDDIConstants.SORT_BY_DATE_DESC%>" value="<%=UDDIConstants.SORT_BY_DATE_DESC%>"> <%=UDDIConstants.SORT_BY_DATE_DESC%></div>
                            <div ><input type="checkbox" name="<%=UDDIConstants.SORT_BY_NAME_ASC%>" value="<%=UDDIConstants.SORT_BY_NAME_ASC%>"> <%=UDDIConstants.SORT_BY_NAME_ASC%></div>
                            <div ><input type="checkbox" name="<%=UDDIConstants.SORT_BY_NAME_DESC%>" value="<%=UDDIConstants.SORT_BY_NAME_DESC%>"> <%=UDDIConstants.SORT_BY_NAME_DESC%></div>
                            </div>
                            <div ><input type="checkbox" name="<%=UDDIConstants.SUPPRESS_PROJECTED_SERVICES%>" value="<%=UDDIConstants.SUPPRESS_PROJECTED_SERVICES%>"> <%=UDDIConstants.SUPPRESS_PROJECTED_SERVICES%></div>
                            <div ><input type="checkbox" name="<%=UDDIConstants.UTS_10%>" value="<%=UDDIConstants.UTS_10%>"> <%=UDDIConstants.UTS_10%></div>

                            <Br>
                            <br>
                            <div>
                                <br><Br>
                                <input type="text" placeholder="Type something…" id="searchcontent">
                                <input type="text" placeholder="Language" id="lang"><br>
                                <%= ResourceLoader.GetResource(session, "search.tip")%>
                                <br>
                                <a href="javascript:search();" class="btn btn-primary btn-large"><%= ResourceLoader.GetResource(session, "navbar.search")%> </a>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="accordion-group">
                    <div class="accordion-heading">
                        <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapseTwo">
                             <%= ResourceLoader.GetResource(session, "search.results")%>
                        </a>
                    </div>
                    <div id="collapseTwo" class="accordion-body collapse">
                        <div class="accordion-inner" id="resultdivs">
                             <%= ResourceLoader.GetResource(session, "search.searchfirst")%>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>
    <script type="text/javascript">
        var offset=0;
        var maxrecords=20;
        function search()
        {
         /*   
            $.each($('input:checkbox'), function(index,item){
                var itemname = item.name;
                if (item.checked)
                {
                    fqs+=itemname+",";
                }
            });
           */ 
            //var fqs = $('input:checkbox').length ? $('input:checked').val() : '';
            //alert (fqs);
            var selection = $("#searchcriteria > button.btn.active").val();
            //alert(selection);
                                    
            //var findqualifier = $("#findqualifier").val();
            //alert(findqualifier);
            var searchfor = $("#searchfor  > button.btn.active").val();
            //alert(searchfor);
            var searchcontent = $("#searchcontent").val();
            //alert(searchcontent );
            var url='ajax/search.jsp';
            
            $("#collapseTwo").collapse("show");
            $("#collapseOne").collapse("hide");
            
            var postbackdata = new Array();
           
            postbackdata.push({
                name:"selection", 
                value: selection
            });
            
            postbackdata.push({
                name:"searchcontent", 
                value: searchcontent
            });
            
            postbackdata.push({
                name:"lang", 
                value: $("#lang").val()
            });
            
            
            $.each($('input:checkbox'), function(index,item){
                var itemname = item.name;
                if (item.checked)
                {
                    postbackdata.push({
                        name:"findqualifier", 
                        value: itemname
                    });
                }
            });
            
            
            postbackdata.push({
                name:"searchfor", 
                value: searchfor
            });
            
            postbackdata.push({
                name:"nonce", 
                value: $("#nonce").val()
            });
            
            var request=   $.ajax({
                url: url,
                type:"POST",
                //  dataType: "html", 
                cache: false, 
                //  processData: false,f
                data: postbackdata
            });
                
                
            request.done(function(msg) {
                window.console && console.log('postback done '  + url);                
        
                $("#resultdivs").html(msg);
                
        
            });

            request.fail(function(jqXHR, textStatus) {
                window.console && console.log('postback failed ' + url);                                
                $("#resultdivs").html(jqXHR.responseText  + textStatus);
                //$(".alert").alert();
                
        
            });
                                    
        }
                                
    </script>
    
    <%@include file="tmodelChooser.jsp" %>
    
    <%@include file="header-bottom.jsp" %>