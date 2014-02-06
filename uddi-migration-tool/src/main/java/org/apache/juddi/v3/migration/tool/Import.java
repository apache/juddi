/*
 * Copyright 2013 The Apache Software Foundation.
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
package org.apache.juddi.v3.migration.tool;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import javax.xml.bind.JAXB;
import org.apache.juddi.api_v3.GetPublisherDetail;
import org.apache.juddi.api_v3.PublisherDetail;
import org.apache.juddi.api_v3.SavePublisher;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.GetTModelDetail;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.TModelDetail;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * Provides UDDI import functions
 *
 * @author Alex O'Ree
 * @since 3.2
 */
public class Import {

        private static UDDISecurityPortType security = null;
        private static JUDDIApiPortType juddiApi = null;
        private static UDDIPublicationPortType publish = null;
        private static UDDIInquiryPortType inquiry;
        private JUDDIApiPortType juddi;
        String token = null;
        String username = null;
        String tmodelfile = null;
        String businessfile = null;
        String publishersfile = null;
        String mappingsfile = null;
        boolean safemode = false;
        boolean stripSig = false;
        boolean preserveOwnership = false;
        Set<String> usernames = new HashSet<String>();
        /**
         * item to user
         */
        Properties mapping = new Properties();
        /**
         * user to password
         */
        Properties userpwd = new Properties();
        String credFile;

        public void Execute(String config, String name, String username, String pass,
                String tmodelIn, String businessIn,
                boolean isJuddi, boolean safe, String publishersFile,
                boolean preserveOwnership,
                String mappingsfile, String credFile, boolean stripSig) throws Exception {

                this.safemode = safe;
                this.credFile = credFile;
                this.stripSig = stripSig;
                this.publishersfile = publishersFile;
                this.tmodelfile = tmodelIn;
                this.businessfile = businessIn;
                this.preserveOwnership = preserveOwnership;
                this.mappingsfile = mappingsfile;

                // create a manager and read the config in the archive; 
                // you can use your config file name
                UDDIClient clerkManager = new UDDIClient(config);
                clerkManager.start();
                UDDIClerk clerk = clerkManager.getClerk(name);
                // a ClerkManager can be a client to multiple UDDI nodes, so 
                // supply the nodeName (defined in your uddi.xml.
                // The transport can be WS, inVM, RMI etc which is defined in the uddi.xml
                Transport transport = clerkManager.getTransport(name);
                // Now you create a reference to the UDDI API
                security = transport.getUDDISecurityService();
                publish = transport.getUDDIPublishService();
                inquiry = transport.getUDDIInquiryService();
                juddi = transport.getJUDDIApiService();
                token = null;
                if (username == null || pass == null) {
                        username = clerk.getPublisher();
                        pass = clerk.getPassword();
                }
                if (username != null && pass != null) {
                }


                //load mapping files
                //prompt for credentials
                LoadProperties();

                if (isJuddi) {
                        ImportPublishers();
                }

                if (preserveOwnership) {
                        //we'll need credentials for all the users
                        EnsureCredentials();
                }



                ImportTmodels();
                ImportBusiness();



        }

        public String GetOwner(String key) {
                return mapping.getProperty(key);
        }

        public String GetPwd(String username) {
                return userpwd.getProperty(username);
        }

        private void ImportTmodels() throws Exception {
                SaveTModel stm = JAXB.unmarshal(new File(tmodelfile), SaveTModel.class);
                if (stripSig) {
                        int x=0;
                        for (int i = 0; i < stm.getTModel().size(); i++) {
                                x+=stm.getTModel().get(i).getSignature().size();
                                stm.getTModel().get(i).getSignature().clear();
                        }
                        System.out.println(x + " signatures stripped");
                }

                if (safemode) {
                        for (int i = 0; i < stm.getTModel().size(); i++) {
                                if (!TModelExists(stm.getTModel().get(i).getTModelKey(), token)) {
                                        SaveTModel stm2 = new SaveTModel();
                                        if (preserveOwnership) {
                                                stm2.setAuthInfo(Common.GetAuthToken(
                                                        GetOwner(stm.getTModel().get(i).getTModelKey()),
                                                        GetPwd(GetOwner(stm.getTModel().get(i).getTModelKey())), security));
                                        } else {
                                                stm2.setAuthInfo(token);
                                        }
                                        stm2.getTModel().add(stm.getTModel().get(i));
                                        try {
                                                publish.saveTModel(stm2);
                                                System.out.println(stm.getTModel().get(i).getTModelKey() + " saved");
                                        } catch (Exception ex) {
                                                System.out.println("Error saving " + stm.getTModel().get(i).getTModelKey() + " " + ex.getMessage());
                                        }
                                } else {
                                        System.out.println(stm.getTModel().get(i).getTModelKey() + " skipped, it exists already");
                                }
                        }
                } else {
                        stm.setAuthInfo(token);

                        publish.saveTModel(stm);
                        System.out.println("All " + stm.getTModel().size() + " saved!");
                }

        }

        private void ImportBusiness() throws Exception {
                SaveBusiness stm = JAXB.unmarshal(new File(businessfile), SaveBusiness.class);
                if (stripSig) {
                        int x=0;
                        for (int i = 0; i < stm.getBusinessEntity().size(); i++) {
                                x +=  stm.getBusinessEntity().get(i).getSignature().size();
                                stm.getBusinessEntity().get(i).getSignature().clear();
                                if (stm.getBusinessEntity().get(i).getBusinessServices() != null) {
                                        for (int i2 = 0; i2 < stm.getBusinessEntity().get(i).getBusinessServices().getBusinessService().size(); i2++) {
                                                x +=  stm.getBusinessEntity().get(i).getBusinessServices().getBusinessService().get(i2).getSignature().size();
                                                stm.getBusinessEntity().get(i).getBusinessServices().getBusinessService().get(i2).getSignature().clear();
                                                if (stm.getBusinessEntity().get(i).getBusinessServices().getBusinessService().get(i2).getBindingTemplates() != null) {
                                                        for (int i3 = 0; i3 < stm.getBusinessEntity().get(i).getBusinessServices().getBusinessService().get(i2).getBindingTemplates().getBindingTemplate().size(); i3++) {
                                                                x += stm.getBusinessEntity().get(i).getBusinessServices().getBusinessService().get(i2).getBindingTemplates().getBindingTemplate().get(i3).getSignature().size();
                                                                stm.getBusinessEntity().get(i).getBusinessServices().getBusinessService().get(i2).getBindingTemplates().getBindingTemplate().get(i3).getSignature().clear();
                                                        }
                                                }
                                        }
                                }
                        }
                        System.out.println(x + " signatures stripped");
                }

                if (safemode) {
                        for (int i = 0; i < stm.getBusinessEntity().size(); i++) {
                                if (!BusinessExists(stm.getBusinessEntity().get(i).getBusinessKey(), token)) {
                                        SaveBusiness stm2 = new SaveBusiness();
                                        if (preserveOwnership) {
                                                stm2.setAuthInfo(Common.GetAuthToken(GetOwner(stm.getBusinessEntity().get(i).getBusinessKey()), GetPwd(GetOwner(stm.getBusinessEntity().get(i).getBusinessKey())), security));
                                        } else {
                                                stm2.setAuthInfo(token);
                                        }
                                        stm2.getBusinessEntity().add(stm.getBusinessEntity().get(i));
                                        try {
                                                publish.saveBusiness(stm2);
                                                System.out.println(stm.getBusinessEntity().get(i).getBusinessKey() + " saved");
                                        } catch (Exception ex) {
                                                System.out.println("Error saving " + stm.getBusinessEntity().get(i).getBusinessKey() + " " + ex.getMessage());
                                        }
                                } else {
                                        System.out.println(stm.getBusinessEntity().get(i).getBusinessKey() + " skipped, it exists already");
                                }
                        }
                } else {
                        stm.setAuthInfo(token);
                        publish.saveBusiness(stm);
                        System.out.println("All " + stm.getBusinessEntity().size() + " businesses aved!");
                }
        }

        private boolean TModelExists(String tModelKey, String token) {
                GetTModelDetail r = new GetTModelDetail();
                r.setAuthInfo(token);
                r.getTModelKey().add(tModelKey);
                try {
                        TModelDetail tModelDetail = inquiry.getTModelDetail(r);
                        if (tModelDetail != null && !tModelDetail.getTModel().isEmpty()) {
                                return true;
                        }
                } catch (Exception ex) {
                }
                return false;
        }

        private boolean BusinessExists(String businessKey, String token) {
                GetBusinessDetail r = new GetBusinessDetail();
                r.setAuthInfo(token);
                r.getBusinessKey().add(businessKey);
                try {
                        BusinessDetail tModelDetail = inquiry.getBusinessDetail(r);
                        if (tModelDetail != null && !tModelDetail.getBusinessEntity().isEmpty()) {
                                return true;
                        }
                } catch (Exception ex) {
                }
                return false;
        }

        private void ImportPublishers() throws Exception {

                SavePublisher stm = JAXB.unmarshal(new File(publishersfile), SavePublisher.class);
                if (safemode) {
                        for (int i = 0; i < stm.getPublisher().size(); i++) {
                                if (!PublisherExists(stm.getPublisher().get(i).getAuthorizedName(), token)) {
                                        SavePublisher stm2 = new SavePublisher();
                                        stm2.setAuthInfo(token);
                                        stm2.getPublisher().add(stm.getPublisher().get(i));
                                        try {
                                                juddi.savePublisher(stm2);
                                                System.out.println(stm.getPublisher().get(i).getAuthorizedName() + " saved");
                                        } catch (Exception ex) {
                                                System.out.println("Error saving " + stm.getPublisher().get(i).getAuthorizedName() + " " + ex.getMessage());
                                        }
                                } else {
                                        System.out.println(stm.getPublisher().get(i).getAuthorizedName() + " skipped, it exists already");
                                }
                        }
                } else {
                        stm.setAuthInfo(token);
                        juddi.savePublisher(stm);
                        System.out.println("All " + stm.getPublisher().size() + " publishers saved!");
                }
        }

        private boolean PublisherExists(String authorizedName, String token) {
                GetPublisherDetail r = new GetPublisherDetail();
                r.setAuthInfo(token);
                r.getPublisherId().add(authorizedName);
                try {
                        PublisherDetail publisherDetail = juddi.getPublisherDetail(r);
                        if (publisherDetail != null && !publisherDetail.getPublisher().isEmpty()) {
                                return true;
                        }
                } catch (Exception ex) {
                }
                return false;
        }

        private void LoadProperties() {


                try {
                        FileInputStream fos = new FileInputStream(mappingsfile);
                        mapping.load(fos);
                        fos.close();
                } catch (Exception ex) {
                        ex.printStackTrace();
                }

                //mapping.getProperty("usernames");

                try {
                        FileInputStream fos = new FileInputStream(credFile);
                        userpwd.load(fos);
                        fos.close();
                } catch (Exception ex) {
                        ex.printStackTrace();
                }
        }

        private void EnsureCredentials() throws Exception {
                Iterator<String> it = usernames.iterator();
                while (it.hasNext()){
                        String u = it.next();
                        if (!userpwd.containsKey(u)){
                                throw new Exception("The credential for user '"+u+"' is not in the credential file");
                        }
                }
        }
}
