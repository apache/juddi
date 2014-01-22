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
import java.util.Collection;
import java.util.List;
import javax.xml.ws.Holder;
import org.apache.juddi.v3.client.UDDIConstants;
import org.uddi.api_v2.AccessPoint;
import org.uddi.api_v2.AddPublisherAssertions;
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
import org.uddi.api_v2.DeleteBinding;
import org.uddi.api_v2.DeleteBusiness;
import org.uddi.api_v2.DeletePublisherAssertions;
import org.uddi.api_v2.DeleteService;
import org.uddi.api_v2.DeleteTModel;
import org.uddi.api_v2.Description;
import org.uddi.api_v2.DiscoveryURL;
import org.uddi.api_v2.DiscoveryURLs;
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
import org.uddi.api_v2.GetBindingDetail;
import org.uddi.api_v2.GetBusinessDetail;
import org.uddi.api_v2.GetServiceDetail;
import org.uddi.api_v2.GetTModelDetail;
import org.uddi.api_v2.KeyType;
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
import org.uddi.api_v3.BusinessDetail;
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

        public static org.uddi.api_v2.BusinessEntity MapBusiness(org.uddi.api_v3.BusinessEntity be) {
                if (be == null) {
                        return null;
                }
                BusinessEntity item = new org.uddi.api_v2.BusinessEntity();
                item.setBusinessKey(be.getBusinessKey());
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
                item.setBindingKey(be.getBindingKey());
                item.setServiceKey(be.getServiceKey());
                item.setAccessPoint(MapAccessPoint(be.getAccessPoint()));
                item.setHostingRedirector(MapHostingRedir(be.getHostingRedirector()));
                item.getDescription().addAll(MapDescription(be.getDescription()));
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
         * @return
         */
        private static OverviewDoc MapOverviewDoc(List<org.uddi.api_v3.OverviewDoc> overviewDoc) {
                if (overviewDoc == null || overviewDoc.isEmpty()) {
                        return null;
                }
                OverviewDoc r = new OverviewDoc();
                r.getDescription().addAll(MapDescription(overviewDoc.get(0).getDescription()));
                if (overviewDoc.get(0).getOverviewURL() != null) {
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
                        return null;
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
                                t.getInstanceDetails().setInstanceParms(tModelInstanceInfo.get(i).getInstanceDetails().getInstanceParms());
                                t.getInstanceDetails().setOverviewDoc(MapOverviewDoc(tModelInstanceInfo.get(i).getInstanceDetails().getOverviewDoc()));
                        }
                        r.add(t);
                }
                return r;
        }

        public static FindBinding MapFindBinding(org.uddi.api_v3.FindBinding body) {
                FindBinding r = new FindBinding();
                r.setFindQualifiers(MapFindQualifiers(body.getFindQualifiers()));
                r.setMaxRows(body.getMaxRows());
                r.setTModelBag(MapTModelBag(body.getTModelBag()));
                r.setServiceKey(body.getServiceKey());
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
                if (body==null)return null;
                DeletePublisherAssertions r = new  DeletePublisherAssertions();
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
                r.getBusinessEntity().addAll(MapBusinessList(body.getBusinessEntity()));
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
                if (findQualifiers == null) {
                        return null;
                }
                FindQualifiers r = new FindQualifiers();
                for (int i = 0; i < findQualifiers.getFindQualifier().size(); i++) {
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.TRANSPORT_HTTP)) {
                                r.getFindQualifier().add("uuid:68DE9E80-AD09-469D-8A37-088422BFBC36");
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.TRANSPORT_EMAIL)) {
                                r.getFindQualifier().add("uuid:93335D49-3EFB-48A0-ACEA-EA102B60DDC6");
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.TRANSPORT_FTP)) {
                                r.getFindQualifier().add("uuid:5FCF5CD0-629A-4C50-8B16-F94E9CF2A674");
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.TRANSPORT_FAX)) {
                                r.getFindQualifier().add("uuid:1A2B00BE-6E2C-42F5-875B-56F32686E0E7");
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.TRANSPORT_POTS)) {
                                r.getFindQualifier().add("uuid:38E12427-5536-4260-A6F9-B5B530E63A07");
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.IS_REPLACED_BY)) {
                                r.getFindQualifier().add("uuid:E59AE320-77A5-11D5-B898-0004AC49CC1E");
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.OWNING_BUSINESS)) {
                                r.getFindQualifier().add("uuid:4064C064-6D14-4F35-8953-9652106476A9");
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.RELATIONSHIPS)) {
                                r.getFindQualifier().add("uuid:807A2C6A-EE22-470D-ADC7-E0424A337C03");
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase("uddi:uddi.org:categorization:nodes")) {
                                r.getFindQualifier().add("uuid:327A56F0-3299-4461-BC23-5CD513E95C55");
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase("uddi:uddi.org:categorization:general_keywords")) {
                                r.getFindQualifier().add("uuid:A035A07C-F362-44dd-8F95-E2B134BF43B4");
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase("uddi:uddi.org:categorization:types")) {
                                r.getFindQualifier().add("uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4");
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.EXACT_MATCH)
                                || findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.EXACT_MATCH_TMODEL)) {
                                r.getFindQualifier().add("exactNameMatch");
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.CASE_SENSITIVE_MATCH)
                                || findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.CASE_SENSITIVE_MATCH_TMODEL)) {
                                r.getFindQualifier().add("caseSensitiveMatch");
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.OR_ALL_KEYS)
                                || findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.OR_ALL_KEYS_TMODEL)) {
                                r.getFindQualifier().add("orAllKeys");
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.OR_LIKE_KEYS)
                                || findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.OR_LIKE_KEYS_TMODEL)) {
                                r.getFindQualifier().add("orLikeKeys");
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.AND_ALL_KEYS)
                                || findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.AND_ALL_KEYS_TMODEL)) {
                                r.getFindQualifier().add("andAllKeys");
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.SORT_BY_DATE_ASC)
                                || findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.SORT_BY_DATE_ASC_TMODEL)) {
                                r.getFindQualifier().add(UDDIConstants.SORT_BY_DATE_ASC);
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.SORT_BY_DATE_DESC)
                                || findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.SORT_BY_DATE_DESC_TMODEL)) {
                                r.getFindQualifier().add(UDDIConstants.SORT_BY_DATE_DESC);
                        }

                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.SORT_BY_NAME_ASC)
                                || findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.SORT_BY_NAME_ASC_TMODEL)) {
                                r.getFindQualifier().add(UDDIConstants.SORT_BY_NAME_ASC);
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.SORT_BY_NAME_DESC)
                                || findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.SORT_BY_NAME_DESC_TMODEL)) {
                                r.getFindQualifier().add(UDDIConstants.SORT_BY_NAME_DESC);
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.SERVICE_SUBSET)
                                || findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.SERVICE_SUBSET_TMODEL)) {
                                r.getFindQualifier().add(UDDIConstants.SERVICE_SUBSET);
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.COMBINE_CATEGORY_BAGS)
                                || findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.COMBINE_CATEGORY_BAGS_TMODEL)) {
                                r.getFindQualifier().add(UDDIConstants.COMBINE_CATEGORY_BAGS);
                        }
                }
                return r;
        }

        private static TModelBag MapTModelBag(org.uddi.api_v3.TModelBag tModelBag) {

                TModelBag r = new TModelBag();
                if (tModelBag != null) {
                        r.getTModelKey().addAll(tModelBag.getTModelKey());
                }
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

        private static List<BusinessEntity> MapBusinessList(List<org.uddi.api_v3.BusinessEntity> businessEntity) {
                List<BusinessEntity> r = new ArrayList<BusinessEntity>();
                if (businessEntity == null) {
                        return r;
                }
                for (int i = 0; i < businessEntity.size(); i++) {
                        r.add(MapBusiness(businessEntity.get(i)));
                }
                return r;
        }

        private static List<TModel> MapTModelList(List<org.uddi.api_v3.TModel> tModel) {
                if (tModel == null) {
                        return null;
                }
                List<TModel> r = new ArrayList<TModel>();
                for (int i = 0; i < tModel.size(); i++) {
                        r.add(MapTModel(tModel.get(i)));
                }
                return r;
        }

        public static SetPublisherAssertions MapSetPublisherAssertions(List<org.uddi.api_v3.PublisherAssertion> value) {
                if (value==null)return null;
                SetPublisherAssertions r = new SetPublisherAssertions();
                r.setGeneric(VERSION);
                r.getPublisherAssertion().addAll(MapPublisherAssertion(value));
                return r;
                
        }

        public static BindingDetail MapBindingDetail(org.uddi.api_v3.BindingDetail findBinding) {
                if (findBinding == null) {
                        return null;
                }
                BindingDetail r = new BindingDetail();
                r.setGeneric(VERSION);
                if (findBinding.getListDescription().getActualCount() == findBinding.getListDescription().getIncludeCount()) {
                        r.setTruncated(Truncated.FALSE);
                } else {
                        r.setTruncated(Truncated.TRUE);
                }
                r.getBindingTemplate().addAll(MapBinding(findBinding.getBindingTemplate()));
                return r;
        }

        public static DispositionReport MapException(DispositionReportFaultMessage ex) {
                if (ex == null) {
                        return null;
                }
                DispositionReport r = new DispositionReport("err", MapDispositionReport(ex.getFaultInfo()));
                return r;
        }

        private static org.uddi.api_v2.DispositionReport MapDispositionReport(org.uddi.api_v3.DispositionReport faultInfo) {
                if (faultInfo == null) {
                        return null;
                }
                org.uddi.api_v2.DispositionReport r = new org.uddi.api_v2.DispositionReport();
                r.setGeneric(VERSION);
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
                }

                return r;
        }

        public static BusinessList MapBusinessListEntity(org.uddi.api_v3.BusinessList findBusiness) {

                if (findBusiness == null) {
                        return null;
                }
                BusinessList r = new BusinessList();
                r.setGeneric(VERSION);
                r.setBusinessInfos(MapBusinessInfos(findBusiness.getBusinessInfos()));
                if (findBusiness.isTruncated()) {
                        r.setTruncated(Truncated.TRUE);
                } else {
                        r.setTruncated(Truncated.FALSE);
                }
                return r;
        }

        public static RelatedBusinessesList MapRelatedBusinessList(org.uddi.api_v3.RelatedBusinessesList findRelatedBusinesses) {
                if (findRelatedBusinesses == null) {
                        return null;
                }
                RelatedBusinessesList r = new RelatedBusinessesList();
                r.setBusinessKey(findRelatedBusinesses.getBusinessKey());
                if (findRelatedBusinesses.isTruncated()) {
                        r.setTruncated(Truncated.TRUE);
                } else {
                        r.setTruncated(Truncated.FALSE);
                }
                if (findRelatedBusinesses.getRelatedBusinessInfos() != null) {
                        r.setRelatedBusinessInfos(new RelatedBusinessInfos());
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

        public static ServiceList MapServiceList(org.uddi.api_v3.ServiceList findService) {
                if (findService == null) {
                        return null;
                }
                ServiceList r = new ServiceList();
                r.setGeneric(VERSION);
                r.setServiceInfos(MapServiceInfos(findService.getServiceInfos()));
                if (findService.isTruncated()) {
                        r.setTruncated(Truncated.TRUE);
                } else {
                        r.setTruncated(Truncated.FALSE);
                }
                return r;
        }

        public static BusinessDetailExt MapBusinessDetailExt(BusinessDetail businessDetail) {
                if (businessDetail == null) {
                        return null;
                }
                BusinessDetailExt r = new BusinessDetailExt();

                for (int i = 0; i < businessDetail.getBusinessEntity().size(); i++) {
                        BusinessEntityExt x = new BusinessEntityExt();
                        x.setBusinessEntity(MapBusiness(businessDetail.getBusinessEntity().get(i)));
                        r.getBusinessEntityExt().add(x);
                }
                return r;

        }

        public static ServiceDetail MapServiceDetail(org.uddi.api_v3.ServiceDetail serviceDetail) {
                if (serviceDetail == null) {
                        return null;
                }
                ServiceDetail r = new ServiceDetail();
                if (serviceDetail.isTruncated()) {
                        r.setTruncated(Truncated.TRUE);
                } else {
                        r.setTruncated(Truncated.FALSE);
                }
                r.setGeneric(VERSION);
                r.getBusinessService().addAll(MapService(serviceDetail.getBusinessService()));
                return r;

        }

        public static TModelList MapTModelListElement(org.uddi.api_v3.TModelList findTModel) {
                if (findTModel == null) {
                        return null;
                }
                TModelList r = new TModelList();
                if (findTModel.isTruncated()) {
                        r.setTruncated(Truncated.TRUE);
                } else {
                        r.setTruncated(Truncated.FALSE);
                }
                if (findTModel.getTModelInfos() != null) {
                        r.setTModelInfos(new TModelInfos());
                        r.getTModelInfos().getTModelInfo().addAll(MapTModelInfo(findTModel.getTModelInfos().getTModelInfo()));
                }
                r.setGeneric(VERSION);
                return r;
        }

        public static org.uddi.api_v2.BusinessDetail MapBusinessDetail(BusinessDetail businessDetail) {
                if (businessDetail == null) {
                        return null;
                }
                org.uddi.api_v2.BusinessDetail r = new org.uddi.api_v2.BusinessDetail();
                r.setGeneric(VERSION);
                if (businessDetail.isTruncated()) {
                        r.setTruncated(Truncated.TRUE);
                } else {
                        r.setTruncated(Truncated.FALSE);
                }

                r.getBusinessEntity().addAll(MapBusinessList(businessDetail.getBusinessEntity()));
                return r;
        }

        public static TModelDetail MapTModelDetail(org.uddi.api_v3.TModelDetail tModelDetail) {
                if (tModelDetail == null) {
                        return null;
                }
                TModelDetail r = new TModelDetail();
                r.setGeneric(VERSION);
                if (tModelDetail.isTruncated()) {
                        r.setTruncated(Truncated.TRUE);
                } else {
                        r.setTruncated(Truncated.FALSE);
                }
                r.getTModel().addAll(MapTModelList(tModelDetail.getTModel()));

                return r;
        }

        private static BusinessInfos MapBusinessInfos(org.uddi.api_v3.BusinessInfos businessInfos) {
                if (businessInfos == null) {
                        return null;
                }
                BusinessInfos r = new BusinessInfos();
                for (int i = 0; i < businessInfos.getBusinessInfo().size(); i++) {
                        BusinessInfo x = new BusinessInfo();
                        x.setBusinessKey(businessInfos.getBusinessInfo().get(i).getBusinessKey());
                        x.setServiceInfos(MapServiceInfos(businessInfos.getBusinessInfo().get(i).getServiceInfos()));
                        x.getDescription().addAll(MapDescription(businessInfos.getBusinessInfo().get(i).getDescription()));
                        r.getBusinessInfo().add(x);
                }

                return r;
        }

        private static ServiceInfos MapServiceInfos(org.uddi.api_v3.ServiceInfos serviceInfos) {
                if (serviceInfos == null) {
                        return null;
                }
                ServiceInfos r = new ServiceInfos();
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

        public static RegisteredInfo MapRegisteredInfo(org.uddi.api_v3.RegisteredInfo registeredInfo) {
                if (registeredInfo == null) {
                        return null;
                }
                RegisteredInfo r = new RegisteredInfo();
                r.setGeneric(VERSION);
                if (registeredInfo.isTruncated()) {
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
                if (registeredInfo.getTModelInfos()!=null){
                        r.setTModelInfos(new TModelInfos());
                        r.getTModelInfos().getTModelInfo().addAll(MapTModelInfo(registeredInfo.getTModelInfos().getTModelInfo()));
                }
                
                return r;

        }

        public static PublisherAssertions MapPublisherAssertions(List<org.uddi.api_v3.PublisherAssertion> publisherAssertions) {
                if (publisherAssertions == null) {
                        return null;
                }
                PublisherAssertions r = new PublisherAssertions();
                r.getPublisherAssertion().addAll(MapPublisherAssertion(publisherAssertions));
                r.setGeneric(VERSION);
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

}
