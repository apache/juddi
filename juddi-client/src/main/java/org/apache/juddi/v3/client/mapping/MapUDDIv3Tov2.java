/*
 * Copyright 2014 The Apache Software Foundation.
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
package org.apache.juddi.v3.client.mapping;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.juddi.v3.client.UDDIConstants;
import org.uddi.api_v2.AccessPoint;
import org.uddi.api_v2.AddPublisherAssertions;
import org.uddi.api_v2.Address;
import org.uddi.api_v2.AddressLine;
import org.uddi.api_v2.AssertionStatusReport;
import org.uddi.api_v2.BindingDetail;
import org.uddi.api_v2.BindingTemplate;
import org.uddi.api_v2.BindingTemplates;
import org.uddi.api_v2.BusinessDetailExt;
import org.uddi.api_v2.BusinessEntity;
import org.uddi.api_v2.BusinessEntityExt;
import org.uddi.api_v2.BusinessInfo;
import org.uddi.api_v2.BusinessInfos;
import org.uddi.api_v2.BusinessList;
import org.uddi.api_v2.BusinessService;
import org.uddi.api_v2.BusinessServices;
import org.uddi.api_v2.CategoryBag;
import org.uddi.api_v2.Contact;
import org.uddi.api_v2.Contacts;
import org.uddi.api_v2.DeleteBinding;
import org.uddi.api_v2.DeleteBusiness;
import org.uddi.api_v2.DeletePublisherAssertions;
import org.uddi.api_v2.DeleteService;
import org.uddi.api_v2.DeleteTModel;
import org.uddi.api_v2.Description;
import org.uddi.api_v2.DiscoveryURL;
import org.uddi.api_v2.DiscoveryURLs;
import org.uddi.api_v2.Email;
import org.uddi.api_v2.ErrInfo;
import org.uddi.api_v2.FindBinding;
import org.uddi.api_v2.FindQualifiers;
import org.uddi.api_v2.FindRelatedBusinesses;
import org.uddi.api_v2.FindService;
import org.uddi.api_v2.GetPublisherAssertions;
import org.uddi.api_v2.GetRegisteredInfo;
import org.uddi.api_v2.HostingRedirector;
import org.uddi.api_v2.IdentifierBag;
import org.uddi.api_v2.InstanceDetails;
import org.uddi.api_v2.KeyedReference;
import org.uddi.api_v2.Name;
import org.uddi.api_v2.OverviewDoc;
import org.uddi.api_v2.SaveBinding;
import org.uddi.api_v2.SaveBusiness;
import org.uddi.api_v2.SaveService;
import org.uddi.api_v2.SaveTModel;
import org.uddi.api_v2.SetPublisherAssertions;
import org.uddi.api_v2.TModel;
import org.uddi.api_v2.TModelBag;
import org.uddi.api_v2.TModelInstanceDetails;
import org.uddi.api_v2.TModelInstanceInfo;
import org.uddi.api_v2.URLType;
import org.uddi.api_v2.FindBusiness;
import org.uddi.api_v2.FindTModel;
import org.uddi.api_v2.GetAssertionStatusReport;
import org.uddi.api_v2.GetBindingDetail;
import org.uddi.api_v2.GetBusinessDetail;
import org.uddi.api_v2.GetServiceDetail;
import org.uddi.api_v2.GetTModelDetail;
import org.uddi.api_v2.KeyType;
import org.uddi.api_v2.KeysOwned;
import org.uddi.api_v2.Phone;
import org.uddi.api_v2.PublisherAssertion;
import org.uddi.api_v2.PublisherAssertions;
import org.uddi.api_v2.RegisteredInfo;
import org.uddi.api_v2.RelatedBusinessInfo;
import org.uddi.api_v2.RelatedBusinessInfos;
import org.uddi.api_v2.RelatedBusinessesList;
import org.uddi.api_v2.Result;
import org.uddi.api_v2.ServiceDetail;
import org.uddi.api_v2.ServiceInfo;
import org.uddi.api_v2.ServiceInfos;
import org.uddi.api_v2.ServiceList;
import org.uddi.api_v2.TModelDetail;
import org.uddi.api_v2.TModelInfo;
import org.uddi.api_v2.TModelInfos;
import org.uddi.api_v2.TModelList;
import org.uddi.api_v2.Truncated;
import org.uddi.api_v3.AssertionStatusItem;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.CompletionStatus;
import org.uddi.v2_service.DispositionReport;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * Converts UDDIv3 objects to UDDI v2 objects. Note: these functions do not take
 * into account the differences in key naming conventions
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class MapUDDIv3Tov2 {

        public static final String VERSION = "2.0";

        public static org.uddi.api_v2.BusinessEntity MapBusiness(org.uddi.api_v3.BusinessEntity be, String operator) {
                if (be == null) {
                        return null;
                }
                BusinessEntity item = new org.uddi.api_v2.BusinessEntity();
                item.setBusinessKey(be.getBusinessKey());
                item.setCategoryBag(MapCategoryBag(be.getCategoryBag()));
                item.setContacts(MapContacts(be.getContacts()));
                item.setDiscoveryURLs(MapDiscoveryURLs(be.getDiscoveryURLs()));
                item.setIdentifierBag(MapIdentBag(be.getIdentifierBag()));
                item.setOperator(operator);
                item.getDescription().addAll(MapDescription(be.getDescription()));

                item.getName().addAll(MapName(be.getName()));
                if (be.getBusinessServices() != null && !be.getBusinessServices().getBusinessService().isEmpty()) {
                        item.setBusinessServices(new BusinessServices());
                        item.getBusinessServices().getBusinessService().addAll(MapService(be.getBusinessServices().getBusinessService()));
                }
                return item;
        }

        public static org.uddi.api_v2.BusinessService MapService(org.uddi.api_v3.BusinessService be) {
                if (be == null) {
                        return null;
                }
                BusinessService item = new org.uddi.api_v2.BusinessService();
                item.setCategoryBag(MapCategoryBag(be.getCategoryBag()));
                item.getDescription().addAll(MapDescription(be.getDescription()));
                item.setBusinessKey(be.getBusinessKey());
                item.setServiceKey(be.getServiceKey());
                item.getName().addAll(MapName(be.getName()));
                if (be.getBindingTemplates() != null && !be.getBindingTemplates().getBindingTemplate().isEmpty()) {
                        item.setBindingTemplates(new BindingTemplates());
                        item.getBindingTemplates().getBindingTemplate().addAll(MapBinding(be.getBindingTemplates().getBindingTemplate()));
                }
                return item;
        }

        public static List<org.uddi.api_v2.BusinessService> MapService(List<org.uddi.api_v3.BusinessService> be) {
                if (be == null) {
                        return null;
                }
                List<org.uddi.api_v2.BusinessService> item = new ArrayList<BusinessService>();
                for (int i = 0; i < be.size(); i++) {
                        item.add(MapService(be.get(i)));
                }
                return item;
        }

        public static org.uddi.api_v2.BindingTemplate MapBinding(org.uddi.api_v3.BindingTemplate be) {
                if (be == null) {
                        return null;
                }
                BindingTemplate item = new org.uddi.api_v2.BindingTemplate();
                item.getDescription().addAll(MapDescription(be.getDescription()));

                item.setBindingKey(be.getBindingKey());
                item.setServiceKey(be.getServiceKey());
                item.setAccessPoint(MapAccessPoint(be.getAccessPoint()));
                item.setHostingRedirector(MapHostingRedir(be.getHostingRedirector()));

                item.setTModelInstanceDetails(MapTModelInstanceDetails(be.getTModelInstanceDetails()));

                return item;
        }

        public static List<org.uddi.api_v2.BindingTemplate> MapBinding(List<org.uddi.api_v3.BindingTemplate> be) {
                if (be == null) {
                        return null;
                }
                List<org.uddi.api_v2.BindingTemplate> item = new ArrayList<BindingTemplate>();
                for (int i = 0; i < be.size(); i++) {
                        item.add(MapBinding(be.get(i)));
                }
                return item;
        }

        public static org.uddi.api_v2.TModel MapTModel(org.uddi.api_v3.TModel be) {
                if (be == null) {
                        return null;
                }
                TModel item = new org.uddi.api_v2.TModel();
                item.setTModelKey(be.getTModelKey());
                item.setName(new Name(be.getName().getValue(), be.getName().getLang()));
                item.setCategoryBag(MapCategoryBag(be.getCategoryBag()));
                item.getDescription().addAll(MapDescription(be.getDescription()));
                item.setIdentifierBag(MapIdentBag(be.getIdentifierBag()));
                item.setOverviewDoc(MapOverviewDoc(be.getOverviewDoc()));
                return item;
        }

        private static List<Name> MapName(List<org.uddi.api_v3.Name> name) {
                List<Name> items = new ArrayList<Name>();
                for (int i = 0; i < name.size(); i++) {
                        Name n = new Name(name.get(i).getValue(), name.get(i).getLang());
                        items.add(n);
                }
                return items;
        }

        private static CategoryBag MapCategoryBag(org.uddi.api_v3.CategoryBag categoryBag) {
                if (categoryBag == null) {
                        return null;
                }
                CategoryBag c = new CategoryBag();
                c.getKeyedReference().addAll(MapKeyedReference(categoryBag.getKeyedReference()));
                return c;
        }

        private static List<Description> MapDescription(List<org.uddi.api_v3.Description> description) {
                List<Description> ret = new ArrayList<Description>();
                if (description == null || description.isEmpty()) {
                        return ret;
                }
                for (int i = 0; i < description.size(); i++) {
                        ret.add(new Description(description.get(i).getValue(), description.get(i).getLang()));
                }
                return ret;

        }

        private static IdentifierBag MapIdentBag(org.uddi.api_v3.IdentifierBag identifierBag) {

                if (identifierBag == null) {
                        return null;
                }
                IdentifierBag r = new IdentifierBag();
                r.getKeyedReference().addAll(MapKeyedReference(identifierBag.getKeyedReference()));
                return r;
        }

        /**
         * limitation, only the first overview doc is mapped
         *
         * @param overviewDoc
         * @return overviewDoc or null
         */
        private static OverviewDoc MapOverviewDoc(List<org.uddi.api_v3.OverviewDoc> overviewDoc) {
                if (overviewDoc == null || overviewDoc.isEmpty()) {
                        return null;
                }
                OverviewDoc r = new OverviewDoc();

                r.getDescription().addAll(MapDescription(overviewDoc.get(0).getDescription()));
                if (overviewDoc.get(0).getOverviewURL() != null && overviewDoc.get(0).getOverviewURL().getValue() != null) {
                        r.setOverviewURL(overviewDoc.get(0).getOverviewURL().getValue());
                }

                return r;
        }

        private static AccessPoint MapAccessPoint(org.uddi.api_v3.AccessPoint accessPoint) {
                if (accessPoint == null) {
                        return null;
                }
                return new AccessPoint(accessPoint.getValue(), MapURLType(accessPoint.getValue()));
        }

        private static HostingRedirector MapHostingRedir(org.uddi.api_v3.HostingRedirector hostingRedirector) {
                if (hostingRedirector == null) {
                        return null;
                }
                HostingRedirector r = new HostingRedirector();
                r.setBindingKey(hostingRedirector.getBindingKey());
                return r;
        }

        private static TModelInstanceDetails MapTModelInstanceDetails(org.uddi.api_v3.TModelInstanceDetails tModelInstanceDetails) {
                if (tModelInstanceDetails == null) {
                        return new TModelInstanceDetails();
                }
                TModelInstanceDetails r = new TModelInstanceDetails();
                r.getTModelInstanceInfo().addAll(MapTModelInstanceInfo(tModelInstanceDetails.getTModelInstanceInfo()));
                return r;
        }

        private static List<KeyedReference> MapKeyedReference(List<org.uddi.api_v3.KeyedReference> keyedReference) {
                List<KeyedReference> r = new ArrayList<KeyedReference>();
                if (keyedReference == null) {
                        return r;
                }
                for (int i = 0; i < keyedReference.size(); i++) {
                        r.add(new KeyedReference(keyedReference.get(i).getTModelKey(), keyedReference.get(i).getKeyName(), keyedReference.get(i).getKeyValue()));
                }
                return r;
        }

        private static URLType MapURLType(String url) {
                if (url == null) {
                        return URLType.OTHER;
                }
                if (url.toLowerCase().startsWith("http:")) {
                        return URLType.HTTP;
                }
                if (url.toLowerCase().startsWith("https:")) {
                        return URLType.HTTPS;
                }
                if (url.toLowerCase().startsWith("ftp:")) {
                        return URLType.FTP;
                }
                if (url.toLowerCase().startsWith("mailto:")) {
                        return URLType.MAILTO;
                }
                if (url.toLowerCase().startsWith("phone:")) {
                        return URLType.PHONE;
                }
                if (url.toLowerCase().startsWith("fax:")) {
                        return URLType.FAX;
                }
                return URLType.OTHER;
        }

        private static List<TModelInstanceInfo> MapTModelInstanceInfo(List<org.uddi.api_v3.TModelInstanceInfo> tModelInstanceInfo) {
                List<TModelInstanceInfo> r = new ArrayList<TModelInstanceInfo>();
                if (tModelInstanceInfo == null) {
                        return r;
                }
                for (int i = 0; i < tModelInstanceInfo.size(); i++) {
                        TModelInstanceInfo t = new TModelInstanceInfo();
                        t.setTModelKey(tModelInstanceInfo.get(i).getTModelKey());
                        t.getDescription().addAll(MapDescription(tModelInstanceInfo.get(i).getDescription()));
                        if (tModelInstanceInfo.get(i).getInstanceDetails() != null) {
                                t.setInstanceDetails(new InstanceDetails());
                                t.getInstanceDetails().getDescription().addAll(MapDescription(tModelInstanceInfo.get(i).getInstanceDetails().getDescription()));
                                t.getInstanceDetails().setInstanceParms(StringEscapeUtils.escapeXml(tModelInstanceInfo.get(i).getInstanceDetails().getInstanceParms()));
                                t.getInstanceDetails().setOverviewDoc(MapOverviewDoc(tModelInstanceInfo.get(i).getInstanceDetails().getOverviewDoc()));
                        }
                        r.add(t);
                }
                return r;
        }

        public static FindBinding MapFindBinding(org.uddi.api_v3.FindBinding body) {
                FindBinding r = new FindBinding();
                r.setFindQualifiers(MapFindQualifiers(body.getFindQualifiers()));
                if (r.getFindQualifiers() == null) {
                        r.setFindQualifiers(new FindQualifiers());
                }
                r.setMaxRows(body.getMaxRows());
                r.setTModelBag(MapTModelBag(body.getTModelBag()));
                if (r.getTModelBag() == null) {
                        r.setTModelBag(new TModelBag());
                        r.getTModelBag().getTModelKey().add("");
                }
                r.setServiceKey(body.getServiceKey());
                if (r.getServiceKey() == null) {
                        r.setServiceKey("");
                }
                r.setGeneric(VERSION);
                return r;

        }

        public static FindBusiness MapFindBusiness(org.uddi.api_v3.FindBusiness body) {
                FindBusiness r = new FindBusiness();
                r.setFindQualifiers(MapFindQualifiers(body.getFindQualifiers()));
                r.setMaxRows(body.getMaxRows());
                r.setTModelBag(MapTModelBag(body.getTModelBag()));
                r.setCategoryBag(MapCategoryBag(body.getCategoryBag()));
                r.setGeneric(VERSION);
                r.setDiscoveryURLs(MapDiscoveryURLs(body.getDiscoveryURLs()));
                r.setIdentifierBag(MapIdentBag(body.getIdentifierBag()));
                r.getName().addAll(MapName(body.getName()));
                if (ContainsWildCard(r.getName())) {
                        //r.setFindQualifiers(AddApproximateMatch(r.getFindQualifiers()));
                }
                return r;
        }

        public static FindRelatedBusinesses MapFindRelatedBusiness(org.uddi.api_v3.FindRelatedBusinesses body) {
                FindRelatedBusinesses r = new FindRelatedBusinesses();
                r.setFindQualifiers(MapFindQualifiers(body.getFindQualifiers()));
                r.setMaxRows(body.getMaxRows());
                r.setBusinessKey(body.getBusinessKey());
                if (body.getKeyedReference() != null) {
                        r.setKeyedReference(new KeyedReference(body.getKeyedReference().getTModelKey(),
                             body.getKeyedReference().getKeyName(),
                             body.getKeyedReference().getKeyValue()));
                }

                r.setGeneric(VERSION);

                return r;
        }

        public static FindService MapFindService(org.uddi.api_v3.FindService body) {
                FindService r = new FindService();
                r.setFindQualifiers(MapFindQualifiers(body.getFindQualifiers()));
                r.setMaxRows(body.getMaxRows());
                r.setTModelBag(MapTModelBag(body.getTModelBag()));
                r.setCategoryBag(MapCategoryBag(body.getCategoryBag()));
                r.setGeneric(VERSION);

                r.setBusinessKey(body.getBusinessKey());
                r.getName().addAll(MapName(body.getName()));
                return r;
        }

        public static FindTModel MapFindTModel(org.uddi.api_v3.FindTModel body) {
                FindTModel r = new FindTModel();
                r.setFindQualifiers(MapFindQualifiers(body.getFindQualifiers()));
                r.setMaxRows(body.getMaxRows());
                r.setCategoryBag(MapCategoryBag(body.getCategoryBag()));
                r.setGeneric(VERSION);

                if (body.getName() != null) {
                        r.setName(new Name(body.getName().getValue(), body.getName().getLang()));
                }
                return r;
        }

        public static org.uddi.api_v2.GetBindingDetail MapGetBindingDetail(org.uddi.api_v3.GetBindingDetail body) {
                GetBindingDetail r = new GetBindingDetail();
                r.getBindingKey().addAll(body.getBindingKey());
                r.setGeneric(VERSION);

                return r;
        }

        public static org.uddi.api_v2.GetBusinessDetail MapGetBusinessDetail(org.uddi.api_v3.GetBusinessDetail body) {
                GetBusinessDetail r = new GetBusinessDetail();
                r.getBusinessKey().addAll(body.getBusinessKey());
                r.setGeneric(VERSION);

                return r;
        }

        public static org.uddi.api_v2.GetServiceDetail MapGetServiceDetail(org.uddi.api_v3.GetServiceDetail body) {
                GetServiceDetail r = new GetServiceDetail();
                r.getServiceKey().addAll(body.getServiceKey());
                r.setGeneric(VERSION);

                return r;
        }

        public static org.uddi.api_v2.GetTModelDetail MapGetTModelDetail(org.uddi.api_v3.GetTModelDetail body) {
                GetTModelDetail r = new GetTModelDetail();
                r.getTModelKey().addAll(body.getTModelKey());
                r.setGeneric(VERSION);

                return r;
        }

        public static AddPublisherAssertions MapAddPublisherAssertions(org.uddi.api_v3.AddPublisherAssertions body) {
                if (body == null) {
                        return null;
                }
                AddPublisherAssertions r = new AddPublisherAssertions();
                r.setAuthInfo(body.getAuthInfo());
                r.setGeneric(VERSION);

                r.getPublisherAssertion().addAll(MapPublisherAssertion(body.getPublisherAssertion()));
                return r;

        }

        public static DeleteBinding MapDeleteBinding(org.uddi.api_v3.DeleteBinding body) {
                DeleteBinding r = new DeleteBinding();
                r.setGeneric(VERSION);

                r.setAuthInfo(body.getAuthInfo());
                r.getBindingKey().addAll(body.getBindingKey());
                return r;

        }

        public static DeleteBusiness MapDeleteBusiness(org.uddi.api_v3.DeleteBusiness body) {
                DeleteBusiness r = new DeleteBusiness();
                r.setGeneric(VERSION);

                r.setAuthInfo(body.getAuthInfo());
                r.getBusinessKey().addAll(body.getBusinessKey());
                return r;
        }

        public static DeletePublisherAssertions MapDeletePublisherAssertions(org.uddi.api_v3.DeletePublisherAssertions body) {
                if (body == null) {
                        return null;
                }
                DeletePublisherAssertions r = new DeletePublisherAssertions();
                r.setAuthInfo(body.getAuthInfo());
                r.getPublisherAssertion().addAll(MapPublisherAssertion(body.getPublisherAssertion()));
                return r;
        }

        public static DeleteService MapDeleteService(org.uddi.api_v3.DeleteService body) {
                DeleteService r = new DeleteService();
                r.setGeneric(VERSION);

                r.setAuthInfo(body.getAuthInfo());
                r.getServiceKey().addAll(body.getServiceKey());
                return r;
        }

        public static DeleteTModel MapDeleteTModel(org.uddi.api_v3.DeleteTModel body) {
                DeleteTModel r = new DeleteTModel();
                r.setGeneric(VERSION);

                r.setAuthInfo(body.getAuthInfo());
                r.getTModelKey().addAll(body.getTModelKey());
                return r;
        }

        public static SaveBinding MapSaveBinding(org.uddi.api_v3.SaveBinding body) {
                SaveBinding r = new SaveBinding();
                r.setGeneric(VERSION);

                r.setAuthInfo(body.getAuthInfo());
                r.getBindingTemplate().addAll(MapBinding(body.getBindingTemplate()));
                return r;
        }

        public static SaveBusiness MapSaveBusiness(org.uddi.api_v3.SaveBusiness body) {
                SaveBusiness r = new SaveBusiness();
                r.setGeneric(VERSION);

                r.setAuthInfo(body.getAuthInfo());
                r.getBusinessEntity().addAll(MapBusinessList(body.getBusinessEntity(), null));
                return r;
        }

        public static SaveService MapSaveService(org.uddi.api_v3.SaveService body) {
                SaveService r = new SaveService();
                r.setGeneric(VERSION);

                r.setAuthInfo(body.getAuthInfo());
                r.getBusinessService().addAll(MapService(body.getBusinessService()));
                return r;
        }

        public static SaveTModel MapSaveTModel(org.uddi.api_v3.SaveTModel body) {
                SaveTModel r = new SaveTModel();
                r.setGeneric(VERSION);
                r.setAuthInfo(body.getAuthInfo());
                r.getTModel().addAll(MapTModelList(body.getTModel()));
                return r;
        }

        public static GetPublisherAssertions MapGetPublisherAssertions(String authInfo) {
                GetPublisherAssertions r = new GetPublisherAssertions();
                r.setAuthInfo(authInfo);
                r.setGeneric(VERSION);

                return r;
        }

        public static GetRegisteredInfo MapGetRegisteredInfo(org.uddi.api_v3.GetRegisteredInfo body) {
                GetRegisteredInfo r = new GetRegisteredInfo();
                r.setAuthInfo(body.getAuthInfo());
                r.setGeneric(VERSION);

                return r;
        }

        private static FindQualifiers MapFindQualifiers(org.uddi.api_v3.FindQualifiers findQualifiers) {
                if (findQualifiers == null || findQualifiers.getFindQualifier().isEmpty()) {
                        return null;
                }
                FindQualifiers r = new FindQualifiers();
                for (int i = 0; i < findQualifiers.getFindQualifier().size(); i++) {
                        /*if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.TRANSPORT_HTTP)) {
                                r.getFindQualifier().add("uuid:68DE9E80-AD09-469D-8A37-088422BFBC36");
                        } else if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.TRANSPORT_EMAIL)) {
                                r.getFindQualifier().add("uuid:93335D49-3EFB-48A0-ACEA-EA102B60DDC6");
                        } else if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.TRANSPORT_FTP)) {
                                r.getFindQualifier().add("uuid:5FCF5CD0-629A-4C50-8B16-F94E9CF2A674");
                        } else if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.TRANSPORT_FAX)) {
                                r.getFindQualifier().add("uuid:1A2B00BE-6E2C-42F5-875B-56F32686E0E7");
                        } else if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.TRANSPORT_POTS)) {
                                r.getFindQualifier().add("uuid:38E12427-5536-4260-A6F9-B5B530E63A07");
                        } else if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.IS_REPLACED_BY)) {
                                r.getFindQualifier().add("uuid:E59AE320-77A5-11D5-B898-0004AC49CC1E");
                        } else if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.OWNING_BUSINESS)) {
                                r.getFindQualifier().add("uuid:4064C064-6D14-4F35-8953-9652106476A9");
                        } else if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.RELATIONSHIPS)) {
                                r.getFindQualifier().add("uuid:807A2C6A-EE22-470D-ADC7-E0424A337C03");
                        } else if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase("uddi:uddi.org:categorization:nodes")) {
                                r.getFindQualifier().add("uuid:327A56F0-3299-4461-BC23-5CD513E95C55");
                        } else if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase("uddi:uddi.org:categorization:general_keywords")) {
                                r.getFindQualifier().add("uuid:A035A07C-F362-44dd-8F95-E2B134BF43B4");
                        } else if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase("uddi:uddi.org:categorization:types")) {
                                r.getFindQualifier().add("uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4");
                        }*/
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.EXACT_MATCH)
                             || findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.EXACT_MATCH_TMODEL)) {
                                r.getFindQualifier().add("exactNameMatch");
                        } else if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.CASE_SENSITIVE_MATCH)
                             || findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.CASE_SENSITIVE_MATCH_TMODEL)) {
                                r.getFindQualifier().add("caseSensitiveMatch");
                        } else if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.OR_ALL_KEYS)
                             || findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.OR_ALL_KEYS_TMODEL)) {
                                r.getFindQualifier().add("orAllKeys");
                        } else if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.OR_LIKE_KEYS)
                             || findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.OR_LIKE_KEYS_TMODEL)) {
                                r.getFindQualifier().add("orLikeKeys");
                        } else if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.AND_ALL_KEYS)
                             || findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.AND_ALL_KEYS_TMODEL)) {
                                r.getFindQualifier().add(UDDIConstants.AND_ALL_KEYS);
                        } else if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.SORT_BY_DATE_ASC)
                             || findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.SORT_BY_DATE_ASC_TMODEL)) {
                                r.getFindQualifier().add(UDDIConstants.SORT_BY_DATE_ASC);
                        } else if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.SORT_BY_DATE_DESC)
                             || findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.SORT_BY_DATE_DESC_TMODEL)) {
                                r.getFindQualifier().add(UDDIConstants.SORT_BY_DATE_DESC);
                        } else if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.SORT_BY_NAME_ASC)
                             || findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.SORT_BY_NAME_ASC_TMODEL)) {
                                r.getFindQualifier().add(UDDIConstants.SORT_BY_NAME_ASC);
                        } else if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.SORT_BY_NAME_DESC)
                             || findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.SORT_BY_NAME_DESC_TMODEL)) {
                                r.getFindQualifier().add(UDDIConstants.SORT_BY_NAME_DESC);
                        } else if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.SERVICE_SUBSET)
                             || findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.SERVICE_SUBSET_TMODEL)) {
                                r.getFindQualifier().add(UDDIConstants.SERVICE_SUBSET);
                        } else if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.COMBINE_CATEGORY_BAGS)
                             || findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.COMBINE_CATEGORY_BAGS_TMODEL)) {
                                r.getFindQualifier().add(UDDIConstants.COMBINE_CATEGORY_BAGS);
                        } else if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.APPROXIMATE_MATCH)
                             || findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.APPROXIMATE_MATCH_TMODEL)) {
                                //ignore it, not supported by UDDI v2
                        
                        } else if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.CASE_INSENSITIVE_MATCH)
                             || findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.CASE_INSENSITIVE_MATCH_TMODEL)) {
                                //ignore it, not supported by UDDI v2
                        } else {
                             //ignore it
                                //r.getFindQualifier().add(findQualifiers.getFindQualifier().get(i));
                        }
                }
                return r;
        }

        private static TModelBag MapTModelBag(org.uddi.api_v3.TModelBag tModelBag) {
                if (tModelBag == null || tModelBag.getTModelKey().isEmpty()) {
                        return null;
                }
                TModelBag r = new TModelBag();
                r.getTModelKey().addAll(tModelBag.getTModelKey());
                return r;

        }

        private static DiscoveryURLs MapDiscoveryURLs(org.uddi.api_v3.DiscoveryURLs discoveryURLs) {
                if (discoveryURLs == null) {
                        return null;
                }
                DiscoveryURLs r = new DiscoveryURLs();
                for (int i = 0; i < discoveryURLs.getDiscoveryURL().size(); i++) {
                        DiscoveryURL d = new DiscoveryURL();
                        d.setUseType(discoveryURLs.getDiscoveryURL().get(i).getUseType());
                        d.setValue(discoveryURLs.getDiscoveryURL().get(i).getValue());
                        r.getDiscoveryURL().add(d);
                }
                return r;

        }

        private static List<BusinessEntity> MapBusinessList(List<org.uddi.api_v3.BusinessEntity> businessEntity, String operator) {
                List<BusinessEntity> r = new ArrayList<BusinessEntity>();
                if (businessEntity == null) {
                        return r;
                }
                for (int i = 0; i < businessEntity.size(); i++) {
                        r.add(MapBusiness(businessEntity.get(i), operator));
                }
                return r;
        }

        private static List<TModel> MapTModelList(List<org.uddi.api_v3.TModel> tModel) {
                List<TModel> r = new ArrayList<TModel>();
                if (tModel == null) {
                        return r;
                }

                for (int i = 0; i < tModel.size(); i++) {
                        r.add(MapTModel(tModel.get(i)));
                }
                return r;
        }

        public static SetPublisherAssertions MapSetPublisherAssertions(List<org.uddi.api_v3.PublisherAssertion> value) {
                if (value == null) {
                        return null;
                }
                SetPublisherAssertions r = new SetPublisherAssertions();
                r.setGeneric(VERSION);

                r.getPublisherAssertion().addAll(MapPublisherAssertion(value));
                return r;

        }

        public static BindingDetail MapBindingDetail(org.uddi.api_v3.BindingDetail findBinding, String operator) {
                if (findBinding == null) {
                        return null;
                }
                BindingDetail r = new BindingDetail();
                r.setGeneric(VERSION);
                r.setOperator(operator);
                if (findBinding.getListDescription().getActualCount() == findBinding.getListDescription().getIncludeCount()) {
                        r.setTruncated(Truncated.FALSE);
                } else {
                        r.setTruncated(Truncated.TRUE);
                }
                r.getBindingTemplate().addAll(MapBinding(findBinding.getBindingTemplate()));
                return r;
        }

        public static DispositionReport MapException(DispositionReportFaultMessage ex, String operator) {
                if (ex == null) {
                        return null;
                }
                DispositionReport r = new DispositionReport(ex.getMessage() + (ex.detail != null ? ex.detail.getMessage() : ""), MapDispositionReport(ex.getFaultInfo(), operator));
                return r;
        }

        private static org.uddi.api_v2.DispositionReport MapDispositionReport(org.uddi.api_v3.DispositionReport faultInfo, String operator) {
                if (faultInfo == null) {
                        return null;
                }
                org.uddi.api_v2.DispositionReport r = new org.uddi.api_v2.DispositionReport();
                r.setGeneric(VERSION);
                r.setOperator(operator);
                r.setTruncated(Truncated.FALSE);
                r.getResult().addAll(MapResults(faultInfo.getResult()));
                return r;
        }

        private static List<Result> MapResults(List<org.uddi.api_v3.Result> result) {
                List<Result> r = new ArrayList<Result>();
                if (result == null) {
                        return r;
                }
                for (int i = 0; i < result.size(); i++) {
                        Result x = new Result();
                        x.setErrno(result.get(i).getErrno());
                        x.setErrno(result.get(i).getErrno());
                        if (result.get(i).getKeyType() != null) {
                                switch (result.get(i).getKeyType()) {
                                        case BINDING_KEY:
                                                x.setKeyType(KeyType.BINDING_KEY);
                                                break;
                                        case BUSINESS_KEY:
                                                x.setKeyType(KeyType.BUSINESS_KEY);
                                                break;
                                        case SERVICE_KEY:
                                                x.setKeyType(KeyType.SERVICE_KEY);
                                                break;
                                        case T_MODEL_KEY:
                                                x.setKeyType(KeyType.T_MODEL_KEY);
                                                break;
                                }
                        }
                        x.setErrInfo(new ErrInfo());
                        x.getErrInfo().setErrCode(result.get(i).getErrInfo().getErrCode());
                        x.getErrInfo().setValue(result.get(i).getErrInfo().getValue());
                        r.add(x);
                }

                return r;
        }

        public static BusinessList MapBusinessListEntity(org.uddi.api_v3.BusinessList findBusiness, String operator) {

                if (findBusiness == null) {
                        return null;
                }
                BusinessList r = new BusinessList();
                r.setGeneric(VERSION);
                r.setOperator(operator);
                r.setBusinessInfos(MapBusinessInfos(findBusiness.getBusinessInfos()));
                if (findBusiness.isTruncated() != null && findBusiness.isTruncated()) {
                        r.setTruncated(Truncated.TRUE);
                } else {
                        r.setTruncated(Truncated.FALSE);
                }
                return r;
        }

        public static RelatedBusinessesList MapRelatedBusinessList(org.uddi.api_v3.RelatedBusinessesList findRelatedBusinesses, String operator) {
                if (findRelatedBusinesses == null) {
                        return null;
                }
                RelatedBusinessesList r = new RelatedBusinessesList();
                r.setGeneric(VERSION);
                r.setOperator(operator);
                r.setBusinessKey(findRelatedBusinesses.getBusinessKey());
                if (findRelatedBusinesses.isTruncated() != null && findRelatedBusinesses.isTruncated()) {
                        r.setTruncated(Truncated.TRUE);
                } else {
                        r.setTruncated(Truncated.FALSE);
                }
                r.setRelatedBusinessInfos(new RelatedBusinessInfos());
                if (findRelatedBusinesses.getRelatedBusinessInfos() != null) {

                        for (int i = 0; i < findRelatedBusinesses.getRelatedBusinessInfos().getRelatedBusinessInfo().size(); i++) {
                                RelatedBusinessInfo x = new RelatedBusinessInfo();
                                x.setBusinessKey(findRelatedBusinesses.getRelatedBusinessInfos().getRelatedBusinessInfo().get(i).getBusinessKey());
                                x.getDescription().addAll(MapDescription(findRelatedBusinesses.getRelatedBusinessInfos().getRelatedBusinessInfo().get(i).getDescription()));
                                x.getName().addAll(MapName(findRelatedBusinesses.getRelatedBusinessInfos().getRelatedBusinessInfo().get(i).getName()));
                                r.getRelatedBusinessInfos().getRelatedBusinessInfo().add(x);
                        }
                }
                return r;
        }

        public static ServiceList MapServiceList(org.uddi.api_v3.ServiceList findService, String operator) {
                if (findService == null) {
                        return null;
                }
                ServiceList r = new ServiceList();
                r.setGeneric(VERSION);
                r.setOperator(operator);
                r.setServiceInfos(MapServiceInfos(findService.getServiceInfos()));
                if (findService.isTruncated() != null && findService.isTruncated()) {
                        r.setTruncated(Truncated.TRUE);
                } else {
                        r.setTruncated(Truncated.FALSE);
                }
                return r;
        }

        public static BusinessDetailExt MapBusinessDetailExt(BusinessDetail businessDetail, String operator) {
                if (businessDetail == null) {
                        return null;
                }
                BusinessDetailExt r = new BusinessDetailExt();

                r.setGeneric(VERSION);
                r.setOperator(operator);
                if (businessDetail.isTruncated() != null && businessDetail.isTruncated().booleanValue()) {
                        r.setTruncated(Truncated.TRUE);
                } else {
                        r.setTruncated(Truncated.FALSE);
                }
                for (int i = 0; i < businessDetail.getBusinessEntity().size(); i++) {
                        BusinessEntityExt x = new BusinessEntityExt();

                        x.setBusinessEntity(MapBusiness(businessDetail.getBusinessEntity().get(i), operator));
                        r.getBusinessEntityExt().add(x);
                }
                return r;

        }

        public static ServiceDetail MapServiceDetail(org.uddi.api_v3.ServiceDetail serviceDetail, String operator) {
                if (serviceDetail == null) {
                        return null;
                }
                ServiceDetail r = new ServiceDetail();
                if (serviceDetail.isTruncated() != null && serviceDetail.isTruncated()) {
                        r.setTruncated(Truncated.TRUE);
                } else {
                        r.setTruncated(Truncated.FALSE);
                }
                r.setGeneric(VERSION);
                r.setOperator(operator);
                r.getBusinessService().addAll(MapService(serviceDetail.getBusinessService()));
                return r;

        }

        public static TModelList MapTModelListElement(org.uddi.api_v3.TModelList findTModel, String operator) {
                TModelList r = new TModelList();
                r.setGeneric(VERSION);
                r.setOperator(operator);
                r.setTModelInfos(new TModelInfos());
                if (findTModel == null) {
                        r.setTruncated(Truncated.FALSE);
                        return r;
                }

                if (findTModel.isTruncated() != null && findTModel.isTruncated()) {
                        r.setTruncated(Truncated.TRUE);
                } else {
                        r.setTruncated(Truncated.FALSE);
                }
                if (findTModel.getTModelInfos() != null) {

                        r.getTModelInfos().getTModelInfo().addAll(MapTModelInfo(findTModel.getTModelInfos().getTModelInfo()));
                }

                return r;
        }

        public static org.uddi.api_v2.BusinessDetail MapBusinessDetail(BusinessDetail businessDetail, String operator) {
                if (businessDetail == null) {
                        return null;
                }
                org.uddi.api_v2.BusinessDetail r = new org.uddi.api_v2.BusinessDetail();
                r.setGeneric(VERSION);
                r.setOperator(operator);
                if (businessDetail.isTruncated() != null && businessDetail.isTruncated()) {
                        r.setTruncated(Truncated.TRUE);
                } else {
                        r.setTruncated(Truncated.FALSE);
                }

                r.getBusinessEntity().addAll(MapBusinessList(businessDetail.getBusinessEntity(), operator));
                return r;
        }

        public static TModelDetail MapTModelDetail(org.uddi.api_v3.TModelDetail tModelDetail, String operator) {
                if (tModelDetail == null) {
                        return null;
                }
                TModelDetail r = new TModelDetail();
                r.setGeneric(VERSION);
                r.setOperator(operator);
                if (tModelDetail.isTruncated() != null && tModelDetail.isTruncated()) {
                        r.setTruncated(Truncated.TRUE);
                } else {
                        r.setTruncated(Truncated.FALSE);
                }
                r.getTModel().addAll(MapTModelList(tModelDetail.getTModel()));

                return r;
        }

        private static BusinessInfos MapBusinessInfos(org.uddi.api_v3.BusinessInfos businessInfos) {
                BusinessInfos r = new BusinessInfos();
                if (businessInfos == null || businessInfos.getBusinessInfo().isEmpty()) {
                        return r;
                }

                for (int i = 0; i < businessInfos.getBusinessInfo().size(); i++) {
                        BusinessInfo x = new BusinessInfo();
                        x.setBusinessKey(businessInfos.getBusinessInfo().get(i).getBusinessKey());
                        x.setServiceInfos(MapServiceInfos(businessInfos.getBusinessInfo().get(i).getServiceInfos()));
                        x.getDescription().addAll(MapDescription(businessInfos.getBusinessInfo().get(i).getDescription()));
                        x.getName().addAll(MapName(businessInfos.getBusinessInfo().get(i).getName()));
                        r.getBusinessInfo().add(x);
                }

                return r;
        }

        private static ServiceInfos MapServiceInfos(org.uddi.api_v3.ServiceInfos serviceInfos) {
                ServiceInfos r = new ServiceInfos();
                if (serviceInfos == null) {
                        return r;
                }
                for (int i = 0; i < serviceInfos.getServiceInfo().size(); i++) {
                        ServiceInfo x = new ServiceInfo();
                        x.setBusinessKey(serviceInfos.getServiceInfo().get(i).getBusinessKey());
                        x.setServiceKey(serviceInfos.getServiceInfo().get(i).getServiceKey());
                        x.getName().addAll(MapName(serviceInfos.getServiceInfo().get(i).getName()));
                        r.getServiceInfo().add(x);
                }
                return r;
        }

        private static List<TModelInfo> MapTModelInfo(List<org.uddi.api_v3.TModelInfo> tModelInfo) {
                List<TModelInfo> r = new ArrayList<TModelInfo>();
                if (tModelInfo == null) {
                        return r;
                }
                for (int i = 0; i < tModelInfo.size(); i++) {
                        TModelInfo x = new TModelInfo();

                        x.setTModelKey(tModelInfo.get(i).getTModelKey());
                        x.setName(new Name(tModelInfo.get(i).getName().getValue(), tModelInfo.get(i).getName().getValue()));
                        r.add(x);
                }
                return r;
        }

        public static RegisteredInfo MapRegisteredInfo(org.uddi.api_v3.RegisteredInfo registeredInfo, String operator) {
                if (registeredInfo == null) {
                        return null;
                }
                RegisteredInfo r = new RegisteredInfo();
                r.setGeneric(VERSION);
                r.setOperator(operator);
                if (registeredInfo.isTruncated() != null && registeredInfo.isTruncated()) {
                        r.setTruncated(Truncated.TRUE);
                } else {
                        r.setTruncated(Truncated.FALSE);
                }
                if (registeredInfo.getBusinessInfos() != null) {
                        r.setBusinessInfos(new BusinessInfos());
                        for (int i = 0; i < registeredInfo.getBusinessInfos().getBusinessInfo().size(); i++) {
                                BusinessInfo x = new BusinessInfo();
                                x.setBusinessKey(registeredInfo.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey());
                                x.getName().addAll(MapName(registeredInfo.getBusinessInfos().getBusinessInfo().get(i).getName()));
                                x.getDescription().addAll(MapDescription(registeredInfo.getBusinessInfos().getBusinessInfo().get(i).getDescription()));
                                x.setServiceInfos(MapServiceInfos(registeredInfo.getBusinessInfos().getBusinessInfo().get(i).getServiceInfos()));
                        }
                }
                if (registeredInfo.getTModelInfos() != null) {
                        r.setTModelInfos(new TModelInfos());
                        r.getTModelInfos().getTModelInfo().addAll(MapTModelInfo(registeredInfo.getTModelInfos().getTModelInfo()));
                }

                return r;

        }

        public static PublisherAssertions MapPublisherAssertions(List<org.uddi.api_v3.PublisherAssertion> publisherAssertions, String operator) {
                if (publisherAssertions == null) {
                        return null;
                }
                PublisherAssertions r = new PublisherAssertions();
                r.getPublisherAssertion().addAll(MapPublisherAssertion(publisherAssertions));
                r.setGeneric(VERSION);
                r.setOperator(operator);
                return r;
        }

        private static List<PublisherAssertion> MapPublisherAssertion(List<org.uddi.api_v3.PublisherAssertion> publisherAssertion) {
                List<PublisherAssertion> r = new ArrayList<PublisherAssertion>();

                for (int i = 0; i < publisherAssertion.size(); i++) {
                        PublisherAssertion x = new PublisherAssertion();
                        x.setFromKey(publisherAssertion.get(i).getFromKey());
                        x.setToKey(publisherAssertion.get(i).getToKey());
                        if (publisherAssertion.get(i).getKeyedReference() != null) {
                                x.setKeyedReference(new KeyedReference(publisherAssertion.get(i).getKeyedReference().getTModelKey(),
                                     publisherAssertion.get(i).getKeyedReference().getKeyName(),
                                     publisherAssertion.get(i).getKeyedReference().getKeyValue()));
                        }
                        r.add(x);
                }
                return r;
        }

        
        private static boolean ContainsWildCard(List<Name> name) {
                for (int i = 0; i < name.size(); i++) {
                        if (name.get(i).getValue() != null && name.get(i).getValue().contains(UDDIConstants.WILDCARD)) {
                                return true;
                        }
                        if (name.get(i).getValue() != null && name.get(i).getValue().contains(UDDIConstants.WILDCARD_CHAR)) {
                                return true;
                        }
                        if (name.get(i).getLang() != null
                             && name.get(i).getLang().contains(UDDIConstants.WILDCARD)) {
                                return true;
                        }
                        if (name.get(i).getLang() != null
                             && name.get(i).getLang().contains(UDDIConstants.WILDCARD_CHAR)) {
                                return true;
                        }
                }
                return false;
        }

        public static GetAssertionStatusReport MapGetAssertionStatusReport(String authInfo, CompletionStatus completionStatus) {
                GetAssertionStatusReport r = new GetAssertionStatusReport();
                r.setAuthInfo(authInfo);
                switch (completionStatus) {
                        case STATUS_BOTH_INCOMPLETE:
                                r.setCompletionStatus(null);
                                break;
                        case STATUS_COMPLETE:
                                r.setCompletionStatus("status:complete");
                                break;
                        case STATUS_FROM_KEY_INCOMPLETE:
                                r.setCompletionStatus("status:fromKey_incomplete");
                                break;
                        case STATUS_TO_KEY_INCOMPLETE:
                                r.setCompletionStatus("status:toKey_incomplete");
                                break;
                }
                r.setGeneric(VERSION);
                return r;
        }

        private static Contacts MapContacts(org.uddi.api_v3.Contacts contacts) {
                if (contacts == null) {
                        return null;
                }
                Contacts c = new Contacts();
                c.getContact().addAll(MapContactList(contacts.getContact()));
                return c;
        }

        private static List<Contact> MapContactList(List<org.uddi.api_v3.Contact> contact) {

                List<Contact> r = new ArrayList<Contact>();
                if (contact == null) {
                        return r;
                }
                for (int i = 0; i < contact.size(); i++) {
                        Contact c = new Contact();
                        c.setUseType(contact.get(i).getUseType());
                        c.setPersonName(contact.get(i).getPersonName().get(0).getValue());
                        c.getAddress().addAll(MapAddress(contact.get(i).getAddress()));
                        c.getDescription().addAll(MapDescription(contact.get(i).getDescription()));
                        c.getEmail().addAll(MapEmail(contact.get(i).getEmail()));
                        c.getPhone().addAll(MapPhone(contact.get(i).getPhone()));

                        r.add(c);
                }
                return r;
        }

        private static List<Address> MapAddress(List<org.uddi.api_v3.Address> address) {
                List<Address> r = new ArrayList<Address>();
                if (address == null) {
                        return r;
                }
                for (int i = 0; i < address.size(); i++) {
                        Address x = new Address();
                        x.setSortCode(address.get(i).getSortCode());
                        x.setTModelKey(address.get(i).getTModelKey());
                        x.setUseType(address.get(i).getUseType());
                        x.getAddressLine().addAll(MapAddressLine(address.get(i).getAddressLine()));
                        r.add(x);
                }
                return r;
        }

        private static List<Email> MapEmail(List<org.uddi.api_v3.Email> email) {
                List<Email> r = new ArrayList<Email>();
                if (email == null) {
                        return r;
                }
                for (int i = 0; i < email.size(); i++) {
                        Email x = new Email();
                        x.setUseType(email.get(i).getUseType());
                        x.setValue(email.get(i).getValue());
                        r.add(x);
                }

                return r;
        }

        private static List<Phone> MapPhone(List<org.uddi.api_v3.Phone> phone) {
                List<Phone> r = new ArrayList<Phone>();
                if (phone == null) {
                        return r;
                }
                for (int i = 0; i < phone.size(); i++) {
                        Phone x = new Phone();
                        x.setUseType(phone.get(i).getUseType());
                        x.setValue(phone.get(i).getValue());
                        r.add(x);
                }

                return r;
        }

        private static List<AddressLine> MapAddressLine(List<org.uddi.api_v3.AddressLine> addressLine) {
                List<AddressLine> r = new ArrayList<AddressLine>();
                if (addressLine == null) {
                        return r;
                }
                for (int i = 0; i < addressLine.size(); i++) {
                        AddressLine x = new AddressLine();
                        x.setKeyName(addressLine.get(i).getKeyName());
                        x.setKeyValue(addressLine.get(i).getKeyValue());
                        x.setValue(addressLine.get(i).getValue());
                        r.add(x);
                }

                return r;
        }

        /**
         * limitation, keys owned is not mapped
         *
         * @param assertionStatusReport
         * @return AssertionStatusReport or null
         */
        public static AssertionStatusReport MapAssertionStatusReport(List<AssertionStatusItem> assertionStatusReport) {
                if (assertionStatusReport == null) {
                        return null;
                }
                AssertionStatusReport r = new AssertionStatusReport();
                r.setGeneric(VERSION);
                for (int i = 0; i < assertionStatusReport.size(); i++) {
                        org.uddi.api_v2.AssertionStatusItem x = new org.uddi.api_v2.AssertionStatusItem();

                        switch (assertionStatusReport.get(i).getCompletionStatus()) {
                                case STATUS_BOTH_INCOMPLETE:
                                        x.setCompletionStatus(null);
                                        break;
                                case STATUS_COMPLETE:
                                        x.setCompletionStatus("status:complete");
                                        break;
                                case STATUS_FROM_KEY_INCOMPLETE:
                                        x.setCompletionStatus("status:fromKey_incomplete");
                                        break;
                                case STATUS_TO_KEY_INCOMPLETE:
                                        x.setCompletionStatus("status:toKey_incomplete");
                                        break;
                        }
                        x.setFromKey(assertionStatusReport.get(i).getFromKey());
                        x.setToKey(assertionStatusReport.get(i).getToKey());
                        if (assertionStatusReport.get(i).getKeyedReference() != null) {
                                x.setKeyedReference(new KeyedReference(assertionStatusReport.get(i).getKeyedReference().getTModelKey(),
                                     assertionStatusReport.get(i).getKeyedReference().getKeyName(),
                                     assertionStatusReport.get(i).getKeyedReference().getKeyValue()));
                        }

                        x.setKeysOwned(new KeysOwned());
                        r.getAssertionStatusItem().add(x);
                        // assertionStatusReport.get(i).
                }

                return r;
        }

}
