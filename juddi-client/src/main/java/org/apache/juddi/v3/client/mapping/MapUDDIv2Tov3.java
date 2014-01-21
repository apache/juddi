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
import org.apache.juddi.v3.client.transport.JAXWSv2TranslationTransport;
import org.uddi.api_v2.Truncated;
import org.uddi.api_v3.AccessPoint;
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
import org.uddi.api_v3.Description;
import org.uddi.api_v3.ErrInfo;
import org.uddi.api_v3.HostingRedirector;
import org.uddi.api_v3.IdentifierBag;
import org.uddi.api_v3.InstanceDetails;
import org.uddi.api_v3.KeyType;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.ListDescription;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.OverviewDoc;
import org.uddi.api_v3.OverviewURL;
import org.uddi.api_v3.PublisherAssertion;
import org.uddi.api_v3.RegisteredInfo;
import org.uddi.api_v3.RelatedBusinessesList;
import org.uddi.api_v3.Result;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.api_v3.ServiceInfo;
import org.uddi.api_v3.ServiceInfos;
import org.uddi.api_v3.ServiceList;
import org.uddi.api_v3.TModel;
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
                        Name n = new Name(name.get(i).getValue(), name.get(i).getValue());
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
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

        public static List<PublisherAssertion> MapListPublisherAssertion(org.uddi.api_v2.PublisherAssertions publisherAssertions) {
                List<PublisherAssertion> r = new ArrayList<PublisherAssertion>();
                if (publisherAssertions == null) {
                        return r;
                }
                for (int i = 0; i < publisherAssertions.getPublisherAssertion().size(); i++) {
                        PublisherAssertion x = new PublisherAssertion();
                        x.setFromKey(publisherAssertions.getPublisherAssertion().get(i).getFromKey());
                        x.setToKey(publisherAssertions.getPublisherAssertion().get(i).getToKey());
                        if (publisherAssertions.getPublisherAssertion().get(i).getKeyedReference() != null) {
                                x.setKeyedReference(new KeyedReference(publisherAssertions.getPublisherAssertion().get(i).getKeyedReference().getTModelKey(),
                                        publisherAssertions.getPublisherAssertion().get(i).getKeyedReference().getKeyName(),
                                        publisherAssertions.getPublisherAssertion().get(i).getKeyedReference().getKeyValue()));
                        }
                        r.add(x);
                }
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
                while (result.getDetail().getDetailEntries().hasNext()) {
                        Object next = result.getDetail().getDetailEntries().next();
                        if (next instanceof DispositionReport) {

                                DispositionReport z = (DispositionReport)next;
                                Result x = new Result();
                                r.addAll(MapResult(z.getFaultInfo().getResult()));
                                
                        }
                        logger.warn("unable to parse fault detail, type:" + next.getClass().getCanonicalName()+" " + next.toString());
                }
                return r;
        }
        private static Log logger = LogFactory.getLog(MapUDDIv2Tov3.class);
}
