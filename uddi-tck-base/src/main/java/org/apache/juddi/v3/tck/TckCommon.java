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
package org.apache.juddi.v3.tck;

import java.rmi.RemoteException;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessInfos;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.Contacts;
import org.uddi.api_v3.DeleteBusiness;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.GetOperationalInfo;
import org.uddi.api_v3.GetServiceDetail;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.OperationalInfos;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.api_v3.ServiceInfos;
import org.uddi.api_v3.ServiceList;
import org.uddi.api_v3.TModelList;
import org.uddi.sub_v3.DeleteSubscription;
import org.uddi.sub_v3.Subscription;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;

/**
 * Common Utilities for TCK tests
 *
 * @author Alex O'Ree
 */
public class TckCommon {

        private static Log logger = LogFactory.getLog(TckCommon.class);
//<editor-fold defaultstate="collapsed" desc="Some basic util functions to print out the data structure">

        /**
         * Converts category bags of tmodels to a readable string
         *
         * @param categoryBag
         * @return human readable category bag
         */
        public static String CatBagToString(CategoryBag categoryBag) {
                StringBuilder sb = new StringBuilder();
                if (categoryBag == null) {
                        return "no data";
                }
                for (int i = 0; i < categoryBag.getKeyedReference().size(); i++) {
                        sb.append(KeyedReferenceToString(categoryBag.getKeyedReference().get(i)));
                }
                for (int i = 0; i < categoryBag.getKeyedReferenceGroup().size(); i++) {
                        sb.append("Key Ref Grp: TModelKey=");
                        for (int k = 0; k < categoryBag.getKeyedReferenceGroup().get(i).getKeyedReference().size(); k++) {
                                sb.append(KeyedReferenceToString(categoryBag.getKeyedReferenceGroup().get(i).getKeyedReference().get(k)));
                        }
                }
                return sb.toString();
        }

        public static String KeyedReferenceToString(KeyedReference item) {
                StringBuilder sb = new StringBuilder();
                sb.append("Key Ref: Name=").
                        append(item.getKeyName()).
                        append(" Value=").
                        append(item.getKeyValue()).
                        append(" tModel=").
                        append(item.getTModelKey()).
                        append(System.getProperty("line.separator"));
                return sb.toString();
        }

        public static void PrintContacts(Contacts contacts) {
                if (contacts == null) {
                        return;
                }
                for (int i = 0; i < contacts.getContact().size(); i++) {
                        System.out.println("Contact " + i + " type:" + contacts.getContact().get(i).getUseType());
                        for (int k = 0; k < contacts.getContact().get(i).getPersonName().size(); k++) {
                                System.out.println("Name: " + contacts.getContact().get(i).getPersonName().get(k).getValue());
                        }
                        for (int k = 0; k < contacts.getContact().get(i).getEmail().size(); k++) {
                                System.out.println("Email: " + contacts.getContact().get(i).getEmail().get(k).getValue());
                        }
                        for (int k = 0; k < contacts.getContact().get(i).getAddress().size(); k++) {
                                System.out.println("Address sort code " + contacts.getContact().get(i).getAddress().get(k).getSortCode());
                                System.out.println("Address use type " + contacts.getContact().get(i).getAddress().get(k).getUseType());
                                System.out.println("Address tmodel key " + contacts.getContact().get(i).getAddress().get(k).getTModelKey());
                                for (int x = 0; x < contacts.getContact().get(i).getAddress().get(k).getAddressLine().size(); x++) {
                                        System.out.println("Address line value " + contacts.getContact().get(i).getAddress().get(k).getAddressLine().get(x).getValue());
                                        System.out.println("Address line key name " + contacts.getContact().get(i).getAddress().get(k).getAddressLine().get(x).getKeyName());
                                        System.out.println("Address line key value " + contacts.getContact().get(i).getAddress().get(k).getAddressLine().get(x).getKeyValue());
                                }
                        }
                        for (int k = 0; k < contacts.getContact().get(i).getDescription().size(); k++) {
                                System.out.println("Desc: " + contacts.getContact().get(i).getDescription().get(k).getValue());
                        }
                        for (int k = 0; k < contacts.getContact().get(i).getPhone().size(); k++) {
                                System.out.println("Phone: " + contacts.getContact().get(i).getPhone().get(k).getValue());
                        }
                }

        }

        /**
         * This function is useful for translating UDDI's somewhat complex data
         * format to something that is more useful.
         *
         * @param bindingTemplates
         */
        public static void PrintBindingTemplates(BindingTemplates bindingTemplates) {
                if (bindingTemplates == null) {
                        return;
                }
                for (int i = 0; i < bindingTemplates.getBindingTemplate().size(); i++) {
                        System.out.println("Binding Key: " + bindingTemplates.getBindingTemplate().get(i).getBindingKey());

                        if (bindingTemplates.getBindingTemplate().get(i).getAccessPoint() != null) {
                                System.out.println("Access Point: " + bindingTemplates.getBindingTemplate().get(i).getAccessPoint().getValue() + " type " + bindingTemplates.getBindingTemplate().get(i).getAccessPoint().getUseType());
                        }

                        if (bindingTemplates.getBindingTemplate().get(i).getHostingRedirector() != null) {
                                System.out.println("Hosting Redirection: " + bindingTemplates.getBindingTemplate().get(i).getHostingRedirector().getBindingKey());
                        }
                }
        }

        public static void PrintBusinessInfo(BusinessInfos businessInfos) {
                if (businessInfos == null) {
                        System.out.println("No data returned");
                } else {
                        for (int i = 0; i < businessInfos.getBusinessInfo().size(); i++) {
                                System.out.println("===============================================");
                                System.out.println("Business Key: " + businessInfos.getBusinessInfo().get(i).getBusinessKey());
                                System.out.println("Name: " + ListToString(businessInfos.getBusinessInfo().get(i).getName()));

                                System.out.println("Name: " + ListToDescString(businessInfos.getBusinessInfo().get(i).getDescription()));
                                System.out.println("Services:");
                                PrintServiceInfo(businessInfos.getBusinessInfo().get(i).getServiceInfos());
                        }
                }
        }

        public static String ListToString(List<Name> name) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < name.size(); i++) {
                        sb.append(name.get(i).getValue()).append(" ");
                }
                return sb.toString();
        }

        public static String ListToDescString(List<Description> name) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < name.size(); i++) {
                        sb.append(name.get(i).getValue()).append(" ");
                }
                return sb.toString();
        }

        public static void PrintServiceInfo(ServiceInfos serviceInfos) {
                for (int i = 0; i < serviceInfos.getServiceInfo().size(); i++) {
                        System.out.println("-------------------------------------------");
                        System.out.println("Service Key: " + serviceInfos.getServiceInfo().get(i).getServiceKey());
                        System.out.println("Owning Business Key: " + serviceInfos.getServiceInfo().get(i).getBusinessKey());
                        System.out.println("Name: " + ListToString(serviceInfos.getServiceInfo().get(i).getName()));
                }
        }

        public static void PrintBusinessDetails(List<BusinessEntity> businessDetail) {


                for (int i = 0; i < businessDetail.size(); i++) {
                        System.out.println("Business Detail - key: " + businessDetail.get(i).getBusinessKey());
                        System.out.println("Name: " + ListToString(businessDetail.get(i).getName()));
                        System.out.println("CategoryBag: " + CatBagToString(businessDetail.get(i).getCategoryBag()));
                        PrintContacts(businessDetail.get(i).getContacts());
                }
        }

        /**
         * use this for clean up actions after running tests. if an exception is
         * raised, it will only be logged
         *
         * @param key
         * @param authInfo
         * @param publish
         */
        public static void DeleteBusiness(String key, String authInfo, UDDIPublicationPortType publish) {
                if (key == null) {
                        return;
                }
                try {
                        DeleteBusiness db = new DeleteBusiness();
                        db.setAuthInfo(authInfo);
                        db.getBusinessKey().add(key);
                        publish.deleteBusiness(db);
                } catch (Exception ex) {
                        logger.warn("failed to delete business " + key + " " + ex.getMessage());
                        logger.debug("failed to delete business " + key + " " + ex.getMessage(), ex);
                }
        }

        public static void removeAllExistingSubscriptions(String authinfo, UDDISubscriptionPortType sub) {
                List<Subscription> subscriptions;
                try {
                        subscriptions = sub.getSubscriptions(authinfo);

                        DeleteSubscription ds = new DeleteSubscription();
                        ds.setAuthInfo(authinfo);
                        for (int i = 0; i < subscriptions.size(); i++) {
                                ds.getSubscriptionKey().add(subscriptions.get(i).getSubscriptionKey());
                        }
                        if (!subscriptions.isEmpty()) {
                                logger.info("Purging " + subscriptions.size() + " old subscriptions");
                                sub.deleteSubscription(ds);
                        }
                } catch (Exception ex) {
                        logger.warn("error clearing subscriptions", ex);
                }
        }

        /**
         * returns true if the System Property "debug" is equal to "true"
         * @return true/false
         */
        public static boolean isDebug() {
                boolean serialize = false;
                try {
                        if (System.getProperty("debug") != null
                                && System.getProperty("debug").equalsIgnoreCase("true")) {
                                serialize = true;
                        }
                } catch (Exception ex) {
                }
                return serialize;
        }
        
        
         public static String DumpAllServices(String authinfo, UDDIInquiryPortType inquiry) {
                StringBuilder sb = new StringBuilder();
                FindService fs = new FindService();
                fs.setAuthInfo(authinfo);
                fs.setFindQualifiers(new FindQualifiers());
                fs.getFindQualifiers().getFindQualifier().add("approximateMatch");
                fs.getName().add(new Name("%", null));
                try {
                        ServiceList findService = inquiry.findService(fs);
                        if (findService.getServiceInfos() == null) {
                                return ("NO SERVICES RETURNED!");
                        } else {
                                for (int i = 0; i < findService.getServiceInfos().getServiceInfo().size(); i++) {
                                        sb.append(findService.getServiceInfos().getServiceInfo().get(i).getName().get(0).getValue()).
                                                append(" lang=").append(findService.getServiceInfos().getServiceInfo().get(i).getName().get(0).getLang()).
                                                append(" ").append(findService.getServiceInfos().getServiceInfo().get(i).getServiceKey()).
                                                append(" ").append(findService.getServiceInfos().getServiceInfo().get(i).getBusinessKey()).
                                                append(System.getProperty("line.separator"));
                                        GetServiceDetail req=new GetServiceDetail();
                                        req.setAuthInfo(authinfo);
                                        req.getServiceKey().add(findService.getServiceInfos().getServiceInfo().get(i).getServiceKey());
                                         ServiceDetail d=inquiry.getServiceDetail(req);
                                        for(BusinessService bs : d.getBusinessService()){
                                           for(BindingTemplate bt:bs.getBindingTemplates().getBindingTemplate()){
                                               sb.append(bt.getBindingKey());
                                               sb.append(" ");
                                               sb.append(bt.getAccessPoint().getValue());
                                               sb. append(System.getProperty("line.separator"));
                                           }
                                        }
                                }
                        }
                } catch (Exception ex) {
                        sb.append(ex.getMessage());
                }
                return sb.toString();
        }

         
        public static String DumpAllTModels(String authinfo, UDDIInquiryPortType inquriy) {
                StringBuilder sb = new StringBuilder();
                FindTModel fs = new FindTModel();
                fs.setAuthInfo(authinfo);
                fs.setFindQualifiers(new FindQualifiers());
                fs.getFindQualifiers().getFindQualifier().add("approximateMatch");
                fs.setName(new Name("%", null));
                try {
                        TModelList findService = inquriy.findTModel(fs);
                        if (findService.getTModelInfos()== null) {
                                return ("NO TMODELS RETURNED!");
                        } else {
                                for (int i = 0; i < findService.getTModelInfos().getTModelInfo().size(); i++) {
                                        sb.append(findService.getTModelInfos().getTModelInfo().get(i).getName().getValue()).
                                                append(" lang=").append(findService.getTModelInfos().getTModelInfo().get(i).getName().getLang()).
                                                append(" ").append(findService.getTModelInfos().getTModelInfo().get(i).getTModelKey())
                                                .append(System.getProperty("line.separator"));
                                }
                        }
                } catch (Exception ex) {
                        return ex.getMessage();
                }
                return sb.toString();
        }         
        public static String DumpAllBusinesses(String authinfo, UDDIInquiryPortType inquriy) {
                StringBuilder sb = new StringBuilder();
                FindBusiness fs = new FindBusiness();
                fs.setAuthInfo(authinfo);
                fs.setFindQualifiers(new FindQualifiers());
                fs.getFindQualifiers().getFindQualifier().add("approximateMatch");
                fs.getName().add(new Name("%", null));
                try {
                        BusinessList findService = inquriy.findBusiness(fs);
                        if (findService.getBusinessInfos() == null) {
                                return ("NO BUSINESSES RETURNED!");
                        } else {
                                for (int i = 0; i < findService.getBusinessInfos().getBusinessInfo().size(); i++) {
                                        sb.append(findService.getBusinessInfos().getBusinessInfo().get(i).getName().get(0).getValue()).
                                                append(" lang=").append(findService.getBusinessInfos().getBusinessInfo().get(i).getName().get(0).getLang()).
                                                append(" ").append(findService.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey())
                                                .append(System.getProperty("line.separator"));
                                }
                        }
                } catch (Exception ex) {
                        return ex.getMessage();
                }
                return sb.toString();
        }

        public static void PrintMarker() {
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>> MARKER <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>> MARKER <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>> MARKER <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>> MARKER <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>> MARKER <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>> MARKER <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        }

        public static void DumpAllTModelsOpInfo(String authInfoJoe, UDDIInquiryPortType uddiInquiryImpl) throws Exception {
                FindTModel ftm = new FindTModel();
                ftm.setAuthInfo(authInfoJoe);
                //org.apache.juddi.v3.client.UDDIConstants.WILDCARD
                ftm.setName(new Name("%", null));
                ftm.setFindQualifiers(new FindQualifiers());
                ftm.getFindQualifiers().getFindQualifier().add("approximateMatch");
                TModelList findTModel = uddiInquiryImpl.findTModel(ftm);

                GetOperationalInfo req = new GetOperationalInfo();
                req.setAuthInfo(authInfoJoe);

                for (int i = 0; i < findTModel.getTModelInfos().getTModelInfo().size(); i++) {
                        req.getEntityKey().add(
                             findTModel.getTModelInfos().getTModelInfo().get(i).getTModelKey());
                }
                OperationalInfos operationalInfo = uddiInquiryImpl.getOperationalInfo(req);

                for (int i = 0; i < operationalInfo.getOperationalInfo().size(); i++) {
                        System.out.println(operationalInfo.getOperationalInfo().get(i).getEntityKey() + " on node "
                             + operationalInfo.getOperationalInfo().get(i).getNodeID() + " is owned by " + operationalInfo.getOperationalInfo().get(i).getAuthorizedName());
                }
        }

}
