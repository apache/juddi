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
package org.apache.juddi.v3.client.cli;

import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.api_v3.*;
import org.uddi.custody_v3.KeyBag;
import org.uddi.custody_v3.TransferEntities;
import org.uddi.custody_v3.TransferToken;
import org.uddi.v3_service.UDDICustodyTransferPortType;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * This provides an example of how to transfer custody of a business from one
 * user to another on the same UDDI node. All child objects are also transfer
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class UddiCustodyTransfer {

        private UDDISecurityPortType security = null;
        private UDDIPublicationPortType publish = null;
        private UDDIInquiryPortType uddiInquiryService = null;
        private UDDICustodyTransferPortType custodyTransferPortType = null;

        public UddiCustodyTransfer() {
                try {
                        // create a manager and read the config in the archive; 
                        // you can use your config file name
                        UDDIClient clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
                        Transport transport = clerkManager.getTransport();
                        // Now you create a reference to the UDDI API
                        security = transport.getUDDISecurityService();

                        publish = transport.getUDDIPublishService();
                        uddiInquiryService = transport.getUDDIInquiryService();
                        custodyTransferPortType = transport.getUDDICustodyTransferService();
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public void main(String args[]) throws Exception {
                UddiCustodyTransfer sp = new UddiCustodyTransfer();

                GetAuthToken getAuthTokenRoot = new GetAuthToken();
                getAuthTokenRoot.setUserID("root");
                getAuthTokenRoot.setCred("root");

                // Making API call that retrieves the authentication token for the 'root' user.
                AuthToken rootAuthToken = security.getAuthToken(getAuthTokenRoot);
                System.out.println("root AUTHTOKEN = " + "don't log auth tokens!");

                getAuthTokenRoot = new GetAuthToken();
                getAuthTokenRoot.setUserID("uddi");
                getAuthTokenRoot.setCred("uddi");

                // Making API call that retrieves the authentication token for the 'root' user.
                AuthToken uddiAuthToken = security.getAuthToken(getAuthTokenRoot);
                System.out.println("uddi AUTHTOKEN = " + "don't log auth tokens!");
                BusinessEntity biz = sp.createBusiness("uddi");

                //save user uddi's business
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(uddiAuthToken.getAuthInfo());
                sb.getBusinessEntity().add(biz);
                BusinessDetail saveBusiness = publish.saveBusiness(sb);

                sp.transferBusiness(uddiAuthToken.getAuthInfo(), "uddi", rootAuthToken.getAuthInfo(), "root", saveBusiness.getBusinessEntity().get(0).getBusinessKey());
        }

        public void transferBusiness(String fromUser, String fromUserAuthToken, String toUser, String toUserAuthToken,
                String BusinessKey) throws Exception {

                System.out.println("Transfering business key " + BusinessKey);
                GregorianCalendar gcal = new GregorianCalendar();
                gcal.setTimeInMillis(System.currentTimeMillis());
       
                //Create a transfer token from fromUser to toUser
                KeyBag kb = new KeyBag();
                kb.getKey().add(BusinessKey);
                Holder<String> nodeidOUT = new Holder<String>();
                Holder<XMLGregorianCalendar> expiresOUT = new Holder<XMLGregorianCalendar>();
                Holder<byte[]> tokenOUT = new Holder<byte[]>();
                custodyTransferPortType.getTransferToken(fromUserAuthToken, kb, nodeidOUT, expiresOUT, tokenOUT);

                System.out.println("Transfer token obtained. Give this to user " + toUser);
                System.out.println("Expires " + expiresOUT.value.toXMLFormat());
                System.out.println("Node " + nodeidOUT.value);
                System.out.println("Token " + org.apache.commons.codec.binary.Base64.encodeBase64String(tokenOUT.value));

                if (toUser == null || toUser.length() == 0 || toUserAuthToken == null || toUserAuthToken.length() == 0) {
                        System.out.println("The toUser parameters are either null or empty, I can't complete the transfer here");
                        return;
                }

                //The magic part happens here, the user ROOT needs to give the user UDDI the token information out of band
                //in practice, all values must match exactly
                //UDDI now accepts the transfer
                TransferEntities te = new TransferEntities();
                te.setAuthInfo(toUserAuthToken);
                te.setKeyBag(kb);
                TransferToken tt = new TransferToken();
                tt.setExpirationTime(expiresOUT.value);
                tt.setNodeID(nodeidOUT.value);
                tt.setOpaqueToken(tokenOUT.value);
                te.setTransferToken(tt);
                System.out.println("Excuting transfer...");
                custodyTransferPortType.transferEntities(te);
                System.out.println("Complete! verifing ownership change...");

                //confirm the transfer
                GetOperationalInfo go = new GetOperationalInfo();
                go.setAuthInfo(fromUserAuthToken);
                go.getEntityKey().add(BusinessKey);
                OperationalInfos operationalInfo = uddiInquiryService.getOperationalInfo(go);
                boolean ok = false;
                boolean found = false;
                for (int i = 0; i < operationalInfo.getOperationalInfo().size(); i++) {
                        if (operationalInfo.getOperationalInfo().get(i).getEntityKey().equalsIgnoreCase(BusinessKey)) {
                                found = true;
                                if (operationalInfo.getOperationalInfo().get(i).getAuthorizedName().equalsIgnoreCase(fromUser)) {
                                        System.out.println("Transfer unexpected failed");
                                }
                                if (operationalInfo.getOperationalInfo().get(i).getAuthorizedName().equalsIgnoreCase(toUser)) {
                                        ok = true;
                                }

                        }
                }
                if (!found) {
                        System.out.println("Could get the operational info the transfed business");
                }
                System.out.println("Transfer " + (ok ? "success" : " failed"));
        }

        private BusinessEntity createBusiness(String user) {
                BusinessEntity be = new BusinessEntity();
                be.getName().add(new Name(user + "'s business", null));
                return be;
        }

}
