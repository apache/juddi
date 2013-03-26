/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.juddi.webconsole;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.rmi.RemoteException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBElement;
import javax.xml.ws.BindingProvider;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.juddi.ClassUtil;
import org.apache.juddi.api_v3.GetAllPublisherDetail;
import org.apache.juddi.api_v3.Publisher;
import org.apache.juddi.api_v3.PublisherDetail;
import org.apache.juddi.api_v3.SavePublisher;
import org.apache.juddi.query.util.FindQualifiers;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.apache.log4j.Level;
import org.uddi.api_v3.Address;
import org.uddi.api_v3.AddressLine;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessInfo;
import org.uddi.api_v3.BusinessInfos;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.Contact;
import org.uddi.api_v3.Contacts;
import org.uddi.api_v3.DeleteBusiness;
import org.uddi.api_v3.DeleteTModel;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.DiscardAuthToken;
import org.uddi.api_v3.DiscoveryURL;
import org.uddi.api_v3.DiscoveryURLs;
import org.uddi.api_v3.Email;
import org.uddi.api_v3.FindBinding;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.api_v3.GetBindingDetail;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.GetServiceDetail;
import org.uddi.api_v3.GetTModelDetail;
import org.uddi.api_v3.IdentifierBag;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.KeyedReferenceGroup;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.OverviewDoc;
import org.uddi.api_v3.OverviewURL;
import org.uddi.api_v3.PersonName;
import org.uddi.api_v3.Phone;
import org.uddi.api_v3.SaveBinding;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.SaveService;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.api_v3.ServiceInfo;
import org.uddi.api_v3.ServiceInfos;
import org.uddi.api_v3.ServiceList;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelBag;
import org.uddi.api_v3.TModelDetail;
import org.uddi.api_v3.TModelInfo;
import org.uddi.api_v3.TModelInstanceDetails;
import org.uddi.api_v3.TModelList;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDICustodyTransferPortType;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.w3._2000._09.xmldsig_.SignatureType;
import org.w3._2000._09.xmldsig_.X509DataType;

/**
 * UddiHub - The hub acts as a single point for managing browser to uddi
 * services. At most 1 instance is allowed per http session
 *
 * @author Alex O'Ree
 */
public class UddiHub {

    public static final String JAVAXNETSSLKEY_STORE = "javax.net.ssl.keyStore";
    public static final String JAVAXNETSSLKEY_STORE_PASSWORD = "javax.net.ssl.keyStorePassword";
    public static final String JAVAXNETSSLTRUST_STORE = "javax.net.ssl.trustStore";
    public static final String JAVAXNETSSLTRUST_STORE_PASSWORD = "javax.net.ssl.trustStorePassword";
    public static final String LOGGER_NAME = "org.apache.juddi";
    URL propertiesurl = null;
    Properties properties = null;
    AuthStyle style = null;
    public static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger("org.apache.juddi");

    private UddiHub() {
    }

    /**
     * removes the Hub from the current http session and clears any tokens
     *
     * @param _session
     */
    public static void reset(HttpSession _session) {
        _session.removeAttribute("hub");
        // token = null;
    }

    public void die() {
        DiscardAuthToken da = new DiscardAuthToken();
        da.setAuthInfo(token);
        try {
            security.discardAuthToken(da);
        } catch (DispositionReportFaultMessage ex) {
            log.log(Level.ERROR, null, ex);
        } catch (RemoteException ex) {
            log.log(Level.ERROR, null, ex);
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        }

        token = null;
        inquiry = null;
        publish = null;
        custody = null;
        security = null;
        juddi = null;
    }

    public static UddiHub getInstance(ServletContext application, HttpSession _session) throws Exception {
        Object j = _session.getAttribute("hub");
        if (j == null) {
            UddiHub hub = new UddiHub(application, _session);
            _session.setAttribute("hub", hub);
            return hub;
        }
        return (UddiHub) j;
    }

    private UddiHub(ServletContext application, HttpSession _session) throws Exception {
        URL prop = application.getResource("/META-INF/config.properties");
        if (prop == null) {
            throw new Exception("Cannot locate the configuration file.");
        }
        session = _session;
        propertiesurl = prop;
        InputStream in = prop.openStream();
        Properties p = new Properties();
        p.load(in);
        in.close();
        properties = p;
        style = (AuthStyle) AuthStyle.valueOf((String) p.get("authtype"));
        try {

            String clazz = UDDIClientContainer.getUDDIClerkManager(null).
                    getClientConfig().getUDDINode("default").getProxyTransport();
            Class<?> transportClass = ClassUtil.forName(clazz, Transport.class);
            if (transportClass != null) {
                Transport transport = (Transport) transportClass.
                        getConstructor(String.class).newInstance("default");

                security = transport.getUDDISecurityService();
                inquiry = transport.getUDDIInquiryService();
                publish = transport.getUDDIPublishService();
                custody = transport.getUDDICustodyTransferService();
                juddi = transport.getJUDDIApiService();

                BindingProvider bp = null;
                Map<String, Object> context = null;
                bp = (BindingProvider) inquiry;
                context = bp.getRequestContext();
                context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, properties.getProperty("inquiryurl"));
                bp = (BindingProvider) publish;
                context = bp.getRequestContext();
                context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, properties.getProperty("publishurl"));
                bp = (BindingProvider) custody;
                context = bp.getRequestContext();
                context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, properties.getProperty("custodyurl"));

                bp = (BindingProvider) juddi;
                context = bp.getRequestContext();
                context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, properties.getProperty("juddipapi"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private HttpSession session;

    public boolean IsJuddiRegistry() {
        String type = properties.getProperty("registryType");
        if (type == null) {
            return false;
        }
        if (type.equalsIgnoreCase("juddi")) {
            return true;
        }
        return false;
    }

    private String GetToken() {
        if (style != AuthStyle.UDDI_AUTH) {
            BindingProvider bp = null;
            Map<String, Object> context = null;
            bp = (BindingProvider) inquiry;
            context = bp.getRequestContext();
            context.put(BindingProvider.USERNAME_PROPERTY, session.getAttribute("username"));
            context.put(BindingProvider.USERNAME_PROPERTY, session.getAttribute(AES.Decrypt("password", (String) properties.get("key"))));

            bp = (BindingProvider) publish;
            context = bp.getRequestContext();
            context.put(BindingProvider.USERNAME_PROPERTY, session.getAttribute("username"));
            context.put(BindingProvider.USERNAME_PROPERTY, session.getAttribute(AES.Decrypt("password", (String) properties.get("key"))));

            bp = (BindingProvider) custody;
            context = bp.getRequestContext();
            context.put(BindingProvider.USERNAME_PROPERTY, session.getAttribute("username"));
            context.put(BindingProvider.USERNAME_PROPERTY, session.getAttribute(AES.Decrypt("password", (String) properties.get("key"))));

            bp = (BindingProvider) juddi;
            context = bp.getRequestContext();
            context.put(BindingProvider.USERNAME_PROPERTY, session.getAttribute("username"));
            context.put(BindingProvider.USERNAME_PROPERTY, session.getAttribute(AES.Decrypt("password", (String) properties.get("key"))));
            return null;
        } else {
            if (token != null) {
                return token;
            }
            GetAuthToken req = new GetAuthToken();
            if (session.getAttribute("username") != null
                    && session.getAttribute("password") != null) {
                req.setUserID((String) session.getAttribute("username"));
                req.setCred(AES.Decrypt((String) session.getAttribute("password"), (String) properties.get("key")));
                try {
                    AuthToken authToken = security.getAuthToken(req);
                    token = authToken.getAuthInfo();
                } catch (DispositionReportFaultMessage ex) {
                    log.log(Level.ERROR, null, ex);
                } catch (RemoteException ex) {
                    log.log(Level.ERROR, null, ex);
                } catch (Exception ex) {
                    log.log(Level.ERROR, null, ex);
                }
            }
        }
        return token;
    }

    public boolean getUddiIsAuthenticated() {
        return (token != null && !token.isEmpty());
    }
    private UDDISecurityPortType security = null;
    private UDDIInquiryPortType inquiry = null;
    private UDDIPublicationPortType publish = null;
    private UDDICustodyTransferPortType custody = null;
    private JUDDIApiPortType juddi = null;
    private String token = null;

    public PagableContainer GetBusinessListAsHtml(int offset, int maxrecords, String keyword, String lang) {
        PagableContainer ret = new PagableContainer();
        ret.offset = offset;
        ret.displaycount = 0;
        ret.totalrecords = 0;
        StringBuilder sb = new StringBuilder();

        try {
            FindBusiness fb = new FindBusiness();
            fb.setMaxRows(maxrecords);
            fb.setListHead(offset);
            fb.setAuthInfo(GetToken());
            org.uddi.api_v3.FindQualifiers fq = new org.uddi.api_v3.FindQualifiers();
            fq.getFindQualifier().add(FindQualifiers.APPROXIMATE_MATCH);

            fb.setFindQualifiers(fq);
            Name searchname = new Name();
            searchname.setLang(lang);
            searchname.setValue(keyword);
            fb.getName().add(searchname);
            BusinessList findBusiness = inquiry.findBusiness(fb);
            if (findBusiness == null || findBusiness.getBusinessInfos() == null) {
                sb.append("No data returned");
            } else {
                ret.displaycount = findBusiness.getListDescription().getIncludeCount();
                ret.offset = findBusiness.getListDescription().getListHead();
                ret.totalrecords = findBusiness.getListDescription().getActualCount();
                sb.append("<table class=\"table\"<tr><th>Name</th><th>Details</th><th>Services</th></tr>");
                for (int i = 0; i < findBusiness.getBusinessInfos().getBusinessInfo().size(); i++) {
                    sb.append("<tr><td><span title=\"").append(StringEscapeUtils.escapeHtml(findBusiness.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey())).
                            append("\">").append(StringEscapeUtils.escapeHtml(ListToString(findBusiness.getBusinessInfos().getBusinessInfo().get(i).getName()))).
                            append("</span></td><td><a class=\"btn btn-primary\" href=\"businessEditor2.jsp?id=").
                            append(StringEscapeUtils.escapeHtml(findBusiness.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey())).append("\">Details</a>").
                            //  sb.append(StringEscapeUtils.escapeHtml(ListToDescString(findBusiness.getBusinessInfos().getBusinessInfo().get(i).getDescription()))).
                            append("</td><td>").
                            append("<a class=\"btn btn-primary\" href=\"javascript:ShowServicesByBusinessKey('").append(StringEscapeUtils.escapeJavaScript(findBusiness.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey())).append("');\">");

                    if (findBusiness.getBusinessInfos().getBusinessInfo().get(i).getServiceInfos() == null) {
                        sb.append("0");
                    } else {
                        sb.append(findBusiness.getBusinessInfos().getBusinessInfo().get(i).getServiceInfos().getServiceInfo().size());
                    }
                    sb.append("</a><a class=\"btn btn-primary\" href=\"serviceEditor.jsp?bizid=").append(StringEscapeUtils.escapeHtml(findBusiness.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey())).
                            append("\">+</a></td></tr>");

                    sb.append("<tr><td colspan=3><div id=\"").
                            append(StringEscapeUtils.escapeHtml(findBusiness.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey())).
                            append("\"></div></td></tr>");
                }
                sb.append("</table>");
            }

        } catch (Exception ex) {
            sb.append("error caught! : ").append(ex.getMessage());
        }
        ret.renderedHtml = sb.toString();
        return ret;
    }

    public String GetServiceDetailAsHtml(String serviceid) {
        if (serviceid == null || serviceid.length() == 0) {
            return "No business id specified";
        }
        StringBuilder sb = new StringBuilder();
        try {
            GetServiceDetail gbd = new GetServiceDetail();
            gbd.setAuthInfo(GetToken());
            gbd.getServiceKey().add(serviceid);
            ServiceDetail get = inquiry.getServiceDetail(gbd);
            for (int i = 0; i < get.getBusinessService().size(); i++) {
                session.setAttribute(get.getBusinessService().get(i).getServiceKey(), get.getBusinessService().get(i));
                sb.append("<b>Name:</b><div class=\"editable\" id=\"ServiceName\">").append(StringEscapeUtils.escapeHtml(ListToString(get.getBusinessService().get(i).getName()))).append("</div><Br>");
                sb.append("<b>Desc:</b><div class=\"editable\" id=\"ServiceDescription\">").append(StringEscapeUtils.escapeHtml((ListToDescString(get.getBusinessService().get(i).getDescription())))).append("</div><Br>");
                sb.append("<b>Key:</b><div class=\"editable\" id=\"ServiceKey\">").append(StringEscapeUtils.escapeHtml((get.getBusinessService().get(i).getServiceKey()))).append("</div><Br>");
                sb.append("<b>Category Bag:</b> ").append(CatBagToString(get.getBusinessService().get(i).getCategoryBag())).append("<Br>");
                if (!get.getBusinessService().get(i).getSignature().isEmpty()) {
                    sb.append("Item is digitally signed").append("<Br>");
                } else {
                    sb.append("Item is not digitally signed").append("<Br>");
                }

                sb.append(PrintBindingTemplates(get.getBusinessService().get(i).getBindingTemplates())).append("<Br>");
            }
        } catch (Exception ex) {
            sb.append("error ").append(StringEscapeUtils.escapeHtml((ex.getMessage())));
        }
        return sb.toString();
    }

    public BusinessService GetServiceDetail(String serviceid) {
        if (serviceid == null || serviceid.length() == 0) {
            return null;
        }

        try {
            GetServiceDetail gbd = new GetServiceDetail();
            gbd.setAuthInfo(GetToken());
            gbd.getServiceKey().add(serviceid);
            ServiceDetail get = inquiry.getServiceDetail(gbd);
            if (get == null || get.getBusinessService().isEmpty()) {
                return null;
            }
            return get.getBusinessService().get(0);

        } catch (Exception ex) {
            log.log(Level.WARN, null, ex);
        }
        return null;
    }

    /**
     * Returns a new map, filtering the original map by key string starts with
     *
     * @param map
     * @param pattern
     * @return
     */
    private Map MapFilter(Map map, String pattern) {
        Map ret = new HashMap();
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (key.startsWith(pattern)) {
                ret.put(key, map.get(key));
            }
        }
        return ret;
    }

    public String SaveService(BusinessService be) {
        try {
            SaveService sb = new SaveService();
            sb.setAuthInfo(GetToken());
            sb.getBusinessService().add(be);
            publish.saveService(sb);
            return "Saved!";
        } catch (DispositionReportFaultMessage ex) {
            log.log(Level.ERROR, null, ex);
            return "UDDI Disposition Fault Error caught! " + ex.getMessage() + " " + ex.detail.getMessage();
        } catch (RemoteException ex) {
            log.log(Level.ERROR, null, ex);
            return "Remote Exception Error caught! " + ex.getMessage() + " " + ex.detail.getMessage();
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
            return "Unexpected Error caught! " + ex.getMessage();
        }
    }

    public String SaveBindingTemplate(BindingTemplate be) {
        try {
            SaveBinding sb = new SaveBinding();
            sb.setAuthInfo(GetToken());
            sb.getBindingTemplate().add(be);
            publish.saveBinding(sb);
            return "Saved!";
        } catch (DispositionReportFaultMessage ex) {
            log.log(Level.ERROR, null, ex);
            return "UDDI Disposition Fault Error caught! " + ex.getMessage() + " " + ex.detail.getMessage();
        } catch (RemoteException ex) {
            log.log(Level.ERROR, null, ex);
            return "Remote Exception Error caught! " + ex.getMessage() + " " + ex.detail.getMessage();
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
            return "Unexpected Error caught! " + ex.getMessage();
        }
    }

    private void SaveServiceDetails(HttpServletRequest request) {

        String servicekey = (String) request.getParameter("ServiceKey");

        BusinessService svc = new BusinessService();
        //TODO

        //   svc.set
        //      svc.getDescription().clear();
        Description d = new Description();
        d.setValue(request.getParameter("ServiceDescription"));
        //svc.getDescription().add();

//        publish.saveBusiness(sb);
    }

    public String SaveBusinessDetails(BusinessEntity be) {
        try {
            SaveBusiness sb = new SaveBusiness();
            sb.setAuthInfo(GetToken());
            sb.getBusinessEntity().add(be);
            BusinessDetail saveBusiness = publish.saveBusiness(sb);
            return "Saved!";
        } catch (DispositionReportFaultMessage ex) {
            log.log(Level.ERROR, null, ex);
            return "UDDI Disposition Fault Error caught! " + ex.getMessage() + " " + ex.detail.getMessage();
        } catch (RemoteException ex) {
            log.log(Level.ERROR, null, ex);
            return "Remote Exception Error caught! " + ex.getMessage() + " " + ex.detail.getMessage();
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
            return "Unexpected Error caught! " + ex.getMessage();
        }
    }

    /**
     * Save Business
     *
     * This method saves a business to a UDDI registry, preserving the service
     * listing The request is build from the HTTP post back parameters. A human
     * readable response message is returned
     *
     * @param request
     * @return
     */
    public String SaveBusinessDetails(HttpServletRequest request) {

        BusinessEntity GetBusinessDetails = GetBusinessDetails(request.getParameter(PostBackConstants.BUSINESSKEY).trim());

        BusinessEntity be = new BusinessEntity();
        be.setBusinessKey(request.getParameter(PostBackConstants.BUSINESSKEY).trim());
        if (be.getBusinessKey().equalsIgnoreCase(PostBackConstants.CLICK_TO_EDIT)) {
            be.setBusinessKey(null);
        }
        be.getName().addAll(BuildNames(MapFilter(request.getParameterMap(), PostBackConstants.NAME), PostBackConstants.NAME));
        if (GetBusinessDetails == null) //this is a new business
        {
        } else {
            be.setBusinessServices(GetBusinessDetails.getBusinessServices());
        }
        //TODO signature
        be.setContacts(BuildContacts(request.getParameterMap()));

        be.getDescription().addAll(BuildDescription(MapFilter(request.getParameterMap(), PostBackConstants.DESCRIPTION), PostBackConstants.DESCRIPTION));
        be.setDiscoveryURLs(BuildDisco(MapFilter(request.getParameterMap(), PostBackConstants.DISCOVERYURL), PostBackConstants.DISCOVERYURL));
        CategoryBag cb = new CategoryBag();
        cb.getKeyedReference().addAll(BuildKeyedReference(MapFilter(request.getParameterMap(), PostBackConstants.CATBAG_KEY_REF), PostBackConstants.CATBAG_KEY_REF));
        cb.getKeyedReferenceGroup().addAll(BuildKeyedReferenceGroup(MapFilter(request.getParameterMap(), PostBackConstants.CATBAG_KEY_REF_GRP), PostBackConstants.CATBAG_KEY_REF_GRP));

        if (!cb.getKeyedReference().isEmpty() || !cb.getKeyedReferenceGroup().isEmpty()) {
            be.setCategoryBag(cb);
        }
        be.setIdentifierBag(BuildIdentBag(MapFilter(request.getParameterMap(), PostBackConstants.IDENT_KEY_REF), PostBackConstants.IDENT_KEY_REF));
        return SaveBusinessDetails(be);
    }

    /**
     * returns an html formatted list of services for a specific business used
     * on browse.jsp
     *
     * @param bizid
     * @return retu
     */
    public String GetServiceList(String bizid) {
        if (bizid == null || bizid.isEmpty()) {
            return "No business id specified";
        }
        StringBuilder sb = new StringBuilder();
        try {
            GetBusinessDetail gbd = new GetBusinessDetail();
            gbd.setAuthInfo(GetToken());

            gbd.getBusinessKey().add(bizid);

            BusinessDetail businessDetail = inquiry.getBusinessDetail(gbd);
            for (int i = 0; i < businessDetail.getBusinessEntity().size(); i++) {
                if (businessDetail.getBusinessEntity().get(i).getBusinessServices() == null) {
                    sb.append("No services are defined.");
                } else {

                    for (int k = 0; k < businessDetail.getBusinessEntity().get(i).getBusinessServices().getBusinessService().size(); k++) {
                        sb.append("<div><a href=\"serviceEditor.jsp?id=")
                                .append(StringEscapeUtils.escapeHtml(businessDetail.getBusinessEntity().get(i).getBusinessServices().getBusinessService().get(k).getServiceKey()))
                                .append("\">")
                                .append(StringEscapeUtils.escapeHtml(ListToString(businessDetail.getBusinessEntity().get(i).getBusinessServices().getBusinessService().get(k).getName())))
                                .append("</a></div>");
                    }

                }
            }
        } catch (Exception ex) {
            sb.append("error ").append(ex.getMessage());
        }
        return sb.toString();
    }

    /**
     * This function is useful for translating UDDI's somewhat complex data
     * format to something that is more useful.
     *
     * @param bindingTemplates
     */
    private String PrintBindingTemplates(BindingTemplates bindingTemplates) {
        if (bindingTemplates == null) {
            return "No binding templates";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bindingTemplates.getBindingTemplate().size(); i++) {
            sb.append("Binding Key: ").append(bindingTemplates.getBindingTemplate().get(i).getBindingKey()).
                    append("<Br>");
            sb.append("Description: ").append(ListToDescString(bindingTemplates.getBindingTemplate().get(i).getDescription())).
                    append("<Br>");
            sb.append("CatBag: ").append(CatBagToString(bindingTemplates.getBindingTemplate().get(i).getCategoryBag())).
                    append("<Br>");
            sb.append("tModels: ").append(TModelInfoToString(bindingTemplates.getBindingTemplate().get(i).getTModelInstanceDetails())).append("<Br>");

            //TODO The UDDI spec is kind of strange at this point.
            //An access point could be a URL, a reference to another UDDI binding key, a hosting redirector (which is 
            //esscentially a pointer to another UDDI registry) or a WSDL Deployment
            //From an end client's perspective, all you really want is the endpoint.

            //So if you have a wsdlDeployment useType, fetch the wsdl and parse for the invocation URL
            //If its hosting director, you'll have to fetch that data from uddi recursively until the leaf node is found
            //Consult the UDDI specification for more information

            if (bindingTemplates.getBindingTemplate().get(i).getAccessPoint() != null) {
                sb.append("Access Point: ").append(bindingTemplates.getBindingTemplate().get(i).getAccessPoint().getValue())
                        .append(" type ").append(bindingTemplates.getBindingTemplate().get(i).getAccessPoint().getUseType())
                        .append("<Br>");
            }
            if (bindingTemplates.getBindingTemplate().get(i).getHostingRedirector() != null) {
                sb.append("Hosting Director: ").append(bindingTemplates.getBindingTemplate().get(i).getHostingRedirector().getBindingKey()).append("<br>");

            }
        }
        return sb.toString();
    }

    public String TModelInfoToString(TModelInstanceDetails info) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < info.getTModelInstanceInfo().size(); i++) {
            sb.append(info.getTModelInstanceInfo().get(i).getTModelKey());

        }
        return sb.toString();
    }

    public String GetBusinessDetailsAsHtml(String bizid) throws Exception {
        if (bizid == null || bizid.isEmpty()) {
            return "No business id specified";
        }
        StringBuilder sb = new StringBuilder();
        try {
            GetBusinessDetail gbd = new GetBusinessDetail();
            gbd.setAuthInfo(GetToken());

            gbd.getBusinessKey().add(bizid);

            BusinessDetail businessDetail = inquiry.getBusinessDetail(gbd);
            for (int i = 0; i < businessDetail.getBusinessEntity().size(); i++) {
                sb.append("Business Detail - key: ").append(businessDetail.getBusinessEntity().get(i).getBusinessKey()).append("<br>");
                sb.append("Name: ").append(ListToString(businessDetail.getBusinessEntity().get(i).getName())).append("<br>");
                sb.append("Description: ").append(ListToDescString(businessDetail.getBusinessEntity().get(i).getDescription())).append("<br>");
                sb.append("Discovery URLs: ").append(ListDiscoToString(businessDetail.getBusinessEntity().get(i).getDiscoveryURLs())).append("<br>");
                sb.append("Identifiers: ").append(ListIdentBagToString(businessDetail.getBusinessEntity().get(i).getIdentifierBag())).append("<br>");
                sb.append("CategoryBag: ").append(CatBagToString(businessDetail.getBusinessEntity().get(i).getCategoryBag())).append("<br>");
                PrintContacts(businessDetail.getBusinessEntity().get(i).getContacts());
            }
        } catch (Exception ex) {
            sb.append("error ").append(ex.getMessage());
        }
        return sb.toString();
    }

    /**
     * Gets a business's details used for the businessEditor
     *
     * @param bizid
     * @return null if no id is provided
     * @throws Exception if there's an error
     */
    public BusinessEntity GetBusinessDetails(String bizid) {
        if (bizid == null || bizid.isEmpty()) {
            return null;
        }

        try {
            GetBusinessDetail gbd = new GetBusinessDetail();
            gbd.setAuthInfo(GetToken());

            gbd.getBusinessKey().add(bizid);

            BusinessDetail businessDetail = inquiry.getBusinessDetail(gbd);
            if (businessDetail.getBusinessEntity().size() == 1) {
                return businessDetail.getBusinessEntity().get(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;

    }

    String ListIdentBagToString(IdentifierBag info) {
        StringBuilder sb = new StringBuilder();
        if (info == null) {
            return "";
        }

        for (int i = 0; i < info.getKeyedReference().size(); i++) {

            sb.append(KeyedReferenceToString(info.getKeyedReference().get(i)));

        }
        return sb.toString();
    }

    String ListDiscoToString(DiscoveryURLs info) {
        StringBuilder sb = new StringBuilder();
        if (info == null) {
            return "";
        }
        for (int i = 0; i < info.getDiscoveryURL().size(); i++) {
            sb.append("Type: ").append(StringEscapeUtils.escapeHtml(info.getDiscoveryURL().get(i).getValue()))
                    .append(" ")
                    .append(StringEscapeUtils.escapeHtml(info.getDiscoveryURL().get(i).getValue()));
        }
        return sb.toString();
    }

    /**
     * Converts category bags of tmodels to a readable string
     *
     * @param categoryBag
     * @return
     */
    private String CatBagToString(CategoryBag categoryBag) {
        StringBuilder sb = new StringBuilder();
        if (categoryBag == null) {
            return "no data";
        }
        for (int i = 0; i < categoryBag.getKeyedReference().size(); i++) {
            sb.append(KeyedReferenceToString(categoryBag.getKeyedReference().get(i)));
        }
        for (int i = 0; i < categoryBag.getKeyedReferenceGroup().size(); i++) {
            sb.append("Key Ref Grp: TModelKey=");
            for (int k = 0; k < categoryBag.getKeyedReferenceGroup().get(i).getKeyedReference().size(); k++) {
                sb.append(KeyedReferenceToString(categoryBag.getKeyedReferenceGroup().get(i).getKeyedReference().get(k)));
            }
        }
        return sb.toString();
    }

    private String KeyedReferenceToString(KeyedReference item) {
        StringBuilder sb = new StringBuilder();
        sb.append("Key Ref: Name=").
                append(item.getKeyName()).
                append(" Value=").
                append(item.getKeyValue()).
                append(" tModel=").
                append(item.getTModelKey()).
                append(System.getProperty("line.separator"));
        return sb.toString();
    }

    /**
     * converts contacts to a simple string output
     */
    private String PrintContacts(Contacts contacts) {
        if (contacts == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < contacts.getContact().size(); i++) {
            sb.append("Contact ").append(i).append(" type:").append(contacts.getContact().get(i).getUseType()).append("<br>");
            for (int k = 0; k < contacts.getContact().get(i).getPersonName().size(); k++) {
                sb.append("Name: ").append(contacts.getContact().get(i).getPersonName().get(k).getValue()).append("<br>");
            }
            for (int k = 0; k < contacts.getContact().get(i).getEmail().size(); k++) {
                sb.append("Email: ").append(contacts.getContact().get(i).getEmail().get(k).getValue()).append("<br>");
            }
            for (int k = 0; k < contacts.getContact().get(i).getAddress().size(); k++) {
                sb.append("Address sort code ").append(contacts.getContact().get(i).getAddress().get(k).getSortCode()).append("<br>");
                sb.append("Address use type ").append(contacts.getContact().get(i).getAddress().get(k).getUseType()).append("<br>");
                sb.append("Address tmodel key ").append(contacts.getContact().get(i).getAddress().get(k).getTModelKey()).append("<br>");
                for (int x = 0; x < contacts.getContact().get(i).getAddress().get(k).getAddressLine().size(); x++) {
                    sb.append("Address line value ").append(contacts.getContact().get(i).getAddress().get(k).getAddressLine().get(x).getValue()).append("<br>");
                    sb.append("Address line key name ").append(contacts.getContact().get(i).getAddress().get(k).getAddressLine().get(x).getKeyName()).append("<br>");
                    sb.append("Address line key value ").append(contacts.getContact().get(i).getAddress().get(k).getAddressLine().get(x).getKeyValue()).append("<br>");
                }
            }
            for (int k = 0; k < contacts.getContact().get(i).getDescription().size(); k++) {
                sb.append("Desc: ").append(contacts.getContact().get(i).getDescription().get(k).getValue()).append("<br>");
            }
            for (int k = 0; k < contacts.getContact().get(i).getPhone().size(); k++) {
                sb.append("Phone: ").append(contacts.getContact().get(i).getPhone().get(k).getValue()).append("<br>");
            }
        }
        return sb.toString();
    }
    /**
     * important - regex to separate postback names from indexes, do not remove
     * or alter
     */
    static final Pattern p = Pattern.compile("[a-zA-Z]");

    /**
     * contactX
     *
     * @param map
     * @return
     */
    private Contacts BuildContacts(Map map) {
        Contacts cb = new Contacts();
        Map contactdata = MapFilter(map, PostBackConstants.CONTACT_PREFIX);
        Iterator it = contactdata.keySet().iterator();
        List<String> processedIndexes = new ArrayList<String>();
        while (it.hasNext()) {
            String key = (String) it.next();
            key = key.replace(PostBackConstants.CONTACT_PREFIX, "");
            //key should now be a number (index), follwed

            Matcher match = p.matcher(key);
            if (match.find()) {
                String index = key.substring(0, match.start());
                if (!processedIndexes.contains(index)) {
                    cb.getContact().add(
                            BuildSingleContact(
                            MapFilter(contactdata, PostBackConstants.CONTACT_PREFIX + index),
                            PostBackConstants.CONTACT_PREFIX + index));
                    processedIndexes.add(index);
                }
            } else {
                //some kind of parsing error or invalud format data
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }

        if (cb.getContact().isEmpty()) {
            return null;
        }
        return cb;
    }

    private Contact BuildSingleContact(Map m, String prefix) {
        Contact c = new Contact();
        String[] t = (String[]) m.get(prefix + PostBackConstants.TYPE);
        c.setUseType(t[0]);

        //get the Names
        c.getPersonName().addAll(
                BuildContactPersonNames(
                MapFilter(m, prefix + PostBackConstants.NAME),
                prefix + PostBackConstants.NAME));
        //get the descriptions
        c.getDescription().addAll(
                BuildDescription(
                MapFilter(m, prefix + PostBackConstants.DESCRIPTION),
                prefix + PostBackConstants.DESCRIPTION));
        c.getEmail().addAll(BuildEmail(MapFilter(m, prefix + PostBackConstants.EMAIL), prefix + PostBackConstants.EMAIL));

        c.getPhone().addAll(BuildPhone(MapFilter(m, prefix + PostBackConstants.PHONE), prefix + PostBackConstants.PHONE));
        c.getAddress().addAll(BuildAddress(MapFilter(m, prefix + PostBackConstants.ADDRESS), prefix + PostBackConstants.ADDRESS));
        return c;
    }

    /**
     * Prefix should be contactXName
     *
     * @param map
     * @param prefix
     * @return
     */
    private List<PersonName> BuildContactPersonNames(Map map, String prefix) {
        List<PersonName> ret = new ArrayList();
        Iterator it = map.keySet().iterator();

        List<String> processedIndexes = new ArrayList<String>();
        while (it.hasNext()) {
            String key = (String) it.next();
            String filteredkey = key.replace(prefix, "");
            //key should now be a number (index), follwed

            Matcher match = p.matcher(filteredkey);
            if (match.find()) {
                String index = filteredkey.substring(0, match.start());
                if (!processedIndexes.contains(index)) {
                    PersonName pn = new PersonName();
                    String[] t = (String[]) map.get(prefix + index + PostBackConstants.LANG);
                    if (t[0] == null || t[0].equalsIgnoreCase(PostBackConstants.CLICK_TO_EDIT)) {
                        pn.setLang(null);
                    } else {
                        pn.setLang(t[0]);
                    }
                    t = (String[]) map.get(prefix + index + PostBackConstants.VALUE);
                    pn.setValue(t[0]);
                    ret.add(pn);
                    processedIndexes.add(index);
                }
            } else {
                //some kind of parsing error or invalud format data
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }
        return ret;
    }

    private List<Description> BuildDescription(Map map, String prefix) {
        List<Description> ret = new ArrayList();
        Iterator it = map.keySet().iterator();

        List<String> processedIndexes = new ArrayList<String>();
        while (it.hasNext()) {
            String key = (String) it.next();
            String filteredkey = key.replace(prefix, "");
            //key should now be a number (index), follwed

            Matcher match = p.matcher(filteredkey);
            if (match.find()) {
                String index = filteredkey.substring(0, match.start());
                if (!processedIndexes.contains(index)) {
                    Description pn = new Description();
                    String[] t = (String[]) map.get(prefix + index + PostBackConstants.LANG);
                    if (t[0] == null || t[0].equalsIgnoreCase(PostBackConstants.CLICK_TO_EDIT)) {
                        pn.setLang(null);
                    } else {
                        pn.setLang(t[0]);
                    }
                    t = (String[]) map.get(prefix + index + PostBackConstants.VALUE);
                    pn.setValue(t[0]);
                    ret.add(pn);
                    processedIndexes.add(index);
                }
            } else {
                //some kind of parsing error or invalud format data
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }
        return ret;

    }

    private List<Email> BuildEmail(Map map, String prefix) {
        List<Email> list = new ArrayList<Email>();
        Iterator it = map.keySet().iterator();

        List<String> processedIndexes = new ArrayList<String>();
        while (it.hasNext()) {
            String key = (String) it.next();
            String filteredkey = key.replace(prefix, "");
            //key should now be a number (index), follwed

            Matcher match = p.matcher(filteredkey);
            if (match.find()) {
                String index = filteredkey.substring(0, match.start());
                if (!processedIndexes.contains(index)) {
                    Email pn = new Email();
                    String[] t = (String[]) map.get(prefix + index + PostBackConstants.TYPE);
                    pn.setUseType(t[0]);
                    t = (String[]) map.get(prefix + index + PostBackConstants.VALUE);
                    pn.setValue(t[0]);

                    list.add(pn);
                    processedIndexes.add(index);
                }
            } else {
                //some kind of parsing error or invalud format data
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }

        return list;
    }

    private DiscoveryURLs BuildDisco(Map map, String prefix) {
        DiscoveryURLs list = new DiscoveryURLs();
        Iterator it = map.keySet().iterator();

        List<String> processedIndexes = new ArrayList<String>();
        while (it.hasNext()) {
            String key = (String) it.next();
            String filteredkey = key.replace(prefix, "");
            //key should now be a number (index), follwed

            Matcher match = p.matcher(filteredkey);
            if (match.find()) {
                String index = filteredkey.substring(0, match.start());
                if (!processedIndexes.contains(index)) {
                    DiscoveryURL pn = new DiscoveryURL();
                    String[] t = (String[]) map.get(prefix + index + PostBackConstants.TYPE);
                    pn.setUseType(t[0]);
                    t = (String[]) map.get(prefix + index + PostBackConstants.VALUE);
                    pn.setValue(t[0]);

                    list.getDiscoveryURL().add(pn);
                    processedIndexes.add(index);
                }
            } else {
                //some kind of parsing error or invalud format data
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }
        if (list.getDiscoveryURL().isEmpty()) {
            return null;
        }
        return list;
    }

    private List<Name> BuildNames(Map map, String prefix) {
        List<Name> ret = new ArrayList();
        Iterator it = map.keySet().iterator();

        List<String> processedIndexes = new ArrayList<String>();
        while (it.hasNext()) {
            String key = (String) it.next();
            String filteredkey = key.replace(prefix, "");
            //key should now be a number (index), follwed

            Matcher match = p.matcher(filteredkey);
            if (match.find()) {
                String index = filteredkey.substring(0, match.start());
                if (!processedIndexes.contains(index)) {
                    Name pn = new Name();
                    String[] t = (String[]) map.get(prefix + index + PostBackConstants.LANG);
                    if (t[0].equalsIgnoreCase(PostBackConstants.CLICK_TO_EDIT)) {
                        pn.setLang(null);
                    } else {
                        pn.setLang(t[0]);
                    }
                    t = (String[]) map.get(prefix + index + PostBackConstants.VALUE);
                    pn.setValue(t[0]);


                    ret.add(pn);
                    processedIndexes.add(index);
                }
            } else {
                //some kind of parsing error or invalud format data
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }
        return ret;
    }

    private List< Phone> BuildPhone(Map map, String prefix) {
        List<Phone> ret = new ArrayList();
        Iterator it = map.keySet().iterator();

        List<String> processedIndexes = new ArrayList<String>();
        while (it.hasNext()) {
            String key = (String) it.next();
            String filteredkey = key.replace(prefix, "");
            //key should now be a number (index), follwed

            Matcher match = p.matcher(filteredkey);
            if (match.find()) {
                String index = filteredkey.substring(0, match.start());
                if (!processedIndexes.contains(index)) {
                    Phone pn = new Phone();
                    String[] t = (String[]) map.get(prefix + index + PostBackConstants.TYPE);
                    pn.setUseType(t[0]);
                    t = (String[]) map.get(prefix + index + PostBackConstants.VALUE);
                    pn.setValue(t[0]);

                    ret.add(pn);
                    processedIndexes.add(index);
                }
            } else {
                //some kind of parsing error or invalud format data
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }
        return ret;
    }

    private List<Address> BuildAddress(Map map, String prefix) {
        List<Address> ret = new ArrayList();
        Iterator it = map.keySet().iterator();

        List<String> processedIndexes = new ArrayList<String>();
        while (it.hasNext()) {
            String key = (String) it.next();
            String filteredkey = key.replace(prefix, "");
            //key should now be a number (index), follwed

            Matcher match = p.matcher(filteredkey);
            if (match.find()) {
                String index = filteredkey.substring(0, match.start());
                if (!processedIndexes.contains(index)) {
                    Address pn = new Address();
                    String[] t = (String[]) map.get(prefix + index + PostBackConstants.LANG);
                    if (t[0] == null || t[0].equalsIgnoreCase(PostBackConstants.CLICK_TO_EDIT)) {
                        pn.setLang(null);
                    } else {
                        pn.setLang(t[0]);
                    }
                    t = (String[]) map.get(prefix + index + PostBackConstants.TYPE);
                    if (t[0] == null || t[0].equalsIgnoreCase(PostBackConstants.CLICK_TO_EDIT)) {
                        pn.setUseType(null);
                    } else {
                        pn.setUseType(t[0]);
                    }

                    t = (String[]) map.get(prefix + index + PostBackConstants.SORTCODE);
                    if (t[0] == null || t[0].equalsIgnoreCase(PostBackConstants.CLICK_TO_EDIT)) {
                        pn.setSortCode(null);
                    } else {
                        pn.setSortCode(t[0]);
                    }
                    t = (String[]) map.get(prefix + index + PostBackConstants.KEYNAME);
                    if (t[0] == null || t[0].equalsIgnoreCase(PostBackConstants.CLICK_TO_EDIT)) {
                        pn.setTModelKey(null);
                    } else {
                        pn.setTModelKey(t[0]);
                    }
                    pn.getAddressLine().addAll(BuildAddressLine(MapFilter(map, prefix + index + PostBackConstants.ADDRESSLINE), prefix + index + PostBackConstants.ADDRESSLINE));


                    ret.add(pn);
                    processedIndexes.add(index);
                }
            } else {
                //some kind of parsing error or invalud format data
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }
        return ret;
    }

    private List<AddressLine> BuildAddressLine(Map map, String prefix) {
        List<AddressLine> ret = new ArrayList();
        Iterator it = map.keySet().iterator();

        List<String> processedIndexes = new ArrayList<String>();
        while (it.hasNext()) {
            String key = (String) it.next();
            String filteredkey = key.replace(prefix, "");
            //key should now be a number (index), follwed

            Matcher match = p.matcher(filteredkey);
            if (match.find()) {
                String index = filteredkey.substring(0, match.start());
                if (!processedIndexes.contains(index)) {
                    AddressLine pn = new AddressLine();
                    String[] t = (String[]) map.get(prefix + index + PostBackConstants.VALUE);
                    pn.setValue(t[0]);
                    t = (String[]) map.get(prefix + index + PostBackConstants.KEYNAME);
                    pn.setKeyName(t[0]);
                    t = (String[]) map.get(prefix + index + PostBackConstants.KEYVALUE);
                    pn.setKeyValue(t[0]);


                    ret.add(pn);
                    processedIndexes.add(index);
                }
            } else {
                //some kind of parsing error or invalud format data
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }
        return ret;
    }

    private CategoryBag BuildCatBag(Map map, String prefix, Map mapgrp, String grpprefix) {
        CategoryBag ret = new CategoryBag();
        Iterator it = map.keySet().iterator();

        List<String> processedIndexes = new ArrayList<String>();
        while (it.hasNext()) {
            String key = (String) it.next();
            String filteredkey = key.replace(prefix, "");
            //key should now be a number (index), follwed

            Matcher match = p.matcher(filteredkey);
            if (match.find()) {
                String index = filteredkey.substring(0, match.start());
                if (!processedIndexes.contains(index)) {
                    KeyedReference pn = new KeyedReference();
                    String[] t = (String[]) map.get(prefix + index + PostBackConstants.VALUE);
                    pn.setTModelKey(t[0]);
                    t = (String[]) map.get(prefix + index + PostBackConstants.KEYNAME);
                    pn.setKeyName(t[0]);
                    t = (String[]) map.get(prefix + index + PostBackConstants.KEYVALUE);
                    pn.setKeyValue(t[0]);


                    ret.getKeyedReference().add(pn);
                    processedIndexes.add(index);
                }
            } else {
                //some kind of parsing error or invalud format data
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }

        return ret;
    }

    private IdentifierBag BuildIdentBag(Map map, String prefix) {
        IdentifierBag ret = new IdentifierBag();
        ret.getKeyedReference().addAll(BuildKeyedReference(map, prefix));

        if (ret.getKeyedReference().isEmpty()) {
            return null;
        }
        return ret;
    }

    private List<KeyedReferenceGroup> BuildKeyedReferenceGroup(Map map, String prefix) {
        List<KeyedReferenceGroup> ret = new ArrayList<KeyedReferenceGroup>();
        Iterator it = map.keySet().iterator();

        List<String> processedIndexes = new ArrayList<String>();
        while (it.hasNext()) {
            String key = (String) it.next();
            String filteredkey = key.replace(prefix, "");
            //key should now be a number (index), follwed

            Matcher match = p.matcher(filteredkey);
            if (match.find()) {
                String index = filteredkey.substring(0, match.start());
                if (!processedIndexes.contains(index)) {
                    KeyedReferenceGroup pn = new KeyedReferenceGroup();
                    String[] t = (String[]) map.get(prefix + index + PostBackConstants.VALUE);
                    pn.setTModelKey(t[0]);
                    pn.getKeyedReference().addAll(BuildKeyedReference(MapFilter(map, prefix + index + PostBackConstants.KEY_REF), prefix + index + PostBackConstants.KEY_REF));
                    ret.add(pn);
                    processedIndexes.add(index);
                }
            } else {
                //some kind of parsing error or invalud format data
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }

        return ret;
    }

    private List<KeyedReference> BuildKeyedReference(Map map, String prefix) {
        List<KeyedReference> ret = new ArrayList<KeyedReference>();

        Iterator it = map.keySet().iterator();

        List<String> processedIndexes = new ArrayList<String>();
        while (it.hasNext()) {
            String key = (String) it.next();
            String filteredkey = key.replace(prefix, "");
            //key should now be a number (index), follwed

            Matcher match = p.matcher(filteredkey);
            if (match.find()) {
                String index = filteredkey.substring(0, match.start());
                if (!processedIndexes.contains(index)) {
                    KeyedReference pn = new KeyedReference();
                    String[] t = (String[]) map.get(prefix + index + PostBackConstants.VALUE);
                    pn.setTModelKey(t[0]);
                    t = (String[]) map.get(prefix + index + PostBackConstants.KEYNAME);
                    pn.setKeyName(t[0]);
                    t = (String[]) map.get(prefix + index + PostBackConstants.KEYVALUE);
                    pn.setKeyValue(t[0]);


                    ret.add(pn);
                    processedIndexes.add(index);
                }
            } else {
                //some kind of parsing error or invalud format data
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }
        return ret;
    }

    public enum AuthStyle {

        HTTP_BASIC,
        HTTP_DIGEST,
        HTTP_NTLM,
        UDDI_AUTH,
        HTTP_CLIENT_CERT
    }

    private String ListToString(List<Name> name) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.size(); i++) {
            sb.append(name.get(i).getValue()).append(" ");
        }
        return sb.toString();
    }

    private String ListToDescString(List<Description> name) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.size(); i++) {
            sb.append(name.get(i).getValue()).append(" ");
        }
        return sb.toString();
    }

    public PagableContainer SearchForServices(String keyword, String lang, int maxrecords, int offset) {
        PagableContainer ret = new PagableContainer();
        ret.displaycount = 0;
        ret.offset = offset;
        ret.totalrecords = 0;
        try {

            FindService fs = new FindService();
            fs.setAuthInfo(GetToken());
            fs.setMaxRows(maxrecords);
            fs.setListHead(offset);
            Name n = new Name();
            if (lang == null || lang.equalsIgnoreCase(PostBackConstants.CLICK_TO_EDIT)) {
                n.setLang(null);
            } else {
                n.setLang(lang);
            }
            n.setValue(keyword);
            fs.getName().add(n);
            fs.setFindQualifiers(new org.uddi.api_v3.FindQualifiers());
            fs.getFindQualifiers().getFindQualifier().add(FindQualifiers.APPROXIMATE_MATCH);
            ServiceList findService = inquiry.findService(fs);
            if (findService.getServiceInfos() == null) {
                ret.renderedHtml = "No services returned";
                return ret;
            }
            ret.displaycount = findService.getListDescription().getIncludeCount();
            ret.totalrecords = findService.getListDescription().getActualCount();
            StringBuilder sb = new StringBuilder();
            sb.append("<table class=\"table\"><tr><th>Name</th><th>Key</th><th>Business</th></tr>");
            for (int i = 0; i < findService.getServiceInfos().getServiceInfo().size(); i++) {
                sb.append("<tr><td><a href=\"serviceEditor.jsp?id=").
                        append(StringEscapeUtils.escapeHtml(findService.getServiceInfos().getServiceInfo().get(i).getServiceKey())).
                        append("\" title=\"").
                        append(StringEscapeUtils.escapeHtml(findService.getServiceInfos().getServiceInfo().get(i).getServiceKey()))
                        .append("\">");
                sb.append(ListToString(findService.getServiceInfos().getServiceInfo().get(i).getName())).append("</a></td><td>");
                sb.append((findService.getServiceInfos().getServiceInfo().get(i).getServiceKey())).append("</td><td>");
                sb.append(StringEscapeUtils.escapeHtml((findService.getServiceInfos().getServiceInfo().get(i).getBusinessKey())))
                        .append("</td></tr>");
            }
            sb.append("</table>");
            ret.renderedHtml = sb.toString();
            return ret;
        } catch (DispositionReportFaultMessage ex) {
            log.log(Level.ERROR, null, ex);
            ret.renderedHtml = "error " + ex.getMessage() + " " + ex.detail.getMessage();
        } catch (RemoteException ex) {
            log.log(Level.ERROR, null, ex);
            ret.renderedHtml = "error " + ex.getMessage();
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
            ret.renderedHtml = "error " + ex.getMessage();
        }
        return ret;

    }

    public String AddPublisher(HttpServletRequest request) {
        try {
            SavePublisher sp = new SavePublisher();
            sp.setAuthInfo(GetToken());
            Publisher p = new Publisher();
            //           p.s
            sp.getPublisher().add(p);
            PublisherDetail savePublisher = juddi.savePublisher(sp);
            return "Success";
        } catch (DispositionReportFaultMessage ex) {
            log.log(Level.ERROR, null, ex);
            return ex.getMessage() + " " + ex.detail.getMessage();
        } catch (RemoteException ex) {
            log.log(Level.ERROR, null, ex);
            return ex.getMessage() + " " + ex.detail.getMessage();
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
            return ex.getMessage();
        }


    }

    /**
     * returns an html listing of Juddi authorized publishers
     *
     * @return
     */
    public String GetPublisherListAsHtml() {
        if (this.IsJuddiRegistry()) {
            return "This function is only available on Juddi registries";
        }
        try {
            GetAllPublisherDetail gpd = new GetAllPublisherDetail();
            gpd.setAuthInfo(GetToken());
            PublisherDetail allPublisherDetail = juddi.getAllPublisherDetail(gpd);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < allPublisherDetail.getPublisher().size(); i++) {
                sb.append("Authorized Name = ").append(allPublisherDetail.getPublisher().get(i).getAuthorizedName()).append("<br>");
                sb.append("Email = ").append(allPublisherDetail.getPublisher().get(i).getEmailAddress()).append("<br>");
                sb.append("Is Administrator ? = ").append(allPublisherDetail.getPublisher().get(i).getIsAdmin()).append("<br>");
                sb.append("Is Enabled? = ").append(allPublisherDetail.getPublisher().get(i).getIsEnabled()).append("<br>");
                sb.append("Max bindings per service = ").append(allPublisherDetail.getPublisher().get(i).getMaxBindingsPerService()).append("<br>");
                sb.append("Max businesses = ").append(allPublisherDetail.getPublisher().get(i).getMaxBusinesses()).append("<br>");
                sb.append("Max Services per Business = ").append(allPublisherDetail.getPublisher().get(i).getMaxServicePerBusiness()).append("<br>");
                sb.append("Max tModels = ").append(allPublisherDetail.getPublisher().get(i).getMaxTModels()).append("<br>");
                sb.append("Publisher Name = ").append(allPublisherDetail.getPublisher().get(i).getPublisherName()).append("<br><br>");
            }
            return sb.toString();
        } catch (DispositionReportFaultMessage ex) {
            log.log(Level.ERROR, null, ex);
            return ex.getMessage() + " " + ex.detail.getMessage();
        } catch (RemoteException ex) {
            log.log(Level.ERROR, null, ex);
            return ex.getMessage() + " " + ex.detail.getMessage();
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
            return ex.getMessage();
        }
    }

    /**
     * Adds a special tModel key generator keyGenerator: Marking a tModel with
     * this categorization designates it as one whose tModelKey identifies a key
     * generator partition that can be used by its owner to derive and assign
     * other entity keys. This categorization is reserved for key generator
     * tModels. Any attempt to use this categorization for something other than
     * a key generator tModel will fail with E_valueNotAllowed returned.
     *
     * @param partitionName
     * @return
     */
    public String AddTmodelKenGenerator(String partitionName, String name, String lang) {
        try {
            if (!partitionName.startsWith("uddi:")) {
                return "A tModel partition key generator must have a name that starts with uddi:";
            }
            if (!partitionName.endsWith(":keyGenerator")) {
                return "A tModel partition key generator must have a name that ends with :keyGenerator";
            }


            SaveTModel st = new SaveTModel();
            st.setAuthInfo(GetToken());
            TModel tm = new TModel();
            tm.setName(new Name());
            tm.getName().setValue(name);
            if (lang == null || lang.equalsIgnoreCase(PostBackConstants.CLICK_TO_EDIT)) {
                tm.getName().setLang(null);
            } else {
                tm.getName().setLang(lang);
            }
            tm.setCategoryBag(new CategoryBag());
            KeyedReference kr = new KeyedReference();
            kr.setTModelKey("uddi:uddi.org:categorization:types");
            kr.setKeyName("uddi-org:keyGenerator");
            kr.setKeyValue("keyGenerator");
            tm.getCategoryBag().getKeyedReference().add(kr);
            tm.setTModelKey(partitionName.toLowerCase());
            st.getTModel().add(tm);
            publish.saveTModel(st);
            return "Success";
        } catch (DispositionReportFaultMessage ex) {
            log.log(Level.ERROR, null, ex);
            return ex.getMessage() + " " + ex.detail.getMessage();
        } catch (RemoteException ex) {
            log.log(Level.ERROR, null, ex);
            return ex.getMessage() + " " + ex.detail.getMessage();
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
            return ex.getMessage();
        }
    }

    /**
     * provides based tmodel searching/browser capability that's pagable
     *
     * @param keyword
     * @param lang
     * @param offset
     * @param maxrecords
     * @return
     */
    public PagableContainer tModelListAsHtml(String keyword, String lang, int offset, int maxrecords) {
        PagableContainer ret = new PagableContainer();
        try {
            FindTModel fm = new FindTModel();
            fm.setAuthInfo(GetToken());
            fm.setMaxRows(maxrecords);
            fm.setListHead(offset);
            fm.setName(new Name());
            if (lang == null || lang.equalsIgnoreCase(PostBackConstants.CLICK_TO_EDIT)) {
                fm.getName().setLang(null);
            } else {
                fm.getName().setLang(lang);
            }
            fm.getName().setValue(keyword);
            fm.setFindQualifiers(new org.uddi.api_v3.FindQualifiers());
            fm.getFindQualifiers().getFindQualifier().add(FindQualifiers.APPROXIMATE_MATCH);
            TModelList findTModel = inquiry.findTModel(fm);

            ret.offset = offset;
            ret.displaycount = findTModel.getListDescription().getIncludeCount();
            ret.totalrecords = findTModel.getListDescription().getActualCount();
            if (findTModel == null || findTModel.getTModelInfos() == null || findTModel.getTModelInfos().getTModelInfo().isEmpty()) {
                ret.renderedHtml = "No tModels are defined";
            } else {
                StringBuilder sb = new StringBuilder();

                sb.append("<table class=\"table\">");
                for (int i = 0; i < findTModel.getTModelInfos().getTModelInfo().size(); i++) {
                    sb.append("<tr><td><a href=\"tmodelEditor.jsp?id=")
                            .append(StringEscapeUtils.escapeHtml(findTModel.getTModelInfos().getTModelInfo().get(i).getTModelKey()))
                            .append("\" class=\"btn btn-primary\">Edit</a></td><td>");
                    sb.append("tModel Key = ").append(
                            StringEscapeUtils.escapeHtml(findTModel.getTModelInfos().getTModelInfo().get(i).getTModelKey()))
                            .append("<br>");
                    sb.append("Description = ").append(
                            StringEscapeUtils.escapeHtml(ListToDescString(findTModel.getTModelInfos().getTModelInfo().get(i).getDescription())))
                            .append("<br>");
                    sb.append("Name = ").append(StringEscapeUtils.escapeHtml(findTModel.getTModelInfos().getTModelInfo().get(i).getName().getValue()))
                            .append(", ")
                            .append(StringEscapeUtils.escapeHtml(findTModel.getTModelInfos().getTModelInfo().get(i).getName().getLang()));
                    sb.append("</td></tr>");
                }
                sb.append("</table>");
                ret.renderedHtml = sb.toString();
            }
        } catch (DispositionReportFaultMessage ex) {
            log.log(Level.ERROR, null, ex);
            ret.renderedHtml = ex.getMessage() + " " + ex.detail.getMessage();
        } catch (RemoteException ex) {
            log.log(Level.ERROR, null, ex);
            ret.renderedHtml = ex.getMessage() + " " + ex.detail.getMessage();
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
            ret.renderedHtml = ex.getMessage();
        }
        return ret;
    }

    /**
     * Returns the details of a tModel by tModelKey
     *
     * @param id
     * @return the details or null if it doesn't exist or a null value was
     * passed
     */
    public TModel getTmodelDetails(String id) {
        try {
            if (id == null || id.length() == 0) {
                return null;
            }

            GetTModelDetail req = new GetTModelDetail();
            req.setAuthInfo(GetToken());
            req.getTModelKey().add(id);
            TModelDetail tModelDetail = inquiry.getTModelDetail(req);
            if (tModelDetail != null && !tModelDetail.getTModel().isEmpty()) {
                return tModelDetail.getTModel().get(0);

            }

        } catch (DispositionReportFaultMessage ex) {
            log.log(Level.ERROR, null, ex);
        } catch (RemoteException ex) {
            log.log(Level.ERROR, null, ex);
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        }
        return null;
    }

    public BusinessEntity GetBusinessDetailsAsObject(String key) {
        return GetBusinessDetails(key);
    }

    public BusinessService GetServiceDetailsAsObject(String key) {
        return GetServiceDetail(key);
    }

    public BindingTemplate GetBindingDetailsAsObject(String key) {
        try {
            GetBindingDetail r = new GetBindingDetail();
            r.setAuthInfo(GetToken());
            r.getBindingKey().add(key);
            BindingDetail bindingDetail = inquiry.getBindingDetail(r);
            return bindingDetail.getBindingTemplate().get(0);
        } catch (DispositionReportFaultMessage ex) {
            log.log(Level.ERROR, null, ex);

        } catch (RemoteException ex) {
            log.log(Level.ERROR, null, ex);

        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        }
        return null;
    }

    public TModel GettModelDetailsAsObject(String key) {
        try {
            GetTModelDetail r = new GetTModelDetail();
            r.setAuthInfo(GetToken());
            r.getTModelKey().add(key);
            TModelDetail tModelDetail = inquiry.getTModelDetail(r);
            return tModelDetail.getTModel().get(0);
        } catch (DispositionReportFaultMessage ex) {
            log.log(Level.ERROR, null, ex);

        } catch (RemoteException ex) {
            log.log(Level.ERROR, null, ex);

        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        }
        return null;
    }

    public enum FindType {

        Business, RelatedBusiness, Service, tModel, Publisher, BindingTemplate
    }

    public enum CriteriaType {

        Name, Category, uid, tmodel, identbag
    }

    public String Search(FindType type, CriteriaType criteria, String parameters, String lang, String[] findqualifier) {
        switch (type) {
            case BindingTemplate:
                return FindBindingTemplateToHtml(criteria, parameters, lang, findqualifier);
            case Business:
                return FindBusiness(criteria, parameters, lang, findqualifier);
            case RelatedBusiness:
                return FindRelatedBusiness(criteria, parameters, lang, findqualifier);
            case Publisher:
                return FindPublishers(criteria, parameters, lang, findqualifier);
            case Service:
                return FindService(criteria, parameters, lang, findqualifier);
            case tModel:
                return FindtModels(criteria, parameters, lang, findqualifier);
        }
        return "unknown error";
    }

    private String FindBindingTemplateToHtml(CriteriaType criteria, String parameters, String lang, String[] fq) {
        try {
            FindBinding fb = new FindBinding();
            fb.setAuthInfo(GetToken());
            if (fq != null) {
                fb.setFindQualifiers(new org.uddi.api_v3.FindQualifiers());
                if (fq != null) {
                    for (int i = 0; i < fq.length; i++) {
                        fb.getFindQualifiers().getFindQualifier().add(fq[i]);
                    }
                }
            }
            BindingDetail findBusiness = null;
            switch (criteria) {
                case Category:
                    fb.setCategoryBag(new CategoryBag());
                    KeyedReference kr = new KeyedReference();
                    kr.setTModelKey(parameters);
                    fb.getCategoryBag().getKeyedReference().add(kr);
                    break;
                case Name:
                    break;
                case tmodel:
                    fb.setTModelBag(new TModelBag());
                    fb.getTModelBag().getTModelKey().add(parameters);
                    break;
                case uid:
                    BusinessEntity t = GetBusinessDetails(parameters);
                    findBusiness = new BindingDetail();
                    BindingTemplate bt = GetBindingDetailsAsObject(parameters);
                    findBusiness.getBindingTemplate().add(bt);

                    break;

            }
            if (findBusiness == null) {
                findBusiness = inquiry.findBinding(fb);
            }
            if (findBusiness.getBindingTemplate() != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("<table class=\"table\">");
                for (int i = 0; i < findBusiness.getBindingTemplate().size(); i++) {
                    sb.append("<tr><td>");
                    sb.append("<a href=\"serviceEditor.jsp?id=").
                            append(StringEscapeUtils.escapeHtml(findBusiness.getBindingTemplate().get(i).getServiceKey())).
                            append("\">");
                    if (findBusiness.getBindingTemplate().get(i).getDescription().isEmpty()) {
                        sb.append(StringEscapeUtils.escapeHtml(findBusiness.getBindingTemplate().get(i).getServiceKey()));
                    } else {
                        sb.append(StringEscapeUtils.escapeHtml(ListToDescString(findBusiness.getBindingTemplate().get(i).getDescription())));
                    }
                    sb.append("</a>");
                    sb.append("</td></tr>");
                }
                sb.append("</table>");
                return sb.toString();
            } else {
                return "no result returned.";
            }
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
            return "error caught! " + ex.getMessage();
        }
    }

    private String FindBusiness(CriteriaType criteria, String parameters, String lang, String[] fq) {
        try {
            FindBusiness fb = new FindBusiness();
            fb.setAuthInfo(GetToken());
            if (fq != null) {
                fb.setFindQualifiers(new org.uddi.api_v3.FindQualifiers());
                if (fq != null) {
                    for (int i = 0; i < fq.length; i++) {
                        fb.getFindQualifiers().getFindQualifier().add(fq[i]);
                    }
                }
            }
            BusinessList findBusiness = null;
            switch (criteria) {
                case Category:
                    fb.setCategoryBag(new CategoryBag());
                    KeyedReference kr = new KeyedReference();
                    kr.setTModelKey(parameters);
                    fb.getCategoryBag().getKeyedReference().add(kr);
                    break;
                case Name:
                    Name n = new Name();
                    n.setLang(lang);
                    n.setValue(parameters);
                    fb.getName().add(n);
                    break;
                case tmodel:
                    fb.setTModelBag(new TModelBag());
                    fb.getTModelBag().getTModelKey().add(parameters);
                    break;
                case uid:
                    BusinessEntity t = GetBusinessDetails(parameters);
                    findBusiness = new BusinessList();
                    findBusiness.setBusinessInfos(new BusinessInfos());

                    BusinessInfo bd = new BusinessInfo();
                    bd.setBusinessKey(t.getBusinessKey());
                    bd.getName().addAll(t.getName());
                    findBusiness.getBusinessInfos().getBusinessInfo().add(bd);
                    break;

            }
            if (findBusiness == null) {
                findBusiness = inquiry.findBusiness(fb);
            }
            if (findBusiness.getBusinessInfos() != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("<table class=\"table\">");
                for (int i = 0; i < findBusiness.getBusinessInfos().getBusinessInfo().size(); i++) {
                    sb.append("<tr><td>");
                    sb.append("<a href=\"businessEditor2.jsp?id=").
                            append(StringEscapeUtils.escapeHtml(findBusiness.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey())).
                            append("\">");
                    if (findBusiness.getBusinessInfos().getBusinessInfo().get(i).getName().isEmpty()) {
                        sb.append(StringEscapeUtils.escapeHtml(findBusiness.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey()));
                    } else {
                        sb.append(StringEscapeUtils.escapeHtml(ListToString(findBusiness.getBusinessInfos().getBusinessInfo().get(i).getName())));
                    }
                    sb.append("</a>");
                    sb.append("</td></tr>");
                }
                sb.append("</table>");
                return sb.toString();
            } else {
                return "no result returned.";
            }
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
            return "error caught! " + ex.getMessage();
        }
    }

    private String FindRelatedBusiness(CriteriaType criteria, String parameters, String lang, String[] fq) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private String FindPublishers(CriteriaType criteria, String parameters, String lang, String[] fq) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private String FindService(CriteriaType criteria, String parameters, String lang, String[] fq) {
        try {
            FindService fb = new FindService();
            fb.setAuthInfo(GetToken());
            if (fq != null) {
                fb.setFindQualifiers(new org.uddi.api_v3.FindQualifiers());
                if (fq != null) {
                    for (int i = 0; i < fq.length; i++) {
                        fb.getFindQualifiers().getFindQualifier().add(fq[i]);
                    }
                }
            }
            ServiceList findBusiness = null;
            switch (criteria) {
                case Category:
                    fb.setCategoryBag(new CategoryBag());
                    KeyedReference kr = new KeyedReference();
                    kr.setTModelKey(parameters);
                    fb.getCategoryBag().getKeyedReference().add(kr);
                    break;
                case Name:
                    Name n = new Name();
                    n.setLang(lang);
                    n.setValue(parameters);
                    fb.getName().add(n);
                    break;
                case tmodel:
                    fb.setTModelBag(new TModelBag());
                    fb.getTModelBag().getTModelKey().add(parameters);
                    break;
                case uid:
                    BusinessEntity t = GetBusinessDetails(parameters);
                    findBusiness = new ServiceList();
                    findBusiness.setServiceInfos(new ServiceInfos());
                    BusinessService GetServiceDetail = GetServiceDetail(parameters);
                    ServiceInfo si = new ServiceInfo();
                    si.setBusinessKey(GetServiceDetail.getBusinessKey());
                    si.setServiceKey(GetServiceDetail.getServiceKey());
                    si.getName().addAll(GetServiceDetail.getName());
                    findBusiness.getServiceInfos().getServiceInfo().add(si);
                    break;

            }
            if (findBusiness == null) {
                findBusiness = inquiry.findService(fb);
            }
            if (findBusiness.getServiceInfos() != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("<table class=\"table\">");
                for (int i = 0; i < findBusiness.getServiceInfos().getServiceInfo().size(); i++) {
                    sb.append("<tr><td>");
                    sb.append("<a href=\"serviceEditor.jsp?id=").
                            append(StringEscapeUtils.escapeHtml(findBusiness.getServiceInfos().getServiceInfo().get(i).getServiceKey())).
                            append("\">");
                    if (findBusiness.getServiceInfos().getServiceInfo().get(i).getName().isEmpty()) {
                        sb.append(StringEscapeUtils.escapeHtml(findBusiness.getServiceInfos().getServiceInfo().get(i).getServiceKey()));
                    } else {
                        sb.append(StringEscapeUtils.escapeHtml(ListToString(findBusiness.getServiceInfos().getServiceInfo().get(i).getName())));
                    }
                    sb.append("</a>");
                    sb.append("</td></tr>");
                }
                sb.append("</table>");
                return sb.toString();
            } else {
                return "no result returned.";
            }
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
            return "error caught! " + ex.getMessage();
        }
    }

    private String FindtModels(CriteriaType criteria, String parameters, String lang, String[] fq) {
        try {
            FindTModel fb = new FindTModel();
            fb.setAuthInfo(GetToken());
            if (fq != null) {
                fb.setFindQualifiers(new org.uddi.api_v3.FindQualifiers());
                if (fq != null) {
                    for (int i = 0; i < fq.length; i++) {
                        fb.getFindQualifiers().getFindQualifier().add(fq[i]);
                    }
                }
            }
            TModelList findBusiness = null;
            switch (criteria) {
                case Category:
                    fb.setCategoryBag(new CategoryBag());
                    KeyedReference kr = new KeyedReference();
                    kr.setTModelKey(parameters);
                    fb.getCategoryBag().getKeyedReference().add(kr);
                    break;
                case Name:
                    Name n = new Name();
                    n.setLang(lang);
                    n.setValue(parameters);
                    fb.setName(n);
                    break;
                case tmodel:
                    //TODO
                    break;
                case uid:
                    BusinessEntity t = GetBusinessDetails(parameters);
                    TModel tmodelDetails = this.getTmodelDetails(parameters);
                    TModelInfo tmi = new TModelInfo();
                    tmi.setName(tmodelDetails.getName());
                    tmi.setTModelKey(tmodelDetails.getTModelKey());
                    tmi.getDescription().addAll(tmodelDetails.getDescription());
                    findBusiness.getTModelInfos().getTModelInfo().add(tmi);

                    break;

            }
            if (findBusiness == null) {
                findBusiness = inquiry.findTModel(fb);
            }
            if (findBusiness.getTModelInfos() != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("<table class=\"table\">");
                for (int i = 0; i < findBusiness.getTModelInfos().getTModelInfo().size(); i++) {
                    sb.append("<tr><td>");
                    sb.append("<a href=\"tmodelEditor.jsp?id=").
                            append(StringEscapeUtils.escapeHtml(findBusiness.getTModelInfos().getTModelInfo().get(i).getTModelKey())).
                            append("\">");
                    if (findBusiness.getTModelInfos().getTModelInfo().get(i).getName() == null) {
                        sb.append(StringEscapeUtils.escapeHtml(findBusiness.getTModelInfos().getTModelInfo().get(i).getTModelKey()));
                    } else {
                        sb.append(StringEscapeUtils.escapeHtml((findBusiness.getTModelInfos().getTModelInfo().get(i).getName().getValue())));
                    }
                    sb.append("</a>");
                    sb.append("</td></tr>");
                }
                sb.append("</table>");
                return sb.toString();
            } else {
                return "no result returned.";
            }
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
            return "error caught! " + ex.getMessage();
        }
    }

    /**
     * Deletes a business
     *
     * @param bizid
     * @return null if successful, otherwise an error message
     */
    public String deleteBusiness(String bizid) {
        if (bizid == null || bizid.length() == 0) {
            return "No input";
        }
        List<String> x = new ArrayList<String>();
        x.add(bizid);
        return deleteBusiness(x);
    }

    /**
     * Deletes a list of UDDI businesses by key
     *
     * @param bizid
     * @return null if successful, otherwise an error message
     */
    public String deleteBusiness(List<String> bizid) {
        if (bizid == null || bizid.isEmpty()) {
            return "No input";
        }
        DeleteBusiness db = new DeleteBusiness();
        db.setAuthInfo(GetToken());
        db.getBusinessKey().addAll(bizid);
        try {
            publish.deleteBusiness(db);
        } catch (DispositionReportFaultMessage ex) {
            log.log(Level.ERROR, null, ex);
            return ex.getMessage() + " " + ex.detail.getMessage();
        } catch (RemoteException ex) {
            log.log(Level.ERROR, null, ex);
            return ex.getMessage() + " " + ex.detail.getMessage();
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
            return ex.getMessage();
        }
        return "Success";
    }

    public String deleteTmodel(String bizid) {
        if (bizid == null || bizid.length() == 0) {
            return "No input";
        }
        List<String> x = new ArrayList<String>();
        x.add(bizid);
        return deleteTmodel(x);
    }

    /**
     * Deletes a list of UDDI tModels by key
     *
     * @param bizid
     * @return null if successful, otherwise an error message
     */
    public String deleteTmodel(List<String> bizid) {
        if (bizid == null || bizid.isEmpty()) {
            return "No input";
        }
        DeleteTModel db = new DeleteTModel();
        db.setAuthInfo(GetToken());
        db.getTModelKey().addAll(bizid);
        try {
            publish.deleteTModel(db);
        } catch (DispositionReportFaultMessage ex) {
            log.log(Level.ERROR, null, ex);
            return ex.getMessage() + " " + ex.detail.getMessage();
        } catch (RemoteException ex) {
            log.log(Level.ERROR, null, ex);
            return ex.getMessage() + " " + ex.detail.getMessage();
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
            return ex.getMessage();
        }
        return "Success";
    }

    public String SaveTModel(TModel be) {
        try {
            SaveTModel sb = new SaveTModel();
            sb.setAuthInfo(GetToken());

            sb.getTModel().add(be);
            TModelDetail saveTModel = publish.saveTModel(sb);
            return "Saved!";
        } catch (DispositionReportFaultMessage ex) {
            log.log(Level.ERROR, null, ex);
            return "UDDI Disposition Fault Error caught! " + ex.getMessage() + " " + ex.detail.getMessage();
        } catch (RemoteException ex) {
            log.log(Level.ERROR, null, ex);
            return "Remote Exception Error caught! " + ex.getMessage() + " " + ex.detail.getMessage();
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
            return "Unexpected Error caught! " + ex.getMessage();
        }
    }

    public String SaveTModel(HttpServletRequest request) {

        TModel be = new TModel();
        be.setTModelKey(request.getParameter(PostBackConstants.SERVICEKEY).trim());
        if (be.getTModelKey() != null && (be.getTModelKey().equalsIgnoreCase(PostBackConstants.CLICK_TO_EDIT))
                || be.getTModelKey().length() == 0) {
            be.setTModelKey(null);
        }
        be.setName(new Name());
        String t = request.getParameter(PostBackConstants.NAME + PostBackConstants.VALUE);
        if (t != null && !t.equalsIgnoreCase(PostBackConstants.CLICK_TO_EDIT) && t.length() > 0) {
            be.getName().setValue(t);
        }
        t = request.getParameter(PostBackConstants.NAME + PostBackConstants.LANG);
        if (t != null && !t.equalsIgnoreCase(PostBackConstants.CLICK_TO_EDIT) && t.length() > 0) {
            be.getName().setLang(t);
        }

        //TODO signature

        be.getDescription().addAll(BuildDescription(MapFilter(request.getParameterMap(), PostBackConstants.DESCRIPTION), PostBackConstants.DESCRIPTION));
        be.getOverviewDoc().addAll(BuildOverviewDocs(MapFilter(request.getParameterMap(), PostBackConstants.OVERVIEW), PostBackConstants.OVERVIEW));

//            be.setDiscoveryURLs(BuildDisco(MapFilter(request.getParameterMap(), PostBackConstants.DISCOVERYURL), PostBackConstants.DISCOVERYURL));
        CategoryBag cb = new CategoryBag();
        cb.getKeyedReference().addAll(BuildKeyedReference(MapFilter(request.getParameterMap(), PostBackConstants.CATBAG_KEY_REF), PostBackConstants.CATBAG_KEY_REF));
        cb.getKeyedReferenceGroup().addAll(BuildKeyedReferenceGroup(MapFilter(request.getParameterMap(), PostBackConstants.CATBAG_KEY_REF_GRP), PostBackConstants.CATBAG_KEY_REF_GRP));

        if (!cb.getKeyedReference().isEmpty() || !cb.getKeyedReferenceGroup().isEmpty()) {
            be.setCategoryBag(cb);
        }
        be.setIdentifierBag(BuildIdentBag(MapFilter(request.getParameterMap(), PostBackConstants.IDENT_KEY_REF), PostBackConstants.IDENT_KEY_REF));

        return SaveTModel(be);

    }

    private List<OverviewDoc> BuildOverviewDocs(Map map, String prefix) {

        List<OverviewDoc> ret = new ArrayList<OverviewDoc>();
        Iterator it = map.keySet().iterator();
        List<String> processedIndexes = new ArrayList<String>();
        //
        while (it.hasNext()) {
            String key = (String) it.next();
            String filteredkey = key.replace(prefix, "");
            //key should now be a number (index), follwed

            Matcher match = p.matcher(filteredkey);
            if (match.find()) {
                String index = filteredkey.substring(0, match.start());
                if (!processedIndexes.contains(index)) {
                    OverviewDoc pn = new OverviewDoc();
                    //TODO is this a required field?
                    pn.setOverviewURL(new OverviewURL());

                    String[] t = (String[]) map.get(prefix + index + PostBackConstants.VALUE);
                    pn.getOverviewURL().setValue(t[0]);
                    t = (String[]) map.get(prefix + index + PostBackConstants.TYPE);
                    pn.getOverviewURL().setUseType(t[0]);

                    pn.getDescription().addAll(BuildDescription(MapFilter(map, prefix + index + PostBackConstants.DESCRIPTION), prefix + index + PostBackConstants.DESCRIPTION));

                    ret.add(pn);
                    processedIndexes.add(index);
                }
            } else {
                //some kind of parsing error or invalud format data
                throw new IllegalArgumentException("Invalid form data posted");
            }
        }
        return ret;
    }

    public static String SignatureToReadable(SignatureType sig) {
        StringBuilder sb = new StringBuilder();
        // X509Certificate signingcert = null;
        //sb.append("Signature Id: ").append(sig.getKeyInfo().getId());
        for (int i = 0; i < sig.getKeyInfo().getContent().size(); i++) {
            sb.append("Signature #").append((i + 1)).append(": ");
            JAXBElement get = (JAXBElement) sig.getKeyInfo().getContent().get(i);

            if (get.getValue() instanceof org.w3._2000._09.xmldsig_.X509DataType) {
                X509DataType xd = (X509DataType) get.getValue();
                for (int k = 0; k < xd.getX509IssuerSerialOrX509SKIOrX509SubjectName().size(); k++) {
                    if (xd.getX509IssuerSerialOrX509SKIOrX509SubjectName().get(k) instanceof JAXBElement) {
                        JAXBElement element = (JAXBElement) xd.getX509IssuerSerialOrX509SKIOrX509SubjectName().get(k);
                        if (element.getValue() instanceof byte[]) {
                            try {
                                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                                InputStream is = new ByteArrayInputStream((byte[]) element.getValue());
                                X509Certificate cert = (X509Certificate) cf.generateCertificate(is);
                                sb.append(cert.getSubjectDN().getName());
                            } catch (Exception ex) {
                            }
                        } else if (element.getValue() instanceof String) {
                            sb.append((String) element.getValue());
                        }
                    }
                }
            }
        }
        return sb.toString();
    }
}
