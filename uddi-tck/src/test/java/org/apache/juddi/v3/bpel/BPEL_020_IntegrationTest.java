/*
 * Copyright 2001-2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.juddi.v3.bpel;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.wsdl.Definition;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import junit.framework.Assert;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.mapping.URLLocalizerDefaultImpl;
import org.apache.juddi.v3.client.mapping.wsdl.BPEL2UDDI;
import org.apache.juddi.v3.client.mapping.wsdl.ReadWSDL;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3.client.transport.TransportException;
import org.apache.juddi.v3.tck.TckBusiness;
import org.apache.juddi.v3.tck.TckBusinessService;
import org.apache.juddi.v3.tck.TckPublisher;
import org.apache.juddi.v3.tck.TckSecurity;
import org.apache.juddi.v3.tck.TckTModel;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class BPEL_020_IntegrationTest {

        private static Log logger = LogFactory.getLog(BPEL_010_IntegrationTest.class);
        private static TckTModel tckTModel = null;
        private static TckBusinessService tckService = null;
        private static TckBusiness tckBusiness = null;
        private static String authInfoRiftSaw = null;
        private static UDDIClient manager;
        static ReadWSDL rw;

        @BeforeClass
        public static void startManager() throws ConfigurationException {

                logger.info("BPEL_020_IntegrationTest");
                manager = new UDDIClient();
                manager.start();

                logger.debug("Getting auth token for user riftsaw/riftsaw..");
                try {
                        Transport transport = manager.getTransport("uddiv3");

                        UDDISecurityPortType security = transport.getUDDISecurityService();
                        authInfoRiftSaw = TckSecurity.getAuthToken(security, TckPublisher.getRiftSawPublisherId(), TckPublisher.getRiftSawPassword());
                        //Assert.assertNotNull(authInfoRiftSaw);

                        UDDIPublicationPortType publication = transport.getUDDIPublishService();
                        UDDIInquiryPortType inquiry = transport.getUDDIInquiryService();
                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) publication, TckPublisher.getRiftSawPublisherId(), TckPublisher.getRiftSawPassword());
                                TckSecurity.setCredentials((BindingProvider) inquiry, TckPublisher.getRiftSawPublisherId(), TckPublisher.getRiftSawPassword());
                        }

                        tckTModel = new TckTModel(publication, inquiry);
                        tckService = new TckBusinessService(publication, inquiry);
                        tckBusiness = new TckBusiness(publication, inquiry);

                } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("Could not obtain authInfo token.");
                }
                rw = new ReadWSDL();
        }
        @AfterClass
        public static void cleanup() throws ConfigurationException{
                tckTModel.deleteCreatedTModels(authInfoRiftSaw);
                manager.stop();
        }

        @Before //jUDDI only to add the keygenerator and business
        public void saveRiftSawKeyGenerator() {
                tckTModel.saveTModel(authInfoRiftSaw, TckTModel.RIFTSAW_PUBLISHER_TMODEL_XML, TckTModel.RIFTSAW_PUBLISHER_TMODEL_KEY);
                tckBusiness.saveBusiness(authInfoRiftSaw, TckBusiness.RIFTSAW_BUSINESS_XML, TckBusiness.RIFTSAW_BUSINESS_KEY);
        }
        
        @After //jUDDI only to add the keygenerator and business
        public void saveRiftSawKeyGeneratorAfter() {
                tckBusiness.deleteBusiness(authInfoRiftSaw, TckBusiness.RIFTSAW_BUSINESS_XML, TckBusiness.RIFTSAW_BUSINESS_KEY);
                tckTModel.deleteTModel(authInfoRiftSaw, TckTModel.RIFTSAW_PUBLISHER_TMODEL_XML, TckTModel.RIFTSAW_PUBLISHER_TMODEL_KEY);
        }

        @Test
        public void parseWSDL_PortTypeTModels() throws WSDLException, Exception {

                Definition wsdlDefinition = rw.readWSDL("uddi_data/bpel/riftsaw/bpel-technote.wsdl");
                @SuppressWarnings("unchecked")
                Map<QName, PortType> portTypes = (Map<QName, PortType>) wsdlDefinition.getAllPortTypes();
                String ns = wsdlDefinition.getTargetNamespace();
                logger.info("Namespace: " + ns);

                boolean foundInterfaceOfTravelAgent=false;
                boolean foundInterfaceOfCustomer=false;
          
                Iterator<QName> iterator = portTypes.keySet().iterator();
                while (iterator.hasNext()) {
                        QName qName = iterator.next();
                        String nsp = qName.getNamespaceURI();
                        String localpart = qName.getLocalPart();
                        logger.info("Namespace: " + nsp);
                        logger.info("LocalPart: " + localpart);
                        if (localpart.equals("InterfaceOfTravelAgent"))
                                foundInterfaceOfTravelAgent=true;
                        if (localpart.equals("InterfaceOfCustomer"))
                                foundInterfaceOfCustomer=true;
                }
                org.junit.Assert.assertTrue("InterfaceOfCustomer wasn't found, wsdl parsing error", foundInterfaceOfCustomer);
                org.junit.Assert.assertTrue("InterfaceOfTravelAgent wasn't found, wsdl parsing error", foundInterfaceOfTravelAgent);
        }

        @Test
        public void registerBPELProcess() throws WSDLException, ConfigurationException,
                MalformedURLException, RemoteException, TransportException, Exception {

                UDDIClerk clerk = new UDDIClerk();
                clerk.setManagerName(manager.getName());
                clerk.setName("testClerk");
                clerk.setPublisher(TckPublisher.getRiftSawPublisherId());
                clerk.setPassword(TckPublisher.getRiftSawPassword());

                clerk.setUDDINode(manager.getClientConfig().getHomeNode());

                Properties properties = manager.getClientConfig().getHomeNode().getProperties();
                properties.put("keyDomain", "riftsaw.jboss.org");
                properties.put("nodeName", "localhost");
                properties.put("businessName", "redhat-jboss");
                BPEL2UDDI bpel2UDDI = new BPEL2UDDI(clerk, new URLLocalizerDefaultImpl(), properties);

                Definition wsdlDefinition = rw.readWSDL("uddi_data/bpel/riftsaw/HelloWorld.wsdl");
                QName serviceName = new QName("http://www.jboss.org/bpel/examples/wsdl", "HelloService");
                String portName = "HelloPort";
                URL serviceUrl = new URL("http://localhost:8080/helloworld");
                bpel2UDDI.register(serviceName, portName, serviceUrl, wsdlDefinition);

                logger.info("DONE");

                bpel2UDDI.unRegister(serviceName, portName, serviceUrl);
        }
}
