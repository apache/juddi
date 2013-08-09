package org.apache.juddi.webconsole.hub;

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
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXB;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.BindingProvider;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api_v3.ClientSubscriptionInfoDetail;
import org.apache.juddi.api_v3.DeleteClientSubscriptionInfo;
import org.apache.juddi.api_v3.DeletePublisher;
import org.apache.juddi.api_v3.GetAllPublisherDetail;
import org.apache.juddi.api_v3.GetPublisherDetail;
import org.apache.juddi.api_v3.Publisher;
import org.apache.juddi.api_v3.PublisherDetail;
import org.apache.juddi.api_v3.SaveClientSubscriptionInfo;
import org.apache.juddi.api_v3.SavePublisher;
import org.apache.juddi.api_v3.SyncSubscription;
import org.apache.juddi.api_v3.SyncSubscriptionDetail;
import org.apache.juddi.v3.client.ClassUtil;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.apache.juddi.webconsole.AES;
import org.apache.juddi.webconsole.resources.ResourceLoader;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.DeleteTModel;
import org.uddi.api_v3.DiscardAuthToken;
import org.uddi.api_v3.DispositionReport;
import org.uddi.api_v3.GetAuthToken;

import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * UddiHub - The hub acts as a single point for managing browser to uddi
 * services. At most 1 instance is allowed per http session. In general, all
 * methods in the class trigger web service call outs. All callouts also support
 * expired UDDI tokens and will attempt to reauthenticate and retry the request.
 *
 * @author Alex O'Ree
 */
public class UddiAdminHub {

    /**
     * The logger name
     */
    public static final String LOGGER_NAME = "org.apache.juddi";
    AuthStyle style = null;
    URL propertiesurl = null;
    Properties properties = null;
    /**
     * The Log4j logger. This is also referenced from the Builders class, thus
     * it is public
     */
    public static final Log log = LogFactory.getLog(LOGGER_NAME);
    private DatatypeFactory df;

    private UddiAdminHub() throws DatatypeConfigurationException {
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

    /**
     * This kills any authentication tokens, logs the user out and nulls out all
     * services
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
        security = null;
        juddi = null;
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
            throw new Exception("Cannot locate the configuration file.");
        }
        session = _session;
        propertiesurl = prop;
        InputStream in = prop.openStream();
        Properties p = new Properties();
        p.load(in);
        in.close();
        session = _session;
        properties = p;
        style = (AuthStyle) AuthStyle.valueOf((String) p.get("authtype"));
        try {

            String clazz = UDDIClientContainer.getUDDIClient(null).
                    getClientConfig().getUDDINode("default").getProxyTransport();
            Class<?> transportClass = ClassUtil.forName(clazz, Transport.class);
            if (transportClass != null) {
                Transport transport = (Transport) transportClass.
                        getConstructor(String.class).newInstance("default");

                security = transport.getUDDISecurityService();
                juddi = transport.getJUDDIApiService();


            }
        } catch (Exception ex) {
            HandleException(ex);
        }
    }
    private HttpSession session;
    private UDDISecurityPortType security = null;
    private JUDDIApiPortType juddi = null;
    private String token = null;

    /**
     * This function provides a basic error handling rutine that will pull out
     * the true error message in a UDDI fault message, returning bootstrap
     * stylized html error message
     *
     * @param ex
     * @return
     */
    private String HandleException(Exception ex) {
        if (ex instanceof DispositionReportFaultMessage) {
            DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
            log.error(null, ex);
            return ResourceLoader.GetResource(session, "errors.uddi") + " " + ex.getMessage() + " " + f.detail.getMessage();
        }
        if (ex instanceof RemoteException) {
            RemoteException f = (RemoteException) ex;
            log.error(null, ex);
            return ResourceLoader.GetResource(session, "errors.generic") + " " + ex.getMessage() + " " + f.detail.getMessage();
        }
        log.error(null, ex);
        return //"<div class=\"alert alert-error\" ><h3><i class=\"icon-warning-sign\"></i> "
                ResourceLoader.GetResource(session, "errors.generic") + " " + StringEscapeUtils.escapeHtml(ex.getMessage());
        //+ "</h3></div>";

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
                delete_publisher(parameters);
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
                //    return save_Clerk(parameters);
            }
            if (action.equalsIgnoreCase("save_ClientSubscriptionInfo")) {
                return save_ClientSubscriptionInfo(parameters);
            }
            if (action.equalsIgnoreCase("save_Node")) {
                //    return save_Node(parameters);
            }
            if (action.equalsIgnoreCase("save_publisher")) {
                return save_publisher(parameters);
            }
        } catch (Exception ex) {
        }
        return "error!";
    }

    public enum AuthStyle {

        /**
         * Http Basic
         */
        HTTP_BASIC,
        /**
         * Http Digest
         */
        HTTP_DIGEST,
        /**
         * HTTP NTLM
         */
        HTTP_NTLM,
        /**
         * UDDI Authentication via the Security API
         */
        UDDI_AUTH,
        /**
         * HTTP Client Certificate Authentication
         */
        HTTP_CLIENT_CERT
    }

    private String GetToken() {
        if (style != AuthStyle.UDDI_AUTH) {
            BindingProvider bp = null;
            Map<String, Object> context = null;
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
                } catch (Exception ex) {
                    return HandleException(ex);
                }
            }
        }
        return token;
    }

    private void delete_publisher(HttpServletRequest parameters) throws Exception {
        DeletePublisher sb = new DeletePublisher();
        sb.setAuthInfo(GetToken());
        sb.getPublisherId().add(parameters.getParameter("delete_publisherKEY"));
        try {
            juddi.deletePublisher(sb);
        } catch (Exception ex) {
            if (ex instanceof DispositionReportFaultMessage) {
                DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                    token = null;
                    sb.setAuthInfo(GetToken());
                    juddi.deletePublisher(sb);
                }
            } else {
                throw ex;
            }
        }
    }

    private String getAllPublisherDetail(HttpServletRequest parameters) {
        StringBuilder ret = new StringBuilder();
        GetAllPublisherDetail sb = new GetAllPublisherDetail();
        sb.setAuthInfo(GetToken());
        PublisherDetail d = null;
        try {
            d = juddi.getAllPublisherDetail(sb);
        } catch (Exception ex) {
            if (ex instanceof DispositionReportFaultMessage) {
                DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                    token = null;
                    sb.setAuthInfo(GetToken());
                    try {
                        d = juddi.getAllPublisherDetail(sb);
                    } catch (Exception ex1) {
                        return HandleException(ex);
                    }
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
                .append(StringEscapeUtils.escapeHtml(p.getIsAdmin()))
                .append("<br>")
                .append("Enabled = ")
                .append(StringEscapeUtils.escapeHtml(p.getIsEnabled()))
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
            if (ex instanceof DispositionReportFaultMessage) {
                DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                    token = null;
                    sb.setAuthInfo(GetToken());
                    try {
                        d = juddi.getPublisherDetail(sb);
                    } catch (Exception ex1) {
                        return HandleException(ex);
                    }
                }
            } else {
                return HandleException(ex);
            }
        }
        if (d != null) {
            ret.append("<table class=\"table table-hover\"><tr>th>Name</th><th><th>Info</th></tr>");
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
            if (ex instanceof DispositionReportFaultMessage) {
                DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                    token = null;
                    sb.setAuthInfo(GetToken());
                    try {
                        d = juddi.invokeSyncSubscription(sb);
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

    private String save_ClientSubscriptionInfo(HttpServletRequest parameters) {
        StringBuilder ret = new StringBuilder();
        SaveClientSubscriptionInfo sb = new SaveClientSubscriptionInfo();

        ClientSubscriptionInfoDetail d = null;
        try {
            StringReader sr = new StringReader(parameters.getParameter("invokeSyncSubscriptionXML").trim());
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

    private String save_publisher(HttpServletRequest parameters) {

        SavePublisher sb = new SavePublisher();
        Publisher p = new Publisher();
        p.setAuthorizedName(parameters.getParameter("savePublisherAuthorizedName"));
        p.setPublisherName(parameters.getParameter("savePublisherNAME"));
        p.setEmailAddress(parameters.getParameter("savePublisherEMAIL"));
        p.setIsAdmin(parameters.getParameter("savePublisherIsAdmin"));
        p.setIsEnabled(parameters.getParameter("savePublisherIsEnabled"));

        sb.getPublisher().add(p);
        PublisherDetail d = null;
        sb.setAuthInfo(GetToken());
        try {
            p.setMaxBindingsPerService(Integer.parseInt(parameters.getParameter("savePublisherMaxBindings")));
            p.setMaxServicePerBusiness(Integer.parseInt(parameters.getParameter("savePublisherMaxServices")));
            p.setMaxBusinesses(Integer.parseInt(parameters.getParameter("savePublisherMaxBusiness")));
            p.setMaxTModels(Integer.parseInt(parameters.getParameter("savePublisherMaxTModels")));
            d = juddi.savePublisher(sb);
        } catch (Exception ex) {
            if (ex instanceof DispositionReportFaultMessage) {
                DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                    token = null;
                    sb.setAuthInfo(GetToken());
                    try {
                        d = juddi.savePublisher(sb);
                    } catch (Exception ex1) {
                        return HandleException(ex);
                    }
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
            if (ex instanceof DispositionReportFaultMessage) {
                DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                    token = null;
                    sb.setAuthInfo(GetToken());
                    try {
                        juddi.adminDeleteTModel(sb);
                    } catch (Exception ex1) {
                        return HandleException(ex);
                    }
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
            if (ex instanceof DispositionReportFaultMessage) {
                DispositionReportFaultMessage f = (DispositionReportFaultMessage) ex;
                if (f.getFaultInfo().countainsErrorCode(DispositionReport.E_AUTH_TOKEN_EXPIRED)) {
                    token = null;
                    sb.setAuthInfo(GetToken());
                    try {
                        juddi.deleteClientSubscriptionInfo(sb);
                    } catch (Exception ex1) {
                        return HandleException(ex);
                    }
                }
            } else {
                return HandleException(ex);
            }
        }
        return "Success";
    }
}
