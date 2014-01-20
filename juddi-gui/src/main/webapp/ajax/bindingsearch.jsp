<%-- 
    Document   : tmodelsearch
    Created on : Mar 13, 2013, 8:54:47 PM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<%@page import="org.apache.juddi.webconsole.PostBackConstants"%>
<%@page import="org.apache.juddi.webconsole.hub.PagableContainer"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include  file="../csrf.jsp" %>
<!DOCTYPE html>
<% 
    UddiHub x = UddiHub.getInstance(application, request.getSession());
    int maxrecords = 50;
    int offset = 0;
    String lang = request.getParameter("lang");
    if (lang == null || lang.length() == 0) {
        lang = null;
    }
    if (lang != null && lang.equalsIgnoreCase(ResourceLoader.GetResource(session, "items.clicktoedit"))) {
        lang = null;
    }

    String keyword = request.getParameter("keyword");
    if (keyword == null || keyword.length() == 0) {
        keyword = "%";
    }
    try {
        maxrecords = Integer.parseInt(request.getParameter("maxrecords"));
    } catch (Exception ex) {
    }
    try {
        offset = Integer.parseInt(request.getParameter("offset"));
    } catch (Exception ex) {
    }
    if (offset < 0) {
        offset = 0;
    }
    if (maxrecords > 50) {
        maxrecords = 50;
    }
    boolean isChooser = false;
    try {
        isChooser = Boolean.parseBoolean(request.getParameter("chooser"));
    } catch (Exception ex) {
    }
    PagableContainer ret = (x.SearchForBinding(keyword, lang, offset, maxrecords, isChooser));

    out.write(ret.renderedHtml);
%>
<script type="text/javascript">
    totalrecordsBinding=<%=ret.totalrecords%>;
    $("#totalrecordsBinding").text(totalrecordsBinding);
    $("#offsetBinding").text('<%=offset%>');
    $("#displayrecordsBinding").text ('<%=ret.displaycount%>');
    refresh();
</script>