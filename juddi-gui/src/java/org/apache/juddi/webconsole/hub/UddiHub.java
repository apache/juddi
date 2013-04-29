/*
 * Copyright 2001-2013 The Apache Software Foundation.
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
 *
 */
package org.apache.juddi.webconsole.hub;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.RemoteException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.juddi.v3.client.ClassUtil;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.webconsole.AES;
import org.apache.juddi.webconsole.PostBackConstants;
import org.apache.juddi.webconsole.hub.builders.Builders;
import org.apache.juddi.webconsole.hub.builders.Printers;
import org.apache.juddi.webconsole.resources.ResourceLoader;
import org.apache.log4j.Level;
import org.uddi.api_v3.*;
import org.uddi.custody_v3.DiscardTransferToken;
import org.uddi.custody_v3.TransferEntities;
import org.uddi.custody_v3.TransferToken;
import org.uddi.sub_v3.*;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDICustodyTransferPortType;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;
import org.w3._2000._09.xmldsig_.SignatureType;
import org.w3._2000._09.xmldsig_.X509DataType;
import sun.misc.BASE64Encoder;

/**
 * UddiHub - The hub acts as a single point for managing browser to uddi
 * services. At most 1 instance is allowed per http session. In general, all
 * methods in the class trigger web service call outs
 *
 * @author Alex O'Ree
 */
public class UddiHub {

    public static final String LOGGER_NAME = "org.apache.juddi";
    URL propertiesurl = null;
    Properties properties = null;
    AuthStyle style = null;
    public static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LOGGER_NAME);
    private DatatypeFactory df;

    private UddiHub() throws DatatypeConfigurationException {
        df = DatatypeFactory.newInstance();
    }

    /**
     * removes the Hub from the current http session
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
        } catch (Exception ex) {
            HandleException(ex);
        }

        token = null;
        inquiry = null;
        publish = null;
        custody = null;
        security = null;
        //juddi = null;
        subscription = null;
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
    String locale = "en";

    public String GetRawConfigurationPath() throws URISyntaxException {
        return propertiesurl.toString();
    }

    public Properties GetRawConfiguration() {
        return properties;
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
                subscription = transport.getUDDISubscriptionService();
                publish = transport.getUDDIPublishService();
                custody = transport.getUDDICustodyTransferService();
                //  juddi = transport.getJUDDIApiService();

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


                bp = (BindingProvider) security;
                context = bp.getRequestContext();
                context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, properties.getProperty("securityurl"));

                bp = (BindingProvider) subscription;
                context = bp.getRequestContext();
                context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, properties.getProperty("subscriptionurl"));


                /*bp = (BindingProvider) juddi;
                 context = bp.getRequestContext();
                 context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, properties.getProperty("juddipapi"));*/
            }
        } catch (Exception ex) {
            HandleException(ex);
        }
    }
    private HttpSession session;
    /*
     public boolean IsJuddiRegistry() {
     String type = properties.getProperty("registryType");
     if (type == null) {
     return false;
     }
     if (type.equalsIgnoreCase("juddi")) {
     return true;
     }
     return false;
     }*/

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

            bp = (BindingProvider) subscription;
            context = bp.getRequestContext();
            context.put(BindingProvider.USERNAME_PROPERTY, session.getAttribute("username"));
            context.put(BindingProvider.USERNAME_PROPERTY, session.getAttribute(AES.Decrypt("password", (String) properties.get("key"))));

            /*
             bp = (BindingProvider) juddi;
             context = bp.getRequestContext();
             context.put(BindingProvider.USERNAME_PROPERTY, session.getAttribute("username"));
             context.put(BindingProvider.USERNAME_PROPERTY, session.getAttribute(AES.Decrypt("password", (String) properties.get("key"))));*/
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
                } catch (Exception ex) {
                    return HandleException(ex);
                }
            }
        }
        return token;
    }

    public boolean getUddiIsAuthenticated() {
        return (token != null && !token.isEmpty());
    }
    private UDDISubscriptionPortType subscription = null;
    private UDDISecurityPortType security = null;
    private UDDIInquiryPortType inquiry = null;
    private UDDIPublicationPortType publish = null;
    private UDDICustodyTransferPortType custody = null;
    //private JUDDIApiPortType juddi = null;
    private String token = null;

    public PagableContainer GetBusinessListAsHtml(int offset, int maxrecords, String keyword, String lang, boolean isChooser) {
        PagableContainer ret = new PagableContainer();
        ret.offset = offset;
        ret.displaycount = 0;
        ret.totalrecords = 0;

        try {
            FindBusiness fb = new FindBusiness();
            fb.setMaxRows(maxrecords);
            fb.setListHead(offset);
            fb.setAuthInfo(GetToken());
            org.uddi.api_v3.FindQualifiers fq = new org.uddi.api_v3.FindQualifiers();
            fq.getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);

            fb.setFindQualifiers(fq);
            Name searchname = new Name();
            searchname.setLang(lang);
            searchname.setValue(keyword);
            fb.getName().add(searchname);
            //transport
            BusinessList findBusiness = null;
            try {
                findBusiness = inquiry.findBusiness(fb);
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        fb.setAuthInfo(GetToken());
                        findBusiness = inquiry.findBusiness(fb);
                    }
                }
            }
            if (findBusiness == null || findBusiness.getBusinessInfos() == null) {
                ret.renderedHtml = (ResourceLoader.GetResource(session, "errors.nodatareturned"));
            } else {
                ret.displaycount = findBusiness.getListDescription().getIncludeCount();
                ret.offset = findBusiness.getListDescription().getListHead();
                ret.totalrecords = findBusiness.getListDescription().getActualCount();
                ret.renderedHtml = Printers.BusinessListAsTable(findBusiness, session, isChooser);
            }

        } catch (Exception ex) {
            ret.renderedHtml = (HandleException(ex));
        }

        return ret;
    }

    /**
     * The get_registeredInfo API call is used to get an abbreviated list of all
     * businessEntity and tModel data that are controlled by a publisher. When
     * the registry distinguishes between publishers, this is the individual
     * associated with the credentials passed in the authInfo element. This
     * returned information is intended, for example, for driving tools that
     * display lists of registered information and then provide drill-down
     * features. This is the recommended API to use after a network problem
     * results in an unknown status of saved information.
     *
     * @return
     */
    public String GetMyTransferableKeys(boolean businesses, boolean tModels) {

        StringBuilder sb = new StringBuilder();


        RegisteredInfo findBusiness = null;
        try {
            GetRegisteredInfo r = new GetRegisteredInfo();
            r.setAuthInfo(GetToken());
            if (r.getAuthInfo() == null) {
                return null;
            }
            r.setInfoSelection(InfoSelection.ALL);

            try {
                findBusiness = publish.getRegisteredInfo(r);
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        r.setAuthInfo(GetToken());
                        findBusiness = publish.getRegisteredInfo(r);
                    }
                } else {
                    throw ex;
                }
            }

        } catch (Exception ex) {
            return HandleException(ex);
        }


        if (findBusiness == null || findBusiness.getBusinessInfos() == null) {
            return (ResourceLoader.GetResource(session, "errors.nodatareturned"));

        } else {
            if (findBusiness.getBusinessInfos() != null && businesses) {
                sb.append("<select id=\"businesslist\" multiple=\"multiple\" size=\"10\">");
                for (int i = 0; i < findBusiness.getBusinessInfos().getBusinessInfo().size(); i++) {
                    sb.append("<option class=\"transferable\" id=\"").
                            append(StringEscapeUtils.escapeHtml(findBusiness.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey())).
                            append("\" title=\"").
                            append(StringEscapeUtils.escapeHtml(findBusiness.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey())).
                            append("\">").
                            append(StringEscapeUtils.escapeHtml(Printers.ListNamesToString(findBusiness.getBusinessInfos().getBusinessInfo().get(i).getName()))).
                            append("</option>");
                }
                sb.append("</select>");
            }
            if (findBusiness.getTModelInfos() != null && tModels) {
                sb.append("<select id=\"tmodellist\" multiple=\"multiple\" size=\"10\">");
                for (int i = 0; i < findBusiness.getTModelInfos().getTModelInfo().size(); i++) {
                    sb.append("<option  class=\"transferable\" id=\"").
                            append(StringEscapeUtils.escapeHtml(findBusiness.getTModelInfos().getTModelInfo().get(i).getTModelKey())).
                            append("\" title=\"").
                            append(StringEscapeUtils.escapeHtml(findBusiness.getTModelInfos().getTModelInfo().get(i).getTModelKey())).
                            append("\">").
                            append(StringEscapeUtils.escapeHtml((findBusiness.getTModelInfos().getTModelInfo().get(i).getName().getValue()))).
                            append("</option>");
                }
                sb.append("</select>");
            }

            return sb.toString();
        }

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
            ServiceDetail get = null;//inquiry.getServiceDetail(gbd);
            try {
                get = inquiry.getServiceDetail(gbd);
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        gbd.setAuthInfo(GetToken());
                        get = inquiry.getServiceDetail(gbd);
                    }
                } else {
                    throw ex;
                }
            }
            if (get != null) {
                for (int i = 0; i < get.getBusinessService().size(); i++) {
                    session.setAttribute(get.getBusinessService().get(i).getServiceKey(), get.getBusinessService().get(i));
                    sb.append("<b>").append(ResourceLoader.GetResource(session, "items.name")).append(":</b><div class=\"editable\" id=\"ServiceName\">").append(StringEscapeUtils.escapeHtml(Printers.ListNamesToString(get.getBusinessService().get(i).getName()))).append("</div><Br>");
                    sb.append("<b>").append(ResourceLoader.GetResource(session, "items.description")).append(":</b><div class=\"editable\" id=\"ServiceDescription\">").append(StringEscapeUtils.escapeHtml((Printers.ListToDescString(get.getBusinessService().get(i).getDescription())))).append("</div><Br>");
                    sb.append("<b>").append(ResourceLoader.GetResource(session, "items.key")).append(":</b><div class=\"editable\" id=\"ServiceKey\">").append(StringEscapeUtils.escapeHtml((get.getBusinessService().get(i).getServiceKey()))).append("</div><Br>");
                    sb.append("<b>").append(ResourceLoader.GetResource(session, "items.keyrefcat")).append(":</b> ").append(Printers.CatBagToString(get.getBusinessService().get(i).getCategoryBag(), (String) session.getAttribute("locale"))).append("<Br>");
                    if (!get.getBusinessService().get(i).getSignature().isEmpty()) {
                        sb.append(ResourceLoader.GetResource(session, "items.signed")).append("<Br>");
                    } else {
                        sb.append(ResourceLoader.GetResource(session, "items.signed.not")).append("<Br>");
                    }

                    sb.append(Printers.PrintBindingTemplates(get.getBusinessService().get(i).getBindingTemplates(), (String) session.getAttribute("locale"))).append("<Br>");
                }
            } else {
                sb.append(ResourceLoader.GetResource(session, "errors.nodatareturned"));
            }
        } catch (Exception ex) {
            sb.append(HandleException(ex));
        }
        return sb.toString();
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
            return ResourceLoader.GetResource(session, "errors.nobusinessid");
        }
        StringBuilder sb = new StringBuilder();
        try {
            GetBusinessDetail gbd = new GetBusinessDetail();
            gbd.setAuthInfo(GetToken());
            gbd.getBusinessKey().add(bizid);
            BusinessDetail businessDetail = null;
            try {
                businessDetail = inquiry.getBusinessDetail(gbd);
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        gbd.setAuthInfo(GetToken());
                        businessDetail = inquiry.getBusinessDetail(gbd);
                    }
                } else {
                    throw ex;
                }
            }
            if (businessDetail != null) {
                for (int i = 0; i < businessDetail.getBusinessEntity().size(); i++) {
                    if (businessDetail.getBusinessEntity().get(i).getBusinessServices() == null) {
                        sb.append(ResourceLoader.GetResource(session, "errors.noservicesdefined"));
                    } else {
                        for (int k = 0; k < businessDetail.getBusinessEntity().get(i).getBusinessServices().getBusinessService().size(); k++) {
                            sb.append("<div><a href=\"serviceEditor.jsp?id=").
                                    append(StringEscapeUtils.escapeHtml(businessDetail.getBusinessEntity().get(i).getBusinessServices().getBusinessService().get(k).getServiceKey())).append("\">").
                                    append(StringEscapeUtils.escapeHtml(Printers.ListNamesToString(businessDetail.getBusinessEntity().get(i).getBusinessServices().getBusinessService().get(k).getName()))).append("</a></div>");
                        }
                    }
                }
            } else {
                sb.append(ResourceLoader.GetResource(session, "errors.nodatareturned"));
            }
        } catch (Exception ex) {
            sb.append(ResourceLoader.GetResource(session, "errors.generic")).append(ex.getMessage());
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
            ServiceDetail get = null;
            try {
                get = inquiry.getServiceDetail(gbd);
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        gbd.setAuthInfo(GetToken());
                        get = inquiry.getServiceDetail(gbd);
                    }
                } else {
                    throw ex;
                }
            }

            if (get == null || get.getBusinessService().isEmpty()) {
                return null;
            }
            return get.getBusinessService().get(0);

        } catch (Exception ex) {
            HandleException(ex);
        }
        return null;
    }

    public String SaveService(BusinessService be) {
        try {
            SaveService sb = new SaveService();
            sb.setAuthInfo(GetToken());
            sb.getBusinessService().add(be);
            try {
                publish.saveService(sb);
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        sb.setAuthInfo(GetToken());
                        publish.saveService(sb);
                    }
                } else {
                    throw ex;
                }
            }

            return ResourceLoader.GetResource(session, "actions.saved");
        } catch (Exception ex) {
            return HandleException(ex);
        }
    }

    /**
     * don't think this is used yet
     *
     * @param be
     * @return
     */
    @Deprecated
    public String SaveBindingTemplate(BindingTemplate be) {
        try {
            SaveBinding sb = new SaveBinding();
            sb.setAuthInfo(GetToken());
            sb.getBindingTemplate().add(be);
            try {
                publish.saveBinding(sb);
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        sb.setAuthInfo(GetToken());
                        publish.saveBinding(sb);
                    }
                } else {
                    throw ex;
                }
            }
            return ResourceLoader.GetResource(session, "actions.save.bindingtemplate");
        } catch (Exception ex) {
            return HandleException(ex);
        }
    }

    public String SaveServiceDetails(HttpServletRequest request) {

        BusinessService be = new BusinessService();
        be.setBusinessKey(request.getParameter(PostBackConstants.BUSINESSKEY).trim());
        be.setServiceKey(request.getParameter(PostBackConstants.SERVICEKEY).trim());

        if (be.getServiceKey().equalsIgnoreCase(ResourceLoader.GetResource(session, "items.clicktoedit"))) {
            be.setServiceKey(null);
        }
        if (be.getBusinessKey() == null || be.getBusinessKey().length() == 0) {
            return ResourceLoader.GetResource(session, "errors.noinput.businesskey");
        }

        be.getName().addAll(Builders.BuildNames(Builders.MapFilter(request.getParameterMap(), PostBackConstants.NAME), PostBackConstants.NAME, ResourceLoader.GetResource(session, "items.clicktoedit")));
        BindingTemplates bt = new BindingTemplates();
        bt.getBindingTemplate().addAll(Builders.BuildBindingTemplates(Builders.MapFilter(request.getParameterMap(), PostBackConstants.BINDINGTEMPLATE), PostBackConstants.BINDINGTEMPLATE, ResourceLoader.GetResource(session, "items.clicktoedit")));
        if (!bt.getBindingTemplate().isEmpty()) {
            be.setBindingTemplates(bt);
        }

        be.getDescription().addAll(Builders.BuildDescription(Builders.MapFilter(request.getParameterMap(), PostBackConstants.DESCRIPTION), PostBackConstants.DESCRIPTION, ResourceLoader.GetResource(session, "items.clicktoedit")));

        CategoryBag cb = new CategoryBag();
        cb.getKeyedReference().addAll(Builders.BuildKeyedReference(Builders.MapFilter(request.getParameterMap(), PostBackConstants.CATBAG_KEY_REF), PostBackConstants.CATBAG_KEY_REF));
        cb.getKeyedReferenceGroup().addAll(Builders.BuildKeyedReferenceGroup(Builders.MapFilter(request.getParameterMap(), PostBackConstants.CATBAG_KEY_REF_GRP), PostBackConstants.CATBAG_KEY_REF_GRP));

        if (!cb.getKeyedReference().isEmpty() || !cb.getKeyedReferenceGroup().isEmpty()) {
            be.setCategoryBag(cb);
        }

        return SaveServiceDetails(be);
    }

    public String SaveServiceDetails(BusinessService be) {
        try {
            SaveService sb = new SaveService();
            sb.setAuthInfo(GetToken());
            sb.getBusinessService().add(be);
            try {
                publish.saveService(sb);
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        sb.setAuthInfo(GetToken());
                        publish.saveService(sb);
                    }
                } else {
                    throw ex;
                }
            }
            return ResourceLoader.GetResource(session, "actions.save.service");
        } catch (Exception ex) {
            return HandleException(ex);
        }
    }

    /**
     * Saves a business entity
     *
     * @param be
     * @return
     */
    public String SaveBusinessDetails(BusinessEntity be) {
        try {
            SaveBusiness sb = new SaveBusiness();
            sb.setAuthInfo(GetToken());
            sb.getBusinessEntity().add(be);
            try {
                publish.saveBusiness(sb);
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        sb.setAuthInfo(GetToken());
                        publish.saveBusiness(sb);
                    }
                } else {
                    throw ex;
                }
            }

            return ResourceLoader.GetResource(session, "actions.saved");
        } catch (Exception ex) {
            return HandleException(ex);
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
        if (be.getBusinessKey().equalsIgnoreCase(ResourceLoader.GetResource(session, "items.clicktoedit"))) {
            be.setBusinessKey(null);
        }
        be.getName().addAll(Builders.BuildNames(Builders.MapFilter(request.getParameterMap(), PostBackConstants.NAME), PostBackConstants.NAME, ResourceLoader.GetResource(session, "items.clicktoedit")));
        if (GetBusinessDetails == null) //this is a new business
        {
        } else {
            be.setBusinessServices(GetBusinessDetails.getBusinessServices());
        }

        be.setContacts(Builders.BuildContacts(request.getParameterMap(), ResourceLoader.GetResource(session, "items.clicktoedit")));

        be.getDescription().addAll(Builders.BuildDescription(Builders.MapFilter(request.getParameterMap(), PostBackConstants.DESCRIPTION), PostBackConstants.DESCRIPTION, ResourceLoader.GetResource(session, "items.clicktoedit")));
        be.setDiscoveryURLs(Builders.BuildDisco(Builders.MapFilter(request.getParameterMap(), PostBackConstants.DISCOVERYURL), PostBackConstants.DISCOVERYURL));
        CategoryBag cb = new CategoryBag();
        cb.getKeyedReference().addAll(Builders.BuildKeyedReference(Builders.MapFilter(request.getParameterMap(), PostBackConstants.CATBAG_KEY_REF), PostBackConstants.CATBAG_KEY_REF));
        cb.getKeyedReferenceGroup().addAll(Builders.BuildKeyedReferenceGroup(Builders.MapFilter(request.getParameterMap(), PostBackConstants.CATBAG_KEY_REF_GRP), PostBackConstants.CATBAG_KEY_REF_GRP));

        if (!cb.getKeyedReference().isEmpty() || !cb.getKeyedReferenceGroup().isEmpty()) {
            be.setCategoryBag(cb);
        }
        be.setIdentifierBag(Builders.BuildIdentBag(Builders.MapFilter(request.getParameterMap(), PostBackConstants.IDENT_KEY_REF), PostBackConstants.IDENT_KEY_REF));
        return SaveBusinessDetails(be);
    }

    public String GetBusinessDetailsAsHtml(String bizid) throws Exception {
        if (bizid == null || bizid.isEmpty()) {
            return ResourceLoader.GetResource(session, "errors.noinput.businesskey");
        }
        StringBuilder sb = new StringBuilder();
        try {
            GetBusinessDetail gbd = new GetBusinessDetail();
            gbd.setAuthInfo(GetToken());

            gbd.getBusinessKey().add(bizid);

            BusinessDetail businessDetail = null;

            try {
                businessDetail = inquiry.getBusinessDetail(gbd);
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        gbd.setAuthInfo(GetToken());
                        businessDetail = inquiry.getBusinessDetail(gbd);
                    }
                } else {
                    throw ex;
                }
            }
            if (businessDetail != null) {
                for (int i = 0; i < businessDetail.getBusinessEntity().size(); i++) {
                    sb.append("Business Detail - key: ").append(businessDetail.getBusinessEntity().get(i).getBusinessKey()).append("<br>");
                    sb.append(ResourceLoader.GetResource(session, "items.name"));
                    sb.append(": ").append(Printers.ListNamesToString(businessDetail.getBusinessEntity().get(i).getName())).append("<br>");
                    sb.append(ResourceLoader.GetResource(session, "items.description"));
                    sb.append(": ").append(Printers.ListToDescString(businessDetail.getBusinessEntity().get(i).getDescription())).append("<br>");
                    sb.append(ResourceLoader.GetResource(session, "items.discoveryurl"));
                    sb.append(": ").append(Printers.ListDiscoToString(businessDetail.getBusinessEntity().get(i).getDiscoveryURLs())).append("<br>");
                    sb.append(ResourceLoader.GetResource(session, "items.identifiers"));
                    sb.append(": ").append(Printers.ListIdentBagToString(businessDetail.getBusinessEntity().get(i).getIdentifierBag(), (String) session.getAttribute("locale"))).append("<br>");
                    sb.append(ResourceLoader.GetResource(session, "items.keyrefcats"));
                    sb.append(": ").append(Printers.CatBagToString(businessDetail.getBusinessEntity().get(i).getCategoryBag(), (String) session.getAttribute("locale"))).append("<br>");
                    Printers.PrintContacts(businessDetail.getBusinessEntity().get(i).getContacts(), (String) session.getAttribute("locale"));
                }
            } else {
                sb.append(ResourceLoader.GetResource(session, "errors.nodatareturned"));
            }
        } catch (Exception ex) {

            sb.append(HandleException(ex));
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

            BusinessDetail businessDetail = null;
            try {
                businessDetail = inquiry.getBusinessDetail(gbd);
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        gbd.setAuthInfo(GetToken());
                        businessDetail = inquiry.getBusinessDetail(gbd);
                    }
                } else {
                    throw ex;
                }
            }
            if (businessDetail != null && businessDetail.getBusinessEntity().size() == 1) {
                return businessDetail.getBusinessEntity().get(0);
            }
        } catch (Exception ex) {
            HandleException(ex);
        }
        return null;

    }

    public enum AuthStyle {

        HTTP_BASIC,
        HTTP_DIGEST,
        HTTP_NTLM,
        UDDI_AUTH,
        HTTP_CLIENT_CERT
    }

    public PagableContainer SearchForServices(String keyword, String lang, int maxrecords, int offset, boolean isChooser) {
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
            if (lang == null || lang.equalsIgnoreCase(ResourceLoader.GetResource(session, "items.clicktoedit"))) {
                n.setLang(null);
            } else {
                n.setLang(lang);
            }
            n.setValue(keyword);
            fs.getName().add(n);
            fs.setFindQualifiers(new org.uddi.api_v3.FindQualifiers());
            fs.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
            ServiceList findService = null;//inquiry.findService(fs);
            try {
                findService = inquiry.findService(fs);
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        fs.setAuthInfo(GetToken());
                        findService = inquiry.findService(fs);
                    }
                } else {
                    throw ex;
                }
            }

            if (findService == null || findService.getServiceInfos() == null) {
                ret.renderedHtml = ResourceLoader.GetResource(session, "errors.norecordsfound");
                return ret;
            }
            ret.displaycount = findService.getListDescription().getIncludeCount();
            ret.totalrecords = findService.getListDescription().getActualCount();
            ret.renderedHtml = Printers.ServiceListAsHtml(findService, isChooser, session);

            //  ret.renderedHtml = sb.toString();
            return ret;
        } catch (Exception ex) {
            ret.renderedHtml = HandleException(ex);
        }
        return ret;

    }

    /**
     * not used yet?
     *
     * @param request
     * @return
     * @ deprecated
     *
     * @ Deprecated public String AddPublisher(HttpServletRequest request) { try
     * { SavePublisher sp = new SavePublisher(); sp.setAuthInfo(GetToken());
     * Publisher p = new Publisher(); //TODO code sp.getPublisher().add(p);
     * PublisherDetail savePublisher = juddi.savePublisher(sp); return
     * "Success"; //TODO resource this } catch (Exception ex) { return
     * HandleException(ex); }
     *
     *
     * }
     */
    /**
     * returns an html listing of Juddi authorized publishers
     *
     * @return
     *
     * public String GetPublisherListAsHtml() { if (!this.IsJuddiRegistry()) {
     * return "This function is only available on Juddi registries"; } try {
     * GetAllPublisherDetail gpd = new GetAllPublisherDetail();
     * gpd.setAuthInfo(GetToken()); PublisherDetail allPublisherDetail =
     * juddi.getAllPublisherDetail(gpd); StringBuilder sb = new StringBuilder();
     * for (int i = 0; i < allPublisherDetail.getPublisher().size(); i++) {
     * sb.append(ResourceLoader.GetResource(session, "items.pubisher.name"))
     * .append(" =
     * ").append(allPublisherDetail.getPublisher().get(i).getPublisherName()).append("<br>");
     * sb.append(ResourceLoader.GetResource(session,
     * "items.pubisher.authname")).append(" =
     * ").append(allPublisherDetail.getPublisher().get(i).getAuthorizedName()).append("<br>");
     * sb.append(ResourceLoader.GetResource(session, "items.email"));
     * sb.append(" =
     * ").append(allPublisherDetail.getPublisher().get(i).getEmailAddress()).append("<br>");
     * sb.append(ResourceLoader.GetResource(session, "items.publisher.admin"))
     * .append(" =
     * ").append(allPublisherDetail.getPublisher().get(i).getIsAdmin()).append("<br>");
     * sb.append("Is Enabled? =
     * ").append(allPublisherDetail.getPublisher().get(i).getIsEnabled()).append("<br>");
     * sb.append("Max bindings per service =
     * ").append(allPublisherDetail.getPublisher().get(i).getMaxBindingsPerService()).append("<br>");
     * sb.append("Max businesses =
     * ").append(allPublisherDetail.getPublisher().get(i).getMaxBusinesses()).append("<br>");
     * sb.append("Max Services per Business =
     * ").append(allPublisherDetail.getPublisher().get(i).getMaxServicePerBusiness()).append("<br>");
     * sb.append("Max tModels =
     * ").append(allPublisherDetail.getPublisher().get(i).getMaxTModels()).append("<br><br>");
     *
     * }
     * return sb.toString(); } catch (Exception ex) { return
     * HandleException(ex); } }
     */
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
                return ResourceLoader.GetResource(session, "errors.tmodel.prefix");

            }
            if (!partitionName.endsWith(":keyGenerator")) {
                return ResourceLoader.GetResource(session, "errors.tmodel.postfix");
            }


            SaveTModel st = new SaveTModel();
            st.setAuthInfo(GetToken());
            TModel tm = new TModel();
            tm.setName(new Name());
            tm.getName().setValue(name);
            if (lang == null || lang.equalsIgnoreCase(ResourceLoader.GetResource(session, "items.clicktoedit"))) {
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
            return ResourceLoader.GetResource(session, "messages.success");
            // "Success";
        } catch (Exception ex) {
            return HandleException(ex);
        }
    }

    String HandleException(Exception ex) {
        if (ex instanceof DispositionReportFaultMessage) {
            DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
            log.log(Level.ERROR, null, ex);
            return ResourceLoader.GetResource(session, "errors.uddi") + " " + ex.getMessage() + " " + f.detail.getMessage();
        }
        if (ex instanceof RemoteException) {
            RemoteException f = (RemoteException) ex;
            log.log(Level.ERROR, null, ex);
            return ResourceLoader.GetResource(session, "errors.generic") + " " + ex.getMessage() + " " + f.detail.getMessage();
        }
        log.log(Level.ERROR, null, ex);
        return ResourceLoader.GetResource(session, "errors.generic") + " " + ex.getMessage();
    }

    /**
     * provides based tmodel searching/browser capability that's pagable
     *
     * @param keyword
     * @param lang
     * @param offset
     * @param maxrecords
     * @param isChooser if true, tModel keys will not be clickable and will
     * instead be render for a modal dialog box
     * @return
     */
    public PagableContainer tModelListAsHtml(String keyword, String lang, int offset, int maxrecords, boolean isChooser) {
        PagableContainer ret = new PagableContainer();
        try {
            FindTModel fm = new FindTModel();
            fm.setAuthInfo(GetToken());
            fm.setMaxRows(maxrecords);
            fm.setListHead(offset);
            fm.setName(new Name());
            if (lang == null || lang.equalsIgnoreCase(ResourceLoader.GetResource(session, "items.clicktoedit"))) {
                fm.getName().setLang(null);
            } else {
                fm.getName().setLang(lang);
            }
            fm.getName().setValue(keyword);
            fm.setFindQualifiers(new org.uddi.api_v3.FindQualifiers());
            fm.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
            TModelList findTModel = null;
            try {
                findTModel = inquiry.findTModel(fm);
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        fm.setAuthInfo(GetToken());
                        findTModel = inquiry.findTModel(fm);
                    }
                } else {
                    throw ex;
                }
            }

            ret.offset = offset;
            ret.displaycount = findTModel.getListDescription().getIncludeCount();
            ret.totalrecords = findTModel.getListDescription().getActualCount();
            if (findTModel == null || findTModel.getTModelInfos() == null || findTModel.getTModelInfos().getTModelInfo().isEmpty()) {
                ret.renderedHtml = ResourceLoader.GetResource(session, "errors.norecordsfound");//"No tModels are defined";
            } else {
                // if (!isChooser) {
                ret.renderedHtml = Printers.PrintTModelListAsHtml(findTModel, session, isChooser);
                // } else {
                //     ret.renderedHtml = Printers.PrintTModelListAsHtmlModel(findTModel, session);
                // }

            }
        } catch (Exception ex) {
            ret.renderedHtml = HandleException(ex);
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
            TModelDetail tModelDetail = null;
            try {
                tModelDetail = inquiry.getTModelDetail(req);

            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        req.setAuthInfo(GetToken());
                        tModelDetail = inquiry.getTModelDetail(req);

                    }
                } else {
                    throw ex;
                }
            }

            if (tModelDetail != null && !tModelDetail.getTModel().isEmpty()) {
                return tModelDetail.getTModel().get(0);
            }

        } catch (Exception ex) {
            HandleException(ex);
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
            BindingDetail bindingDetail = null;
            try {
                bindingDetail = inquiry.getBindingDetail(r);
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        r.setAuthInfo(GetToken());
                        bindingDetail = inquiry.getBindingDetail(r);
                    }
                } else {
                    throw ex;
                }
            }
            return bindingDetail.getBindingTemplate().get(0);
        } catch (Exception ex) {
            HandleException(ex);
        }
        return null;
    }

    public TModel GettModelDetailsAsObject(String key) {
        try {
            GetTModelDetail r = new GetTModelDetail();
            r.setAuthInfo(GetToken());
            r.getTModelKey().add(key);
            TModelDetail tModelDetail = null;
            try {
                tModelDetail = inquiry.getTModelDetail(r);
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        r.setAuthInfo(GetToken());
                        tModelDetail = inquiry.getTModelDetail(r);
                    }
                } else {
                    throw ex;
                }
            }

            return tModelDetail.getTModel().get(0);
        } catch (Exception ex) {
            HandleException(ex);
        }
        return null;
    }

    public enum FindType {

        Business, RelatedBusiness, Service, tModel, BindingTemplate
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
                try {
                    findBusiness = inquiry.findBinding(fb);
                } catch (Exception ex) {
                    if (ex instanceof DispositionReportFaultMessage) {
                        DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                        if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                            token = null;
                            fb.setAuthInfo(GetToken());
                            findBusiness = inquiry.findBinding(fb);
                        }
                    } else {
                        throw ex;
                    }
                }

            }
            if (findBusiness != null && findBusiness.getBindingTemplate() != null) {
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
                        sb.append(StringEscapeUtils.escapeHtml(Printers.ListToDescString(findBusiness.getBindingTemplate().get(i).getDescription())));
                    }
                    sb.append("</a>");
                    sb.append("</td></tr>");
                }
                sb.append("</table>");
                return sb.toString();
            } else {
                return ResourceLoader.GetResource(session, "errors.norecordsfound");
            }
        } catch (Exception ex) {
            return HandleException(ex);
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

                try {
                    findBusiness = inquiry.findBusiness(fb);
                } catch (Exception ex) {
                    if (ex instanceof DispositionReportFaultMessage) {
                        DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                        if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                            token = null;
                            fb.setAuthInfo(GetToken());
                            findBusiness = inquiry.findBusiness(fb);
                        }
                    } else {
                        throw ex;
                    }
                }
            }
            if (findBusiness != null && findBusiness.getBusinessInfos() != null) {
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
                        sb.append(StringEscapeUtils.escapeHtml(Printers.ListNamesToString(findBusiness.getBusinessInfos().getBusinessInfo().get(i).getName())));
                    }
                    sb.append("</a>");
                    sb.append("</td></tr>");
                }
                sb.append("</table>");
                return sb.toString();
            } else {
                return ResourceLoader.GetResource(session, "errors.norecordsfound");
            }
        } catch (Exception ex) {
            return HandleException(ex);
        }
    }

    private String FindRelatedBusiness(CriteriaType criteria, String parameters, String lang, String[] fq) {
        try {
            FindRelatedBusinesses fb = new FindRelatedBusinesses();
            fb.setAuthInfo(GetToken());
            fb.setBusinessKey(parameters);
            RelatedBusinessesList findBusiness = null;
            switch (criteria) {
                case Category:
                    break;
                case Name:
                    break;
                case tmodel:
                    break;
                case uid:
                    break;
            }

            try {
                findBusiness = inquiry.findRelatedBusinesses(fb);
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        fb.setAuthInfo(GetToken());
                        findBusiness = inquiry.findRelatedBusinesses(fb);
                    }
                } else {
                    throw ex;
                }
            }


            if (findBusiness != null && findBusiness.getRelatedBusinessInfos() != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("<table class=\"table\">");
                for (int i = 0; i < findBusiness.getRelatedBusinessInfos().getRelatedBusinessInfo().size(); i++) {
                    sb.append("<tr><td>");
                    sb.append("<a href=\"businessEditor2.jsp?id=").
                            append(StringEscapeUtils.escapeHtml(findBusiness.getRelatedBusinessInfos().getRelatedBusinessInfo().get(i).getBusinessKey())).
                            append("\">");
                    if (findBusiness.getRelatedBusinessInfos().getRelatedBusinessInfo().get(i).getName().isEmpty()) {
                        sb.append(StringEscapeUtils.escapeHtml(findBusiness.getRelatedBusinessInfos().getRelatedBusinessInfo().get(i).getBusinessKey()));
                    } else {
                        sb.append(StringEscapeUtils.escapeHtml(Printers.ListNamesToString(findBusiness.getRelatedBusinessInfos().getRelatedBusinessInfo().get(i).getName())));
                    }
                    sb.append("</a>");
                    sb.append("</td></tr>");
                }
                sb.append("</table>");
                return sb.toString();
            } else {
                return ResourceLoader.GetResource(session, "errors.norecordsfound");
            }
        } catch (Exception ex) {
            return HandleException(ex);
        }
    }

    private String FindService(CriteriaType criteria, String parameters, String lang, String[] fq) {
        try {
            FindService fb = new FindService();
            fb.setAuthInfo(GetToken());
            if (fq != null) {
                fb.setFindQualifiers(new org.uddi.api_v3.FindQualifiers());
                if (fq != null) {
                    fb.getFindQualifiers().getFindQualifier().addAll(Arrays.asList(fq));
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
                try {
                    findBusiness = inquiry.findService(fb);
                } catch (Exception ex) {
                    if (ex instanceof DispositionReportFaultMessage) {
                        DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                        if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                            token = null;
                            fb.setAuthInfo(GetToken());
                            findBusiness = inquiry.findService(fb);
                        }
                    } else {
                        throw ex;
                    }
                }

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
                        sb.append(StringEscapeUtils.escapeHtml(Printers.ListNamesToString(findBusiness.getServiceInfos().getServiceInfo().get(i).getName())));
                    }
                    sb.append("</a>");
                    sb.append("</td></tr>");
                }
                sb.append("</table>");
                return sb.toString();
            } else {
                return ResourceLoader.GetResource(session, "errors.norecordsfound");
            }
        } catch (Exception ex) {
            return HandleException(ex);
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
                try {
                    findBusiness = inquiry.findTModel(fb);
                } catch (Exception ex) {
                    if (ex instanceof DispositionReportFaultMessage) {
                        DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                        if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                            token = null;
                            fb.setAuthInfo(GetToken());
                            findBusiness = inquiry.findTModel(fb);
                        }
                    } else {
                        throw ex;
                    }
                }

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
                return ResourceLoader.GetResource(session, "errors.norecordsfound");
            }
        } catch (Exception ex) {
            return HandleException(ex);
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
            return ResourceLoader.GetResource(session, "errors.noinput");
        }
        List<String> x = new ArrayList<String>();
        x.add(bizid.trim());
        return deleteBusiness(x);
    }

    public String deleteService(String serviceId) {
        if (serviceId == null || serviceId.length() == 0) {
            return ResourceLoader.GetResource(session, "errors.noinput");
        }
        List<String> x = new ArrayList<String>();
        x.add(serviceId.trim());
        return deleteService(x);
    }

    public String deleteService(List<String> serviceId) {
        if (serviceId == null || serviceId.isEmpty()) {
            return ResourceLoader.GetResource(session, "errors.noinput");
        }
        DeleteService db = new DeleteService();
        db.setAuthInfo(GetToken());
        db.getServiceKey().addAll(serviceId);
        try {
            try {
                publish.deleteService(db);
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        db.setAuthInfo(GetToken());
                        publish.deleteService(db);
                    }
                } else {
                    throw ex;
                }
            }
        } catch (Exception ex) {
            return HandleException(ex);
        }
        return ResourceLoader.GetResource(session, "actions.delete.service");
    }

    /**
     * Deletes a list of UDDI businesses by key
     *
     * @param bizid
     * @return null if successful, otherwise an error message
     */
    public String deleteBusiness(List<String> bizid) {
        if (bizid == null || bizid.isEmpty()) {
            return ResourceLoader.GetResource(session, "errors.noinput");
        }
        DeleteBusiness db = new DeleteBusiness();
        db.setAuthInfo(GetToken());
        db.getBusinessKey().addAll(bizid);
        try {
            try {
                publish.deleteBusiness(db);
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        db.setAuthInfo(GetToken());
                        publish.deleteBusiness(db);
                    }
                } else {
                    throw ex;
                }
            }
        } catch (Exception ex) {
            return HandleException(ex);
        }
        return ResourceLoader.GetResource(session, "actions.delete.business");
    }

    public String deleteTmodel(String bizid) {
        if (bizid == null || bizid.length() == 0) {
            return ResourceLoader.GetResource(session, "errors.noinput");
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
            return ResourceLoader.GetResource(session, "errors.noinput");
        }
        DeleteTModel db = new DeleteTModel();
        db.setAuthInfo(GetToken());
        db.getTModelKey().addAll(bizid);
        try {
            try {
                publish.deleteTModel(db);
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        db.setAuthInfo(GetToken());
                        publish.deleteTModel(db);
                    }
                } else {
                    throw ex;
                }
            }
        } catch (Exception ex) {
            return HandleException(ex);
        }
        return ResourceLoader.GetResource(session, "actions.delete.tmodel");
    }

    /**
     * saves a tmodel object
     *
     * @param be
     * @return
     */
    public String SaveTModel(TModel be) {
        try {
            SaveTModel sb = new SaveTModel();
            sb.setAuthInfo(GetToken());

            sb.getTModel().add(be);
            JAXB.marshal(be, System.out);
            try {
                publish.saveTModel(sb);
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        sb.setAuthInfo(GetToken());
                        publish.saveTModel(sb);
                    }
                } else {
                    throw ex;
                }
            }
            return ResourceLoader.GetResource(session, "actions.saved");
        } catch (Exception ex) {
            return HandleException(ex);
        }
    }

    /**
     * This rebuild a tmodel from the http request, such as from the tmodel
     * editor page
     *
     * @param request
     * @return
     */
    public String SaveTModel(HttpServletRequest request) {

        TModel be = new TModel();
        be.setTModelKey(request.getParameter(PostBackConstants.SERVICEKEY).trim());
        if (be.getTModelKey() != null && (be.getTModelKey().equalsIgnoreCase(ResourceLoader.GetResource(session, "items.clicktoedit")))
                || be.getTModelKey().length() == 0) {
            be.setTModelKey(null);
        }
        be.setName(new Name());
        String t = request.getParameter(PostBackConstants.NAME + PostBackConstants.VALUE);
        if (t != null && !t.equalsIgnoreCase(ResourceLoader.GetResource(session, "items.clicktoedit")) && t.length() > 0) {
            be.getName().setValue(t);
        }
        t = request.getParameter(PostBackConstants.NAME + PostBackConstants.LANG);
        if (t != null && !t.equalsIgnoreCase(ResourceLoader.GetResource(session, "items.clicktoedit")) && t.length() > 0) {
            be.getName().setLang(t);
        }

        t = request.getParameter(PostBackConstants.TMODEL_DELETED);
        if (t != null) {
            if (t.equalsIgnoreCase("checked")) {
                be.setDeleted(Boolean.TRUE);
            }
        }
        if (!be.isDeleted()) {
            be.setDeleted(Boolean.FALSE);
        }


        //TODO signature

        be.getDescription().addAll(Builders.BuildDescription(Builders.MapFilter(request.getParameterMap(), PostBackConstants.DESCRIPTION), PostBackConstants.DESCRIPTION, ResourceLoader.GetResource(session, "items.clicktoedit")));
        be.getOverviewDoc().addAll(Builders.BuildOverviewDocs(Builders.MapFilter(request.getParameterMap(), PostBackConstants.OVERVIEW), PostBackConstants.OVERVIEW, ResourceLoader.GetResource(session, "items.clicktoedit")));

//            be.setDiscoveryURLs(BuildDisco(MapFilter(request.getParameterMap(), PostBackConstants.DISCOVERYURL), PostBackConstants.DISCOVERYURL));
        CategoryBag cb = new CategoryBag();
        cb.getKeyedReference().addAll(Builders.BuildKeyedReference(Builders.MapFilter(request.getParameterMap(), PostBackConstants.CATBAG_KEY_REF), PostBackConstants.CATBAG_KEY_REF));
        cb.getKeyedReferenceGroup().addAll(Builders.BuildKeyedReferenceGroup(Builders.MapFilter(request.getParameterMap(), PostBackConstants.CATBAG_KEY_REF_GRP), PostBackConstants.CATBAG_KEY_REF_GRP));

        if (!cb.getKeyedReference().isEmpty() || !cb.getKeyedReferenceGroup().isEmpty()) {
            be.setCategoryBag(cb);
        }
        be.setIdentifierBag(Builders.BuildIdentBag(Builders.MapFilter(request.getParameterMap(), PostBackConstants.IDENT_KEY_REF), PostBackConstants.IDENT_KEY_REF));

        JAXB.marshal(be, System.out);
        return SaveTModel(be);

    }

    public static String SignatureToReadable(SignatureType sig) {
        StringBuilder sb = new StringBuilder();
        // X509Certificate signingcert = null;
        //sb.append("Signature Id: ").append(sig.getKeyInfo().getId());
        for (int i = 0; i < sig.getKeyInfo().getContent().size(); i++) {
            //sb.append("Signature #").append((i + 1)).append(": ");
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
                                is.close();
                                sb.append(cert.getSubjectDN().getName());
                            } catch (Exception ex) {
                            }
                        } else if (element.getValue() instanceof String) {
                            // sb.append((String) element.getValue());
                        }
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * converts a UDDI Signature Type element into a base64 string containing
     * the raw data for the signing certificate, if present
     *
     * @param sig
     * @return
     */
    public String SignatureToBase64(SignatureType sig) {
        if (sig == null) {
            return "Error, the signature was nullavailable";
        }
        for (int i = 0; i < sig.getKeyInfo().getContent().size(); i++) {
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
                                is.close();
                                //this is the most supportable way to do this
                                BASE64Encoder encoder = new BASE64Encoder();
                                return encoder.encodeBuffer(cert.getEncoded());

                            } catch (Exception ex) {
                                return HandleException(ex);
                            }
                        } else if (element.getValue() instanceof String) {
                        }
                    }
                }
            }
        }
        return ResourceLoader.GetResource(session, "errors.nocertavaiable");
    }

    /**
     * Retrieves the UDDI entity, then attempts to return a base64 encoded
     * certificate of the N'th indexed signature
     *
     * @param ft The type of item requested
     * @param id the unique identifier of the entity
     * @param index the offset of the signature, starting at 0
     * @return the base64 encoded certification, omitting headers and footers OR
     * "Error" with an error message
     */
    public String GetCertificate(FindType ft, String id, int index) {
        SignatureType st = null;
        switch (ft) {
            case BindingTemplate:
                BindingTemplate GetBindingDetailsAsObject = GetBindingDetailsAsObject(id);
                if (GetBindingDetailsAsObject != null) {
                    return SignatureToBase64(GetBindingDetailsAsObject.getSignature().get(index));
                }
                break;
            case Business:
                BusinessEntity GetBusinessDetailsAsObject = GetBusinessDetailsAsObject(id);
                if (GetBusinessDetailsAsObject != null) {
                    return SignatureToBase64(GetBusinessDetailsAsObject.getSignature().get(index));
                }
                break;
            case Service:
                BusinessService GetServiceDetailsAsObject = GetServiceDetailsAsObject(id);
                if (GetServiceDetailsAsObject != null) {
                    return SignatureToBase64(GetServiceDetailsAsObject.getSignature().get(index));
                }
                break;
            case tModel:
                TModel GettModelDetailsAsObject = GettModelDetailsAsObject(id);
                if (GettModelDetailsAsObject != null) {
                    return SignatureToBase64(GettModelDetailsAsObject.getSignature().get(index));
                }
                break;

        }
        return ResourceLoader.GetResource(session, "errors.unknownentity");
    }

    /**
     *
     * @return null if there's an error
     */
    public List<Subscription> GetSubscriptions() {
        try {
            try {
                return subscription.getSubscriptions(GetToken());
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        return subscription.getSubscriptions(GetToken());
                    }
                } else {
                    throw ex;
                }
            }
        } catch (Exception ex) {
            HandleException(ex);
        }
        return null;
    }

    public String AddSubscription(Subscription sub) {
        Holder<List<Subscription>> data = new Holder<List<Subscription>>();
        data.value = new ArrayList<Subscription>();
        data.value.add(sub);
        try {
            try {
                subscription.saveSubscription(GetToken(), data);
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        subscription.saveSubscription(GetToken(), data);
                    }
                } else {
                    throw ex;
                }
            }
        } catch (Exception ex) {
            HandleException(ex);
        }
        return null;
    }

    public String RemoveSubscription(String key) {
        DeleteSubscription ds = new DeleteSubscription();
        ds.setAuthInfo(GetToken());
        ds.getSubscriptionKey().add(key);
        try {
            try {
                subscription.deleteSubscription(ds);
                return ResourceLoader.GetResource(session, "actions.deleted");
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        ds.setAuthInfo(GetToken());
                        subscription.deleteSubscription(ds);
                        return ResourceLoader.GetResource(session, "actions.deleted");
                    }
                } else {
                    throw ex;
                }
            }
        } catch (Exception ex) {
            return HandleException(ex);
        }
        return null;
    }

    /**
     *
     * @param ft
     * @param id
     * @return null if theres an error
     */
    public List<OperationalInfo> GetOperationalInfo(String id) {
        if (id == null) {
            return null;
        }
        GetOperationalInfo goi = new GetOperationalInfo();
        goi.setAuthInfo(GetToken());
        goi.getEntityKey().add(id);
        OperationalInfos operationalInfo = null;
        try {
            try {
                operationalInfo = inquiry.getOperationalInfo(goi);
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        goi.setAuthInfo(GetToken());
                        operationalInfo = inquiry.getOperationalInfo(goi);
                    }
                } else {
                    throw ex;
                }
            }

            return operationalInfo.getOperationalInfo();
        } catch (Exception ex) {
            HandleException(ex);
        }
        return null;
    }

    /**
     * GetOperationalInfo
     *
     * @param info
     * @return
     */
    public String GetOperationalInfo(List<OperationalInfo> info) {
        StringBuilder sb = new StringBuilder();

        if (info != null) {
            sb.append("<table class=\"table table-hover\">");
            for (int i = 0; i < info.size(); i++) {
                sb.append("<tr><th>").
                        append(ResourceLoader.GetResource(session, "items.nodeid")).
                        append("</th><th>").
                        append(ResourceLoader.GetResource(session, "items.authorizedname")).
                        append("</th><th>").
                        append(ResourceLoader.GetResource(session, "items.key")).
                        append("</th><th>").
                        append(ResourceLoader.GetResource(session, "items.created")).
                        append("</th><th>").
                        append(ResourceLoader.GetResource(session, "items.modified")).
                        append("</th><th>").
                        append(ResourceLoader.GetResource(session, "items.modifiedwithchildren")).
                        append("</th></tr>");
                sb.append("<tr><td>");
                sb.append(StringEscapeUtils.escapeHtml(info.get(i).getNodeID()))
                        .append("</td><td>")
                        .append(StringEscapeUtils.escapeHtml(info.get(i).getAuthorizedName()))
                        .append("</td><td>")
                        .append(StringEscapeUtils.escapeHtml(info.get(i).getEntityKey()))
                        .append("</td><td>")
                        .append(StringEscapeUtils.escapeHtml(info.get(i).getCreated().toString()))
                        .append("</td><td>")
                        .append(StringEscapeUtils.escapeHtml(info.get(i).getModified().toString()))
                        .append("</td><td>")
                        .append(StringEscapeUtils.escapeHtml(info.get(i).getModifiedIncludingChildren().toString()))
                        .append("</td></tr>");
            }
            sb.append("</table>");
        }
        return sb.toString();
    }

    /**
     * The get_registeredInfo API call is used to get an abbreviated list of all
     * businessEntity and tModel data that are controlled by a publisher. When
     * the registry distinguishes between publishers, this is the individual
     * associated with the credentials passed in the authInfo element. This
     * returned information is intended, for example, for driving tools that
     * display lists of registered information and then provide drill-down
     * features. This is the recommended API to use after a network problem
     * results in an unknown status of saved information.
     *
     * @return
     */
    public RegisteredInfo GetNodeInformation() {
        try {
            GetRegisteredInfo r = new GetRegisteredInfo();
            r.setAuthInfo(GetToken());
            if (r.getAuthInfo() == null) {
                return null;
            }
            r.setInfoSelection(InfoSelection.ALL);
            RegisteredInfo registeredInfo = null;
            try {
                registeredInfo = publish.getRegisteredInfo(r);
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        r.setAuthInfo(GetToken());
                        registeredInfo = publish.getRegisteredInfo(r);
                    }
                } else {
                    throw ex;
                }
            }
            return registeredInfo;
        } catch (Exception ex) {
            HandleException(ex);
        }
        return null;
    }

    public List<AssertionStatusItem> GetPublisherAssertions(AtomicReference<String> msg) {
        List<AssertionStatusItem> out = new ArrayList<AssertionStatusItem>();
        //first, get all the assertions
/*
         List<PublisherAssertion> publisherAssertions = null;
         try {
         try {
         publisherAssertions = publish.getPublisherAssertions(GetToken());
         } catch (Exception ex) {
         if (ex instanceof DispositionReportFaultMessage) {
         DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
         if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
         token = null;
         publisherAssertions = publish.getPublisherAssertions(GetToken());
         }
         } else {
         throw ex;
         }
         }
         } catch (Exception ex) {
         msg.set(HandleException(ex));
         }
         */
        //then, all of the status of each item
        /*
         * The get_assertionStatusReport API call provides administrative support for determining 
         * the status of current and outstanding publisher assertions that involve any of the business 
         * registrations managed by the individual publisher.  Using this API, a publisher can see the 
         * status of assertions that they have made, as well as see assertions that others have made that 
         * involve businessEntity structures controlled by the requesting publisher.   See Appendix A 
         * Relationships and Publisher Assertions for more information.
         */
        List<AssertionStatusItem> STATUS_COMPLETE = null;

        try {
            try {
                STATUS_COMPLETE = publish.getAssertionStatusReport(GetToken(), CompletionStatus.STATUS_COMPLETE);
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        STATUS_COMPLETE = publish.getAssertionStatusReport(GetToken(), CompletionStatus.STATUS_COMPLETE);
                    }
                } else {
                    throw ex;
                }
            }
        } catch (Exception ex) {
            msg.set(HandleException(ex));
        }
        if (STATUS_COMPLETE != null) {
            out.addAll(STATUS_COMPLETE);
        }
        List<AssertionStatusItem> STATUS_FROM_KEY_INCOMPLETE = null;
        try {
            try {
                STATUS_FROM_KEY_INCOMPLETE = publish.getAssertionStatusReport(GetToken(), CompletionStatus.STATUS_FROM_KEY_INCOMPLETE);
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        STATUS_FROM_KEY_INCOMPLETE = publish.getAssertionStatusReport(GetToken(), CompletionStatus.STATUS_FROM_KEY_INCOMPLETE);

                    }
                } else {
                    throw ex;
                }
            }
        } catch (Exception ex) {
            msg.set(HandleException(ex));
        }
        if (STATUS_FROM_KEY_INCOMPLETE != null) {
            out.addAll(STATUS_FROM_KEY_INCOMPLETE);
        }
        List<AssertionStatusItem> STATUS_TO_KEY_INCOMPLETE = null;
        try {
            try {
                STATUS_TO_KEY_INCOMPLETE = publish.getAssertionStatusReport(GetToken(), CompletionStatus.STATUS_TO_KEY_INCOMPLETE);

            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        STATUS_TO_KEY_INCOMPLETE = publish.getAssertionStatusReport(GetToken(), CompletionStatus.STATUS_TO_KEY_INCOMPLETE);

                    }
                } else {
                    throw ex;
                }
            }
        } catch (Exception ex) {
            msg.set(HandleException(ex));
        }
        if (STATUS_TO_KEY_INCOMPLETE != null) {
            out.addAll(STATUS_TO_KEY_INCOMPLETE);
        }


        return out;
        //return publisherAssertions;
    }

    public String DeletePublisherAssertion(String tokey, String fromkey, String tmodelkey, String keyname, String keyvalue) {
        DeletePublisherAssertions dp = new DeletePublisherAssertions();
        dp.setAuthInfo(GetToken());
        PublisherAssertion add = new PublisherAssertion();
        add.setToKey(tokey);
        add.setFromKey(fromkey);
        add.setKeyedReference(new KeyedReference());
        add.getKeyedReference().setTModelKey(tmodelkey);
        add.getKeyedReference().setKeyName(keyname);
        add.getKeyedReference().setKeyValue(keyvalue);
        dp.getPublisherAssertion().add(add);
        try {
            try {
                publish.deletePublisherAssertions(dp);
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        dp.setAuthInfo(GetToken());
                        publish.deletePublisherAssertions(dp);
                    }
                } else {
                    throw ex;
                }
            }
        } catch (Exception ex) {
            return HandleException(ex);
        }
        return ResourceLoader.GetResource(session, "actions.saved");
    }

    /**
     *
     * @param tokey
     * @param fromkey
     * @param keyname
     * @param keyvalue
     * @return
     */
    public String AddPublisherAssertion(String tokey, String fromkey, String tmodelkey, String keyname, String keyvalue) {
        // List<PublisherAssertion> x = GetPublisherAssertions();
        AddPublisherAssertions r = new AddPublisherAssertions();
        r.setAuthInfo(GetToken());
        PublisherAssertion add = new PublisherAssertion();
        add.setToKey(tokey);
        add.setFromKey(fromkey);
        add.setKeyedReference(new KeyedReference());
        add.getKeyedReference().setTModelKey(tmodelkey);
        add.getKeyedReference().setKeyName(keyname);
        add.getKeyedReference().setKeyValue(keyvalue);
        //TODO signatures? :(
        r.getPublisherAssertion().add(add);
        try {
            try {
                publish.addPublisherAssertions(r);

            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        r.setAuthInfo(GetToken());
                        publish.addPublisherAssertions(r);
                    }
                } else {
                    throw ex;
                }
            }
        } catch (Exception ex) {
            return HandleException(ex);
        }
        return ResourceLoader.GetResource(session, "actions.saved");
    }

    public String GetNewsFeed(XMLGregorianCalendar lastRefresh) throws DatatypeConfigurationException {
        if (df == null) {
            df = DatatypeFactory.newInstance();
        }
        List<Subscription> subscriptions = new ArrayList<Subscription>();
        try {
            try {
                subscriptions = subscription.getSubscriptions(GetToken());

            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;

                        subscriptions = subscription.getSubscriptions(GetToken());
                    }
                } else {
                    throw ex;
                }
            }
        } catch (Exception ex) {
            return HandleException(ex);
        }



        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());

        GetSubscriptionResults r = new GetSubscriptionResults();
        r.setAuthInfo(GetToken());
        r.setCoveragePeriod(new CoveragePeriod());
        r.getCoveragePeriod().setEndPoint(df.newXMLGregorianCalendar(gcal));

        r.getCoveragePeriod().setStartPoint(lastRefresh);
        StringBuilder sb = new StringBuilder();
        for (int k = 0; k < subscriptions.size(); k++) {

            r.setSubscriptionKey(subscriptions.get(k).getSubscriptionKey());
            SubscriptionResultsList subscriptionResults = null;
            try {
                try {
                    subscriptionResults = subscription.getSubscriptionResults(r);

                } catch (Exception ex) {
                    if (ex instanceof DispositionReportFaultMessage) {
                        DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                        if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                            token = null;
                            r.setAuthInfo(GetToken());
                            subscriptionResults = subscription.getSubscriptionResults(r);
                        }
                    } else {
                        throw ex;
                    }
                }
            } catch (Exception ex) {
                return HandleException(ex);
            }

            if (subscriptionResults != null) {
                //    subscriptionResults.getAssertionStatusReport().
                if (subscriptionResults.getAssertionStatusReport() != null) {
                    sb.append(ResourceLoader.GetResource(session, "items.subscriptions.assertion")).
                            append("<br><table class=\"table table-hover\">");
                    for (int i = 0; i < subscriptionResults.getAssertionStatusReport().getAssertionStatusItem().size(); i++) {
                        sb.append("<tr><td>");
                        sb.append(StringEscapeUtils.escapeHtml(subscriptionResults.getAssertionStatusReport().getAssertionStatusItem().get(i).getFromKey()));
                        sb.append("</td><td>");
                        sb.append(StringEscapeUtils.escapeHtml(subscriptionResults.getAssertionStatusReport().getAssertionStatusItem().get(i).getToKey()));
                        sb.append("</td><td>");
                        sb.append(StringEscapeUtils.escapeHtml(subscriptionResults.getAssertionStatusReport().getAssertionStatusItem().get(i).getCompletionStatus().toString()));
                        sb.append("</td></tr>");
                    }
                    sb.append("</table><br><br>");
                } else if (subscriptionResults.getBindingDetail() != null) {
                    sb.append(ResourceLoader.GetResource(session, "items.subscriptions.bindings")).
                            append("<br><table class=\"table table-hover\">");
                    for (int i = 0; i < subscriptionResults.getBindingDetail().getBindingTemplate().size(); i++) {
                        sb.append("<tr><td>");
                        sb.append(StringEscapeUtils.escapeHtml(subscriptionResults.getBindingDetail().getBindingTemplate().get(i).getServiceKey()));
                        sb.append("</td><td>");
                        sb.append(StringEscapeUtils.escapeHtml(subscriptionResults.getBindingDetail().getBindingTemplate().get(i).getBindingKey()));
                        sb.append("</td></tr>");
                    }
                    sb.append("</table><br><br>");
                } else if (subscriptionResults.getBusinessDetail() != null) {
                    sb.append(ResourceLoader.GetResource(session, "items.subscriptions.business")).
                            append("<br><table class=\"table table-hover\">");
                    for (int i = 0; i < subscriptionResults.getBusinessDetail().getBusinessEntity().size(); i++) {
                        sb.append("<tr><td>");
                        sb.append(StringEscapeUtils.escapeHtml(subscriptionResults.getBusinessDetail().getBusinessEntity().get(i).getBusinessKey()));
                        sb.append("</td></tr>");
                    }
                    sb.append("</table><br><br>");
                } else if (subscriptionResults.getRelatedBusinessesList() != null) {
                    sb.append(ResourceLoader.GetResource(session, "items.subscriptions.assertion2")).
                            append("<br><table class=\"table table-hover\">");
                    // for (int i = 0; i < subscriptionResults.getRelatedBusinessesList().getBusinessKey().size(); i++) {
                    sb.append("<tr><td>");
                    sb.append(StringEscapeUtils.escapeHtml(subscriptionResults.getRelatedBusinessesList().getBusinessKey()));
                    sb.append("</td></tr>");
                    //}
                    sb.append("</table><br><br>");
                } else if (subscriptionResults.getServiceDetail() != null) {
                    sb.append(ResourceLoader.GetResource(session, "items.subscriptions.services")).
                            append("d<br><table class=\"table table-hover\">");
                    for (int i = 0; i < subscriptionResults.getServiceDetail().getBusinessService().size(); i++) {
                        sb.append("<tr><td>");
                        sb.append(StringEscapeUtils.escapeHtml(subscriptionResults.getServiceDetail().getBusinessService().get(i).getServiceKey()));
                        sb.append("</td></tr>");
                    }
                    sb.append("</table><br><br>");
                } else if (subscriptionResults.getServiceList() != null) {
                    sb.append(ResourceLoader.GetResource(session, "items.subscriptions.servicelist")).append("<br><table class=\"table table-hover\">");
                    for (int i = 0; i < subscriptionResults.getServiceList().getServiceInfos().getServiceInfo().size(); i++) {
                        sb.append("<tr><td>");
                        sb.append(StringEscapeUtils.escapeHtml(subscriptionResults.getServiceList().getServiceInfos().getServiceInfo().get(i).getServiceKey()));
                        sb.append("</td><td>");
                        sb.append(StringEscapeUtils.escapeHtml(Printers.ListNamesToString(subscriptionResults.getServiceList().getServiceInfos().getServiceInfo().get(i).getName())));
                        sb.append("</td></tr>");
                    }
                    sb.append("</table><br><br>");
                } else if (subscriptionResults.getTModelDetail() != null) {
                    sb.append(ResourceLoader.GetResource(session, "items.subscriptions.tmodels")).append("<br><table class=\"table table-hover\">");
                    for (int i = 0; i < subscriptionResults.getTModelDetail().getTModel().size(); i++) {
                        sb.append("<tr><td>");
                        sb.append(StringEscapeUtils.escapeHtml(subscriptionResults.getTModelDetail().getTModel().get(i).getTModelKey()));
                        sb.append("</td><td>");
                        sb.append(StringEscapeUtils.escapeHtml((subscriptionResults.getTModelDetail().getTModel().get(i).getName().getValue())));
                        sb.append("</td></tr>");
                    }
                    sb.append("</table><br><br>");
                } else if (subscriptionResults.getTModelList() != null) {
                    sb.append(ResourceLoader.GetResource(session, "items.subscriptions.tmodels2"))
                            .append("<br><table class=\"table table-hover\">");
                    for (int i = 0; i < subscriptionResults.getTModelList().getTModelInfos().getTModelInfo().size(); i++) {
                        sb.append("<tr><td>");
                        sb.append(StringEscapeUtils.escapeHtml(subscriptionResults.getTModelList().getTModelInfos().getTModelInfo().get(i).getTModelKey()));
                        sb.append("</td><td>");
                        sb.append(StringEscapeUtils.escapeHtml((subscriptionResults.getTModelList().getTModelInfos().getTModelInfo().get(i).getName().getValue())));
                        sb.append("</td></tr>");
                    }
                    sb.append("</table><br><br>");
                }

            }
        }
        return sb.toString();
    }

    public PagableContainer SearchForBinding(String keyword, String lang, int offset, int maxrecords, boolean isChooser) {
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
            if (lang == null || lang.equalsIgnoreCase(ResourceLoader.GetResource(session, "items.clicktoedit"))) {
                n.setLang(null);
            } else {
                n.setLang(lang);
            }
            n.setValue(keyword);
            fs.getName().add(n);
            fs.setFindQualifiers(new org.uddi.api_v3.FindQualifiers());
            fs.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
            ServiceList findService = null;//inquiry.findService(fs);
            try {
                findService = inquiry.findService(fs);
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        fs.setAuthInfo(GetToken());
                        findService = inquiry.findService(fs);
                    }
                } else {
                    throw ex;
                }
            }

            if (findService == null || findService.getServiceInfos() == null) {
                ret.renderedHtml = ResourceLoader.GetResource(session, "errors.norecordsfound");
                return ret;
            }
            ret.displaycount = findService.getListDescription().getIncludeCount();
            ret.totalrecords = findService.getListDescription().getActualCount();

            GetServiceDetail gs = new GetServiceDetail();
            gs.setAuthInfo(GetToken());
            for (int i = 0; i < findService.getServiceInfos().getServiceInfo().size(); i++) {
                gs.getServiceKey().add(findService.getServiceInfos().getServiceInfo().get(i).getServiceKey());
            }
            ServiceDetail serviceDetail = null;
            try {
                serviceDetail = inquiry.getServiceDetail(gs);
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        fs.setAuthInfo(GetToken());
                        serviceDetail = inquiry.getServiceDetail(gs);
                    }
                } else {
                    throw ex;
                }
            }
            if (serviceDetail == null || serviceDetail.getBusinessService().isEmpty()) {
                ret.renderedHtml = ResourceLoader.GetResource(session, "errors.norecordsfound");
                return ret;
            }
            


            StringBuilder sb = new StringBuilder();
            sb.append("<table class=\"table\"><tr><th>").
                    append("</th><th>").
                    append(ResourceLoader.GetResource(session, "items.business")).
                    append("</th><th>").
                    append(ResourceLoader.GetResource(session, "items.service")).
                    append("</th><th>").
                    append(ResourceLoader.GetResource(session, "items.bindingtemplate.key")).
                    append("</th><th>").
                    append(ResourceLoader.GetResource(session, "items.accesspoint.value")).
                    append("</th></tr>");

            for (int i = 0; i < serviceDetail.getBusinessService().size(); i++) {
                //   System.out.println(serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().size());
                if (serviceDetail.getBusinessService().get(i).getBindingTemplates() != null) {
                    for (int k = 0; k < serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().size(); k++) {
                        //System.out.println(serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getAccessPoint().getValue());
                        sb.append("<tr><td><input type=\"checkbox\" class=\"modalableBinding\" id=\"").
                                append(StringEscapeUtils.escapeHtml(serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getBindingKey())).
                                append("\">").
                                append("</td><td>").
                                append("<a href=\"businessEditor2.jsp?id=").
                                append(StringEscapeUtils.escapeHtml(findService.getServiceInfos().getServiceInfo().get(i).getBusinessKey())).
                                append("\">").
                                append(StringEscapeUtils.escapeHtml((findService.getServiceInfos().getServiceInfo().get(i).getBusinessKey()))).
                                append("</a>").
                                append("</td><td>").append("<a href=\"serviceEditor.jsp?id=").
                                append(StringEscapeUtils.escapeHtml(serviceDetail.getBusinessService().get(i).getServiceKey())).
                                append("\" title=\"").
                                append(StringEscapeUtils.escapeHtml(serviceDetail.getBusinessService().get(i).getServiceKey())).
                                append("\">").
                                append(Printers.ListNamesToString(serviceDetail.getBusinessService().get(i).getName())).
                                append("</a>").
                                append("</td><td>").
                                append(StringEscapeUtils.escapeHtml(serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getBindingKey())).
                                append("</td><td>");
                        if (serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getAccessPoint() != null) {
                            sb.append(StringEscapeUtils.escapeHtml(serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getAccessPoint().getValue()));
                        }
                        sb.append("</td></tr>");
                    }
                }
            }

            sb.append("</table>");
            ret.renderedHtml = sb.toString();
            return ret;
        } catch (Exception ex) {
            ret.renderedHtml = HandleException(ex);
        }
        return ret;

    }

    /**
     *
     *
     * authInfo: This OPTIONAL argument is an element that contains an
     * authentication token. Authentication tokens are obtained using the
     * get_authToken API call or through some other means external to this
     * specification, and represent the identity of the publisher at a UDDI
     * node.
     *
     * · transferToken: This is a known transferToken obtained by a publisher at
     * the node where the get_transferToken API was invoked.
     *
     * · keyBag: One or more uddiKeys associated either with businessEntity or
     * tModel entities owned by the publisher that were to be transferred to
     * some other publisher and/or node in the registry as the result of
     * invocation of get_transferToken. At least one businessKey or tModelKey
     * must be provided in a keyBag.
     *
     * @param keys
     * @param nodeid
     * @param outExpires
     * @param outToken
     * @return
     */
    public String GetCustodyTransferToken(org.uddi.custody_v3.KeyBag keys, Holder<String> nodeid, Holder<XMLGregorianCalendar> outExpires, Holder<byte[]> outToken) {

        // org.uddi.custody_v3.KeyBag kb = new org.uddi.custody_v3.KeyBag();
        // kb.getKey().addAll(keys);
        try {
            try {
                custody.getTransferToken(GetToken(), keys, nodeid, outExpires, outToken);
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        custody.getTransferToken(GetToken(), keys, nodeid, outExpires, outToken);
                    }
                } else {
                    throw ex;
                }
            }
        } catch (Exception ex) {
            return HandleException(ex);

        }
        return null;//"Success";
    }

    /**
     *
     * @param tokenxml
     * @return The discard_transferToken API is a client API used to discard a
     * transferToken obtained through the get_transferToken API at the same
     * node. This API accepts either a transferToken or a keyBag as parameters
     * to remove the permission to transfer data associated with a particular
     * transferToken. If a keyBag is provided, all tokens corresponding to the
     * keys in the keyBag will be discarded and will no longer be valid for
     * custody or ownership transfer after the discard_transferToken is
     * processed, irrespective of whether the keys match any known business or
     * tmodelKey values. In the event that the keyBag represents a subset of the
     * keyBag for one or more transferToken elements, the transferToken is
     * discarded and will no longer be valid for transferring any entity. If the
     * token passed in the transferToken argument does not match an existing
     * token known to the system, no action is taken and success is reported.
     * Keys in the keyBag argument that do not have a corresponding token are
     * ignored.
     */
    public String DiscardToken(String tokenxml) {
        DiscardTransferToken r = new DiscardTransferToken();
        r.setAuthInfo(GetToken());
        r.setTransferToken(JAXB.unmarshal(new StringReader(tokenxml), TransferToken.class));

        try {
            try {
                custody.discardTransferToken(r);
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        custody.discardTransferToken(r);
                    }
                } else {
                    throw ex;
                }
            }
        } catch (Exception ex) {
            return HandleException(ex);
        }
        return null;//"Success";

    }

    public String AcceptCustodyTranferToken(String tokenXML, String keyBagXML) {
        try {
            TransferEntities te = new TransferEntities();
            te.setAuthInfo(GetToken());
            StringReader sr = new StringReader(tokenXML.trim());
            te.setTransferToken(JAXB.unmarshal(sr, TransferToken.class));
            sr = new StringReader(keyBagXML.trim());
            te.setKeyBag(JAXB.unmarshal(sr, org.uddi.custody_v3.KeyBag.class));

            try {
                custody.transferEntities(te);
            } catch (Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                    DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                    if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                        token = null;
                        custody.transferEntities(te);
                    }
                } else {
                    throw ex;
                }
            }
        } catch (Exception ex) {
            return HandleException(ex);
        }
        return null;//"Success";

    }

    public Subscription GetSubscriptionDetails(String id) {
        if (id == null) {
            return null;
        }
        List<Subscription> GetSubscriptions = this.GetSubscriptions();
        if (GetSubscriptions == null) {
            return null;
        }
        for (int i = 0; i < GetSubscriptions.size(); i++) {
            if (GetSubscriptions.get(i).getSubscriptionKey() != null && GetSubscriptions.get(i).getSubscriptionKey().equalsIgnoreCase(id)) {
                return GetSubscriptions.get(i);
            }
        }
        return null;
    }
}
