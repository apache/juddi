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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import javax.xml.bind.JAXB;
import org.apache.juddi.api_v3.GetAllPublisherDetail;
import org.apache.juddi.api_v3.PublisherDetail;
import org.apache.juddi.api_v3.SavePublisher;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.GetTModelDetail;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelInfo;
import org.uddi.api_v3.TModelList;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * Provides UDDI export functions
 *
 * @author Alex O'Ree
 * @since 3.2
 */
public class Export {

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
    boolean myitemsonly = false;
    Set<String> usernames = new HashSet<String>();
    Properties mapping = new Properties();

    public void Execute(String config, String name, String user, String pass,
            String tmodelout, String businessOut,
            boolean isJuddi, boolean safe, String publishersFile,
            boolean myItemsOnly,
            String mappingsfile) throws Exception {
        // create a manager and read the config in the archive; 
        // you can use your config file name
        this.publishersfile = publishersFile;
        this.tmodelfile = tmodelout;
        this.businessfile = businessOut;
        this.myitemsonly = myItemsOnly;
        this.mappingsfile = mappingsfile;
        UDDIClient clerkManager = new UDDIClient(config);
        clerkManager.start();
        UDDIClerk clerk = clerkManager.getClerk(name);

        // register the clerkManager with the client side container
        UDDIClientContainer.addClient(clerkManager);            // a ClerkManager can be a client to multiple UDDI nodes, so 
        // a ClerkManager can be a client to multiple UDDI nodes, so 
        // supply the nodeName (defined in your uddi.xml.
        // The transport can be WS, inVM, RMI etc which is defined in the uddi.xml
        Transport transport = clerkManager.getTransport(name);
        // Now you create a reference to the UDDI API
        security = transport.getUDDISecurityService();
        publish = transport.getUDDIPublishService();
        inquiry = transport.getUDDIInquiryService();
        juddi = transport.getJUDDIApiService();
        this.username = user;
        if (username == null || pass == null) {
            username = clerk.getPublisher();
            pass = clerk.getPassword();
        }
        if (username != null && pass != null
                || username.length() != 0 && pass.length() != 0) {
            GetAuthToken getAuthTokenRoot = new GetAuthToken();
            getAuthTokenRoot.setUserID(username);
            getAuthTokenRoot.setCred(pass);
            token = security.getAuthToken(getAuthTokenRoot).getAuthInfo();
        } else {
            System.out.println("No credentials are available. This will probably fail spectacularly");

        }


        ExportTmodels();
        ExportBusiness();

        if (isJuddi) {
            //optional juddi
            ExportNodes();
            ExportClerks();
            ExportPublishers();
        }
        SaveProperties();
        clerkManager.stop();
    }

    private void ExportBusiness() throws Exception {
        FileOutputStream fos = new FileOutputStream(businessfile);
        FindBusiness req = new FindBusiness();
        req.setAuthInfo(token);
        req.getName().add(new Name(UDDIConstants.WILDCARD, null));
        req.setFindQualifiers(new FindQualifiers());
        req.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
        int offset = 0;
        int maxrows = 20;

        //TODO param
        req.setMaxRows(maxrows);
        req.setListHead(offset);
        BusinessList findTModel = null;
        SaveBusiness sb = new SaveBusiness();
        //TODO exclusion list
        do {
            findTModel = inquiry.findBusiness(req);
            if (findTModel.getBusinessInfos() != null) {
                for (int i = 0; i < findTModel.getBusinessInfos().getBusinessInfo().size(); i++) {
                    boolean go = true;
                    String owner = Common.GetOwner(findTModel.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey(), token, inquiry);
                    if (!usernames.contains(owner)) {
                        usernames.add(owner);
                    }
                    if (myitemsonly) {
                        if (owner == null || !owner.equalsIgnoreCase(username)) {
                            go = false;
                            System.out.println("skipping " + findTModel.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey() + " owned by " + owner);
                        }
                    }
                    if (go) {
                        mapping.setProperty(findTModel.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey(), owner);
                        System.out.println("Exporting " + findTModel.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey());
                        sb.getBusinessEntity().add(GetBusiness(findTModel.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey(), token));
                    }
                }
            }
            //do stuff
            offset = offset + maxrows;
            req.setListHead(offset);

        } while (findTModel.getListDescription().getIncludeCount() > 0);
        System.out.println("Saving to disk");
        JAXB.marshal(sb, fos);
        fos.close();
        System.out.println("Done with businesses");
    }

    private void ExportTmodels() throws Exception {
        FileOutputStream fos = new FileOutputStream(tmodelfile);
        FindTModel req = new FindTModel();
        req.setName(new Name(UDDIConstants.WILDCARD, null));
        req.setAuthInfo(token);
        req.setFindQualifiers(new FindQualifiers());
        req.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
        int offset = 0;
        int maxrows = 20;

        req.setMaxRows(maxrows);
        req.setListHead(offset);
        TModelList findTModel = null;
        SaveTModel stm = new SaveTModel();
        do {
            findTModel = inquiry.findTModel(req);
            if (findTModel.getTModelInfos() != null) {
                for (int i = 0; i < findTModel.getTModelInfos().getTModelInfo().size(); i++) {
                    boolean go = true;
                    String owner = Common.GetOwner(findTModel.getTModelInfos().getTModelInfo().get(i).getTModelKey(), token, inquiry);
                    if (!usernames.contains(owner)) {
                        usernames.add(owner);
                    }
                    if (myitemsonly) {
                        if (owner == null || !owner.equalsIgnoreCase(username)) {
                            go = false;
                        }
                    }
                    if (go) {
                        mapping.setProperty(findTModel.getTModelInfos().getTModelInfo().get(i).getTModelKey(), owner);
                        System.out.println("Exporting " + findTModel.getTModelInfos().getTModelInfo().get(i).getTModelKey());
                        stm.getTModel().add(GetTmodel(findTModel.getTModelInfos().getTModelInfo().get(i), token));
                    }
                }
            }
            offset = offset + maxrows;
            req.setListHead(offset);
        } while (findTModel.getListDescription().getIncludeCount() > 0);
        System.out.println("Storing to disk ");
        JAXB.marshal(stm, fos);
        fos.close();
        System.out.println("Done with tModels");
    }

    private TModel GetTmodel(TModelInfo get, String token) throws Exception {
        GetTModelDetail r = new GetTModelDetail();
        r.setAuthInfo(token);
        r.getTModelKey().add(get.getTModelKey());
        return inquiry.getTModelDetail(r).getTModel().get(0);
    }

    private BusinessEntity GetBusiness(String businessKey, String token) throws Exception {
        GetBusinessDetail r = new GetBusinessDetail();
        r.getBusinessKey().add(businessKey);
        r.setAuthInfo(token);
        return inquiry.getBusinessDetail(r).getBusinessEntity().get(0);
    }

    private void ExportNodes() {
    }

    private void ExportClerks() {
    }

    private void ExportPublishers() throws Exception {
        FileOutputStream fos = new FileOutputStream(publishersfile);

        GetAllPublisherDetail r = new GetAllPublisherDetail();
        r.setAuthInfo(token);
        PublisherDetail allPublisherDetail = juddi.getAllPublisherDetail(r);
        SavePublisher saver = new SavePublisher();
        saver.getPublisher().addAll(allPublisherDetail.getPublisher());
        System.out.println("Storing to disk ");
        JAXB.marshal(saver, fos);
        fos.close();
        System.out.println("Done with Publishers");
    }

    private void SaveProperties() throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = usernames.iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            if (it.hasNext()) {
                sb.append(",");
            }
        }
        mapping.put("usernames", sb.toString());
        try {
            FileOutputStream fos = new FileOutputStream(mappingsfile);
            mapping.store(fos, token);
            fos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
