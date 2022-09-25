/*
 * Copyright 2001-2008 The Apache Software Foundation.
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
package org.apache.juddi.subscription.notify;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.model.BindingTemplate;
import org.apache.juddi.v3.client.UDDIService;
import org.uddi.api_v3.DispositionReport;
import org.uddi.subr_v3.NotifySubscriptionListener;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDISubscriptionListenerPortType;

public class HTTPNotifier implements Notifier {

    private static String SUBR_V3_NAMESPACE = "urn:uddi-org:v3_service";
    private static String SUBSCRIPTION_LISTENER_SERVICE = "UDDISubscriptionListenerImplPort";
    Log log = LogFactory.getLog(this.getClass());
    UDDISubscriptionListenerPortType subscriptionListenerPort = null;

    public HTTPNotifier(BindingTemplate bindingTemplate) throws IOException {
        super();
        String accessPointUrl = bindingTemplate.getAccessPointUrl().toLowerCase();
        if (!accessPointUrl.startsWith("http")) {
            log.warn("http accessPointUrl for bindingTemplate " + bindingTemplate.getEntityKey()
                    + " should start with 'http' or 'https'");
        }
        //fix for JIRA JUDDI-597
        accessPointUrl = bindingTemplate.getAccessPointUrl();
        if (AccessPointType.WSDL_DEPLOYMENT.toString().equalsIgnoreCase(bindingTemplate.getAccessPointType())) {
            //WSDL deployment type
            //TODO, let user override the SUBSCRIPTION_LISTENER_SERVICE setting
            QName qName = new QName(SUBR_V3_NAMESPACE, SUBSCRIPTION_LISTENER_SERVICE);
            Service service = Service.create(new URL(bindingTemplate.getAccessPointUrl()), qName);
            subscriptionListenerPort = (UDDISubscriptionListenerPortType) service.getPort(UDDISubscriptionListenerPortType.class);
        } else if (AccessPointType.END_POINT.toString().equalsIgnoreCase(bindingTemplate.getAccessPointType())) {
            //endpoint deployment type
            UDDIService uddiService = new UDDIService();
            subscriptionListenerPort = uddiService.getUDDISubscriptionListenerPort();
            Map<String, Object> requestContext = ((BindingProvider) subscriptionListenerPort).getRequestContext();
            requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, accessPointUrl);
        }
        /*if (accessPointUrl.toLowerCase().startsWith("https://")) {
            try {
                String file = AppConfig.getConfiguration().getString(Property.JUDDI_SUBSCRIPTION_TRUSTSTORE_FILE);
                String type = AppConfig.getConfiguration().getString(Property.JUDDI_SUBSCRIPTION_TRUSTSTORE_TYPE);
                String password = AppConfig.getConfiguration().getString(Property.JUDDI_SUBSCRIPTION_TRUSTSTORE_PASSWORD);
                if (AppConfig.getConfiguration().getBoolean(Property.JUDDI_SUBSCRIPTION_TRUSTSTORE_ENCRYPTED, false)) {
                    password = CryptorFactory.getCryptor(AppConfig.getConfiguration().getString(Property.JUDDI_SUBSCRIPTION_TRUSTSTORE_CRYPTOPROVIDER)).decrypt(password);
                }
                
            } catch (Exception ex) {
                log.error(null, ex);
            }

        }*/
    }

    public DispositionReport notifySubscriptionListener(NotifySubscriptionListener body) throws DispositionReportFaultMessage, RemoteException {
        return subscriptionListenerPort.notifySubscriptionListener(body);
    }
}
