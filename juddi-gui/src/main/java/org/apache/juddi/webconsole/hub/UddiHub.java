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
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
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
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.ClassUtil;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.ClientConfig;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDINode;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.webconsole.AES;
import org.apache.juddi.webconsole.PostBackConstants;
import org.apache.juddi.webconsole.hub.builders.Builders;
import org.apache.juddi.webconsole.hub.builders.Printers;
import org.apache.juddi.webconsole.resources.ResourceLoader;
import org.uddi.api_v3.*;
import org.uddi.custody_v3.DiscardTransferToken;
import org.uddi.custody_v3.GetTransferToken;
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

/**
 * UddiHub - The hub acts as a single point for managing browser to uddi
 * services. At most 1 instance is allowed per http session. In general, all
 * methods in the class trigger web service call outs. All callouts also support
 * expired UDDI tokens and will attempt to reauthenticate and retry the request.
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class UddiHub implements Serializable {

        /**
         * The logger name
         */
        public static final String LOGGER_NAME = "org.apache.juddi";
        private static final long serialVersionUID = 1L;
        AuthStyle style = null;
        private String nodename = "default";
        private final String clientName = "juddigui";
        private boolean WS_Transport = false;
        private boolean WS_securePorts = false;
        private transient HttpSession session;
        private transient Transport transport = null;
        private transient ClientConfig clientConfig;
        private transient Properties properties;
        private transient UDDISubscriptionPortType subscription = null;
        private transient UDDISecurityPortType security = null;
        private transient UDDIInquiryPortType inquiry = null;
        private transient UDDIPublicationPortType publish = null;
        private transient UDDICustodyTransferPortType custody = null;
        //private JUDDIApiPortType juddi = null;
        private transient String token = null;
        /**
         * The Log4j logger. This is also referenced from the Builders class,
         * thus it is public
         */
        public transient static final Log log = LogFactory.getLog(LOGGER_NAME);
        private transient DatatypeFactory df;
        /**
         * the name of the 'node' property in the config
         */
        public static final String PROP_CONFIG_NODE = "config.props.node";
        /**
         *
         */
        public static final String PROP_AUTH_TYPE = "config.props.authtype";
        /**
         *
         */
        public static final String PROP_AUTO_LOGOUT = "config.props.automaticLogouts.enable";
        /**
         *
         */
        public static final String PROP_AUTO_LOGOUT_TIMER = "config.props.automaticLogouts.duration";
        /**
         *
         */
        public static final String PROP_PREFIX = "config.props.";
        /**
         *
         *
         */
        public static final String PROP_ADMIN_LOCALHOST_ONLY = "config.props.configLocalHostOnly";

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

        public String verifyLogin() {
                EnsureConfig();
                if (style != AuthStyle.UDDI_AUTH) {
                        if (WS_Transport) {
                                BindingProvider bp = null;
                                Map<String, Object> context = null;
                                bp = (BindingProvider) inquiry;
                                context = bp.getRequestContext();
                                context.remove(BindingProvider.USERNAME_PROPERTY);
                                context.remove(BindingProvider.PASSWORD_PROPERTY);

                                context.put(BindingProvider.USERNAME_PROPERTY, session.getAttribute("username"));
                                context.put(BindingProvider.PASSWORD_PROPERTY, session.getAttribute(AES.Decrypt("password", (String) properties.get("key"))));

                                bp = (BindingProvider) publish;
                                context = bp.getRequestContext();
                                context.remove(BindingProvider.USERNAME_PROPERTY);
                                context.remove(BindingProvider.PASSWORD_PROPERTY);

                                context.put(BindingProvider.USERNAME_PROPERTY, session.getAttribute("username"));
                                context.put(BindingProvider.PASSWORD_PROPERTY, session.getAttribute(AES.Decrypt("password", (String) properties.get("key"))));

                                bp = (BindingProvider) custody;
                                context = bp.getRequestContext();
                                context.remove(BindingProvider.USERNAME_PROPERTY);
                                context.remove(BindingProvider.PASSWORD_PROPERTY);

                                context.put(BindingProvider.USERNAME_PROPERTY, session.getAttribute("username"));
                                context.put(BindingProvider.PASSWORD_PROPERTY, session.getAttribute(AES.Decrypt("password", (String) properties.get("key"))));

                                bp = (BindingProvider) subscription;
                                context = bp.getRequestContext();
                                context.remove(BindingProvider.USERNAME_PROPERTY);
                                context.remove(BindingProvider.PASSWORD_PROPERTY);

                                context.put(BindingProvider.USERNAME_PROPERTY, session.getAttribute("username"));
                                context.put(BindingProvider.PASSWORD_PROPERTY, session.getAttribute(AES.Decrypt("password", (String) properties.get("key"))));
                        }
                        FindBusiness fb = new FindBusiness();
                        fb.setListHead(0);
                        fb.setMaxRows(1);
                        fb.setFindQualifiers(new FindQualifiers());
                        fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                        fb.getName().add(new Name(UDDIConstants.WILDCARD, null));
                        try {
                                inquiry.findBusiness(fb);
                        } catch (Exception ex) {
                                return HandleException(ex);
                        }
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
                        if (WS_Transport) {
                                BindingProvider bp = null;
                                Map<String, Object> context = null;

                                bp = (BindingProvider) inquiry;
                                context = bp.getRequestContext();
                                context.remove(BindingProvider.USERNAME_PROPERTY);
                                context.remove(BindingProvider.PASSWORD_PROPERTY);

                                bp = (BindingProvider) publish;
                                context = bp.getRequestContext();
                                context.remove(BindingProvider.USERNAME_PROPERTY);
                                context.remove(BindingProvider.PASSWORD_PROPERTY);

                                bp = (BindingProvider) custody;
                                context = bp.getRequestContext();
                                context.remove(BindingProvider.USERNAME_PROPERTY);
                                context.remove(BindingProvider.PASSWORD_PROPERTY);

                                bp = (BindingProvider) subscription;
                                context = bp.getRequestContext();
                                context.remove(BindingProvider.USERNAME_PROPERTY);
                                context.remove(BindingProvider.PASSWORD_PROPERTY);
                        }
                        GetAuthToken req = new GetAuthToken();
                        try {
                                if (security == null) {
                                        security = transport.getUDDISecurityService();
                                }
                        } catch (Exception ex) {
                                return HandleException(ex);
                        }
                        if (session.getAttribute("username") != null
                                && session.getAttribute("password") != null) {
                                req.setUserID((String) session.getAttribute("username"));
                                req.setCred(AES.Decrypt((String) session.getAttribute("password"), (String) properties.get("key")));
                                log.info("AUDIT: fetching auth token for " + req.getUserID() + " Auth Mode is " + ((security == null) ? "HTTP" : "AUTH_TOKEN"));
                                try {
                                        AuthToken authToken = security.getAuthToken(req);
                                        token = authToken.getAuthInfo();
                                        return null;
                                } catch (Exception ex) {
                                        return HandleException(ex);
                                }
                        }
                }
                return "Unexpected error";
        }

        /**
         * This kills any authentication tokens, logs the user out and nulls out
         * all services
         */
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

        /**
         * This is the singleton accessor UddiHub. There should be at most 1
         * instance per HTTP Session (user login)
         *
         * @param application
         * @param _session
         * @return
         * @throws Exception
         */
        public static UddiHub getInstance(ServletContext application, HttpSession _session) throws Exception {
                Object j = _session.getAttribute("hub");
                if (j == null) {
                        UddiHub hub = new UddiHub(application, _session);
                        _session.setAttribute("hub", hub);
                        hub.locale = (String) _session.getAttribute("locale");
                        return hub;
                }

                return (UddiHub) j;
        }
        String locale = "en";

        /**
         * gets the user selected locale
         *
         * @return
         */
        public String getLocale() {
                return locale;
        }

        private void EnsureConfig() {
                if (clientConfig == null) {
                        try {
                                UDDIClient client = new UDDIClient();
                                clientConfig = client.getClientConfig();
                                try {
                                        style = AuthStyle.valueOf(clientConfig.getConfiguration().getString(PROP_AUTH_TYPE));
                                } catch (Exception ex) {
                                        log.warn("'UDDI_AUTH' is not defined in the config (" + PROP_AUTH_TYPE + ")! defaulting to UDDI_AUTH");
                                        style = AuthStyle.UDDI_AUTH;
                                }

                                nodename = clientConfig.getConfiguration().getString(PROP_CONFIG_NODE);
                                if (nodename == null || nodename.equals("")) {
                                        log.warn("'node' is not defined in the config! defaulting to 'default'");
                                        nodename = "default";
                                }
                                UDDINode uddiNode = clientConfig.getUDDINode(nodename);

                                String clazz = uddiNode.getProxyTransport();
                                if (clazz.contains("JAXWSTransport")) {
                                        WS_Transport = true;
                                }
                                Class<?> transportClass = ClassUtil.forName(clazz, Transport.class);
                                if (transportClass != null) {
                                        transport = client.getTransport(nodename);

                                        security = transport.getUDDISecurityService();
                                        inquiry = transport.getUDDIInquiryService();
                                        subscription = transport.getUDDISubscriptionService();
                                        publish = transport.getUDDIPublishService();
                                        custody = transport.getUDDICustodyTransferService();

                                        if (WS_Transport) {
                                                if (uddiNode.getPublishUrl().toLowerCase().startsWith("https://")
                                                        && (uddiNode.getSecurityUrl() != null && uddiNode.getSecurityUrl().toLowerCase().startsWith("https://"))
                                                        && uddiNode.getInquiryUrl().toLowerCase().startsWith("https://")
                                                        && (uddiNode.getCustodyTransferUrl() != null && uddiNode.getCustodyTransferUrl().toLowerCase().startsWith("https://"))
                                                        && uddiNode.getSubscriptionUrl().toLowerCase().startsWith("https://")) {
                                                        WS_securePorts = true;
                                                }
                                        }

                                }
                        } catch (Exception ex) {
                                HandleException(ex);
                        }
                }

        }

        private UddiHub(ServletContext application, HttpSession _session) throws Exception {
                session = _session;

                URL prop = application.getResource("/META-INF/config.properties");
                if (prop == null) {
                        prop = application.getResource("META-INF/config.properties");
                }
                if (prop == null) {
                        throw new Exception("Cannot locate the configuration file.");
                }

                InputStream in = prop.openStream();
                Properties p = new Properties();
                p.load(in);
                in.close();
                properties = p;

                EnsureConfig();
        }

        /**
         * returns true if we are using JAXWS transport AND all of the URLs
         * start with https://
         *
         * @return
         */
        public boolean isSecure() {

                EnsureConfig();
                return WS_securePorts;
        }

        /**
         * gets a reference to the current juddi client config file. this is a
         * live instance changes can be stored to disk, usually
         *
         * @return
         * @throws ConfigurationException g
         */
        public ClientConfig GetJuddiClientConfig() throws ConfigurationException {
                EnsureConfig();
                return clientConfig;
        }

        /**
         * returns all of the current properties defining digital signatures
         *
         * @return
         */
        public Properties GetDigitalSignatureConfig() {
                try {
                        return GetJuddiClientConfig().getDigitalSignatureConfiguration();
                } catch (Exception ex) {
                        log.error("error fetching uddi.xml", ex);
                }
                return new Properties();
        }

        private String GetToken() {
                EnsureConfig();
                if (style != AuthStyle.UDDI_AUTH) {
                        if (WS_Transport) {
                                BindingProvider bp = null;
                                Map<String, Object> context = null;
                                bp = (BindingProvider) inquiry;
                                context = bp.getRequestContext();
                                context.remove(BindingProvider.USERNAME_PROPERTY);
                                context.remove(BindingProvider.PASSWORD_PROPERTY);

                                context.put(BindingProvider.USERNAME_PROPERTY, session.getAttribute("username"));
                                context.put(BindingProvider.PASSWORD_PROPERTY, session.getAttribute(AES.Decrypt("password", (String) properties.get("key"))));

                                bp = (BindingProvider) publish;
                                context = bp.getRequestContext();
                                context.remove(BindingProvider.USERNAME_PROPERTY);
                                context.remove(BindingProvider.PASSWORD_PROPERTY);

                                context.put(BindingProvider.USERNAME_PROPERTY, session.getAttribute("username"));
                                context.put(BindingProvider.PASSWORD_PROPERTY, session.getAttribute(AES.Decrypt("password", (String) properties.get("key"))));

                                bp = (BindingProvider) custody;
                                context = bp.getRequestContext();
                                context.remove(BindingProvider.USERNAME_PROPERTY);
                                context.remove(BindingProvider.PASSWORD_PROPERTY);

                                context.put(BindingProvider.USERNAME_PROPERTY, session.getAttribute("username"));
                                context.put(BindingProvider.PASSWORD_PROPERTY, session.getAttribute(AES.Decrypt("password", (String) properties.get("key"))));

                                bp = (BindingProvider) subscription;
                                context = bp.getRequestContext();
                                context.remove(BindingProvider.USERNAME_PROPERTY);
                                context.remove(BindingProvider.PASSWORD_PROPERTY);

                                context.put(BindingProvider.USERNAME_PROPERTY, session.getAttribute("username"));
                                context.put(BindingProvider.PASSWORD_PROPERTY, session.getAttribute(AES.Decrypt("password", (String) properties.get("key"))));
                        }
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
                        if (WS_Transport) {
                                BindingProvider bp = null;
                                Map<String, Object> context = null;

                                bp = (BindingProvider) inquiry;
                                context = bp.getRequestContext();
                                context.remove(BindingProvider.USERNAME_PROPERTY);
                                context.remove(BindingProvider.PASSWORD_PROPERTY);

                                bp = (BindingProvider) publish;
                                context = bp.getRequestContext();
                                context.remove(BindingProvider.USERNAME_PROPERTY);
                                context.remove(BindingProvider.PASSWORD_PROPERTY);

                                bp = (BindingProvider) custody;
                                context = bp.getRequestContext();
                                context.remove(BindingProvider.USERNAME_PROPERTY);
                                context.remove(BindingProvider.PASSWORD_PROPERTY);

                                bp = (BindingProvider) subscription;
                                context = bp.getRequestContext();
                                context.remove(BindingProvider.USERNAME_PROPERTY);
                                context.remove(BindingProvider.PASSWORD_PROPERTY);
                        }
                        GetAuthToken req = new GetAuthToken();
                        try {
                                if (security == null) {
                                        security = transport.getUDDISecurityService();
                                }
                        } catch (Exception ex) {
                                log.error(ex);
                        }
                        if (session.getAttribute("username") != null
                                && session.getAttribute("password") != null) {
                                req.setUserID((String) session.getAttribute("username"));
                                req.setCred(AES.Decrypt((String) session.getAttribute("password"), (String) properties.get("key")));
                                log.info("AUDIT: fetching auth token for " + req.getUserID() + " Auth Mode is " + ((security == null) ? "HTTP" : "AUTH_TOKEN"));
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

        /**
         * Returns true if the current user has a token and is signed in. Does
         * not apply to non-UDDI security API logins
         *
         * @return
         */
        public boolean getUddiIsAuthenticated() {
                return (token != null && !token.isEmpty());
        }

        /**
         * Performs a find_business call in the inquiry API
         *
         * @param offset
         * @param maxrecords
         * @param keyword
         * @param lang
         * @param isChooser
         * @return
         */
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
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        fb.setAuthInfo(GetToken());
                                        findBusiness = inquiry.findBusiness(fb);
                                } else {
                                        throw ex;
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
         * The get_registeredInfo API call is used to get an abbreviated list of
         * all businessEntity and tModel data that are controlled by a
         * publisher. When the registry distinguishes between publishers, this
         * is the individual associated with the credentials passed in the
         * authInfo element. This returned information is intended, for example,
         * for driving tools that display lists of registered information and
         * then provide drill-down features. This is the recommended API to use
         * after a network problem results in an unknown status of saved
         * information.
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
                                return ToErrorAlert(ResourceLoader.GetResource(session, "errors.notsignedin"));
                        }
                        r.setInfoSelection(InfoSelection.ALL);

                        try {
                                findBusiness = publish.getRegisteredInfo(r);
                        } catch (Exception ex) {
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        r.setAuthInfo(GetToken());
                                        findBusiness = publish.getRegisteredInfo(r);
                                } else {
                                        throw ex;
                                }
                        }

                } catch (Exception ex) {
                        return ToErrorAlert(HandleException(ex));
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

        /**
         * Performs Inquiry Find_service API used from servicedetails.jsp
         *
         * @param serviceid
         * @return
         */
        public String GetServiceDetailAsHtml(String serviceid) {
                if (serviceid == null || serviceid.length() == 0) {
                        return ResourceLoader.GetResource(session, "errors.noinput");
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
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        gbd.setAuthInfo(GetToken());
                                        get = inquiry.getServiceDetail(gbd);

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

                                        sb.append(Printers.PrintBindingTemplates(get.getBusinessService().get(i).getBindingTemplates(), locale)).append("<Br>");
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
         * return true if the word expire is in the exception or the UDDI error
         * code representing an expired token
         *
         * @param ex
         * @return r
         */
        public static boolean isExceptionExpiration(Exception ex) {
                if (ex == null) {
                        return false;
                }
                if (ex instanceof DispositionReportFaultMessage) {
                        DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                        if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED) || ex.getMessage().contains(DispositionReport.E_AUTH_TOKEN_EXPIRED) || ex.getMessage().toLowerCase().contains("expired")) {
                                return true;
                        }
                }

                if (ex.getMessage() == null) {
                        return false;
                }
                if (ex.getMessage().toLowerCase().contains("expire")) {
                        return true;
                }

                if (ex.getMessage().toLowerCase().contains(DispositionReport.E_AUTH_TOKEN_EXPIRED.toLowerCase())) {
                        return true;
                }
                if (ex.getLocalizedMessage() == null) {
                        return false;
                }
                if (ex.getLocalizedMessage().toLowerCase().contains("expire")) {
                        return true;
                }
                return false;
        }

        /**
         * returns an html formatted list of services for a specific business
         * used on browse.jsp
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
                                if (isExceptionExpiration(ex)) {

                                        token = null;
                                        gbd.setAuthInfo(GetToken());
                                        businessDetail = inquiry.getBusinessDetail(gbd);

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

        /**
         * Performs a getServiceDetails in Inquiry API
         *
         * @param serviceid
         * @return null if no id was specified or if it didn't exist
         */
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
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        gbd.setAuthInfo(GetToken());
                                        get = inquiry.getServiceDetail(gbd);

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
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        sb.setAuthInfo(GetToken());
                                        publish.saveBinding(sb);

                                } else {
                                        throw ex;
                                }
                        }
                        return ResourceLoader.GetResource(session, "actions.save.bindingtemplate");
                } catch (Exception ex) {
                        return HandleException(ex);
                }
        }

        /**
         * This method will rebuild a Service entity from the HTTP request from
         * the Service Editor page and will then attempt to save it.
         *
         * @param request
         * @return a localized Saved or an error message
         */
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

                be.getName().addAll(Builders.BuildNames(Builders.MapFilter(request.getParameterMap(), PostBackConstants.NAME), PostBackConstants.NAME, ResourceLoader.GetResource(session, "items.clicktoedit"), locale));
                BindingTemplates bt = new BindingTemplates();
                bt.getBindingTemplate().addAll(Builders.BuildBindingTemplates(Builders.MapFilter(request.getParameterMap(), PostBackConstants.BINDINGTEMPLATE), PostBackConstants.BINDINGTEMPLATE, ResourceLoader.GetResource(session, "items.clicktoedit"), locale));
                if (!bt.getBindingTemplate().isEmpty()) {
                        be.setBindingTemplates(bt);
                }

                be.getDescription().addAll(Builders.BuildDescription(Builders.MapFilter(request.getParameterMap(), PostBackConstants.DESCRIPTION), PostBackConstants.DESCRIPTION, ResourceLoader.GetResource(session, "items.clicktoedit"), locale));

                CategoryBag cb = new CategoryBag();
                cb.getKeyedReference().addAll(Builders.BuildKeyedReference(Builders.MapFilter(request.getParameterMap(), PostBackConstants.CATBAG_KEY_REF), PostBackConstants.CATBAG_KEY_REF, locale));
                cb.getKeyedReferenceGroup().addAll(Builders.BuildKeyedReferenceGroup(Builders.MapFilter(request.getParameterMap(), PostBackConstants.CATBAG_KEY_REF_GRP), PostBackConstants.CATBAG_KEY_REF_GRP, locale));

                if (!cb.getKeyedReference().isEmpty() || !cb.getKeyedReferenceGroup().isEmpty()) {
                        be.setCategoryBag(cb);
                }

                return SaveServiceDetails(be);
        }

        /**
         * Saves a Service
         *
         * @param be
         * @return a readable error message or, success
         */
        public String SaveServiceDetails(BusinessService be) {
                try {
                        SaveService sb = new SaveService();
                        sb.setAuthInfo(GetToken());
                        ServiceDetail saveService = null;
                        sb.getBusinessService().add(be);
                        try {
                                saveService = publish.saveService(sb);
                        } catch (Exception ex) {
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        sb.setAuthInfo(GetToken());
                                        saveService = publish.saveService(sb);

                                } else {
                                        throw ex;
                                }
                        }
                        return ResourceLoader.GetResource(session, "actions.saved") + " "
                                + "<a href=\"serviceEditor.jsp?id=" + URLEncoder.encode(saveService.getBusinessService().get(0).getServiceKey(), "UTF8")
                                + "\">" + StringEscapeUtils.escapeHtml(saveService.getBusinessService().get(0).getServiceKey()) + "</a>";
                } catch (Exception ex) {
                        return HandleException(ex);
                }
        }

        /**
         * Saves a business entity
         *
         * @param be
         * @return a readable error message
         */
        public String SaveBusinessDetails(BusinessEntity be) {
                try {
                        SaveBusiness sb = new SaveBusiness();
                        sb.setAuthInfo(GetToken());
                        sb.getBusinessEntity().add(be);
                        BusinessDetail saveBusiness = null;
                        try {
                                saveBusiness = publish.saveBusiness(sb);
                        } catch (Exception ex) {
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        sb.setAuthInfo(GetToken());
                                        saveBusiness = publish.saveBusiness(sb);

                                } else {
                                        throw ex;
                                }
                        }

                        return ResourceLoader.GetResource(session, "actions.saved") + " "
                                + "<a href=\"businessEditor2.jsp?id=" + URLEncoder.encode(saveBusiness.getBusinessEntity().get(0).getBusinessKey(), "UTF8")
                                + "\">" + StringEscapeUtils.escapeHtml(saveBusiness.getBusinessEntity().get(0).getBusinessKey()) + "</a>";
                } catch (Exception ex) {
                        return HandleException(ex);
                }
        }

        /**
         * Save Business
         *
         * This method saves a business to a UDDI registry, preserving the
         * service listing The request is build from the HTTP post back
         * parameters. A human readable response message is returned
         *
         * @param request
         * @return
         */
        public String SaveBusinessDetails(HttpServletRequest request) {

                BusinessEntity be = new BusinessEntity();
                be.setBusinessKey(request.getParameter(PostBackConstants.BUSINESSKEY).trim());
                if (be.getBusinessKey().equalsIgnoreCase(ResourceLoader.GetResource(session, "items.clicktoedit"))) {
                        be.setBusinessKey(null);
                } else {
                        BusinessEntity GetBusinessDetails = GetBusinessDetails(be.getBusinessKey());
                        if (GetBusinessDetails == null) //this is a new business
                        {
                        } else {
                                //copy over the existing child element, business
                                be.setBusinessServices(GetBusinessDetails.getBusinessServices());
                        }
                }
                be.getName().addAll(Builders.BuildNames(Builders.MapFilter(request.getParameterMap(), PostBackConstants.NAME), PostBackConstants.NAME, ResourceLoader.GetResource(session, "items.clicktoedit"), locale));

                be.setContacts(Builders.BuildContacts(request.getParameterMap(), ResourceLoader.GetResource(session, "items.clicktoedit"), locale));

                be.getDescription().addAll(Builders.BuildDescription(Builders.MapFilter(request.getParameterMap(), PostBackConstants.DESCRIPTION), PostBackConstants.DESCRIPTION, ResourceLoader.GetResource(session, "items.clicktoedit"), locale));
                be.setDiscoveryURLs(Builders.BuildDisco(Builders.MapFilter(request.getParameterMap(), PostBackConstants.DISCOVERYURL), PostBackConstants.DISCOVERYURL, locale));
                CategoryBag cb = new CategoryBag();
                cb.getKeyedReference().addAll(Builders.BuildKeyedReference(Builders.MapFilter(request.getParameterMap(), PostBackConstants.CATBAG_KEY_REF), PostBackConstants.CATBAG_KEY_REF, locale));
                cb.getKeyedReferenceGroup().addAll(Builders.BuildKeyedReferenceGroup(Builders.MapFilter(request.getParameterMap(), PostBackConstants.CATBAG_KEY_REF_GRP), PostBackConstants.CATBAG_KEY_REF_GRP, locale));

                if (!cb.getKeyedReference().isEmpty() || !cb.getKeyedReferenceGroup().isEmpty()) {
                        be.setCategoryBag(cb);
                }
                be.setIdentifierBag(Builders.BuildIdentBag(Builders.MapFilter(request.getParameterMap(), PostBackConstants.IDENT_KEY_REF), PostBackConstants.IDENT_KEY_REF, locale));
                return SaveBusinessDetails(be);
        }

        /**
         * Returns
         *
         * @param bizid
         * @return
         * @throws Exception
         */
        /**
         * Gets a business's details used for the businessEditor
         *
         * @param bizid
         * @return null if no id is provided or if there is a remote error
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
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        gbd.setAuthInfo(GetToken());
                                        businessDetail = inquiry.getBusinessDetail(gbd);

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

        /**
         * returns a bootstrap html stylizies an error message with a warning
         * icon
         *
         * @param HandleException, any string representing an error message
         * @return
         */
        public static String ToErrorAlert(String HandleException) {
                return "<div class=\"alert alert-error\"><i class=\"icon-warning-sign icon-large\"></i>&nbsp;" + HandleException + "</div>";
        }

        /**
         * AuthStyles for the Hub to use, default is UDDI_AUTH
         */
        public enum AuthStyle {

                /**
                 * Http
                 */
                HTTP,
                /**
                 * UDDI Authentication via the Security API
                 */
                UDDI_AUTH
        }

        /**
         * Search for services using find_services
         *
         * @param keyword
         * @param lang
         * @param maxrecords
         * @param offset
         * @param isChooser
         * @return
         */
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
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        fs.setAuthInfo(GetToken());
                                        findService = inquiry.findService(fs);

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
         * Adds a special tModel key generator keyGenerator: Marking a tModel
         * with this categorization designates it as one whose tModelKey
         * identifies a key generator partition that can be used by its owner to
         * derive and assign other entity keys. This categorization is reserved
         * for key generator tModels. Any attempt to use this categorization for
         * something other than a key generator tModel will fail with
         * E_valueNotAllowed returned.
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
                        OverviewDoc overviewDoc = new OverviewDoc();
                        OverviewURL overviewUrl = new OverviewURL();
                        overviewUrl.setUseType("text");
                        overviewUrl.setValue("http://uddi.org/pubs/uddi_v3.htm#keyGen");
                        overviewDoc.setOverviewURL(overviewUrl);
                        tm.getOverviewDoc().add(overviewDoc);
                        tm.setTModelKey(partitionName.toLowerCase());
                        st.getTModel().add(tm);
                        publish.saveTModel(st);
                        return ResourceLoader.GetResource(session, "messages.success");
                        // "Success";
                } catch (Exception ex) {
                        return HandleException(ex);
                }
        }

        /**
         * This function provides a basic error handling rutine that will pull
         * out the true error message in a UDDI fault message, returning
         * bootstrap stylized html error message
         *
         * @param ex
         * @return
         */
        private String HandleException(Exception ex) {
                if (ex instanceof DispositionReportFaultMessage) {
                        DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                        log.error(ex.getMessage() + (f.detail != null && f.detail.getMessage() != null ? StringEscapeUtils.escapeHtml(f.detail.getMessage()) : ""));
                        log.debug(ex.getMessage(), ex);
                        return ResourceLoader.GetResource(session, "errors.uddi") + " " + StringEscapeUtils.escapeHtml(ex.getMessage()) + " " + (f.detail != null && f.detail.getMessage() != null ? StringEscapeUtils.escapeHtml(f.detail.getMessage()) : "");
                } else if (ex instanceof RemoteException) {
                        RemoteException f = (RemoteException) ex;
                        log.error("RemoteException " + ex.getMessage());
                        log.debug("RemoteException " + ex.getMessage(), ex);
                        return ResourceLoader.GetResource(session, "errors.generic") + " " + StringEscapeUtils.escapeHtml(ex.getMessage()) + " " + (f.detail != null && f.detail.getMessage() != null ? StringEscapeUtils.escapeHtml(f.detail.getMessage()) : "");
                } else if (ex instanceof NullPointerException) {
                        log.error("NPE! Please report! " + ex.getMessage(), ex);
                        log.debug("NPE! Please report! " + ex.getMessage(), ex);
                        return ResourceLoader.GetResource(session, "errors.generic") + " " + StringEscapeUtils.escapeHtml(ex.getMessage());
                } else {
                        log.error("Unexpected error " + ex.getMessage(), ex);
                        //log.debug(ex.getMessage(), ex);
                        return ResourceLoader.GetResource(session, "errors.generic") + " " + StringEscapeUtils.escapeHtml(ex.getMessage());
                }
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
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        fm.setAuthInfo(GetToken());
                                        findTModel = inquiry.findTModel(fm);

                                } else {
                                        throw ex;
                                }
                        }

                        ret.offset = offset;
                        if (findTModel.getListDescription() != null) {
                                ret.displaycount = findTModel.getListDescription().getIncludeCount();
                                ret.totalrecords = findTModel.getListDescription().getActualCount();
                        }
                        if (findTModel.getTModelInfos() == null || findTModel.getTModelInfos().getTModelInfo().isEmpty()) {
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
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        req.setAuthInfo(GetToken());
                                        tModelDetail = inquiry.getTModelDetail(req);

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

        /**
         * A convenience function for GetBusinessDetails
         *
         * @param key
         * @return
         */
        public BusinessEntity GetBusinessDetailsAsObject(String key) {
                return GetBusinessDetails(key);
        }

        /**
         * A convenience function for GetServiceDetail
         *
         * @param key
         * @return
         */
        public BusinessService GetServiceDetailsAsObject(String key) {
                return GetServiceDetail(key);
        }

        /**
         * Returns a specific binding template as an object
         *
         * @param key
         * @return null if not found
         */
        public BindingTemplate GetBindingDetailsAsObject(String key) {
                try {
                        GetBindingDetail r = new GetBindingDetail();
                        r.setAuthInfo(GetToken());
                        r.getBindingKey().add(key);
                        BindingDetail bindingDetail = null;
                        try {
                                bindingDetail = inquiry.getBindingDetail(r);
                        } catch (Exception ex) {
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        r.setAuthInfo(GetToken());
                                        bindingDetail = inquiry.getBindingDetail(r);

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

        /**
         * Returns a tmodel given the key
         *
         * @param key
         * @return null if not found
         */
        public TModel GettModelDetailsAsObject(String key) {
                try {
                        GetTModelDetail r = new GetTModelDetail();
                        r.setAuthInfo(GetToken());
                        r.getTModelKey().add(key);
                        TModelDetail tModelDetail = null;
                        try {
                                tModelDetail = inquiry.getTModelDetail(r);
                        } catch (Exception ex) {
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        r.setAuthInfo(GetToken());
                                        tModelDetail = inquiry.getTModelDetail(r);
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

        /**
         * An enum to help make UDDI searching easier to work with
         */
        public enum FindType {

                /**
                 * search for a business
                 */
                Business,
                /**
                 * search for a related business
                 */
                RelatedBusiness,
                /**
                 * search for a business
                 */
                Service,
                /**
                 * search for a tmodel
                 */
                tModel,
                /**
                 * search for a binding template
                 */
                BindingTemplate
        }

        /**
         * An enum to help make UDDI searching easier to work with
         */
        public enum CriteriaType {

                /**
                 * search by name
                 */
                Name,
                /**
                 * by category
                 */
                Category,
                /**
                 * by key
                 */
                uid,
                /**
                 * by tmodel
                 */
                tmodel,
                /**
                 * by identifier bag
                 */
                identbag
        }

        /**
         * Provides a simple search interface for the complex UDDI search APIs
         *
         * @param type
         * @param criteria
         * @param parameters
         * @param lang
         * @param findqualifier
         * @return stylized html
         */
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
                return ResourceLoader.GetResource(session, "items.unknown");
        }

        private String FindBindingTemplateToHtml(CriteriaType criteria, String parameters, String lang, String[] fq) {
                try {
                        FindBinding fb = new FindBinding();
                        fb.setAuthInfo(GetToken());
                        if (fq != null) {
                                fb.setFindQualifiers(new org.uddi.api_v3.FindQualifiers());
                                for (int i = 0; i < fq.length; i++) {
                                        fb.getFindQualifiers().getFindQualifier().add(fq[i]);
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
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                fb.setAuthInfo(GetToken());
                                                findBusiness = inquiry.findBinding(fb);

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
                                                sb.append(StringEscapeUtils.escapeHtml(findBusiness.getBindingTemplate().get(i).getBindingKey()));
                                        } else {
                                                String t = StringEscapeUtils.escapeHtml(Printers.ListToDescString(findBusiness.getBindingTemplate().get(i).getDescription()));
                                                if (t == null || t.trim().length() == 0) {
                                                        sb.append(StringEscapeUtils.escapeHtml(findBusiness.getBindingTemplate().get(i).getBindingKey()));
                                                } else {
                                                        sb.append(t);
                                                }
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
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                fb.setAuthInfo(GetToken());
                                                findBusiness = inquiry.findBusiness(fb);

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
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        fb.setAuthInfo(GetToken());
                                        findBusiness = inquiry.findRelatedBusinesses(fb);

                                } else {
                                        throw ex;
                                }
                        }

                        if (findBusiness != null && findBusiness.getRelatedBusinessInfos() != null) {
                                StringBuilder sb = new StringBuilder();
                                sb.append("<table class=\"table table-hover\">");
                                sb.append("<tr><th>" + ResourceLoader.GetResource(session, "items.business") + "</th><th>"
                                        + ResourceLoader.GetResource(session, "items.publisherassertions.relationship") + "</th><tr>");
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

                                        sb.append("</td><td>");
                                        sb.append(Printers.PrintPublisherAssertion(findBusiness.getRelatedBusinessInfos().getRelatedBusinessInfo().get(i).getSharedRelationships(), locale));
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
                                        if (GetServiceDetail != null) {
                                                ServiceInfo si = new ServiceInfo();
                                                si.setBusinessKey(GetServiceDetail.getBusinessKey());
                                                si.setServiceKey(GetServiceDetail.getServiceKey());
                                                si.getName().addAll(GetServiceDetail.getName());
                                                findBusiness.getServiceInfos().getServiceInfo().add(si);
                                        }
                                        break;

                        }
                        if (findBusiness == null) {
                                try {
                                        findBusiness = inquiry.findService(fb);
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                fb.setAuthInfo(GetToken());
                                                findBusiness = inquiry.findService(fb);

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
                                        fb.setCategoryBag(new CategoryBag());
                                        KeyedReference kr2 = new KeyedReference();
                                        kr2.setTModelKey(parameters);
                                        fb.getCategoryBag().getKeyedReference().add(kr2);
                                        //TODO
                                        break;
                                case uid:
                                        BusinessEntity t = GetBusinessDetails(parameters);
                                        TModel tmodelDetails = this.getTmodelDetails(parameters);
                                        TModelInfo tmi = new TModelInfo();
                                        tmi.setName(tmodelDetails.getName());
                                        tmi.setTModelKey(tmodelDetails.getTModelKey());
                                        tmi.getDescription().addAll(tmodelDetails.getDescription());
                                        findBusiness.setTModelInfos(new TModelInfos());
                                        findBusiness.getTModelInfos().getTModelInfo().add(tmi);

                                        break;

                        }
                        if (findBusiness == null) {
                                try {
                                        findBusiness = inquiry.findTModel(fb);
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                fb.setAuthInfo(GetToken());
                                                findBusiness = inquiry.findTModel(fb);

                                        } else {
                                                throw ex;
                                        }
                                }

                        }
                        if (findBusiness != null && findBusiness.getTModelInfos() != null) {
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

        /**
         * delete a service
         *
         * @param serviceId
         * @return
         */
        public String deleteService(String serviceId) {
                if (serviceId == null || serviceId.length() == 0) {
                        return ResourceLoader.GetResource(session, "errors.noinput");
                }
                List<String> x = new ArrayList<String>();
                x.add(serviceId.trim());
                return deleteService(x);
        }

        /**
         * deletes a list of services
         *
         * @param serviceId
         * @return
         */
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
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        db.setAuthInfo(GetToken());
                                        publish.deleteService(db);

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
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        db.setAuthInfo(GetToken());
                                        publish.deleteBusiness(db);

                                } else {
                                        throw ex;
                                }
                        }
                } catch (Exception ex) {
                        return HandleException(ex);
                }
                return ResourceLoader.GetResource(session, "actions.delete.business");
        }

        /**
         * delete a tmodel
         *
         * @param bizid
         * @return
         */
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
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        db.setAuthInfo(GetToken());
                                        publish.deleteTModel(db);

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
                        TModelDetail saveTModel = null;
                        sb.getTModel().add(be);
                        //JAXB.marshal(be, System.out);
                        try {
                                saveTModel = publish.saveTModel(sb);
                        } catch (Exception ex) {
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        sb.setAuthInfo(GetToken());
                                        saveTModel = publish.saveTModel(sb);

                                } else {
                                        throw ex;
                                }
                        }
                        //return ResourceLoader.GetResource(session, "actions.saved");
                        return ResourceLoader.GetResource(session, "actions.saved") + " "
                                + "<a href=\"tmodelEditor.jsp?id=" + URLEncoder.encode(saveTModel.getTModel().get(0).getTModelKey(), "UTF8")
                                + "\">" + StringEscapeUtils.escapeHtml(saveTModel.getTModel().get(0).getTModelKey()) + "</a>";
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

                be.getDescription().addAll(Builders.BuildDescription(Builders.MapFilter(request.getParameterMap(), PostBackConstants.DESCRIPTION), PostBackConstants.DESCRIPTION, ResourceLoader.GetResource(session, "items.clicktoedit"), locale));
                be.getOverviewDoc().addAll(Builders.BuildOverviewDocs(Builders.MapFilter(request.getParameterMap(), PostBackConstants.OVERVIEW), PostBackConstants.OVERVIEW, ResourceLoader.GetResource(session, "items.clicktoedit"), locale));

                CategoryBag cb = new CategoryBag();
                cb.getKeyedReference().addAll(Builders.BuildKeyedReference(Builders.MapFilter(request.getParameterMap(), PostBackConstants.CATBAG_KEY_REF), PostBackConstants.CATBAG_KEY_REF, locale));
                cb.getKeyedReferenceGroup().addAll(Builders.BuildKeyedReferenceGroup(Builders.MapFilter(request.getParameterMap(), PostBackConstants.CATBAG_KEY_REF_GRP), PostBackConstants.CATBAG_KEY_REF_GRP, locale));

                if (!cb.getKeyedReference().isEmpty() || !cb.getKeyedReferenceGroup().isEmpty()) {
                        be.setCategoryBag(cb);
                }
                be.setIdentifierBag(Builders.BuildIdentBag(Builders.MapFilter(request.getParameterMap(), PostBackConstants.IDENT_KEY_REF), PostBackConstants.IDENT_KEY_REF, locale));

                return SaveTModel(be);

        }

        /**
         * Converts a UDDI Signature to a readable representation of the signing
         * certificate subject name
         *
         * @param sig
         * @return
         */
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
         * converts a UDDI Signature Type element into a base64 string
         * containing the raw data for the signing certificate, if present
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
                                                                return org.apache.commons.codec.binary.Base64.encodeBase64String(cert.getEncoded());
                                                                //BASE64Encoder encoder = new BASE64Encoder();
                                                                //return encoder.encodeBuffer(cert.getEncoded());

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
         * @return the base64 encoded certification, omitting headers and
         * footers OR "Error" with an error message
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
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        return subscription.getSubscriptions(GetToken());

                                } else {
                                        throw ex;
                                }
                        }
                } catch (Exception ex) {
                        HandleException(ex);
                }
                return null;
        }

        /**
         * attempts to save subscription
         *
         * @param sub
         * @return a success or fail message
         */
        public String AddSubscription(Subscription sub) {
                Holder<List<Subscription>> data = new Holder<List<Subscription>>();
                data.value = new ArrayList<Subscription>();
                data.value.add(sub);
                try {
                        try {
                                subscription.saveSubscription(GetToken(), data);
                        } catch (Exception ex) {
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        subscription.saveSubscription(GetToken(), data);

                                } else {
                                        throw ex;
                                }
                        }
                        return ResourceLoader.GetResource(session, "messages.success")
                                + " <a href=\"editSubscription.jsp?id=" + URLEncoder.encode(data.value.get(0).getSubscriptionKey(), "UTF8")
                                + "\">" + StringEscapeUtils.escapeHtml(data.value.get(0).getSubscriptionKey()) + "</a>";
                } catch (Exception ex) {
                        return HandleException(ex);
                }
        }

        /**
         * Removes/deletes a subscription
         *
         * @param key
         * @return sucess or failure message
         */
        public String RemoveSubscription(String key) {
                DeleteSubscription ds = new DeleteSubscription();
                ds.setAuthInfo(GetToken());
                ds.getSubscriptionKey().add(key);
                try {
                        try {
                                subscription.deleteSubscription(ds);
                                return ResourceLoader.GetResource(session, "actions.deleted");
                        } catch (Exception ex) {
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        ds.setAuthInfo(GetToken());
                                        subscription.deleteSubscription(ds);
                                        return ResourceLoader.GetResource(session, "actions.deleted");

                                } else {
                                        throw ex;
                                }
                        }
                } catch (Exception ex) {
                        return HandleException(ex);
                }
                // return ResourceLoader.GetResource(session, "messages.success");
        }

        /**
         *
         * @param ft
         * @param id
         * @return null if theres an error
         */
        public List<OperationalInfo> GetOperationalInfo(String id) {
                if (id == null || id.equalsIgnoreCase("null")) {
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
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        goi.setAuthInfo(GetToken());
                                        operationalInfo = inquiry.getOperationalInfo(goi);
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
         * This function returns all businesses that the current user
         * owns<br><br>
         * The get_registeredInfo API call is used to get an abbreviated list of
         * all businessEntity and tModel data that are controlled by a
         * publisher. When the registry distinguishes between publishers, this
         * is the individual associated with the credentials passed in the
         * authInfo element. This returned information is intended, for example,
         * for driving tools that display lists of registered information and
         * then provide drill-down features. This is the recommended API to use
         * after a network problem results in an unknown status of saved
         * information.
         *
         * @return
         */
        public RegisteredInfo GetNodeInformation(AtomicReference<String> outmsg) {
                if (outmsg == null) {
                        outmsg = new AtomicReference<String>();
                }
                try {
                        GetRegisteredInfo r = new GetRegisteredInfo();
                        r.setAuthInfo(GetToken());
                        if (r.getAuthInfo() == null) {
                                outmsg.set(ResourceLoader.GetResource(session, "errors.notsignedin"));
                                return null;
                        }
                        r.setInfoSelection(InfoSelection.ALL);
                        RegisteredInfo registeredInfo = null;
                        try {
                                registeredInfo = publish.getRegisteredInfo(r);
                        } catch (Exception ex) {
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        r.setAuthInfo(GetToken());
                                        registeredInfo = publish.getRegisteredInfo(r);

                                } else {
                                        throw ex;
                                }
                        }
                        return registeredInfo;
                } catch (Exception ex) {
                        outmsg.set(HandleException(ex));
                }
                return null;
        }

        /**
         * Gets a list of all assertions for all businesses owned by the current
         * user
         *
         * The get_assertionStatusReport API call provides administrative
         * support for determining the status of current and outstanding
         * publisher assertions that involve any of the business registrations
         * managed by the individual publisher. Using this API, a publisher can
         * see the status of assertions that they have made, as well as see
         * assertions that others have made that involve businessEntity
         * structures controlled by the requesting publisher. See Appendix A
         * Relationships and Publisher Assertions for more information.
         *
         * @param msg
         * @return
         */
        public List<AssertionStatusItem> GetPublisherAssertions(AtomicReference<String> msg) {
                List<AssertionStatusItem> out = new ArrayList<AssertionStatusItem>();

                if (GetToken() == null) {
                        msg.set(ResourceLoader.GetResource(session, "errors.notsignedin"));
                        return null;
                }
                List<AssertionStatusItem> STATUS_COMPLETE = null;

                try {
                        try {
                                STATUS_COMPLETE = publish.getAssertionStatusReport(GetToken(), CompletionStatus.STATUS_COMPLETE);
                        } catch (Exception ex) {
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        STATUS_COMPLETE = publish.getAssertionStatusReport(GetToken(), CompletionStatus.STATUS_COMPLETE);

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
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        STATUS_FROM_KEY_INCOMPLETE = publish.getAssertionStatusReport(GetToken(), CompletionStatus.STATUS_FROM_KEY_INCOMPLETE);

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
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        STATUS_TO_KEY_INCOMPLETE = publish.getAssertionStatusReport(GetToken(), CompletionStatus.STATUS_TO_KEY_INCOMPLETE);

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

        /**
         * deletes a publisher assertion, all fields must match exactly
         *
         * @param tokey
         * @param fromkey
         * @param tmodelkey
         * @param keyname
         * @param keyvalue
         * @return
         */
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
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        dp.setAuthInfo(GetToken());
                                        publish.deletePublisherAssertions(dp);

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
         * Adds a new publisher assertion
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
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        r.setAuthInfo(GetToken());
                                        publish.addPublisherAssertions(r);

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
         * Returns bootstrap stylized html representing all changes in the last
         * refresh
         *
         * @param lastRefresh
         * @return
         * @throws DatatypeConfigurationException
         */
        public String GetNewsFeed(XMLGregorianCalendar lastRefresh) throws DatatypeConfigurationException {
                if (GetToken() == null) {
                        return ToErrorAlert(ResourceLoader.GetResource(session, "errors.notsignedin"));
                }
                if (df == null) {
                        df = DatatypeFactory.newInstance();
                }
                List<Subscription> subscriptions = new ArrayList<Subscription>();
                try {
                        try {
                                subscriptions = subscription.getSubscriptions(GetToken());

                        } catch (Exception ex) {
                                if (isExceptionExpiration(ex)) {
                                        token = null;

                                        subscriptions = subscription.getSubscriptions(GetToken());

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
                if (subscriptions.isEmpty()) {
                        return ToErrorAlert(ResourceLoader.GetResource(session, "errors.subscriptionfeed.nosubs"));
                }
                for (int k = 0; k < subscriptions.size(); k++) {

                        r.setSubscriptionKey(subscriptions.get(k).getSubscriptionKey());
                        SubscriptionResultsList subscriptionResults = null;
                        try {
                                try {
                                        subscriptionResults = subscription.getSubscriptionResults(r);

                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                r.setAuthInfo(GetToken());
                                                subscriptionResults = subscription.getSubscriptionResults(r);

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
                                                append("<table class=\"table table-hover\">");
                                        for (int i = 0; i < subscriptionResults.getAssertionStatusReport().getAssertionStatusItem().size(); i++) {
                                                sb.append("<tr><td>");
                                                sb.append(StringEscapeUtils.escapeHtml(subscriptionResults.getAssertionStatusReport().getAssertionStatusItem().get(i).getFromKey()));
                                                sb.append("</td><td>");
                                                sb.append(StringEscapeUtils.escapeHtml(subscriptionResults.getAssertionStatusReport().getAssertionStatusItem().get(i).getToKey()));
                                                sb.append("</td><td>");
                                                sb.append(StringEscapeUtils.escapeHtml(subscriptionResults.getAssertionStatusReport().getAssertionStatusItem().get(i).getCompletionStatus().toString()));
                                                sb.append("</td></tr>");
                                        }
                                        sb.append("</table><br>");
                                }
                                if (subscriptionResults.getBindingDetail() != null) {
                                        sb.append(ResourceLoader.GetResource(session, "items.subscriptions.bindings")).
                                                append("<table class=\"table table-hover\">");
                                        for (int i = 0; i < subscriptionResults.getBindingDetail().getBindingTemplate().size(); i++) {
                                                sb.append("<tr><td>");
                                                sb.append(StringEscapeUtils.escapeHtml(subscriptionResults.getBindingDetail().getBindingTemplate().get(i).getServiceKey()));
                                                sb.append("</td><td>");
                                                sb.append(StringEscapeUtils.escapeHtml(subscriptionResults.getBindingDetail().getBindingTemplate().get(i).getBindingKey()));
                                                sb.append("</td></tr>");
                                        }
                                        sb.append("</table><br>");
                                }
                                if (subscriptionResults.getBusinessDetail() != null) {
                                        sb.append(ResourceLoader.GetResource(session, "items.subscriptions.business")).
                                                append("<table class=\"table table-hover\">");
                                        for (int i = 0; i < subscriptionResults.getBusinessDetail().getBusinessEntity().size(); i++) {
                                                sb.append("<tr><td><a href=\"businessEditor2.jsp?id=");
                                                sb.append(StringEscapeUtils.escapeHtml(subscriptionResults.getBusinessDetail().getBusinessEntity().get(i).getBusinessKey()));
                                                sb.append("\">");
                                                sb.append(StringEscapeUtils.escapeHtml(subscriptionResults.getBusinessDetail().getBusinessEntity().get(i).getBusinessKey()));
                                                sb.append("<i class=\"icon-large icon-edit\"></i></a></td></tr>");
                                        }
                                        sb.append("</table><br>");
                                }
                                if (subscriptionResults.getRelatedBusinessesList() != null) {
                                        sb.append(ResourceLoader.GetResource(session, "items.subscriptions.assertion2")).
                                                append("<table class=\"table table-hover\">");
                                        // for (int i = 0; i < subscriptionResults.getRelatedBusinessesList().getBusinessKey().size(); i++) {
                                        sb.append("<tr><td>");
                                        sb.append(StringEscapeUtils.escapeHtml(subscriptionResults.getRelatedBusinessesList().getBusinessKey()));
                                        sb.append("</td></tr>");
                                        //}
                                        sb.append("</table><br>");
                                }
                                if (subscriptionResults.getServiceDetail() != null) {
                                        sb.append(ResourceLoader.GetResource(session, "items.subscriptions.services")).
                                                append("<table class=\"table table-hover\">");
                                        for (int i = 0; i < subscriptionResults.getServiceDetail().getBusinessService().size(); i++) {
                                                sb.append("<tr><td><a href=\"serviceEditor.jsp?id=");
                                                sb.append(StringEscapeUtils.escapeHtml(subscriptionResults.getServiceDetail().getBusinessService().get(i).getServiceKey()));
                                                sb.append("\">");
                                                sb.append(StringEscapeUtils.escapeHtml(subscriptionResults.getServiceDetail().getBusinessService().get(i).getServiceKey()));
                                                sb.append("<i class=\"icon-large icon-edit\"></i></a></td></tr>");
                                        }
                                        sb.append("</table><br>");
                                }
                                if (subscriptionResults.getServiceList() != null) {
                                        sb.append(ResourceLoader.GetResource(session, "items.subscriptions.servicelist")).
                                                append("<table class=\"table table-hover\">");
                                        for (int i = 0; i < subscriptionResults.getServiceList().getServiceInfos().getServiceInfo().size(); i++) {
                                                sb.append("<tr><td>");
                                                sb.append(StringEscapeUtils.escapeHtml(subscriptionResults.getServiceList().getServiceInfos().getServiceInfo().get(i).getServiceKey()));

                                                sb.append("</td><td>");
                                                sb.append(StringEscapeUtils.escapeHtml(Printers.ListNamesToString(subscriptionResults.getServiceList().getServiceInfos().getServiceInfo().get(i).getName())));
                                                sb.append("</td></tr>");
                                        }
                                        sb.append("</table><br>");
                                }
                                if (subscriptionResults.getTModelDetail() != null) {
                                        sb.append(ResourceLoader.GetResource(session, "items.subscriptions.tmodels")).append("<br><table class=\"table table-hover\">");
                                        for (int i = 0; i < subscriptionResults.getTModelDetail().getTModel().size(); i++) {
                                                sb.append("<tr><td>");
                                                sb.append(StringEscapeUtils.escapeHtml(subscriptionResults.getTModelDetail().getTModel().get(i).getTModelKey()));
                                                sb.append("</td><td>");
                                                sb.append(StringEscapeUtils.escapeHtml((subscriptionResults.getTModelDetail().getTModel().get(i).getName().getValue())));
                                                sb.append("</td></tr>");
                                        }
                                        sb.append("</table><br>");
                                }
                                if (subscriptionResults.getTModelList() != null) {
                                        sb.append(ResourceLoader.GetResource(session, "items.subscriptions.tmodels2"))
                                                .append("<table class=\"table table-hover\">");
                                        for (int i = 0; i < subscriptionResults.getTModelList().getTModelInfos().getTModelInfo().size(); i++) {
                                                sb.append("<tr><td><a href=\"serviceEditor.jsp?id=");
                                                sb.append(StringEscapeUtils.escapeHtml(subscriptionResults.getTModelList().getTModelInfos().getTModelInfo().get(i).getTModelKey()));
                                                sb.append("\">");
                                                sb.append(StringEscapeUtils.escapeHtml(subscriptionResults.getTModelList().getTModelInfos().getTModelInfo().get(i).getTModelKey()));
                                                sb.append("<i class=\"icon-large icon-edit\"></i></a></td><td>");
                                                sb.append(StringEscapeUtils.escapeHtml((subscriptionResults.getTModelList().getTModelInfos().getTModelInfo().get(i).getName().getValue())));
                                                sb.append("</td></tr>");
                                        }
                                        sb.append("</table>");
                                }

                        }
                }
                return sb.toString();
        }

        /**
         * Searches first for a service, then iterates through to identify
         * bindings matching the specified criteria. Since UDDI does not have a
         * find_binding API, this is as good as it gets.
         *
         * @param keyword
         * @param lang
         * @param offset
         * @param maxrecords
         * @param isChooser
         * @return
         */
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
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        fs.setAuthInfo(GetToken());
                                        findService = inquiry.findService(fs);

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
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        fs.setAuthInfo(GetToken());
                                        serviceDetail = inquiry.getServiceDetail(gs);

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
         * Get a custody transfer token for giving away control of the specified
         * business or tmodel keys
         *
         * authInfo: This OPTIONAL argument is an element that contains an
         * authentication token. Authentication tokens are obtained using the
         * get_authToken API call or through some other means external to this
         * specification, and represent the identity of the publisher at a UDDI
         * node.
         *
         * · transferToken: This is a known transferToken obtained by a
         * publisher at the node where the get_transferToken API was invoked.
         *
         * · keyBag: One or more uddiKeys associated either with businessEntity
         * or tModel entities owned by the publisher that were to be transferred
         * to some other publisher and/or node in the registry as the result of
         * invocation of get_transferToken. At least one businessKey or
         * tModelKey must be provided in a keyBag.
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
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        custody.getTransferToken(GetToken(), keys, nodeid, outExpires, outToken);

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
         * @return The discard_transferToken API is a client API used to discard
         * a transferToken obtained through the get_transferToken API at the
         * same node. This API accepts either a transferToken or a keyBag as
         * parameters to remove the permission to transfer data associated with
         * a particular transferToken. If a keyBag is provided, all tokens
         * corresponding to the keys in the keyBag will be discarded and will no
         * longer be valid for custody or ownership transfer after the
         * discard_transferToken is processed, irrespective of whether the keys
         * match any known business or tmodelKey values. In the event that the
         * keyBag represents a subset of the keyBag for one or more
         * transferToken elements, the transferToken is discarded and will no
         * longer be valid for transferring any entity. If the token passed in
         * the transferToken argument does not match an existing token known to
         * the system, no action is taken and success is reported. Keys in the
         * keyBag argument that do not have a corresponding token are ignored.
         */
        public String DiscardToken(String tokenxml) {
                DiscardTransferToken r = new DiscardTransferToken();
                r.setAuthInfo(GetToken());
                r.setTransferToken(JAXB.unmarshal(new StringReader(tokenxml), TransferToken.class));

                try {
                        try {
                                custody.discardTransferToken(r);
                        } catch (Exception ex) {
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        r.setAuthInfo(GetToken());
                                        custody.discardTransferToken(r);

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
         * Accepts a transfer token and transfers the entities.
         *
         * @param tokenXML
         * @param keyBagXML
         * @return
         */
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
                                if (isExceptionExpiration(ex)) {
                                        token = null;
                                        te.setAuthInfo(GetToken());
                                        custody.transferEntities(te);

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
         * returns a subscription by id, since UDDI does not provide this
         * function, it simply gets all of them for the current user then
         * filters out the requested item
         *
         * @param id
         * @return null if not found
         */
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

        /**
         *
         * @param request
         * @param service
         * @param method
         * @return
         * @see UDDIRequestsAsXML
         */
        public String SendAdvancedQuery(Object request, String service, String method) {
                if (service.equalsIgnoreCase(UDDIRequestsAsXML.inquiry)) {
                        return SendAdvancedQueryInquiry(method, request);
                }
                if (service.equalsIgnoreCase(UDDIRequestsAsXML.publish)) {
                        return SendAdvancedQueryPublish(method, request);
                }
                if (service.equalsIgnoreCase(UDDIRequestsAsXML.custody)) {
                        return SendAdvancedQueryCustody(method, request);
                }
                if (service.equalsIgnoreCase(UDDIRequestsAsXML.subscription)) {
                        return SendAdvancedQuerySubscription(method, request);
                }
                return "Unknown error";
        }

        private String SendAdvancedQueryInquiry(String method, Object request) {
                Object response = null;
                try {
                        if (method.equalsIgnoreCase("findBinding")) {
                                ((FindBinding) request).setAuthInfo(GetToken());
                                try {
                                        response = inquiry.findBinding((FindBinding) request);
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                ((FindBinding) request).setAuthInfo(GetToken());
                                                response = inquiry.findBinding((FindBinding) request);

                                        } else {
                                                throw ex;
                                        }
                                }
                        }
                        if (method.equalsIgnoreCase("findBusiness")) {
                                ((FindBusiness) request).setAuthInfo(GetToken());
                                try {
                                        response = inquiry.findBusiness((FindBusiness) request);
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                ((FindBusiness) request).setAuthInfo(GetToken());
                                                response = inquiry.findBusiness((FindBusiness) request);

                                        } else {
                                                throw ex;
                                        }
                                }

                        }
                        if (method.equalsIgnoreCase("findService")) {
                                ((FindService) request).setAuthInfo(GetToken());
                                try {
                                        response = inquiry.findService((FindService) request);
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                ((FindService) request).setAuthInfo(GetToken());
                                                response = inquiry.findService((FindService) request);

                                        } else {
                                                throw ex;
                                        }
                                }

                        }
                        if (method.equalsIgnoreCase("findRelatedBusines")) {
                                ((FindRelatedBusinesses) request).setAuthInfo(GetToken());
                                try {
                                        response = inquiry.findRelatedBusinesses((FindRelatedBusinesses) request);
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                ((FindRelatedBusinesses) request).setAuthInfo(GetToken());
                                                response = inquiry.findRelatedBusinesses((FindRelatedBusinesses) request);

                                        } else {
                                                throw ex;
                                        }
                                }

                        }
                        if (method.equalsIgnoreCase("findTModel")) {
                                ((FindTModel) request).setAuthInfo(GetToken());

                                try {
                                        response = inquiry.findTModel((FindTModel) request);
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                ((FindTModel) request).setAuthInfo(GetToken());
                                                response = inquiry.findTModel((FindTModel) request);

                                        } else {
                                                throw ex;
                                        }
                                }

                        }
                        if (method.equalsIgnoreCase("getBindingDetail")) {
                                ((GetBindingDetail) request).setAuthInfo(GetToken());
                                try {
                                        response = inquiry.getBindingDetail((GetBindingDetail) request);
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                ((GetBindingDetail) request).setAuthInfo(GetToken());
                                                response = inquiry.getBindingDetail((GetBindingDetail) request);

                                        } else {
                                                throw ex;
                                        }
                                }

                        }
                        if (method.equalsIgnoreCase("getBusinessDetail")) {
                                ((GetBusinessDetail) request).setAuthInfo(GetToken());

                                try {
                                        response = inquiry.getBusinessDetail((GetBusinessDetail) request);
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                ((GetBusinessDetail) request).setAuthInfo(GetToken());
                                                response = inquiry.getBusinessDetail((GetBusinessDetail) request);

                                        } else {
                                                throw ex;
                                        }
                                }

                        }
                        if (method.equalsIgnoreCase("getServiceDetail")) {
                                ((GetServiceDetail) request).setAuthInfo(GetToken());

                                try {
                                        response = inquiry.getServiceDetail((GetServiceDetail) request);
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                ((GetServiceDetail) request).setAuthInfo(GetToken());
                                                response = inquiry.getServiceDetail((GetServiceDetail) request);

                                        } else {
                                                throw ex;
                                        }
                                }

                        }
                        if (method.equalsIgnoreCase("getOperationalInfo")) {
                                ((GetOperationalInfo) request).setAuthInfo(GetToken());
                                response = inquiry.getOperationalInfo((GetOperationalInfo) request);
                                try {
                                        response = inquiry.findBinding((FindBinding) request);
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                response = inquiry.findBinding((FindBinding) request);

                                        } else {
                                                throw ex;
                                        }
                                }

                        }
                        if (method.equalsIgnoreCase("getTModelDetail")) {
                                ((GetTModelDetail) request).setAuthInfo(GetToken());
                                response = inquiry.getTModelDetail((GetTModelDetail) request);
                                try {
                                        response = inquiry.findBinding((FindBinding) request);
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                response = inquiry.findBinding((FindBinding) request);

                                        } else {
                                                throw ex;
                                        }
                                }
                        }
                        if (response == null) {
                                return ResourceLoader.GetResource(session, "actions.success");
                        }
                        StringWriter sw = new StringWriter();
                        JAXB.marshal(response, sw);
                        return sw.toString();
                } catch (Exception ex) {
                        return HandleException(ex);
                }
        }

        private String SendAdvancedQueryPublish(String method, Object request) {
                Object response = null;
                try {
                        if (method.equalsIgnoreCase("addPublisherAssertions")) {
                                ((AddPublisherAssertions) request).setAuthInfo(GetToken());
                                try {
                                        publish.addPublisherAssertions((AddPublisherAssertions) request);
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                ((AddPublisherAssertions) request).setAuthInfo(GetToken());
                                                publish.addPublisherAssertions((AddPublisherAssertions) request);

                                        } else {
                                                throw ex;
                                        }
                                }
                        }
                        if (method.equalsIgnoreCase("deleteBinding")) {
                                try {
                                        ((DeleteBinding) request).setAuthInfo(GetToken());
                                        publish.deleteBinding((DeleteBinding) request);
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                ((DeleteBinding) request).setAuthInfo(GetToken());
                                                publish.deleteBinding((DeleteBinding) request);

                                        } else {
                                                throw ex;
                                        }
                                }
                        }
                        if (method.equalsIgnoreCase("deleteBusiness")) {
                                try {
                                        ((DeleteBusiness) request).setAuthInfo(GetToken());
                                        publish.deleteBusiness((DeleteBusiness) request);
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                ((DeleteBusiness) request).setAuthInfo(GetToken());
                                                publish.deleteBusiness((DeleteBusiness) request);

                                        } else {
                                                throw ex;
                                        }
                                }
                        }
                        if (method.equalsIgnoreCase("deletePublisherAssertions")) {
                                try {
                                        ((DeletePublisherAssertions) request).setAuthInfo(GetToken());
                                        publish.deletePublisherAssertions((DeletePublisherAssertions) request);
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                ((DeleteBusiness) request).setAuthInfo(GetToken());
                                                publish.deletePublisherAssertions((DeletePublisherAssertions) request);

                                        } else {
                                                throw ex;
                                        }
                                }
                        }
                        if (method.equalsIgnoreCase("deleteService")) {
                                try {
                                        ((DeleteService) request).setAuthInfo(GetToken());
                                        publish.deleteService((DeleteService) request);
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                ((DeleteBusiness) request).setAuthInfo(GetToken());
                                                publish.deleteService((DeleteService) request);

                                        } else {
                                                throw ex;
                                        }
                                }
                        }
                        if (method.equalsIgnoreCase("deleteTModel")) {
                                try {
                                        ((DeleteTModel) request).setAuthInfo(GetToken());
                                        publish.deleteTModel((DeleteTModel) request);
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                ((DeleteTModel) request).setAuthInfo(GetToken());
                                                publish.deleteTModel((DeleteTModel) request);

                                        } else {
                                                throw ex;
                                        }
                                }
                        }
                        if (method.equalsIgnoreCase("getAssertionStatusReport")) {
                                CompletionStatus stat = ((GetAssertionStatusReport) request).getCompletionStatus();
                                try {
                                        response = publish.getAssertionStatusReport(GetToken(), stat);
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                response = publish.getAssertionStatusReport(GetToken(), stat);

                                        } else {
                                                throw ex;
                                        }
                                }

                        }
                        if (method.equalsIgnoreCase("getPublisherAssertions")) {
                                try {
                                        response = publish.getPublisherAssertions(GetToken());
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                response = publish.getPublisherAssertions(GetToken());

                                        } else {
                                                throw ex;
                                        }
                                }
                        }
                        if (method.equalsIgnoreCase("getRegisteredInfo")) {
                                try {
                                        ((GetRegisteredInfo) request).setAuthInfo(GetToken());
                                        response = publish.getRegisteredInfo((GetRegisteredInfo) request);
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                ((GetRegisteredInfo) request).setAuthInfo(GetToken());
                                                publish.getRegisteredInfo((GetRegisteredInfo) request);

                                        } else {
                                                throw ex;
                                        }
                                }
                        }
                        if (method.equalsIgnoreCase("saveBinding")) {
                                try {
                                        ((SaveBinding) request).setAuthInfo(GetToken());
                                        response = publish.saveBinding((SaveBinding) request);
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                ((SaveBinding) request).setAuthInfo(GetToken());
                                                publish.saveBinding((SaveBinding) request);

                                        } else {
                                                throw ex;
                                        }
                                }
                        }
                        if (method.equalsIgnoreCase("saveBusiness")) {
                                try {
                                        ((SaveBusiness) request).setAuthInfo(GetToken());
                                        response = publish.saveBusiness((SaveBusiness) request);
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                ((SaveBusiness) request).setAuthInfo(GetToken());
                                                publish.saveBusiness((SaveBusiness) request);

                                        } else {
                                                throw ex;
                                        }
                                }
                        }
                        if (method.equalsIgnoreCase("saveTModel")) {
                                try {
                                        ((SaveTModel) request).setAuthInfo(GetToken());
                                        response = publish.saveTModel((SaveTModel) request);
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                ((SaveTModel) request).setAuthInfo(GetToken());
                                                publish.saveTModel((SaveTModel) request);

                                        } else {
                                                throw ex;
                                        }
                                }
                        }
                        if (method.equalsIgnoreCase("saveService")) {
                                try {
                                        ((SaveService) request).setAuthInfo(GetToken());
                                        response = publish.saveService((SaveService) request);
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                ((SaveService) request).setAuthInfo(GetToken());
                                                publish.saveService((SaveService) request);

                                        } else {
                                                throw ex;
                                        }
                                }
                        }
                        if (method.equalsIgnoreCase("setPublisherAssertions")) {
                                Holder<List<PublisherAssertion>> list = new Holder<List<PublisherAssertion>>();
                                list.value = ((SetPublisherAssertions) request).getPublisherAssertion();
                                try {
                                        publish.setPublisherAssertions(GetToken(), list);
                                        response = list.value;
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                ((SetPublisherAssertions) request).setAuthInfo(GetToken());
                                                publish.setPublisherAssertions(GetToken(), list);
                                                response = list.value;

                                        } else {
                                                throw ex;
                                        }
                                }

                        }
                        if (response == null) {
                                return "The operation completed without error";
                        }
                        StringWriter sw = new StringWriter();
                        JAXB.marshal(response, sw);
                        return sw.toString();
                } catch (Exception ex) {
                        return HandleException(ex);
                }
        }

        private String SendAdvancedQueryCustody(String method, Object request) {
                Object response = null;
                try {

                        if (method.equalsIgnoreCase("discardTransferToken")) {
                                try {
                                        ((DiscardTransferToken) request).setAuthInfo(GetToken());
                                        custody.discardTransferToken((DiscardTransferToken) request);
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                ((DiscardTransferToken) request).setAuthInfo(GetToken());
                                                custody.discardTransferToken((DiscardTransferToken) request);

                                        } else {
                                                throw ex;
                                        }
                                }
                        }
                        if (method.equalsIgnoreCase("getTransferToken")) {
                                GetTransferToken r = ((GetTransferToken) request);
                                TransferToken tt = new TransferToken();
                                Holder<String> node = new Holder<String>();
                                Holder<XMLGregorianCalendar> xcal = new Holder<XMLGregorianCalendar>();
                                Holder<byte[]> ttoken = new Holder<byte[]>();
                                try {
                                        custody.getTransferToken(GetToken(), r.getKeyBag(), node, xcal, ttoken);
                                        tt.setNodeID(node.value);
                                        tt.setOpaqueToken(ttoken.value);
                                        tt.setExpirationTime(xcal.value);
                                        response = tt;
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                custody.getTransferToken(GetToken(), r.getKeyBag(), node, xcal, ttoken);
                                                tt.setNodeID(node.value);
                                                tt.setOpaqueToken(ttoken.value);
                                                tt.setExpirationTime(xcal.value);
                                                response = tt;

                                        } else {
                                                throw ex;
                                        }
                                }
                        }
                        if (method.equalsIgnoreCase("transferEntities")) {
                                try {
                                        ((TransferEntities) request).setAuthInfo(GetToken());
                                        custody.transferEntities((TransferEntities) request);
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                ((TransferEntities) request).setAuthInfo(GetToken());
                                                custody.transferEntities((TransferEntities) request);

                                        } else {
                                                throw ex;
                                        }
                                }
                        }

                        if (response == null) {
                                return "The operation completed without error";
                        }
                        StringWriter sw = new StringWriter();
                        JAXB.marshal(response, sw);
                        return sw.toString();
                } catch (Exception ex) {
                        return HandleException(ex);
                }
        }

        private String SendAdvancedQuerySubscription(String method, Object request) {
                Object response = null;
                try {
                        if (method.equalsIgnoreCase("deleteSubscription")) {
                                try {
                                        ((DeleteSubscription) request).setAuthInfo(GetToken());
                                        subscription.deleteSubscription((DeleteSubscription) request);
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                ((DeleteSubscription) request).setAuthInfo(GetToken());
                                                subscription.deleteSubscription((DeleteSubscription) request);

                                        } else {
                                                throw ex;
                                        }
                                }
                        }
                        if (method.equalsIgnoreCase("getSubscriptionResults")) {
                                try {
                                        ((GetSubscriptionResults) request).setAuthInfo(GetToken());
                                        response = subscription.getSubscriptionResults((GetSubscriptionResults) request);
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                ((GetSubscriptionResults) request).setAuthInfo(GetToken());
                                                subscription.getSubscriptionResults((GetSubscriptionResults) request);

                                        } else {
                                                throw ex;
                                        }
                                }
                        }
                        if (method.equalsIgnoreCase("getSubscriptions")) {
                                try {

                                        response = subscription.getSubscriptions(GetToken());
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;

                                                response = subscription.getSubscriptions(GetToken());

                                        } else {
                                                throw ex;
                                        }
                                }
                        }
                        if (method.equalsIgnoreCase("saveSubscription")) {
                                SaveSubscription ss = (SaveSubscription) request;
                                Holder<List<Subscription>> h = new Holder<List<Subscription>>(ss.getSubscription());
                                try {

                                        subscription.saveSubscription(GetToken(), h);
                                        response = h.value;
                                } catch (Exception ex) {
                                        if (isExceptionExpiration(ex)) {
                                                token = null;
                                                subscription.saveSubscription(GetToken(), h);
                                                response = h.value;

                                        } else {
                                                throw ex;
                                        }
                                }
                        }
                        if (response == null) {
                                return "The operation completed without error";
                        }
                        StringWriter sw = new StringWriter();
                        JAXB.marshal(response, sw);
                        return sw.toString();
                } catch (Exception ex) {
                        return HandleException(ex);
                }
        }

        /**
         * returns true if automatic logouts are configured
         *
         * @return
         */
        public boolean isAutoLogoutEnabled() {

                if (clientConfig == null) {
                        EnsureConfig();
                }
                String val = clientConfig.getConfiguration().getString(PROP_AUTO_LOGOUT);
                if (val == null) {
                        return false;
                }
                try {
                        return Boolean.parseBoolean(val);
                } catch (Exception ex) {
                        log.warn("unable to parse the value for " + PROP_AUTO_LOGOUT + " in uddi.xml, defaulting to false", ex);
                }
                return false;
        }

        /**
         * defaults to 15 minutes if not defined
         *
         * @return
         */
        public long GetAutoLogoutDuration() {
                String val = clientConfig.getConfiguration().getString(PROP_AUTO_LOGOUT_TIMER);
                if (val == null) {
                        return 15 * 60 * 1000;
                }
                try {
                        return Long.parseLong(val);
                } catch (Exception ex) {
                        log.warn("unable to parse the value for " + PROP_AUTO_LOGOUT_TIMER + " in uddi.xml, defaulting to 15 minutes", ex);
                }
                return 15 * 60 * 1000;
        }

        /**
         * If false, the configuration page will be available from anywhere. If
         * true, it will only be accessible from the server hosting juddi-gui.
         * if not defined, the result is true.
         *
         * @return
         */
        public boolean isAdminLocalhostOnly() {
                return clientConfig.getConfiguration().getBoolean(PROP_ADMIN_LOCALHOST_ONLY, true);
        }
}
