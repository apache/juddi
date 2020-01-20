/*
 * Copyright 2020 The Apache Software Foundation.
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
 */
package org.apache.juddi.example.juddi.embedded;

import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import javax.xml.ws.Endpoint;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.Registry;
import org.apache.juddi.api.impl.JUDDIApiImpl;
import org.apache.juddi.api.impl.UDDICustodyTransferImpl;
import org.apache.juddi.api.impl.UDDIInquiryImpl;
import org.apache.juddi.api.impl.UDDIPublicationImpl;
import org.apache.juddi.api.impl.UDDISecurityImpl;
import org.apache.juddi.api.impl.UDDISubscriptionImpl;
import org.apache.juddi.api.impl.UDDIValueSetCachingImpl;
import org.apache.juddi.api.impl.UDDIValueSetValidationImpl;
import static org.apache.juddi.config.AppConfig.JUDDI_CONFIGURATION_FILE_SYSTEM_PROPERTY;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.Name;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * Another example using juddi as an embedded standalone process.
 * <ul>
 * <li>This will only use uddi style authentication (http auth not
 * supported).</li>
 * <li>juddi gui will not start embedded using this sample code.</li>
 * <li>juddi rest services will not start with this sample code, only the soap
 * services</li>
 * </ul>
 *
 * @author Alex O'Ree
 */
public class EmbeddedWithWeb {
    
    public static void main(String[] args) throws Exception {

        //tell juddi to load this server configuration file from disk
        File cfg = new File("juddi-server.xml").getCanonicalFile();
        System.setProperty(JUDDI_CONFIGURATION_FILE_SYSTEM_PROPERTY, cfg.getCanonicalPath());

        //start up the services
        Registry.start();
        //note these instance classes will be used to server web requests
        //do not share embedded access with web access classes due to context
        //sharing issues.
        JUDDIApiImpl juddi = new JUDDIApiImpl();
        UDDIPublicationImpl publish = new UDDIPublicationImpl();
        UDDIInquiryImpl inquiry = new UDDIInquiryImpl();
        UDDISecurityImpl security = new UDDISecurityImpl();
        UDDISubscriptionImpl subscription = new UDDISubscriptionImpl();
        UDDICustodyTransferImpl custody = new UDDICustodyTransferImpl();
        UDDIValueSetCachingImpl cache = new UDDIValueSetCachingImpl();
        UDDIValueSetValidationImpl validation = new UDDIValueSetValidationImpl();
        
        

        final HttpServer httpServer = HttpServer.create(new InetSocketAddress(InetAddress.getByName("0.0.0.0"), 8080), 16);
        //TODO tls setup
        
        Endpoint endpointJuddi = Endpoint.create(juddi);
        Endpoint endpointPublish = Endpoint.create(publish);
        Endpoint endpointInquiry = Endpoint.create(inquiry);
        Endpoint endpointSecurity = Endpoint.create(security);
        Endpoint endpointSubscription = Endpoint.create(subscription);
        Endpoint endpointCustody = Endpoint.create(custody);
        Endpoint endpointCache = Endpoint.create(cache);
        Endpoint endpointValidation = Endpoint.create(validation);
        
        endpointJuddi.publish(httpServer.createContext("/juddiv3/services/juddi"));
        endpointPublish.publish(httpServer.createContext("/juddiv3/services/publish"));
        endpointInquiry.publish(httpServer.createContext("/juddiv3/services/inquiry"));
        endpointSecurity.publish(httpServer.createContext("/juddiv3/services/security"));
        endpointSubscription.publish(httpServer.createContext("/juddiv3/services/subscription"));
        endpointCustody.publish(httpServer.createContext("/juddiv3/services/custody-transfer"));
        endpointCache.publish(httpServer.createContext("/juddiv3/services/valueset-caching"));
        endpointValidation.publish(httpServer.createContext("/juddiv3/services/valueset-validation"));
        
        httpServer.start();
        System.out.println("started, verifying http access");

        //clients can access the services via http
        {
            cfg = new File("uddi-http.xml");
            UDDIClient uddiClientHttp = new UDDIClient(cfg.getCanonicalPath());
            uddiClientHttp.start();
            Transport transport = uddiClientHttp.getTransport("default");
            UDDISecurityPortType clientSecurity = transport.getUDDISecurityService();
            UDDIInquiryPortType clientInquiry = transport.getUDDIInquiryService();
            
            System.out.println("started, verifying embedded access");
            FindBusiness fb = new FindBusiness();
            fb.setMaxRows(200);
            fb.setListHead(0);
            // fb.setAuthInfo(GetToken());
            org.uddi.api_v3.FindQualifiers fq = new org.uddi.api_v3.FindQualifiers();
            fq.getFindQualifier().add(UDDIConstants.CASE_INSENSITIVE_MATCH);
            fq.getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
            fq.getFindQualifier().add(UDDIConstants.SORT_BY_NAME_ASC);
            fb.setFindQualifiers(fq);
            Name searchname = new Name();
            searchname.setLang("%");
            searchname.setValue("%");
            fb.getName().add(searchname);
            
            BusinessList result = clientInquiry.findBusiness(fb);
            System.out.println(result.getBusinessInfos().getBusinessInfo().size() + " businesses available");
            uddiClientHttp.stop();
        }

        //clients within this process can use invm transport
        {
            
            cfg = new File("uddi-invm.xml");
            UDDIClient uddiClientHttp = new UDDIClient(cfg.getCanonicalPath());
            uddiClientHttp.start();
            Transport transport = uddiClientHttp.getTransport("default");
            UDDISecurityPortType clientSecurity = transport.getUDDISecurityService();
            UDDIInquiryPortType clientInquiry = transport.getUDDIInquiryService();
            
            System.out.println("started, verifying embedded access");
            FindBusiness fb = new FindBusiness();
            fb.setMaxRows(200);
            fb.setListHead(0);
            // fb.setAuthInfo(GetToken());
            org.uddi.api_v3.FindQualifiers fq = new org.uddi.api_v3.FindQualifiers();
            fq.getFindQualifier().add(UDDIConstants.CASE_INSENSITIVE_MATCH);
            fq.getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
            fq.getFindQualifier().add(UDDIConstants.SORT_BY_NAME_ASC);
            fb.setFindQualifiers(fq);
            Name searchname = new Name();
            searchname.setLang("%");
            searchname.setValue("%");
            fb.getName().add(searchname);
            
            BusinessList result = clientInquiry.findBusiness(fb);
            System.out.println(result.getBusinessInfos().getBusinessInfo().size() + " businesses available");
            //uddiClientHttp.stop();
        }
        
        System.out.println("ready, press enter to stop");
      //  System.console().readLine();

        //shutdown
        endpointJuddi.stop();
        endpointPublish.stop();
        endpointInquiry.stop();
        endpointSecurity.stop();
        endpointSubscription.stop();
        endpointCustody.stop();
        endpointCache.stop();
        endpointValidation.stop();
        httpServer.stop(0);
    }
}
