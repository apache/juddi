<%-- 
    Document   : search
    Created on : Mar 19, 2013, 2:01:55 PM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub.FindType"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub.CriteriaType"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@include file="../csrf.jsp" %>
<%
    if (request.getMethod().equalsIgnoreCase("post")) {
        String lang = request.getParameter("lang"); 
        //searchcriteria name category key tmodel
        String selection = request.getParameter("selection");
        //searchcontent the thing we're looking for
        String searchcontent = request.getParameter("searchcontent");
        //findqualifier - use value as is
        String[] findqualifier = request.getParameterValues("findqualifier");
        //type of thing we need business service bindingTemplate tModel
        String searchfor = request.getParameter("searchfor");
        boolean ok = true;
        if (selection == null) {
            ok = false;
        }

        if (!ok) {
            response.sendRedirect("../index.jsp");
        }
        CriteriaType criteria = null;
        if (selection.equalsIgnoreCase("name")) {
            criteria = CriteriaType.Name;
        }
        if (selection.equalsIgnoreCase("category")) {
            criteria = CriteriaType.Category;
        }
        if (selection.equalsIgnoreCase("key")) {
            criteria = CriteriaType.uid;
        }
        if (selection.equalsIgnoreCase("tmodel")) {
            criteria = CriteriaType.tmodel;
        }

        if (criteria == null) {
            ok = false;
        }
        if (!ok) {
            response.sendRedirect("../index.jsp");
        }
        FindType type = null;
        if (searchfor.equalsIgnoreCase("business")) {
            type = FindType.Business;
        }
        if (searchfor.equalsIgnoreCase("service")) {
            type = FindType.Service;
        }
        if (searchfor.equalsIgnoreCase("bindingTemplate")) {
            type = FindType.BindingTemplate;
        }
        if (searchfor.equalsIgnoreCase("tModel")) {
            type = FindType.tModel;
        }
       
        if (searchfor.equalsIgnoreCase("RelatedBusiness")) {
            type = FindType.RelatedBusiness;
        }
        if (type == null) {
            ok = false;
        }
        if (!ok) {
            response.sendRedirect("../index.jsp");
        }

        UddiHub x = UddiHub.getInstance(application, session);
        String msg=(x.Search(type, criteria, searchcontent, lang, findqualifier));
        if (msg.contains(ResourceLoader.GetResource(session, "errors.generic")))
                response.setStatus(406);
        out.write(msg);
    } else {
        response.sendRedirect("../index.jsp");
    }

%>