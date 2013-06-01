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
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.BindingProvider;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.ClassUtil;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.apache.juddi.webconsole.resources.ResourceLoader;
import org.uddi.api_v3.*;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDICustodyTransferPortType;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;

/**
 * UddiHub - The hub acts as a single point for managing browser to uddi
 * services. At most 1 instance is allowed per http session. In general, all
 * methods in the class trigger web service call outs. All callouts also support
 * expired UDDI tokens and will attempt to reauthenticate and retry the request.
 *
 * @author Alex O'Ree
 */
public class UddiHub {

    /**
     * The logger name
     */
    public static final String LOGGER_NAME = "org.apache.juddi";
    
    /**
     * The Log4j logger. This is also referenced from the Builders class, thus
     * it is public
     */
    public static final Log log = LogFactory.getLog(LOGGER_NAME);
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
        inquiry = null;
        publish = null;
        custody = null;
        security = null;
        juddi = null;
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
            return hub;
        }

        return (UddiHub) j;
    }
    String locale = "en";

    
    private UddiHub(ServletContext application, HttpSession _session) throws Exception {
        
        session = _session;
        
        try {

            String clazz = UDDIClientContainer.getUDDIClient(null).
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
                juddi = transport.getJUDDIApiService();

              
            }
        } catch (Exception ex) {
            HandleException(ex);
        }
    }
    private HttpSession session;
    private UDDISubscriptionPortType subscription = null;
    private UDDISecurityPortType security = null;
    private UDDIInquiryPortType inquiry = null;
    private UDDIPublicationPortType publish = null;
    private UDDICustodyTransferPortType custody = null;
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
}
