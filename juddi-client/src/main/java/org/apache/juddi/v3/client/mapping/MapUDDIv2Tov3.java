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
import javax.xml.soap.SOAPFault;
import javax.xml.ws.soap.SOAPFaultException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.v3.client.UDDIConstants;
import org.uddi.api_v2.AssertionStatusReport;
import org.uddi.api_v2.GetBusinessDetailExt;
import org.uddi.api_v2.GetTModelDetail;
import org.uddi.api_v2.SetPublisherAssertions;
import org.uddi.api_v2.Truncated;
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.AddPublisherAssertions;
import org.uddi.api_v3.Address;
import org.uddi.api_v3.AssertionStatusItem;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessInfo;
import org.uddi.api_v3.BusinessInfos;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.BusinessServices;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.CompletionStatus;
import org.uddi.api_v3.Contact;
import org.uddi.api_v3.Contacts;
import org.uddi.api_v3.DeleteBinding;
import org.uddi.api_v3.DeleteBusiness;
import org.uddi.api_v3.DeletePublisherAssertions;
import org.uddi.api_v3.DeleteService;
import org.uddi.api_v3.DeleteTModel;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.Direction;
import org.uddi.api_v3.DiscoveryURL;
import org.uddi.api_v3.DiscoveryURLs;
import org.uddi.api_v3.Email;
import org.uddi.api_v3.ErrInfo;
import org.uddi.api_v3.FindBinding;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.FindRelatedBusinesses;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.GetBindingDetail;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.GetRegisteredInfo;
import org.uddi.api_v3.GetServiceDetail;
import org.uddi.api_v3.HostingRedirector;
import org.uddi.api_v3.IdentifierBag;
import org.uddi.api_v3.InfoSelection;
import org.uddi.api_v3.InstanceDetails;
import org.uddi.api_v3.KeyType;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.KeysOwned;
import org.uddi.api_v3.ListDescription;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.OverviewDoc;
import org.uddi.api_v3.OverviewURL;
import org.uddi.api_v3.PersonName;
import org.uddi.api_v3.Phone;
import org.uddi.api_v3.PublisherAssertion;
import org.uddi.api_v3.RegisteredInfo;
import org.uddi.api_v3.RelatedBusinessInfo;
import org.uddi.api_v3.RelatedBusinessInfos;
import org.uddi.api_v3.RelatedBusinessesList;
import org.uddi.api_v3.Result;
import org.uddi.api_v3.SaveBinding;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.SaveService;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.api_v3.ServiceInfo;
import org.uddi.api_v3.ServiceInfos;
import org.uddi.api_v3.ServiceList;
import org.uddi.api_v3.SharedRelationships;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelBag;
import org.uddi.api_v3.TModelDetail;
import org.uddi.api_v3.TModelInfo;
import org.uddi.api_v3.TModelInfos;
import org.uddi.api_v3.TModelInstanceDetails;
import org.uddi.api_v3.TModelInstanceInfo;
import org.uddi.api_v3.TModelList;
import org.uddi.v2_service.DispositionReport;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * Converts UDDIv2 objects to UDDI v3 objects. Note: these functions do not take
 * into account the differences in key naming conventions. The good news is that
 * UDDIv3 has backwards compatibility with v2.
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class MapUDDIv2Tov3 {

        public static org.uddi.api_v3.BusinessEntity MapBusiness(org.uddi.api_v2.BusinessEntity be) {
                if (be == null) {
                        return null;
                }
                BusinessEntity item = new org.uddi.api_v3.BusinessEntity();
                item.setBusinessKey(be.getBusinessKey());
                item.getName().addAll(MapName(be.getName()));
                item.setCategoryBag(MapCategoryBag(be.getCategoryBag()));
                item.setDiscoveryURLs(MapDiscoveryURLs(be.getDiscoveryURLs()));
                item.getDescription().addAll(MapDescription(be.getDescription()));
                item.setContacts(MapContacts(be.getContacts()));
                item.setIdentifierBag(MapIdentBag(be.getIdentifierBag()));
                if (be.getBusinessServices() != null && !be.getBusinessServices().getBusinessService().isEmpty()) {
                        item.setBusinessServices(new BusinessServices());
                        item.getBusinessServices().getBusinessService().addAll(MapService(be.getBusinessServices().getBusinessService()));
                }
                return item;
        }

        public static org.uddi.api_v3.BusinessService MapService(org.uddi.api_v2.BusinessService be) {
                if (be == null) {
                        return null;
                }
                BusinessService item = new org.uddi.api_v3.BusinessService();
                item.setBusinessKey(be.getBusinessKey());
                item.setServiceKey(be.getServiceKey());
                item.getName().addAll(MapName(be.getName()));
                if (be.getBindingTemplates() != null && !be.getBindingTemplates().getBindingTemplate().isEmpty()) {
                        item.setBindingTemplates(new BindingTemplates());
                        item.getBindingTemplates().getBindingTemplate().addAll(MapBinding(be.getBindingTemplates().getBindingTemplate()));
                }
                item.setCategoryBag(MapCategoryBag(be.getCategoryBag()));
                item.getDescription().addAll(MapDescription(be.getDescription()));
                return item;
        }

        public static List<org.uddi.api_v3.BusinessService> MapService(List<org.uddi.api_v2.BusinessService> be) {
                if (be == null) {
                        return null;
                }
                List<org.uddi.api_v3.BusinessService> item = new ArrayList<BusinessService>();
                for (int i = 0; i < be.size(); i++) {
                        item.add(MapService(be.get(i)));
                }
                return item;
        }

        public static org.uddi.api_v3.BindingTemplate MapBinding(org.uddi.api_v2.BindingTemplate be) {
                if (be == null) {
                        return null;
                }
                BindingTemplate item = new org.uddi.api_v3.BindingTemplate();
                item.setBindingKey(be.getBindingKey());
                item.setServiceKey(be.getServiceKey());

                item.setAccessPoint(MapAccessPoint(be.getAccessPoint()));
                item.setHostingRedirector(MapHostingRedir(be.getHostingRedirector()));
                item.getDescription().addAll(MapDescription(be.getDescription()));
                item.setTModelInstanceDetails(MapTModelInstanceDetails(be.getTModelInstanceDetails()));

                return item;
        }

        public static List<org.uddi.api_v3.BindingTemplate> MapBinding(List<org.uddi.api_v2.BindingTemplate> be) {
                if (be == null) {
                        return null;
                }
                List<org.uddi.api_v3.BindingTemplate> item = new ArrayList<BindingTemplate>();
                for (int i = 0; i < be.size(); i++) {
                        item.add(MapBinding(be.get(i)));
                }
                return item;
        }

        public static org.uddi.api_v3.TModel MapTModel(org.uddi.api_v2.TModel be) {
                if (be == null) {
                        return null;
                }
                TModel item = new org.uddi.api_v3.TModel();
                item.setName(new Name(be.getName().getValue(), be.getName().getLang()));
                item.setCategoryBag(MapCategoryBag(be.getCategoryBag()));
                item.getDescription().addAll(MapDescription(be.getDescription()));
                item.setIdentifierBag(MapIdentBag(be.getIdentifierBag()));
                item.getOverviewDoc().add(MapOverviewDoc(be.getOverviewDoc()));
                return item;
        }

        private static List<Name> MapName(List<org.uddi.api_v2.Name> name) {
                List<Name> items = new ArrayList<Name>();
                for (int i = 0; i < name.size(); i++) {
                        Name n = new Name(name.get(i).getValue(), name.get(i).getLang());
                        items.add(n);
                }
                return items;
        }

        private static CategoryBag MapCategoryBag(org.uddi.api_v2.CategoryBag categoryBag) {
                if (categoryBag == null) {
                        return null;
                }
                CategoryBag c = new CategoryBag();
                c.getKeyedReference().addAll(MapKeyedReference(categoryBag.getKeyedReference()));
                return c;
        }

        private static List<Description> MapDescription(List<org.uddi.api_v2.Description> description) {
                List<Description> ret = new ArrayList<Description>();
                if (description == null || description.isEmpty()) {
                        return ret;
                }
                for (int i = 0; i < description.size(); i++) {
                        ret.add(new Description(description.get(i).getValue(), description.get(i).getLang()));
                }
                return ret;

        }

        private static IdentifierBag MapIdentBag(org.uddi.api_v2.IdentifierBag identifierBag) {

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
        private static OverviewDoc MapOverviewDoc(org.uddi.api_v2.OverviewDoc overviewDoc) {
                if (overviewDoc == null) {
                        return null;
                }
                OverviewDoc r = new OverviewDoc();
                r.getDescription().addAll(MapDescription(overviewDoc.getDescription()));
                if (overviewDoc.getOverviewURL() != null) {
                        r.setOverviewURL(new OverviewURL());
                        overviewDoc.getDescription();
                        r.getOverviewURL().setValue(overviewDoc.getOverviewURL());
                }
                return r;
        }

        private static AccessPoint MapAccessPoint(org.uddi.api_v2.AccessPoint accessPoint) {
                if (accessPoint == null) {
                        return null;
                }
                return new AccessPoint(accessPoint.getValue(), MapURLType(accessPoint.getValue()));
        }

        private static HostingRedirector MapHostingRedir(org.uddi.api_v2.HostingRedirector hostingRedirector) {
                if (hostingRedirector == null) {
                        return null;
                }
                HostingRedirector r = new HostingRedirector();
                r.setBindingKey(hostingRedirector.getBindingKey());
                return r;
        }

        private static TModelInstanceDetails MapTModelInstanceDetails(org.uddi.api_v2.TModelInstanceDetails tModelInstanceDetails) {
                if (tModelInstanceDetails == null) {
                        return null;
                }
                TModelInstanceDetails r = new TModelInstanceDetails();
                r.getTModelInstanceInfo().addAll(MapTModelInstanceInfo(tModelInstanceDetails.getTModelInstanceInfo()));
                return r;
        }

        private static List<KeyedReference> MapKeyedReference(List<org.uddi.api_v2.KeyedReference> keyedReference) {
                List<KeyedReference> r = new ArrayList<KeyedReference>();
                if (keyedReference == null) {
                        return r;
                }
                for (int i = 0; i < keyedReference.size(); i++) {
                        r.add(new KeyedReference(keyedReference.get(i).getTModelKey(), keyedReference.get(i).getKeyName(), keyedReference.get(i).getKeyValue()));
                }
                return r;
        }

        private static String MapURLType(String url) {
                if (url == null) {
                        return null;
                }
                if (url.toLowerCase().endsWith("wsdl")) {
                        return AccessPointType.WSDL_DEPLOYMENT.toString();
                }
                return AccessPointType.END_POINT.toString();
        }

        private static List<TModelInstanceInfo> MapTModelInstanceInfo(List<org.uddi.api_v2.TModelInstanceInfo> tModelInstanceInfo) {
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
                                t.getInstanceDetails().getOverviewDoc().add(MapOverviewDoc(tModelInstanceInfo.get(i).getInstanceDetails().getOverviewDoc()));
                        }
                        r.add(t);
                }
                return r;
        }

        public static org.uddi.api_v3.BindingDetail MapBindingDetail(org.uddi.api_v2.BindingDetail findBinding) {
                org.uddi.api_v3.BindingDetail r = new org.uddi.api_v3.BindingDetail();
                for (int i = 0; i < findBinding.getBindingTemplate().size(); i++) {
                        r.getBindingTemplate().addAll(MapBinding(findBinding.getBindingTemplate()));
                }
                r.setTruncated(findBinding.getTruncated() == Truncated.TRUE);
                return r;
        }

        public static BusinessList MapBusinessList(org.uddi.api_v2.BusinessList findBinding) {
                org.uddi.api_v3.BusinessList r = new org.uddi.api_v3.BusinessList();
                r.setListDescription(new ListDescription());
                r.getListDescription().setActualCount(0);
                r.getListDescription().setIncludeCount(0);
                r.getListDescription().setListHead(0);
                if (findBinding.getBusinessInfos() != null) {
                        r.getListDescription().setIncludeCount(findBinding.getBusinessInfos().getBusinessInfo().size());
                        r.getListDescription().setActualCount(findBinding.getBusinessInfos().getBusinessInfo().size());
                        r.setBusinessInfos(new BusinessInfos());
                        r.getBusinessInfos().getBusinessInfo().addAll(MapBusinessInfo(findBinding.getBusinessInfos().getBusinessInfo()));

                }
                r.setTruncated(findBinding.getTruncated() == Truncated.TRUE);
                return r;
        }

        public static DispositionReportFaultMessage MapException(DispositionReport ex) {
                org.uddi.api_v3.DispositionReport r = new org.uddi.api_v3.DispositionReport();
                r.setTruncated(ex.getFaultInfo().getTruncated() == Truncated.TRUE);
                r.getResult().addAll(MapResult(ex.getFaultInfo().getResult()));

                DispositionReportFaultMessage x = new DispositionReportFaultMessage(ex.getMessage(), r);
                return x;
        }

        public static RelatedBusinessesList MapRelatedBusinessesList(org.uddi.api_v2.RelatedBusinessesList findRelatedBusinesses) {
                if (findRelatedBusinesses == null) {
                        return null;
                }
                RelatedBusinessesList r = new RelatedBusinessesList();
                r.setTruncated(findRelatedBusinesses.getTruncated() == Truncated.TRUE);
                r.setBusinessKey(findRelatedBusinesses.getBusinessKey());

                if (findRelatedBusinesses.getRelatedBusinessInfos() != null) {
                        r.setRelatedBusinessInfos(new RelatedBusinessInfos());
                        for (int i = 0; i < findRelatedBusinesses.getRelatedBusinessInfos().getRelatedBusinessInfo().size(); i++) {
                                RelatedBusinessInfo x = new RelatedBusinessInfo();
                                x.setBusinessKey(findRelatedBusinesses.getRelatedBusinessInfos().getRelatedBusinessInfo().get(i).getBusinessKey());
                                x.getDescription().addAll(MapDescription(findRelatedBusinesses.getRelatedBusinessInfos().getRelatedBusinessInfo().get(i).getDescription()));
                                x.getName().addAll(MapName(findRelatedBusinesses.getRelatedBusinessInfos().getRelatedBusinessInfo().get(i).getName()));
                                x.getSharedRelationships().addAll(MapSharedRelationship(findRelatedBusinesses.getRelatedBusinessInfos().getRelatedBusinessInfo().get(i).getSharedRelationships()));
                                r.getRelatedBusinessInfos().getRelatedBusinessInfo().add(x);
                        }
                }
                return r;
        }

        public static ServiceList MapServiceList(org.uddi.api_v2.ServiceList serviceDetail) {
                if (serviceDetail == null) {
                        return null;
                }
                ServiceList r = new ServiceList();
                r.setListDescription(new ListDescription());
                r.getListDescription().setActualCount(0);
                r.getListDescription().setIncludeCount(0);
                r.getListDescription().setListHead(0);

                if (serviceDetail.getServiceInfos() != null) {
                        r.setServiceInfos(new ServiceInfos());
                        r.getListDescription().setIncludeCount(serviceDetail.getServiceInfos().getServiceInfo().size());
                        r.getListDescription().setActualCount(serviceDetail.getServiceInfos().getServiceInfo().size());
                        for (int i = 0; i < serviceDetail.getServiceInfos().getServiceInfo().size(); i++) {
                                ServiceInfo x = new ServiceInfo();
                                x.setBusinessKey(serviceDetail.getServiceInfos().getServiceInfo().get(i).getBusinessKey());
                                x.setServiceKey(serviceDetail.getServiceInfos().getServiceInfo().get(i).getServiceKey());
                                x.getName().addAll(MapName(serviceDetail.getServiceInfos().getServiceInfo().get(i).getName()));
                                r.getServiceInfos().getServiceInfo().add(x);
                        }
                }
                r.setTruncated(serviceDetail.getTruncated() == Truncated.TRUE);
                return r;
        }

        public static BusinessDetail MapBusinessDetail(org.uddi.api_v2.BusinessDetail businessDetail) {
                if (businessDetail == null) {
                        return null;
                }
                BusinessDetail r = new BusinessDetail();
                for (int i = 0; i < businessDetail.getBusinessEntity().size(); i++) {
                        r.getBusinessEntity().add(MapBusiness(businessDetail.getBusinessEntity().get(i)));
                }
                r.setTruncated(businessDetail.getTruncated() == Truncated.TRUE);
                return r;
        }

        public static ServiceDetail MapServiceDetail(org.uddi.api_v2.ServiceDetail serviceDetail) {
                if (serviceDetail == null) {
                        return null;
                }
                ServiceDetail r = new ServiceDetail();
                for (int i = 0; i < serviceDetail.getBusinessService().size(); i++) {
                        r.getBusinessService().add(MapService(serviceDetail.getBusinessService().get(i)));
                }
                r.setTruncated(serviceDetail.getTruncated() == Truncated.TRUE);
                return r;
        }

        public static TModelDetail MapTModelDetail(org.uddi.api_v2.TModelDetail tModelDetail) {
                if (tModelDetail == null) {
                        return null;
                }
                TModelDetail r = new TModelDetail();
                for (int i = 0; i < tModelDetail.getTModel().size(); i++) {
                        r.getTModel().add(MapTModel(tModelDetail.getTModel().get(i)));
                }
                r.setTruncated(tModelDetail.getTruncated() == Truncated.TRUE);
                return r;
        }

        public static TModelList MapTModelList(org.uddi.api_v2.TModelList findTModel) {
                org.uddi.api_v3.TModelList r = new org.uddi.api_v3.TModelList();
                r.setListDescription(new ListDescription());
                r.getListDescription().setActualCount(0);
                r.getListDescription().setIncludeCount(0);
                r.getListDescription().setListHead(0);

                if (findTModel.getTModelInfos() != null) {
                        r.setTModelInfos(new TModelInfos());
                        r.getListDescription().setIncludeCount(findTModel.getTModelInfos().getTModelInfo().size());
                        r.getListDescription().setActualCount(findTModel.getTModelInfos().getTModelInfo().size());
                        for (int i = 0; i < findTModel.getTModelInfos().getTModelInfo().size(); i++) {
                                TModelInfo x = new TModelInfo();
                                x.setName(new Name(findTModel.getTModelInfos().getTModelInfo().get(i).getName().getValue(), findTModel.getTModelInfos().getTModelInfo().get(i).getName().getLang()));
                                x.setTModelKey(findTModel.getTModelInfos().getTModelInfo().get(i).getTModelKey());

                                r.getTModelInfos().getTModelInfo().add(x);
                        }

                }
                r.setTruncated(findTModel.getTruncated() == Truncated.TRUE);
                return r;
        }

        public static List<PublisherAssertion> MapListPublisherAssertion(List<org.uddi.api_v2.PublisherAssertion> publisherAssertions) {
                List<PublisherAssertion> r = new ArrayList<PublisherAssertion>();
                if (publisherAssertions == null) {
                        return r;
                }
                for (int i = 0; i < publisherAssertions.size(); i++) {
                        PublisherAssertion x = new PublisherAssertion();
                        x.setFromKey(publisherAssertions.get(i).getFromKey());
                        x.setToKey(publisherAssertions.get(i).getToKey());
                        if (publisherAssertions.get(i).getKeyedReference() != null) {
                                x.setKeyedReference(new KeyedReference(publisherAssertions.get(i).getKeyedReference().getTModelKey(),
                                        publisherAssertions.get(i).getKeyedReference().getKeyName(),
                                        publisherAssertions.get(i).getKeyedReference().getKeyValue()));
                        }
                        r.add(x);
                }
                return r;
        }

        public static List<PublisherAssertion> MapListPublisherAssertion(org.uddi.api_v2.PublisherAssertions publisherAssertions) {
                List<PublisherAssertion> r = new ArrayList<PublisherAssertion>();
                if (publisherAssertions == null) {
                        return r;
                }

                r.addAll(MapListPublisherAssertion(publisherAssertions.getPublisherAssertion()));

                return r;
        }

        public static RegisteredInfo MapListRegisteredInfo(org.uddi.api_v2.RegisteredInfo registeredInfo) {
                if (registeredInfo == null) {
                        return null;
                }
                RegisteredInfo r = new RegisteredInfo();
                if (registeredInfo.getBusinessInfos() != null) {
                        r.setBusinessInfos(new BusinessInfos());
                        r.getBusinessInfos().getBusinessInfo().addAll(MapBusinessInfo(registeredInfo.getBusinessInfos().getBusinessInfo()));
                }
                if (registeredInfo.getTModelInfos() != null) {
                        r.setTModelInfos(new TModelInfos());
                        for (int i = 0; i < registeredInfo.getTModelInfos().getTModelInfo().size(); i++) {
                                TModelInfo x = new TModelInfo();
                                x.setTModelKey(registeredInfo.getTModelInfos().getTModelInfo().get(i).getTModelKey());
                                x.setName(new Name(registeredInfo.getTModelInfos().getTModelInfo().get(i).getName().getValue(),
                                        registeredInfo.getTModelInfos().getTModelInfo().get(i).getName().getValue()));
                                r.getTModelInfos().getTModelInfo().add(x);
                        }

                }
                r.setTruncated(registeredInfo.getTruncated() == Truncated.TRUE);
                return r;
        }

        private static List< BusinessInfo> MapBusinessInfo(List<org.uddi.api_v2.BusinessInfo> businessInfo) {

                List< BusinessInfo> r = new ArrayList<BusinessInfo>();
                if (businessInfo == null) {
                        return r;
                }
                for (int i = 0; i < businessInfo.size(); i++) {
                        BusinessInfo x = new BusinessInfo();
                        x.setBusinessKey(businessInfo.get(i).getBusinessKey());
                        x.setServiceInfos(MapServiceInfo(businessInfo.get(i).getServiceInfos()));
                        x.getName().addAll(MapName(businessInfo.get(i).getName()));
                        x.getDescription().addAll(MapDescription(businessInfo.get(i).getDescription()));
                        r.add(x);
                }

                return r;
        }

        private static ServiceInfos MapServiceInfo(org.uddi.api_v2.ServiceInfos serviceInfos) {
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

        private static List<Result> MapResult(List<org.uddi.api_v2.Result> result) {
                List<Result> r = new ArrayList<Result>();
                if (result == null) {
                        return r;
                }
                for (int i = 0; i < result.size(); i++) {
                        Result x = new Result();
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
                        if (result.get(i).getErrInfo() != null) {
                                x.setErrInfo(new ErrInfo());
                                x.getErrInfo().setErrCode(result.get(i).getErrInfo().getErrCode());
                                x.getErrInfo().setValue(result.get(i).getErrInfo().getValue());
                        }
                        r.add(x);
                }
                return r;
        }

        public static DispositionReportFaultMessage MapException(SOAPFaultException ex) {
                org.uddi.api_v3.DispositionReport r = new org.uddi.api_v3.DispositionReport();
                r.setTruncated(false);
                r.getResult().addAll(MapFault(ex.getFault()));

                DispositionReportFaultMessage x = new DispositionReportFaultMessage(ex.getMessage(), r);
                return x;
        }

        private static List<Result> MapFault(SOAPFault result) {
                List<Result> r = new ArrayList<Result>();
                if (result == null) {
                        return r;
                }
                if (result.getDetail() != null) {
                        while (result.getDetail().getDetailEntries().hasNext()) {
                                Object next = result.getDetail().getDetailEntries().next();
                                if (next instanceof DispositionReport) {

                                        DispositionReport z = (DispositionReport) next;
                                        Result x = new Result();
                                        r.addAll(MapResult(z.getFaultInfo().getResult()));

                                }
                                logger.warn("unable to parse fault detail, type:" + next.getClass().getCanonicalName() + " " + next.toString());
                        }
                }
                return r;
        }
        private static Log logger = LogFactory.getLog(MapUDDIv2Tov3.class);

        public static FindBinding MapFindBinding(org.uddi.api_v2.FindBinding body) {
                if (body == null) {
                        return null;
                }
                FindBinding r = new FindBinding();
                r.setFindQualifiers(MapFindQualifiers(body.getFindQualifiers()));
                r.setMaxRows(body.getMaxRows());
                r.setTModelBag(MapTModelBag(body.getTModelBag()));
                return r;

        }

        private static TModelBag MapTModelBag(org.uddi.api_v2.TModelBag tModelBag) {
                if (tModelBag == null || tModelBag.getTModelKey().isEmpty()) {
                        return null;
                }
                TModelBag r = new TModelBag();
                r.getTModelKey().addAll(tModelBag.getTModelKey());
                return r;
        }

        private static FindQualifiers MapFindQualifiers(org.uddi.api_v2.FindQualifiers findQualifiers) {
                if (findQualifiers == null || findQualifiers.getFindQualifier().isEmpty()) {
                        return null;
                }
                FindQualifiers r = new FindQualifiers();
                for (int i = 0; i < findQualifiers.getFindQualifier().size(); i++) {
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase("uuid:68DE9E80-AD09-469D-8A37-088422BFBC36")) {
                                r.getFindQualifier().add(UDDIConstants.TRANSPORT_HTTP);
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase("uuid:93335D49-3EFB-48A0-ACEA-EA102B60DDC6")) {
                                r.getFindQualifier().add(UDDIConstants.TRANSPORT_EMAIL);
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase("uuid:5FCF5CD0-629A-4C50-8B16-F94E9CF2A674")) {
                                r.getFindQualifier().add(UDDIConstants.TRANSPORT_FTP);
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase("uuid:1A2B00BE-6E2C-42F5-875B-56F32686E0E7")) {
                                r.getFindQualifier().add(UDDIConstants.TRANSPORT_FAX);
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase("uuid:38E12427-5536-4260-A6F9-B5B530E63A07")) {
                                r.getFindQualifier().add(UDDIConstants.TRANSPORT_POTS);
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase("uuid:E59AE320-77A5-11D5-B898-0004AC49CC1E")) {
                                r.getFindQualifier().add(UDDIConstants.IS_REPLACED_BY);
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase("uuid:4064C064-6D14-4F35-8953-9652106476A9")) {
                                r.getFindQualifier().add(UDDIConstants.OWNING_BUSINESS);
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase("uuid:807A2C6A-EE22-470D-ADC7-E0424A337C03")) {
                                r.getFindQualifier().add(UDDIConstants.RELATIONSHIPS);
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase("uuid:327A56F0-3299-4461-BC23-5CD513E95C55")) {
                                r.getFindQualifier().add("uddi:uddi.org:categorization:nodes");
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase("uuid:A035A07C-F362-44dd-8F95-E2B134BF43B4")) {
                                r.getFindQualifier().add("uddi:uddi.org:categorization:general_keywords");
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase("uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4")) {
                                r.getFindQualifier().add("uddi:uddi.org:categorization:types");
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.EXACT_MATCH)) {
                                r.getFindQualifier().add("exactNameMatch");
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.CASE_SENSITIVE_MATCH)) {
                                r.getFindQualifier().add("caseSensitiveMatch");
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.OR_ALL_KEYS)) {
                                r.getFindQualifier().add("orAllKeys");
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.OR_LIKE_KEYS)) {
                                r.getFindQualifier().add("orLikeKeys");
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.AND_ALL_KEYS)) {
                                r.getFindQualifier().add("andAllKeys");
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.SORT_BY_DATE_ASC)) {
                                r.getFindQualifier().add(UDDIConstants.SORT_BY_DATE_ASC);
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.SORT_BY_DATE_DESC)) {
                                r.getFindQualifier().add(UDDIConstants.SORT_BY_DATE_DESC);
                        }

                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.SORT_BY_NAME_ASC)) {
                                r.getFindQualifier().add(UDDIConstants.SORT_BY_NAME_ASC);
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.SORT_BY_NAME_DESC)) {
                                r.getFindQualifier().add(UDDIConstants.SORT_BY_NAME_DESC);
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.SERVICE_SUBSET)) {
                                r.getFindQualifier().add(UDDIConstants.SERVICE_SUBSET);
                        }
                        if (findQualifiers.getFindQualifier().get(i).equalsIgnoreCase(UDDIConstants.COMBINE_CATEGORY_BAGS)) {
                                r.getFindQualifier().add(UDDIConstants.COMBINE_CATEGORY_BAGS);
                        }
                }
                return r;
        }

        public static FindBusiness MapFindBusiness(org.uddi.api_v2.FindBusiness body) {
                if (body == null) {
                        return null;
                }
                FindBusiness r = new FindBusiness();
                r.setCategoryBag(MapCategoryBag(body.getCategoryBag()));
                r.setDiscoveryURLs(MapDiscoveryURLs(body.getDiscoveryURLs()));
                r.setFindQualifiers(MapFindQualifiers(body.getFindQualifiers()));
                r.setTModelBag(MapTModelBag(body.getTModelBag()));
                r.setMaxRows(body.getMaxRows());
                r.getName().addAll(MapName(body.getName()));
                if (ContainsWildCard(r.getName())) {
                        r.setFindQualifiers(AddApproximateMatch(r.getFindQualifiers()));
                }
                return r;
        }

        private static org.uddi.api_v3.FindQualifiers AddApproximateMatch(org.uddi.api_v3.FindQualifiers findQualifiers) {
                if (findQualifiers == null) {
                        findQualifiers = new org.uddi.api_v3.FindQualifiers();
                }
                findQualifiers.getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                return findQualifiers;
        }

        private static boolean ContainsWildCard(List<org.uddi.api_v3.Name> name) {
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

        private static DiscoveryURLs MapDiscoveryURLs(org.uddi.api_v2.DiscoveryURLs discoveryURLs) {
                if (discoveryURLs == null) {
                        return null;
                }
                DiscoveryURLs r = new DiscoveryURLs();
                for (int i = 0; i < discoveryURLs.getDiscoveryURL().size(); i++) {
                        DiscoveryURL x = new DiscoveryURL(discoveryURLs.getDiscoveryURL().get(i).getUseType(), discoveryURLs.getDiscoveryURL().get(i).getValue());
                        r.getDiscoveryURL().add(x);
                }
                return r;
        }

        public static FindRelatedBusinesses MapFindRelatedBusiness(org.uddi.api_v2.FindRelatedBusinesses body) {
                if (body == null) {
                        return null;
                }
                FindRelatedBusinesses r = new FindRelatedBusinesses();
                r.setBusinessKey(body.getBusinessKey());
                r.setFindQualifiers(MapFindQualifiers(body.getFindQualifiers()));
                if (body.getKeyedReference() != null) {
                        r.setKeyedReference(new KeyedReference(body.getKeyedReference().getTModelKey(), body.getKeyedReference().getKeyName(), body.getKeyedReference().getKeyValue()));
                }
                r.setMaxRows(body.getMaxRows());
                return r;
        }

        public static FindService MapFindService(org.uddi.api_v2.FindService body) {
                if (body == null) {
                        return null;
                }
                FindService r = new FindService();
                r.setBusinessKey(body.getBusinessKey());
                r.setCategoryBag(MapCategoryBag(body.getCategoryBag()));
                r.setFindQualifiers(MapFindQualifiers(body.getFindQualifiers()));
                r.setMaxRows(body.getMaxRows());
                r.setTModelBag(MapTModelBag(body.getTModelBag()));
                r.getName().addAll(MapName(body.getName()));
                if (ContainsWildCard(r.getName())) {
                        r.setFindQualifiers(AddApproximateMatch(r.getFindQualifiers()));
                }
                return r;

        }

        public static FindTModel MapFindTModel(org.uddi.api_v2.FindTModel body) {
                if (body == null) {
                        return null;
                }
                FindTModel r = new FindTModel();
                r.setCategoryBag(MapCategoryBag(body.getCategoryBag()));
                r.setFindQualifiers(MapFindQualifiers(body.getFindQualifiers()));
                r.setMaxRows(body.getMaxRows());
                if (body.getName() != null) {
                        r.setName(new Name(body.getName().getValue(), body.getName().getLang()));
                }
                if (ContainsWildCardSingle(r.getName())) {
                        r.setFindQualifiers(AddApproximateMatch(r.getFindQualifiers()));
                }
                return r;
        }

        public static GetBusinessDetail MapGetBusinessDetailExt(GetBusinessDetailExt body) {
                if (body == null) {
                        return null;
                }
                GetBusinessDetail r = new GetBusinessDetail();
                r.getBusinessKey().addAll(body.getBusinessKey());
                return r;
        }

        public static GetServiceDetail MapGetServiceDetail(org.uddi.api_v2.GetServiceDetail body) {
                if (body == null) {
                        return null;
                }
                GetServiceDetail r = new GetServiceDetail();
                r.getServiceKey().addAll(body.getServiceKey());
                return r;
        }

        public static org.uddi.api_v3.GetTModelDetail MapGetTModelDetail(GetTModelDetail body) {
                if (body == null) {
                        return null;
                }
                org.uddi.api_v3.GetTModelDetail r = new org.uddi.api_v3.GetTModelDetail();
                r.getTModelKey().addAll(body.getTModelKey());
                return r;
        }

        public static GetBindingDetail MapGetBindingDetail(org.uddi.api_v2.GetBindingDetail body) {
                if (body == null) {
                        return null;
                }
                GetBindingDetail r = new GetBindingDetail();
                r.getBindingKey().addAll(body.getBindingKey());
                return r;
        }

        public static GetBusinessDetail MapGetBusinessDetail(org.uddi.api_v2.GetBusinessDetail body) {
                if (body == null) {
                        return null;
                }
                GetBusinessDetail r = new GetBusinessDetail();
                r.getBusinessKey().addAll(body.getBusinessKey());
                return r;
        }

        private static List<SharedRelationships> MapSharedRelationship(List<org.uddi.api_v2.SharedRelationships> sharedRelationships) {
                if (sharedRelationships == null) {
                        return null;
                }
                List<SharedRelationships> r = new ArrayList<SharedRelationships>();
                for (int i = 0; i < sharedRelationships.size(); i++) {
                        SharedRelationships x = new SharedRelationships();

                        switch (sharedRelationships.get(i).getDirection()) {
                                case FROM_KEY:
                                        x.setDirection(Direction.FROM_KEY);
                                        break;
                                case TO_KEY:
                                        x.setDirection(Direction.TO_KEY);
                                        break;
                        }
                        x.getKeyedReference().addAll(MapKeyedReference(sharedRelationships.get(i).getKeyedReference()));
                        r.add(x);
                }
                return r;
        }

        public static DeleteBinding MapDeleteBinding(org.uddi.api_v2.DeleteBinding body) {
                if (body == null) {
                        return null;
                }
                DeleteBinding r = new DeleteBinding();
                r.setAuthInfo(body.getAuthInfo());
                r.getBindingKey().addAll(body.getBindingKey());
                return r;
        }

        public static DeleteBusiness MapDeleteBusiness(org.uddi.api_v2.DeleteBusiness body) {
                if (body == null) {
                        return null;
                }
                DeleteBusiness r = new DeleteBusiness();
                r.setAuthInfo(body.getAuthInfo());
                r.getBusinessKey().addAll(body.getBusinessKey());
                return r;
        }

        public static DeletePublisherAssertions MapDeletePublisherAssertion(org.uddi.api_v2.DeletePublisherAssertions body) {
                if (body == null) {
                        return null;
                }
                DeletePublisherAssertions r = new DeletePublisherAssertions();
                r.setAuthInfo(body.getAuthInfo());
                //r.getPublisherAssertion().addAll(Map));
                return r;
        }

        public static DeleteService MapDeleteService(org.uddi.api_v2.DeleteService body) {
                if (body == null) {
                        return null;
                }
                DeleteService r = new DeleteService();
                r.setAuthInfo(body.getAuthInfo());
                r.getServiceKey().addAll(body.getServiceKey());
                return r;
        }

        public static DeleteTModel MapDeleteTModel(org.uddi.api_v2.DeleteTModel body) {
                if (body == null) {
                        return null;
                }
                DeleteTModel r = new DeleteTModel();
                r.setAuthInfo(body.getAuthInfo());
                r.getTModelKey().addAll(body.getTModelKey());
                return r;
        }

        public static SaveTModel MapSaveTModel(org.uddi.api_v2.SaveTModel body) {
                if (body == null) {
                        return null;
                }
                SaveTModel r = new SaveTModel();
                r.setAuthInfo(body.getAuthInfo());
                for (int i = 0; i < body.getTModel().size(); i++) {
                        r.getTModel().add(MapTModel(body.getTModel().get(i)));
                }
                return r;
        }

        public static SaveService MapSaveService(org.uddi.api_v2.SaveService body) {
                if (body == null) {
                        return null;
                }
                SaveService r = new SaveService();
                r.setAuthInfo(body.getAuthInfo());
                for (int i = 0; i < body.getBusinessService().size(); i++) {
                        r.getBusinessService().add(MapService(body.getBusinessService().get(i)));
                }
                return r;
        }

        public static SaveBusiness MapSaveBusiness(org.uddi.api_v2.SaveBusiness body) {
                if (body == null) {
                        return null;
                }
                SaveBusiness r = new SaveBusiness();
                r.setAuthInfo(body.getAuthInfo());
                for (int i = 0; i < body.getBusinessEntity().size(); i++) {
                        r.getBusinessEntity().add(MapBusiness(body.getBusinessEntity().get(i)));
                }
                return r;
        }

        public static GetRegisteredInfo MapGetRegisteredInfo(org.uddi.api_v2.GetRegisteredInfo body) {
                if (body == null) {
                        return null;
                }
                GetRegisteredInfo r = new GetRegisteredInfo();
                r.setAuthInfo(body.getAuthInfo());
                r.setInfoSelection(InfoSelection.ALL);
                return r;
        }

        public static SaveBinding MapSaveBinding(org.uddi.api_v2.SaveBinding body) {
                if (body == null) {
                        return null;
                }
                SaveBinding r = new SaveBinding();
                r.setAuthInfo(body.getAuthInfo());
                for (int i = 0; i < body.getBindingTemplate().size(); i++) {
                        r.getBindingTemplate().add(MapBinding(body.getBindingTemplate().get(i)));
                }
                return r;
        }

        public static List<PublisherAssertion> MapSetPublisherAssertions(SetPublisherAssertions body) {
                if (body == null) {
                        return null;
                }
                return MapListPublisherAssertion(body.getPublisherAssertion());
        }

        private static boolean ContainsWildCardSingle(Name name) {
                if (name != null) {
                        if (name.getValue() != null && name.getValue().contains(UDDIConstants.WILDCARD)) {
                                return true;
                        }
                        if (name.getValue() != null && name.getValue().contains(UDDIConstants.WILDCARD_CHAR)) {
                                return true;
                        }
                        if (name.getLang() != null && name.getLang().contains(UDDIConstants.WILDCARD)) {
                                return true;
                        }
                        if (name.getLang() != null && name.getLang().contains(UDDIConstants.WILDCARD_CHAR)) {
                                return true;
                        }
                }
                return false;
        }

        public static List<AssertionStatusItem> MapAssertionStatusItems(AssertionStatusReport assertionStatusReport) {
                List<AssertionStatusItem> r = new ArrayList<AssertionStatusItem>();
                if (assertionStatusReport == null) {
                        return r;
                }
                for (int i = 0; i < assertionStatusReport.getAssertionStatusItem().size(); i++) {
                        AssertionStatusItem x = new AssertionStatusItem();
                        x.setFromKey(assertionStatusReport.getAssertionStatusItem().get(i).getFromKey());
                        x.setToKey(assertionStatusReport.getAssertionStatusItem().get(i).getToKey());
                        x.setCompletionStatus(MapCompletionStatus(assertionStatusReport.getAssertionStatusItem().get(i).getCompletionStatus()));

                        x.setKeysOwned(MapKeysOwned(assertionStatusReport.getAssertionStatusItem().get(i).getKeysOwned()));
                        if (assertionStatusReport.getAssertionStatusItem().get(i).getKeyedReference() != null) {
                                x.setKeyedReference(new KeyedReference(assertionStatusReport.getAssertionStatusItem().get(i).getKeyedReference().getTModelKey(),
                                        assertionStatusReport.getAssertionStatusItem().get(i).getKeyedReference().getKeyName(),
                                        assertionStatusReport.getAssertionStatusItem().get(i).getKeyedReference().getKeyValue()));
                        }
                }
                return r;
        }

        private static KeysOwned MapKeysOwned(org.uddi.api_v2.KeysOwned keysOwned) {
                if (keysOwned == null) {
                        return null;
                }
                KeysOwned r = new KeysOwned();
                r.setFromKey(keysOwned.getFromKey());
                r.setToKey(keysOwned.getToKey());
                return r;
        }

        private static Contacts MapContacts(org.uddi.api_v2.Contacts contacts) {
                if (contacts == null) {
                        return null;
                }
                Contacts c = new Contacts();
                c.getContact().addAll(MapContactList(contacts.getContact()));
                return c;

        }

        private static List<Contact> MapContactList(List<org.uddi.api_v2.Contact> contact) {
                List<Contact> r = new ArrayList<Contact>();
                if (contact == null) {
                        return r;
                }
                for (int i = 0; i < contact.size(); i++) {
                        Contact c = new Contact();
                        c.setUseType(contact.get(i).getUseType());
                        if (contact.get(i).getPersonName() != null) {
                                c.getPersonName().add(new PersonName(contact.get(i).getPersonName(), null));
                        }
                        c.getAddress().addAll(MapAddress(contact.get(i).getAddress()));
                        c.getDescription().addAll(MapDescription(contact.get(i).getDescription()));
                        c.getEmail().addAll(MapEmail(contact.get(i).getEmail()));
                        c.getPhone().addAll(MapPhone(contact.get(i).getPhone()));

                        r.add(c);
                }
                return r;
        }

        private static Collection<? extends Address> MapAddress(List<org.uddi.api_v2.Address> address) {
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

        private static Collection<? extends Email> MapEmail(List<org.uddi.api_v2.Email> email) {
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

        private static Collection<? extends Phone> MapPhone(List<org.uddi.api_v2.Phone> phone) {
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

        private static List<org.uddi.api_v3.AddressLine> MapAddressLine(List<org.uddi.api_v2.AddressLine> addressLine) {
                List<org.uddi.api_v3.AddressLine> r = new ArrayList<org.uddi.api_v3.AddressLine>();
                if (addressLine == null) {
                        return r;
                }
                for (int i = 0; i < addressLine.size(); i++) {
                        org.uddi.api_v3.AddressLine x = new org.uddi.api_v3.AddressLine();
                        x.setKeyName(addressLine.get(i).getKeyName());
                        x.setKeyValue(addressLine.get(i).getKeyValue());
                        x.setValue(addressLine.get(i).getValue());
                        r.add(x);
                }

                return r;
        }

        public static AddPublisherAssertions MapAddPublisherAssertions(org.uddi.api_v2.AddPublisherAssertions body) {

                if (body == null) {
                        return null;
                }
                AddPublisherAssertions r = new AddPublisherAssertions();
                r.setAuthInfo(body.getAuthInfo());
                r.getPublisherAssertion().addAll(MapListPublisherAssertion(body.getPublisherAssertion()));
                return r;

        }

        public static CompletionStatus MapCompletionStatus(String completionStatus) {

                if ("status:complete".equalsIgnoreCase(completionStatus)) {
                        return (CompletionStatus.STATUS_COMPLETE);
                } else if ("status:toKey_incomplete".equalsIgnoreCase(completionStatus)) {
                        return (CompletionStatus.STATUS_TO_KEY_INCOMPLETE);
                } else if ("status:fromKey_incomplete".equalsIgnoreCase(completionStatus)) {
                        return (CompletionStatus.STATUS_FROM_KEY_INCOMPLETE);
                } else {
                        return (CompletionStatus.STATUS_BOTH_INCOMPLETE);
                }

        }

}
