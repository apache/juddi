<%-- 
    Document   : getTransferToken
    Created on : Apr 27, 2013, 9:13:27 AM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<%@page import="javax.xml.bind.JAXB"%>
<%@page import="java.io.StringWriter"%> 
<%@page import="org.apache.juddi.jaxb.PrintUDDI"%>
<%@page import="org.uddi.custody_v3.TransferToken"%>
<%@page import="javax.xml.datatype.DatatypeFactory"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="javax.xml.datatype.XMLGregorianCalendar"%>
<%@page import="javax.xml.ws.Holder"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:include page="../csrf.jsp" />
<%
    if (request.getMethod().equalsIgnoreCase("POST")) {
        String keylist = request.getParameter("keylist");
        if (keylist == null) {
            response.setStatus(400);
        } else {

            UddiHub x = UddiHub.getInstance(application, session);
            Holder<byte[]> outToken = new Holder<byte[]>();
            Holder<XMLGregorianCalendar> outXcal = new Holder<XMLGregorianCalendar>();
            DatatypeFactory df = DatatypeFactory.newInstance();
            outXcal.value = df.newXMLGregorianCalendar();
            Holder<String> transferTo = new Holder<String>();
            //transferTo.value = username;
            //  List<String> keys = new ArrayList<String>();

            /*if (tmodellist != null) {
             String[] tmodelkeys = tmodellist.split(",");
             for (int i = 0; i < tmodelkeys.length; i++) {
             keys.add(tmodelkeys[i]);
             }
             }*/
            org.uddi.custody_v3.KeyBag keys = new org.uddi.custody_v3.KeyBag();

            if (keylist != null) {
                String[] keylists = keylist.split(",");

                for (int i = 0; i < keylists.length; i++) {
                    keys.getKey().add(keylists[i]);
                }
            }

            String msg = x.GetCustodyTransferToken(keys, transferTo, outXcal, outToken);
            if (msg != null) {
                out.write(msg);
                response.setStatus(400);
            } else {
                TransferToken tt = new TransferToken();
                tt.setExpirationTime(outXcal.value);
                tt.setNodeID(transferTo.value);
                tt.setOpaqueToken(outToken.value);
                try {
                    StringWriter sw = new StringWriter();
//BREAK is replaced via javascript in a popup
                    sw.write(ResourceLoader.GetResource(session, "actions.savethis") + "  BREAK  ");
                    sw.write(ResourceLoader.GetResource(session, "items.transfertoken") + ": BREAK ");
                    JAXB.marshal(tt, sw);


                    sw.write(" BREAK BREAK" + ResourceLoader.GetResource(session, "items.transferkeys") + ": BREAK ");
                    JAXB.marshal(keys, sw);
                    out.write(sw.toString());


                } catch (Exception ex) {
                    response.setStatus(406);
                    out.write(ex.getMessage());
                }


                //out.write(new String(outToken.value) + "<br>");
            }
        }
    }

%>