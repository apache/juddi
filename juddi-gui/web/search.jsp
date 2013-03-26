<%-- 
    Document   : search
    Created on : Feb 24, 2013, 9:14:01 AM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.query.util.FindQualifiers"%>
<%@page import="org.apache.juddi.webconsole.UddiHub"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="header-top.jsp" %>
<div class="container">

    <!-- Main hero unit for a primary marketing message or call to action -->
    <div class="well" >
        <h1>Search</h1>
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
                            <b>Search!</b>
                        </a>
                    </div>
                    <div id="collapseOne" class="accordion-body collapse in">
                        <div class="accordion-inner">
                            What are you looking for?<Br>
                            <div class="btn-group" id="searchfor" data-toggle="buttons-radio">
                                <button type="button" class="btn active" value="business">Business</button>
                                <!--<button type="button" class="btn " >Related Business</button>-->
                                <button type="button" class="btn " value="service">Service</button>
                                <button type="button" class="btn " value="bindingTemplate">Binding Template</button>
                                <button type="button" class="btn " value="tModel">tModel</button>
                                <% if (x.IsJuddiRegistry()) {
                                %>
                                <button type="button" class="btn " >Publisher</button>
                                <% }
                                %>
                            </div><br><Br>
                            Search Criteria<br>
                            <div class="btn-group" id="searchcriteria" data-toggle="buttons-radio">
                                <button type="button" class="btn active" active value="name">By Name</button>
                                <button type="button" class="btn " value="category">By Category</button>
                                <button type="button" class="btn " value="key">Unique Identifier</button>
                                <button type="button" class="btn " value="tmodel">tModel</button>
                            </div><br>
                            Find Qualifiers<br>

                            <div style=" float:left; padding: 2px">

                            <div ><input type="checkbox" name="<%=FindQualifiers.AND_ALL_KEYS%>" value="<%=FindQualifiers.AND_ALL_KEYS%>"> <%=FindQualifiers.AND_ALL_KEYS%></div>
                            <div ><input type="checkbox" name="<%=FindQualifiers.APPROXIMATE_MATCH%>" value="<%=FindQualifiers.APPROXIMATE_MATCH%>"> <%=FindQualifiers.APPROXIMATE_MATCH%></div>
                            <div ><input type="checkbox" name="<%=FindQualifiers.BINARY_SORT%>" value="<%=FindQualifiers.BINARY_SORT%>"> <%=FindQualifiers.BINARY_SORT%></div>
                            <div ><input type="checkbox" name="<%=FindQualifiers.BINDING_SUBSET%>" value="<%=FindQualifiers.BINDING_SUBSET%>"> <%=FindQualifiers.BINDING_SUBSET%></div>
                            <div ><input type="checkbox" name="<%=FindQualifiers.CASE_INSENSITIVE_MATCH%>" value="<%=FindQualifiers.CASE_INSENSITIVE_MATCH%>"> <%=FindQualifiers.CASE_INSENSITIVE_MATCH%></div>
                            </div>
                            <div style=" float:left; padding: 2px">
                            <div ><input type="checkbox" name="<%=FindQualifiers.CASE_INSENSITIVE_SORT%>" value="<%=FindQualifiers.CASE_INSENSITIVE_SORT%>"> <%=FindQualifiers.CASE_INSENSITIVE_SORT%></div>
                            <div ><input type="checkbox" name="<%=FindQualifiers.CASE_SENSITIVE_MATCH%>" value="<%=FindQualifiers.CASE_SENSITIVE_MATCH%>"> <%=FindQualifiers.CASE_SENSITIVE_MATCH%></div>
                            <div ><input type="checkbox" name="<%=FindQualifiers.CASE_SENSITIVE_SORT%>" value="<%=FindQualifiers.CASE_SENSITIVE_SORT%>"> <%=FindQualifiers.CASE_SENSITIVE_SORT%></div>
                            <div ><input type="checkbox" name="<%=FindQualifiers.COMBINE_CATEGORY_BAGS%>" value="<%=FindQualifiers.COMBINE_CATEGORY_BAGS%>"> <%=FindQualifiers.COMBINE_CATEGORY_BAGS%></div>
                            <div ><input type="checkbox" name="<%=FindQualifiers.DIACRITIC_INSENSITIVE_MATCH%>" value="<%=FindQualifiers.DIACRITIC_INSENSITIVE_MATCH%>"> <%=FindQualifiers.DIACRITIC_INSENSITIVE_MATCH%></div>
                            </div>
                            <div style=" float:left; padding: 2px">
                            <div ><input type="checkbox" name="<%=FindQualifiers.DIACRITIC_SENSITIVE_MATCH%>" value="<%=FindQualifiers.DIACRITIC_SENSITIVE_MATCH%>"> <%=FindQualifiers.DIACRITIC_SENSITIVE_MATCH%></div>
                            <div ><input type="checkbox" name="<%=FindQualifiers.EXACT_MATCH%>" value="<%=FindQualifiers.EXACT_MATCH%>"> <%=FindQualifiers.EXACT_MATCH%></div>
                            <div ><input type="checkbox" name="<%=FindQualifiers.OR_ALL_KEYS%>" value="<%=FindQualifiers.OR_ALL_KEYS%>"> <%=FindQualifiers.OR_ALL_KEYS%></div>
                            <div ><input type="checkbox" name="<%=FindQualifiers.OR_LIKE_KEYS%>" value="<%=FindQualifiers.OR_LIKE_KEYS%>"> <%=FindQualifiers.OR_LIKE_KEYS%></div>
                            <div ><input type="checkbox" name="<%=FindQualifiers.SERVICE_SUBSET%>" value="<%=FindQualifiers.SERVICE_SUBSET%>"> <%=FindQualifiers.SERVICE_SUBSET%></div>
                            </div>
                            <div style=" float:left; padding: 2px">
                            <div ><input type="checkbox" name="<%=FindQualifiers.SIGNATURE_PRESENT%>" value="<%=FindQualifiers.SIGNATURE_PRESENT%>"> <%=FindQualifiers.SIGNATURE_PRESENT%></div>
                            <div ><input type="checkbox" name="<%=FindQualifiers.SORT_BY_DATE_ASC%>" value="<%=FindQualifiers.SORT_BY_DATE_ASC%>"> <%=FindQualifiers.SORT_BY_DATE_ASC%></div>
                            <div ><input type="checkbox" name="<%=FindQualifiers.SORT_BY_DATE_DESC%>" value="<%=FindQualifiers.SORT_BY_DATE_DESC%>"> <%=FindQualifiers.SORT_BY_DATE_DESC%></div>
                            <div ><input type="checkbox" name="<%=FindQualifiers.SORT_BY_NAME_ASC%>" value="<%=FindQualifiers.SORT_BY_NAME_ASC%>"> <%=FindQualifiers.SORT_BY_NAME_ASC%></div>
                            <div ><input type="checkbox" name="<%=FindQualifiers.SORT_BY_NAME_DESC%>" value="<%=FindQualifiers.SORT_BY_NAME_DESC%>"> <%=FindQualifiers.SORT_BY_NAME_DESC%></div>
                            </div>
                            <div ><input type="checkbox" name="<%=FindQualifiers.SUPPRESS_PROJECTED_SERVICES%>" value="<%=FindQualifiers.SUPPRESS_PROJECTED_SERVICES%>"> <%=FindQualifiers.SUPPRESS_PROJECTED_SERVICES%></div>
                            <div ><input type="checkbox" name="<%=FindQualifiers.UTS_10%>" value="<%=FindQualifiers.UTS_10%>"> <%=FindQualifiers.UTS_10%></div>

                            <Br>
                            <br>
                            <div>
                                <br><Br>
                                <input type="text" placeholder="Type something…" id="searchcontent">
                                <input type="text" placeholder="Language" id="lang"><br>
                                *Tip: use '%' for any number of wild card characters and '_' for a single wild card character.
                                <br>
                                <a href="javascript:search();" class="btn btn-primary btn-large">Search</a>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="accordion-group">
                    <div class="accordion-heading">
                        <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapseTwo">
                            Results
                        </a>
                    </div>
                    <div id="collapseTwo" class="accordion-body collapse">
                        <div class="accordion-inner" id="resultdivs">
                            Try Searching first...
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
    <%@include file="header-bottom.jsp" %>