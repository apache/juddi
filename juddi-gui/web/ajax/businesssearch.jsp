<%-- 
    Document   : businessbrowse
    Created on : Mar 12, 2013, 9:40:19 PM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<%@page import="org.apache.juddi.webconsole.PostBackConstants"%>
<%@page import="org.apache.juddi.webconsole.hub.PagableContainer"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include  file="../csrf.jsp" %>
<%
    
    UddiHub x = UddiHub.getInstance(application, request.getSession());
    int offset = 0;
    int maxrecords = 20;
    String lang = request.getParameter("lang");
    if (lang == null || lang.length() == 0) {
        lang = null;
    }
    if (lang!=null && lang.equalsIgnoreCase(ResourceLoader.GetResource(session, "items.clicktoedit"))) {
        lang = null;
    }
    String keyword = "%";
    try {
        offset = Integer.parseInt(request.getParameter("offset"));
        if (offset < 0) {
            offset = 0;
        }
    } catch (Exception ex) {
    }
    try {
        keyword =(request.getParameter("keyword"));
      if (keyword==null || keyword.length()==0)
          keyword="%";
    } catch (Exception ex) {
        keyword="%";
    }
    try {
        maxrecords = Integer.parseInt(request.getParameter("maxrecords"));
        if (maxrecords <= 0 || maxrecords > 50) {
            maxrecords = 50;
        }
    } catch (Exception ex) {
    }
     boolean isChooser = false;
    try {
        isChooser = Boolean.parseBoolean(request.getParameter("chooser"));
    } catch (Exception ex) {
    }
     

    PagableContainer ret= (x.GetBusinessListAsHtml(offset, maxrecords, keyword, lang, isChooser));
    out.write(ret.renderedHtml);
%>
<script type="text/javascript">
    totalrecords=<%=ret.totalrecords%>;
    $("#totalrecords").text(totalrecords);
    $("#offset").text(<%=offset%>);
    $("#displayreco rds").text (<%=ret.displaycount%>);
    refresh();
</script>