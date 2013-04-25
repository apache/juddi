<%-- 
    Document   : fromXML
    Created on : Mar 24, 2013, 9:31:37 AM
    Author     : Alex O'Ree
--%><%@page import="java.util.concurrent.atomic.AtomicReference"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Properties"%>
<%@page import="org.apache.juddi.v3.client.crypto.DigSigUtil"%>
<%@page import="java.io.StringReader"%>
<%@page import="javax.xml.bind.JAXB"%>
<%@page import="org.apache.juddi.webconsole.resources.ResourceLoader"%>
<%@page import="org.apache.juddi.jaxb.JAXBMarshaller"%>
<%@page import="org.uddi.api_v3.TModel"%>
<%@page import="org.uddi.api_v3.BindingTemplate"%>
<%@page import="org.uddi.api_v3.BusinessService"%>
<%@page import="org.uddi.api_v3.BusinessEntity"%>
<%@page import="org.apache.juddi.jaxb.EntityCreator"%>
<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page import="org.apache.juddi.jaxb.PrintUDDI"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%><%

    UddiHub x = UddiHub.getInstance(application, session);
    if (request.getMethod().equalsIgnoreCase("POST")) {
        String type = request.getParameter("type");
        String id = request.getParameter("id");
        if ((type != null && type.length() != 0) && (id != null && id.length() != 0)) {


            String signedxml = request.getParameter("data");
            if (signedxml == null) {
                out.write("no input");
            } else {
                signedxml = signedxml.trim();
                if (signedxml.startsWith("<?xml ")) {
                    int idx = signedxml.indexOf(">");
                    signedxml = signedxml.substring(idx + 1, signedxml.length()).trim();

                }
                signedxml = signedxml.trim();

                Object j = null;// JAXBMarshaller.unmarshallFromString(signedxml, JAXBMarshaller.PACKAGE_UDDIAPI);
                StringReader sr = new StringReader(signedxml);
                String msg = null;
                if (type.equalsIgnoreCase("business")) {
                    BusinessEntity be = (BusinessEntity) JAXB.unmarshal(sr, BusinessEntity.class);
                    msg = (x.SaveBusinessDetails(be));
                } else if (type.equalsIgnoreCase("service")) {
                    BusinessService be = (BusinessService) JAXB.unmarshal(sr, BusinessService.class);
                    msg = (x.SaveService(be));
                } else if (type.equalsIgnoreCase("bindingTemplate")) {
                    BindingTemplate be = (BindingTemplate) JAXB.unmarshal(sr, BindingTemplate.class);
                    msg = (x.SaveBindingTemplate(be));
                } else if (type.equalsIgnoreCase("tModel")) {
                    //System.out.println(signedxml);

                    TModel be = (TModel) JAXB.unmarshal(sr, TModel.class);
                    //JAXB.marshal(be, System.out);

/*
                    org.apache.juddi.v3.client.crypto.DigSigUtil dsig = new DigSigUtil();
                    UddiHub hub = UddiHub.getInstance(application, session);
                    Properties config2 = hub.GetRawConfiguration();
                    Set<Entry<Object, Object>> it = config2.entrySet();
                    Iterator it2 = it.iterator();
                    while (it2.hasNext()) {
                        Object j2 = it2.next();
                        Entry<Object, Object> item = (Entry<Object, Object>) j2;
                        dsig.put((String) item.getKey(), (String) item.getValue());
                    }
                    AtomicReference<String> msg2 = new AtomicReference<String>();
                    boolean success = dsig.verifySignedUddiEntity(be, msg2);
                    if (!success) {
                        msg = "WARNING! unable to validate signature!" + msg2.get();
                    }*/
                    msg = (x.SaveTModel(be));
                } else {
                    msg = (ResourceLoader.GetResource(session, "errors.unknownentity"));
                }

                if (!msg.equalsIgnoreCase(ResourceLoader.GetResource(session, "actions.saved"))) {
                    //response.setStatus(500);
                }
                out.write(msg);
            }
        }
    }



    //get parameter type
    //fetch from UDDI
    //convert to string and output



%>