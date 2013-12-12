package org.apache.juddi.v3.tck;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.v3.client.UDDIService;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDINode;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDIReplicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionListenerPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;

/**
 * Checking the UDDI Server for all the required ports specified in the UDDI v3
 * spec.
 *
 * @author kstam
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 *
 */
public class UDDI_001_UDDIServiceTest {

        static UDDIClient manager;
        static UDDINode uddiServer;

        @BeforeClass
        public static void readClientConfig() throws ConfigurationException {
                manager = new UDDIClient();
                manager.start();
                uddiServer = manager.getClientConfig().getHomeNode();
        }

        @AfterClass
        public static void stop() throws ConfigurationException {
                if (manager != null) {
                        manager.stop();
                }
        }

        @Test
        public void findSecurityPort() throws IOException {
                Assume.assumeTrue(TckPublisher.isUDDIAuthMode());
                UDDIService uddiService = new UDDIService();
                UDDISecurityPortType port = uddiService.getUDDISecurityPort();
                assertNotNull(port);
                GetAuthToken body = new GetAuthToken();
                body.setUserID("root");
                body.setCred("root");
                AuthToken token = port.getAuthToken(body);
                System.out.println("token=" + token);
        }

        @Test
        public void findInquiryPort() throws IOException {
                UDDIService uddiService = new UDDIService();
                UDDIInquiryPortType port = uddiService.getUDDIInquiryPort();
                assertNotNull(port);
        }

        @Test
        public void findPublicationPort() throws IOException {
                UDDIService uddiService = new UDDIService();
                UDDIPublicationPortType port = uddiService.getUDDIPublicationPort();
                assertNotNull(port);
        }

        @Test
        public void findSubscriptionPort() throws IOException {
                Assume.assumeTrue(TckPublisher.isSubscriptionEnabled());
                UDDIService uddiService = new UDDIService();
                UDDISubscriptionPortType port = uddiService.getUDDISubscriptionPort();
                assertNotNull(port);
        }

        @Test
        public void findReplicationPort() throws IOException {
                Assume.assumeTrue(TckPublisher.isReplicationEnabled());
                String url = uddiServer.getSecurityUrl();
                if (url != null) { //Replication is option, but if it is configured in the uddi.xml then we assume it is implemented
                        UDDIService uddiService = new UDDIService();
                        UDDIReplicationPortType port = uddiService.getUDDIReplicationPort();
                        assertNotNull(port);
                }
        }

        @Test
        public void findSubscriptionListenerPort() throws IOException {
                Assume.assumeTrue(TckPublisher.isSubscriptionEnabled());
                String url = uddiServer.getSecurityUrl();
                if (url != null) { //Subscription Listener is client side, but if it is configured in the uddi.xml then we assume it is implemented
                        UDDIService uddiService = new UDDIService();
                        UDDISubscriptionListenerPortType port = uddiService.getUDDISubscriptionListenerPort();
                        assertNotNull(port);
                }
        }
}
