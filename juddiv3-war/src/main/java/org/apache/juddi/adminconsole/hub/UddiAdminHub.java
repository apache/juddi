package org.apache.juddi.adminconsole.hub;

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
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXB;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.BindingProvider;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api_v3.Clerk;
import org.apache.juddi.api_v3.ClerkDetail;
import org.apache.juddi.api_v3.ClientSubscriptionInfoDetail;
import org.apache.juddi.api_v3.DeleteClientSubscriptionInfo;
import org.apache.juddi.api_v3.DeletePublisher;
import org.apache.juddi.api_v3.GetAllPublisherDetail;
import org.apache.juddi.api_v3.GetPublisherDetail;
import org.apache.juddi.api_v3.Node;
import org.apache.juddi.api_v3.Publisher;
import org.apache.juddi.api_v3.PublisherDetail;
import org.apache.juddi.api_v3.SaveClerk;
import org.apache.juddi.api_v3.SaveClientSubscriptionInfo;
import org.apache.juddi.api_v3.SaveNode;
import org.apache.juddi.api_v3.SavePublisher;
import org.apache.juddi.api_v3.SyncSubscription;
import org.apache.juddi.api_v3.SyncSubscriptionDetail;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.ClientConfig;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDINode;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.apache.juddi.adminconsole.AES;
import org.apache.juddi.adminconsole.resources.ResourceLoader;
import org.apache.juddi.api_v3.ClientSubscriptionInfo;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.DeleteTModel;
import org.uddi.api_v3.DiscardAuthToken;
import org.uddi.api_v3.DispositionReport;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.api_v3.Name;

import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * UddiHub - The hub acts as a single point for managing browser to uddi
 * services. At most 1 instance is allowed per http session. In general, all
 * methods in the class trigger web service call outs. All callouts also support
 * expired UDDI tokens and will attempt to reauthenticate and retry the request.
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class UddiAdminHub {

        /**
         * The logger name
         */
        public static final String LOGGER_NAME = "org.apache.juddi";
        transient AuthStyle style = null;
        Properties properties = null;
        /**
         * The Log4j logger. This is also referenced from the Builders class,
         * thus it is public
         */
        public static final Log log = LogFactory.getLog(LOGGER_NAME);

        private UddiAdminHub() throws DatatypeConfigurationException {
                //    df = DatatypeFactory.newInstance();
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

        /**
         * This kills any authentication tokens, logs the user out and nulls out
         * all services
         */
        public void die() {
                if (token != null && security != null) {
                        DiscardAuthToken da = new DiscardAuthToken();
                        da.setAuthInfo(token);
                        try {
                                security.discardAuthToken(da);
                        } catch (Exception ex) {
                                HandleException(ex);
                        }
                }
                token = null;
                security = null;
                juddi = null;
        }
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

        private transient UDDISecurityPortType security = null;
        private transient JUDDIApiPortType juddi = null;
        private transient String token = null;
        private transient HttpSession session;
        private transient Transport transport = null;
        private transient ClientConfig clientConfig;
        private static final long serialVersionUID = 1L;
        private String nodename = "default";
        private final String clientName = "juddigui";
        private boolean WS_Transport = false;
        private boolean WS_securePorts = false;

        /**
         * This is the singleton accessor UddiHub. There should be at most 1
         * instance per HTTP Session (user login)
         *
         * @param application
         * @param _session
         * @return
         * @throws Exception
         */
        public static UddiAdminHub getInstance(ServletContext application, HttpSession _session) throws Exception {
                Object j = _session.getAttribute("hub");
                if (j == null) {
                        UddiAdminHub hub = new UddiAdminHub(application, _session);
                        _session.setAttribute("hub", hub);
                        return hub;
                }

                return (UddiAdminHub) j;
        }
        String locale = "en";

        private UddiAdminHub(ServletContext application, HttpSession _session) throws Exception {
                URL prop = application.getResource("/WEB-INF/config.properties");
                if (prop == null) {
                        application.getResource("WEB-INF/config.properties");
                }
                if (prop == null) {
                        throw new Exception("Cannot locate the configuration file.");
                }
                session = _session;

                InputStream in = prop.openStream();
                Properties p = new Properties();
                p.load(in);
                in.close();
                session = _session;
                properties = p;
                EnsureConfig();
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

                                transport = client.getTransport(nodename);
                                security = transport.getUDDISecurityService();
                                juddi = transport.getJUDDIApiService();
                                if (WS_Transport) {
                                        if (uddiNode.getJuddiApiUrl().toLowerCase().startsWith("https://")
                                                && (uddiNode.getSecurityUrl() != null && uddiNode.getSecurityUrl().toLowerCase().startsWith("https://"))) {
                                                WS_securePorts = true;
                                        }
                                }

                        } catch (Exception ex) {
                                HandleException(ex);
                        }
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
         * Handles all API calls to the juddi web service
         *
         * @param parameters
         * @return
         */
        public String go(HttpServletRequest parameters) {
                try {
                        String action = parameters.getParameter("soapaction");
                        if (action.equalsIgnoreCase("adminDelete_tmodel")) {
                                return adminDelete_tmodel(parameters);
                        }
                        if (action.equalsIgnoreCase("delete_ClientSubscriptionInfo")) {
                                return delete_ClientSubscriptionInfo(parameters);
                        }
                        if (action.equalsIgnoreCase("delete_publisher")) {
                                return delete_publisher(parameters);
                        }
                        if (action.equalsIgnoreCase("getAllPublisherDetail")) {
                                return getAllPublisherDetail(parameters);
                        }
                        if (action.equalsIgnoreCase("get_publisherDetail")) {
                                return get_publisherDetail(parameters);
                        }
                        if (action.equalsIgnoreCase("invoke_SyncSubscription")) {
                                return invoke_SyncSubscription(parameters);
                        }
                        if (action.equalsIgnoreCase("save_Clerk")) {
                                return save_Clerk(parameters);
                        }
                        if (action.equalsIgnoreCase("save_ClientSubscriptionInfo")) {
                                return save_ClientSubscriptionInfo(parameters);
                        }
                        if (action.equalsIgnoreCase("save_Node")) {
                                return save_Node(parameters);
                        }
                        if (action.equalsIgnoreCase("save_publisher")) {
                                return save_publisher(parameters);
                        }
                } catch (Exception ex) {
                        return "Error!" + HandleException(ex);
                }
                return "not yet implemented!";
        }

        private String save_Clerk(HttpServletRequest parameters) {
                SaveClerk sc = new SaveClerk();
                sc.setAuthInfo(GetToken());
                Clerk c = new Clerk();
                c.setName(parameters.getParameter("CLERKsetName"));
                Node node = new Node();
                node.setClientName(parameters.getParameter("CLERKNODEsetClientName"));
                node.setCustodyTransferUrl(parameters.getParameter("CLERKNODEsetCustodyTransferUrl"));
                node.setDescription(parameters.getParameter("CLERKNODEsetDescription"));
                node.setFactoryInitial(parameters.getParameter("CLERKNODEsetFactoryInitial"));
                node.setFactoryNamingProvider(parameters.getParameter("CLERKNODEsetFactoryNamingProvider"));
                node.setFactoryURLPkgs(parameters.getParameter("CLERKNODEsetFactoryURLPkgs"));
                node.setInquiryUrl(parameters.getParameter("CLERKNODEsetInquiryUrl"));
                node.setJuddiApiUrl(parameters.getParameter("CLERKNODEsetJuddiApiUrl"));
                node.setName(parameters.getParameter("CLERKNODEsetName"));
                node.setProxyTransport(parameters.getParameter("CLERKNODEsetProxyTransport"));
                node.setPublishUrl(parameters.getParameter("CLERKNODEsetPublishUrl"));
                node.setReplicationUrl(parameters.getParameter("CLERKNODEsetReplicationUrl"));
                node.setSecurityUrl(parameters.getParameter("CLERKNODEsetSecurityUrl"));
                node.setSubscriptionListenerUrl(parameters.getParameter("CLERKNODEsetSubscriptionListenerUrl"));
                node.setSubscriptionUrl(parameters.getParameter("CLERKNODEsetSubscriptionUrl"));
                c.setNode(node);
                c.setPassword(parameters.getParameter("CLERKsetPassword"));
                c.setPublisher(parameters.getParameter("CLERKsetPublisher"));

                sc.getClerk().add(c);
                try {
                        juddi.saveClerk(sc);
                } catch (Exception ex) {
                        if (isExceptionExpiration(ex)) {
                                token = null;
                                sc.setAuthInfo(GetToken());
                                try {
                                        juddi.saveClerk(sc);
                                } catch (Exception ex1) {
                                        return "Error!" + HandleException(ex1);
                                }

                        } else {
                                return "Error!" + HandleException(ex);
                        }
                }
                return "Success";
        }

        private String save_Node(HttpServletRequest parameters) {
                SaveNode sn = new SaveNode();
                sn.setAuthInfo(GetToken());
                Node node = new Node();
                node.setClientName(parameters.getParameter("NODEsetClientName"));
                node.setCustodyTransferUrl(parameters.getParameter("NODEsetCustodyTransferUrl"));
                node.setDescription(parameters.getParameter("NODEsetDescription"));
                node.setFactoryInitial(parameters.getParameter("NODEsetFactoryInitial"));
                node.setFactoryNamingProvider(parameters.getParameter("NODEsetFactoryNamingProvider"));
                node.setFactoryURLPkgs(parameters.getParameter("NODEsetFactoryURLPkgs"));
                node.setInquiryUrl(parameters.getParameter("NODEsetInquiryUrl"));
                node.setJuddiApiUrl(parameters.getParameter("NODEsetJuddiApiUrl"));
                node.setName(parameters.getParameter("NODEsetName"));
                node.setProxyTransport(parameters.getParameter("NODEsetProxyTransport"));
                node.setPublishUrl(parameters.getParameter("NODEsetPublishUrl"));
                node.setReplicationUrl(parameters.getParameter("NODEsetReplicationUrl"));
                node.setSecurityUrl(parameters.getParameter("NODEsetSecurityUrl"));
                node.setSubscriptionListenerUrl(parameters.getParameter("NODEsetSubscriptionListenerUrl"));
                node.setSubscriptionUrl(parameters.getParameter("NODEsetSubscriptionUrl"));
                sn.getNode().add(node);

                try {
                        juddi.saveNode(sn);
                } catch (Exception ex) {
                        if (isExceptionExpiration(ex)) {
                                token = null;
                                sn.setAuthInfo(GetToken());
                                try {
                                        juddi.saveNode(sn);
                                } catch (Exception ex1) {
                                        return "Error!" + HandleException(ex1);
                                }

                        } else {
                                return "Error!" + HandleException(ex);
                        }
                }
                return "Success";
        }

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

        private String GetToken() {
                EnsureConfig();
                if (style != AuthStyle.UDDI_AUTH) {
                        BindingProvider bp = null;
                        if (WS_Transport) {
                                Map<String, Object> context = null;
                                bp = (BindingProvider) juddi;
                                context = bp.getRequestContext();
                                context.put(BindingProvider.USERNAME_PROPERTY, session.getAttribute("username"));
                                context.put(BindingProvider.USERNAME_PROPERTY, session.getAttribute(AES.Decrypt("password", (String) properties.get("key"))));
                        }
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

        private String delete_publisher(HttpServletRequest parameters) {
                DeletePublisher sb = new DeletePublisher();
                sb.setAuthInfo(GetToken());
                sb.getPublisherId().add(parameters.getParameter("delete_publisherKEY"));
                try {
                        juddi.deletePublisher(sb);
                } catch (Exception ex) {
                        if (isExceptionExpiration(ex)) {
                                token = null;
                                sb.setAuthInfo(GetToken());
                                try {
                                        juddi.deletePublisher(sb);
                                } catch (Exception e) {
                                        return HandleException(e);
                                }

                        } else {
                                return HandleException(ex);
                        }
                }
                return "Success";
        }

        private String getAllPublisherDetail(HttpServletRequest parameters) {
                StringBuilder ret = new StringBuilder();
                GetAllPublisherDetail sb = new GetAllPublisherDetail();
                sb.setAuthInfo(GetToken());
                PublisherDetail d = null;
                try {
                        d = juddi.getAllPublisherDetail(sb);
                } catch (Exception ex) {
                        if (isExceptionExpiration(ex)) {
                                token = null;
                                sb.setAuthInfo(GetToken());
                                try {
                                        d = juddi.getAllPublisherDetail(sb);
                                } catch (Exception ex1) {
                                        return HandleException(ex);
                                }

                        } else {
                                return HandleException(ex);
                        }
                }
                if (d != null) {
                        ret.append("<table class=\"table table-hover\"><tr><th>Name</th><th>Info</th></tr>");
                        for (int i = 0; i < d.getPublisher().size(); i++) {
                                ret.append("<tr><td>").append(StringEscapeUtils.escapeHtml(d.getPublisher().get(i).getPublisherName()))
                                        .append("</td><td>");
                                ret.append(PrintPublisherDetail(d.getPublisher().get(i)))
                                        .append("</td></tr>");
                        }
                        ret.append("</table>");
                } else {
                        ret.append("No data returned");
                }
                return ret.toString();
        }

        private String PrintPublisherDetail(Publisher p) {
                StringBuilder ret = new StringBuilder();

                ret.append("Authorized Name = ").append(StringEscapeUtils.escapeHtml(p.getAuthorizedName()))
                        .append("<br>")
                        .append("Publisher Name = ").append(StringEscapeUtils.escapeHtml(p.getPublisherName()))
                        .append("<br>")
                        .append("Email = ")
                        .append(StringEscapeUtils.escapeHtml(p.getEmailAddress()))
                        .append("<br>")
                        .append("Administrator = ")
                        .append((p.isIsAdmin()))
                        .append("<br>")
                        .append("Enabled = ")
                        .append((p.isIsEnabled()))
                        .append("<br>")
                        .append("Max Bindings per = ")
                        .append(p.getMaxBindingsPerService())
                        .append("<br>")
                        .append("Max Businesses = ")
                        .append(p.getMaxBusinesses())
                        .append("<br>")
                        .append("Max Services per = ")
                        .append(p.getMaxServicePerBusiness())
                        .append("<br>")
                        .append("Max tModels = ")
                        .append(p.getMaxTModels())
                        .append("");
                return ret.toString();
        }

        private String get_publisherDetail(HttpServletRequest parameters) {
                StringBuilder ret = new StringBuilder();
                GetPublisherDetail sb = new GetPublisherDetail();
                sb.getPublisherId().add(parameters.getParameter("get_publisherDetailKEY"));
                sb.setAuthInfo(GetToken());
                PublisherDetail d = null;
                try {
                        d = juddi.getPublisherDetail(sb);
                } catch (Exception ex) {
                        if (isExceptionExpiration(ex)) {
                                token = null;
                                sb.setAuthInfo(GetToken());
                                try {
                                        d = juddi.getPublisherDetail(sb);
                                } catch (Exception ex1) {
                                        return HandleException(ex);
                                }

                        } else {
                                return HandleException(ex);
                        }
                }
                if (d != null) {
                        ret.append("<table class=\"table table-hover\"><tr><th>Name</th><th>Info</th></tr>");
                        for (int i = 0; i < d.getPublisher().size(); i++) {
                                ret.append("<tr><td>").append(StringEscapeUtils.escapeHtml(d.getPublisher().get(i).getPublisherName()))
                                        .append("</td><td>");
                                ret.append(PrintPublisherDetail(d.getPublisher().get(i)))
                                        .append("</td></tr>");
                        }
                        ret.append("</table>");
                } else {
                        ret.append("No data returned");
                }
                return ret.toString();
        }

        private String invoke_SyncSubscription(HttpServletRequest parameters) {
                StringBuilder ret = new StringBuilder();
                SyncSubscription sb = new SyncSubscription();

                SyncSubscriptionDetail d = null;
                try {
                        StringReader sr = new StringReader(parameters.getParameter("invokeSyncSubscriptionXML").trim());
                        sb = (JAXB.unmarshal(sr, SyncSubscription.class));
                        sb.setAuthInfo(GetToken());
                        d = juddi.invokeSyncSubscription(sb);
                } catch (Exception ex) {
                        if (isExceptionExpiration(ex)) {
                                token = null;
                                sb.setAuthInfo(GetToken());
                                try {
                                        d = juddi.invokeSyncSubscription(sb);
                                } catch (Exception ex1) {
                                        return HandleException(ex);
                                }

                        } else {
                                return HandleException(ex);
                        }
                }
                if (d != null) {
                        ret.append("<pre>");
                        StringWriter sw = new StringWriter();
                        JAXB.marshal(d, sw);
                        sw.append(PrettyPrintXML(sw.toString()));
                        ret.append("</pre>");
                } else {
                        ret.append("No data returned");
                }
                return ret.toString();
        }

        private static String PrettyPrintXML(String input) {
                try {
                        Transformer transformer = TransformerFactory.newInstance().newTransformer();
                        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//initialize StreamResult with File object to save to file
                        StreamResult result = new StreamResult(new StringWriter());
                        StreamSource source = new StreamSource(new StringReader(input));
                        transformer.transform(source, result);
                        String xmlString = result.getWriter().toString();
                        return (xmlString);
                } catch (Exception ex) {
                }
                return null;
        }

        public static String getSampleSave_ClientSubscriptionInfo(){
                SaveClientSubscriptionInfo x = new SaveClientSubscriptionInfo();
                x.setAuthInfo("");
                x.getClientSubscriptionInfo().add(new ClientSubscriptionInfo());
                x.getClientSubscriptionInfo().get(0).setFromClerk(new Clerk());
                x.getClientSubscriptionInfo().get(0).setToClerk(new Clerk());
                x.getClientSubscriptionInfo().get(0).setSubscriptionKey("subscription key");
                x.getClientSubscriptionInfo().get(0).setLastModified(null);
                x.getClientSubscriptionInfo().get(0).setLastNotified(null);
                x.getClientSubscriptionInfo().get(0).getFromClerk().setName("ClerkName");
                x.getClientSubscriptionInfo().get(0).getFromClerk().setPublisher("username");
                x.getClientSubscriptionInfo().get(0).getFromClerk().setPassword("password");
                x.getClientSubscriptionInfo().get(0).getFromClerk().setNode(new Node());
                x.getClientSubscriptionInfo().get(0).getFromClerk().getNode().setClientName("clientname");
                x.getClientSubscriptionInfo().get(0).getFromClerk().getNode().setName("nodename");
                x.getClientSubscriptionInfo().get(0).getFromClerk().getNode().setDescription("description");
                x.getClientSubscriptionInfo().get(0).getFromClerk().getNode().setInquiryUrl("http://localhost:8080/juddiv3/services/inquiry");
                x.getClientSubscriptionInfo().get(0).getFromClerk().getNode().setPublishUrl("http://localhost:8080/juddiv3/services/publish");
                x.getClientSubscriptionInfo().get(0).getFromClerk().getNode().setCustodyTransferUrl("http://localhost:8080/juddiv3/services/custody-transfer");
                x.getClientSubscriptionInfo().get(0).getFromClerk().getNode().setSubscriptionUrl("http://localhost:8080/juddiv3/services/subscription");
                x.getClientSubscriptionInfo().get(0).getFromClerk().getNode().setSubscriptionListenerUrl("http://localhost:8080/juddiv3/services/subscription-listener");
                x.getClientSubscriptionInfo().get(0).getFromClerk().getNode().setJuddiApiUrl("http://localhost:8080/juddiv3/services/juddi-api");
                x.getClientSubscriptionInfo().get(0).getFromClerk().getNode().setReplicationUrl("http://localhost:8080/juddiv3/services/replication");
                
                x.getClientSubscriptionInfo().get(0).getToClerk().setName("ClerkName");
                x.getClientSubscriptionInfo().get(0).getToClerk().setPublisher("username");
                x.getClientSubscriptionInfo().get(0).getToClerk().setPassword("password");
                x.getClientSubscriptionInfo().get(0).getToClerk().setNode(new Node());
                x.getClientSubscriptionInfo().get(0).getToClerk().getNode().setClientName("clientname");
                x.getClientSubscriptionInfo().get(0).getToClerk().getNode().setName("nodename");
                x.getClientSubscriptionInfo().get(0).getToClerk().getNode().setDescription("description");
                x.getClientSubscriptionInfo().get(0).getToClerk().getNode().setInquiryUrl("http://localhost:8080/juddiv3/services/inquiry");
                x.getClientSubscriptionInfo().get(0).getToClerk().getNode().setPublishUrl("http://localhost:8080/juddiv3/services/publish");
                x.getClientSubscriptionInfo().get(0).getToClerk().getNode().setCustodyTransferUrl("http://localhost:8080/juddiv3/services/custody-transfer");
                x.getClientSubscriptionInfo().get(0).getToClerk().getNode().setSubscriptionUrl("http://localhost:8080/juddiv3/services/subscription");
                x.getClientSubscriptionInfo().get(0).getToClerk().getNode().setSubscriptionListenerUrl("http://localhost:8080/juddiv3/services/subscription-listener");
                x.getClientSubscriptionInfo().get(0).getToClerk().getNode().setJuddiApiUrl("http://localhost:8080/juddiv3/services/juddi-api");
                x.getClientSubscriptionInfo().get(0).getToClerk().getNode().setReplicationUrl("http://localhost:8080/juddiv3/services/replication");
                StringWriter sw = new StringWriter();
                JAXB.marshal(x, sw);
                return sw.toString();
        }
        private String save_ClientSubscriptionInfo(HttpServletRequest parameters) {
                StringBuilder ret = new StringBuilder();
                SaveClientSubscriptionInfo sb = new SaveClientSubscriptionInfo();

                if (parameters.getParameter("ClientSubscriptionInfoDetailXML")==null)
                        return "No input!";
                ClientSubscriptionInfoDetail d = null;
                try {
                        StringReader sr = new StringReader(parameters.getParameter("ClientSubscriptionInfoDetailXML").trim());
                        sb = (JAXB.unmarshal(sr, SaveClientSubscriptionInfo.class));
                        sb.setAuthInfo(GetToken());
                        d = juddi.saveClientSubscriptionInfo(sb);
                } catch (Exception ex) {
                        if (ex instanceof DispositionReportFaultMessage) {
                                DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                                if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                                        token = null;
                                        sb.setAuthInfo(GetToken());
                                        try {
                                                d = juddi.saveClientSubscriptionInfo(sb);
                                        } catch (Exception ex1) {
                                                return HandleException(ex);
                                        }
                                }
                        } else {
                                return HandleException(ex);
                        }
                }
                if (d != null) {
                        ret.append("<pre>");
                        StringWriter sw = new StringWriter();
                        JAXB.marshal(d, sw);
                        sw.append(PrettyPrintXML(sw.toString()));
                        ret.append("</pre>");
                } else {
                        ret.append("No data returned");
                }
                return ret.toString();
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

        private String save_publisher(HttpServletRequest parameters) {

                SavePublisher sb = new SavePublisher();
                Publisher p = new Publisher();
                p.setAuthorizedName(parameters.getParameter("savePublisherAuthorizedName"));
                p.setPublisherName(parameters.getParameter("savePublisherNAME"));
                p.setEmailAddress(parameters.getParameter("savePublisherEMAIL"));
                try {
                        p.setIsAdmin(Boolean.parseBoolean(parameters.getParameter("savePublisherIsAdmin")));
                } catch (Exception ex) {
                }
                if ("on".equalsIgnoreCase(parameters.getParameter("savePublisherIsAdmin"))) {
                        p.setIsAdmin(true);
                }
                try {
                        p.setIsEnabled(Boolean.parseBoolean(parameters.getParameter("savePublisherIsEnabled")));
                } catch (Exception ex) {
                }
                if ("on".equalsIgnoreCase(parameters.getParameter("savePublisherIsEnabled"))) {
                        p.setIsEnabled(true);
                }

                PublisherDetail d = null;
                sb.setAuthInfo(GetToken());
                try {
                        if (parameters.getParameter("savePublisherMaxBindings") != null) {
                                p.setMaxBindingsPerService(Integer.parseInt(parameters.getParameter("savePublisherMaxBindings")));
                        }
                } catch (Exception ex) {
                }
                try {
                        if (parameters.getParameter("savePublisherMaxServices") != null) {
                                p.setMaxServicePerBusiness(Integer.parseInt(parameters.getParameter("savePublisherMaxServices")));
                        }
                } catch (Exception ex) {
                }
                try {
                        if (parameters.getParameter("savePublisherMaxBusiness") != null) {
                                p.setMaxBusinesses(Integer.parseInt(parameters.getParameter("savePublisherMaxBusiness")));
                        }
                } catch (Exception ex) {
                }
                try {
                        if (parameters.getParameter("savePublisherMaxTModels") != null) {
                                p.setMaxTModels(Integer.parseInt(parameters.getParameter("savePublisherMaxTModels")));
                        }
                } catch (Exception ex) {
                }
                sb.getPublisher().add(p);
                try {

                        d = juddi.savePublisher(sb);
                } catch (Exception ex) {

                        if (isExceptionExpiration(ex)) {
                                token = null;
                                sb.setAuthInfo(GetToken());
                                try {
                                        d = juddi.savePublisher(sb);
                                } catch (Exception ex1) {
                                        return HandleException(ex);
                                }
                        } else {
                                return HandleException(ex);
                        }
                }
                return "Success";
        }

        private String adminDelete_tmodel(HttpServletRequest parameters) {
                DeleteTModel sb = new DeleteTModel();
                sb.getTModelKey().add(parameters.getParameter("adminDelete_tmodelKEY"));
                sb.setAuthInfo(GetToken());
                try {
                        juddi.adminDeleteTModel(sb);
                } catch (Exception ex) {
                        if (isExceptionExpiration(ex)) {
                                token = null;
                                sb.setAuthInfo(GetToken());
                                try {
                                        juddi.adminDeleteTModel(sb);
                                } catch (Exception ex1) {
                                        return HandleException(ex);
                                }
                        } else {
                                return HandleException(ex);
                        }
                }
                return "Success";
        }

        private String delete_ClientSubscriptionInfo(HttpServletRequest parameters) {
                DeleteClientSubscriptionInfo sb = new DeleteClientSubscriptionInfo();
                sb.getSubscriptionKey().add(parameters.getParameter("delete_ClientSubscriptionInfoKEY"));
                sb.setAuthInfo(GetToken());
                try {
                        juddi.deleteClientSubscriptionInfo(sb);
                } catch (Exception ex) {
                        if (isExceptionExpiration(ex)) {
                                token = null;
                                sb.setAuthInfo(GetToken());
                                try {
                                        juddi.deleteClientSubscriptionInfo(sb);
                                } catch (Exception ex1) {
                                        return HandleException(ex);
                                }

                        } else {
                                return HandleException(ex);
                        }
                }
                return "Success";
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

        public String verifyLogin() {
                EnsureConfig();
                if (style != AuthStyle.UDDI_AUTH) {
                        if (WS_Transport) {
                                BindingProvider bp = null;
                                Map<String, Object> context = null;

                                bp = (BindingProvider) juddi;
                                context = bp.getRequestContext();
                                context.put(BindingProvider.USERNAME_PROPERTY, session.getAttribute("username"));
                                context.put(BindingProvider.USERNAME_PROPERTY, session.getAttribute(AES.Decrypt("password", (String) properties.get("key"))));
                        }
                        FindBusiness fb = new FindBusiness();
                        fb.setListHead(0);
                        fb.setMaxRows(1);
                        fb.setFindQualifiers(new FindQualifiers());
                        fb.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                        fb.getName().add(new Name(UDDIConstants.WILDCARD, null));
                        try {
                                GetPublisherDetail publisherDetail = new GetPublisherDetail();
                                publisherDetail.getPublisherId().add((String) session.getAttribute("username"));
                                juddi.getPublisherDetail(publisherDetail);

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

                                bp = (BindingProvider) juddi;
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

}
