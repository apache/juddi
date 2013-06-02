/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uddi.examples;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.wsdl.Definition;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.mapping.ReadWSDL;
import org.apache.juddi.v3.client.mapping.URLLocalizerDefaultImpl;
import org.apache.juddi.v3.client.mapping.WSDL2UDDI;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.api_v3.*;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * This class show you how get all available Access Points/Endpoints for a
 * service. This is harder than it sounds due to the complexity of UDDI's data
 * structure. The output is the list of URLs given a service's key
 *
 * @author Alex
 */
public class UddiFindEndpoints {

    private static UDDISecurityPortType security = null;
    private static UDDIInquiryPortType inquiry = null;

    public UddiFindEndpoints() {
        try {
            // create a manager and read the config in the archive; 
            // you can use your config file name
            UDDIClient clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
            // register the clerkManager with the client side container
            UDDIClientContainer.addClient(clerkManager);
            // a ClerkManager can be a client to multiple UDDI nodes, so 
            // supply the nodeName (defined in your uddi.xml.
            // The transport can be WS, inVM, RMI etc which is defined in the uddi.xml
            Transport transport = clerkManager.getTransport("default");
            // Now you create a reference to the UDDI API
            security = transport.getUDDISecurityService();
            inquiry = transport.getUDDIInquiryService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void find() {
        try {
            // Setting up the values to get an authentication token for the 'root' user ('root' user has admin privileges
            // and can save other publishers).
            GetAuthToken getAuthTokenRoot = new GetAuthToken();
            getAuthTokenRoot.setUserID("root");
            getAuthTokenRoot.setCred("root");

            // Making API call that retrieves the authentication token for the 'root' user.
            AuthToken rootAuthToken = security.getAuthToken(getAuthTokenRoot);
            System.out.println("root AUTHTOKEN = " + rootAuthToken.getAuthInfo());

            GetServiceDetail fs = new GetServiceDetail();
            fs.setAuthInfo(rootAuthToken.getAuthInfo());

            //TODO Key! insert your key here!
            String key = "uddi:juddi.apache.org:services-inquiry";

            fs.getServiceKey().add(key);
            ServiceDetail serviceDetail = inquiry.getServiceDetail(fs);
            if (serviceDetail == null || serviceDetail.getBusinessService().isEmpty()) {
                System.out.println(key + " is not registered");
            } else {
                List<String> endpoints = GetEndpoints(serviceDetail, rootAuthToken.getAuthInfo());
                for (int i = 0; i < endpoints.size(); i++) {
                    System.out.println(endpoints.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        UddiFindEndpoints sp = new UddiFindEndpoints();
        sp.find();
    }

    private List<String> GetEndpoints(ServiceDetail serviceDetail, String authInfo) throws Exception {
        List<String> items = new ArrayList<String>();
        if (serviceDetail == null) {
            return items;
        }
        for (int i = 0; i < serviceDetail.getBusinessService().size(); i++) {
            if (serviceDetail.getBusinessService().get(i).getBindingTemplates() != null) {
                for (int k = 0; k < serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().size(); k++) {
                    items.addAll(ParseBinding(serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k), authInfo));
                }
            }
        }
        return items;
    }

    private List<String> GetBindingInfo(String value, String cred) throws Exception {
        List<String> items = new ArrayList<String>();
        if (value == null) {
            return items;
        }
        GetBindingDetail b = new GetBindingDetail();
        b.setAuthInfo(cred);
        b.getBindingKey().add(value);
        BindingDetail bindingDetail = inquiry.getBindingDetail(b);
        for (int i = 0; i < bindingDetail.getBindingTemplate().size(); i++) {
            items.addAll(ParseBinding(bindingDetail.getBindingTemplate().get(i), cred));
        }
        return items;
    }

    private List<String> FetchWSDL(String value) {
        List<String> items = new ArrayList<String>();

        if (value.startsWith("http://") || value.startsWith("https://")) {
            //here, we need an HTTP Get for WSDLs
            org.apache.juddi.v3.client.mapping.ReadWSDL r = new ReadWSDL();
            r.setIgnoreSSLErrors(true);
            try {
                Definition wsdlDefinition = r.readWSDL(new URL(value));
                Properties properties = new Properties();


                properties.put("keyDomain", "domain");
                properties.put("businessName", "biz");
                properties.put("serverName", "localhost");
                properties.put("serverPort", "80");

                WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(null, new URLLocalizerDefaultImpl(), properties);
                BusinessServices businessServices = wsdl2UDDI.createBusinessServices(wsdlDefinition);
                for (int i = 0; i < businessServices.getBusinessService().size(); i++) {
                    if (businessServices.getBusinessService().get(i).getBindingTemplates() != null) {
                        for (int k = 0; k < businessServices.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().size(); k++) {
                            items.addAll(ParseBinding(businessServices.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k), null));
                        }
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(UddiFindEndpoints.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return items;
    }

    private List<String> ParseBinding(BindingTemplate get, String authInfo) throws Exception {
        List<String> items = new ArrayList<String>();
        if (get == null || get.getAccessPoint() == null) {
            return items;
        }
        if (get.getHostingRedirector() != null) {
            //hosting Redirector is the same as "reference this other binding template". It's actually deprecated so 
            //don't expect to see this too often
            items.addAll(GetBindingInfo(get.getHostingRedirector().getBindingKey(), authInfo));
        }
        if (get.getAccessPoint() != null) {
            String usetype = get.getAccessPoint().getUseType();
            if (usetype == null) {
                //this is unexpected, usetype is a required field
                items.add(get.getAccessPoint().getValue());
            } else if (usetype.equalsIgnoreCase(AccessPointType.BINDING_TEMPLATE.toString())) {
                //referencing another binding template
                items.addAll(GetBindingInfo(get.getAccessPoint().getValue(), authInfo));
            } else if (usetype.equalsIgnoreCase(AccessPointType.HOSTING_REDIRECTOR.toString())) {
                //this one is a bit strange. the value should be a binding template
                
                items.addAll(GetBindingInfo(get.getAccessPoint().getValue(), authInfo));
                
            } else if (usetype.equalsIgnoreCase(AccessPointType.WSDL_DEPLOYMENT.toString())) {
                //fetch wsdl and parse
                items.addAll(FetchWSDL(get.getAccessPoint().getValue()));
            } else if (usetype.equalsIgnoreCase(AccessPointType.END_POINT.toString())) {
                items.add(get.getAccessPoint().getValue());
            } else {
                //treat it has an extension or whatever
                items.add(get.getAccessPoint().getValue());
            }

        }
        return items;
    }
}
