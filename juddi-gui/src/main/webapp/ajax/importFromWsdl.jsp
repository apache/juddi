<%-- 
    Document   : importFromWsdl
    Created on : May 11, 2013, 3:26:42 PM
    Author     : Alex O'Ree
--%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="org.apache.juddi.webconsole.hub.builders.Printers"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.util.Set"%>
<%@page import="javax.xml.namespace.QName"%>
<%@page import="javax.wsdl.PortType"%>
<%@page import="java.util.Map"%>
<%@page import="org.uddi.api_v3.BusinessServices"%>
<%@page import="org.apache.juddi.v3.client.mapping.WSDL2UDDI"%>
<%@page import="org.apache.juddi.v3.client.mapping.URLLocalizerDefaultImpl"%>
<%@page import="java.util.Properties"%>
<%@page import="javax.wsdl.Definition"%>
<%@page import="org.apache.juddi.v3.client.mapping.ReadWSDL"%>
<%@page import="org.apache.juddi.v3.client.config.UDDIClerk"%>
<%@page import="org.uddi.api_v3.TModel"%>
<%@page import="java.net.URL"%>
<%@include  file="../csrf.jsp" %>
<%
    if (request.getMethod().equalsIgnoreCase("POST")) {
        String method = request.getParameter("formaction");
        if (method != null && method.length() > 0) {
            if (method.equalsIgnoreCase("preview")) {
                //Fetch the WSDL w/wo credentials
                String uri = request.getParameter("wsdlurl");
                String username = request.getParameter("wsdlusername");
                String password = request.getParameter("wsdlpassword");
                String keydomain = request.getParameter("keydomain");
                String businessname = request.getParameter("businessname");
                boolean ignoreSSL = false;
                try{
                    ignoreSSL = Boolean.parseBoolean(request.getParameter("ignoressl"));
                }catch (Exception ex){}
                try {
                    URL url = new URL(uri);
                    //"http://graphical.weather.gov/xml/SOAP_server/ndfdXMLserver.php?wsdl");
                    String domain = url.getHost();
                    //TModel keygen = UDDIClerk.createKeyGenator("uddi:" + domain + ":keygenerator", domain, "en");

                    ReadWSDL rw = new ReadWSDL();
                    rw.setIgnoreSSLErrors(ignoreSSL);
                    Definition wsdlDefinition = rw.readWSDL(url, username, password);
                    Properties properties = new Properties();
                    properties.put("keyDomain", keydomain);
                    properties.put("businessName", businessname);
                    properties.put("serverName", url.getHost());
                    properties.put("serverPort", url.getPort());
                    String wsdlURL = wsdlDefinition.getDocumentBaseURI();
                    WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(null, new URLLocalizerDefaultImpl(), properties);
                    BusinessServices businessServices = wsdl2UDDI.createBusinessServices(wsdlDefinition);
                    @SuppressWarnings("unchecked")
                    Map<QName, PortType> portTypes = (Map<QName, PortType>) wsdlDefinition.getAllPortTypes();
                    Set<TModel> portTypeTModels = wsdl2UDDI.createWSDLPortTypeTModels(wsdlURL, portTypes);
                    Map allBindings = wsdlDefinition.getAllBindings();
                    Set<TModel> bindingTmodels = wsdl2UDDI.createWSDLBindingTModels(wsdlURL, allBindings);
                    List<TModel> tmodels = new ArrayList<TModel>();
                    tmodels.addAll(bindingTmodels);
                    tmodels.addAll(portTypeTModels);

                    out.write("<i class=\"icon-thumbs-up icon-large\"></i> WSDL successfully parsed! This will create " + portTypeTModels.size()
                            + " portType tmodel(s), " + bindingTmodels.size()
                            + " binding tModel(s), " + allBindings.size()
                            + " binding(s), and " + businessServices.getBusinessService().size() + " service(s).<br>");
                    out.write("Services:<br><ul>");
                    for (int i = 0; i < businessServices.getBusinessService().size(); i++) {
                        out.write("<li>Key:"
                                + StringEscapeUtils.escapeHtml(businessServices.getBusinessService().get(i).getServiceKey())
                                + " <br>Name: "
                                + StringEscapeUtils.escapeHtml(Printers.ListNamesToString(businessServices.getBusinessService().get(i).getName())));
                        if (businessServices.getBusinessService().get(i).getBindingTemplates() != null) {
                            out.write("<br>Binding Templates:<ul>");
                            for (int k = 0; k < businessServices.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().size(); k++) {
                                out.write("<li>Key: " + StringEscapeUtils.escapeHtml(businessServices.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getBindingKey())
                                        + "<br>Access Point: ");
                                if (businessServices.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getAccessPoint() != null) {
                                    out.write(StringEscapeUtils.escapeHtml(
                                            businessServices.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getAccessPoint().getValue()));
                                }
                                out.write("</li>");
                            }
                            out.write("</ul>");
                        }
                        out.write("</li>");
                    }
                    out.write("</ul>");

                    out.write("tModels<br><ul>");
                    for (int i = 0; i < tmodels.size(); i++) {
                        out.write("<li>Key:"
                                + StringEscapeUtils.escapeHtml(tmodels.get(i).getTModelKey())
                                + " Name: "
                                + StringEscapeUtils.escapeHtml(tmodels.get(i).getName().getValue())
                                + "</li>");
                    }
                    out.write("</ul>");


%>


<%
                } catch (Exception ex) {
                    out.write("<i class=\"icon-thumbs-down icon-large\"></i> Error! " +ex.getClass().getCanonicalName() + " " + ex.getMessage());
                }
            }
        }
    }



%>