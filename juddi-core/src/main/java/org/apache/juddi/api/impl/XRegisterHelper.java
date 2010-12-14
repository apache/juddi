/*
 * Copyright 2001-2009 The Apache Software Foundation.
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
package org.apache.juddi.api.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api_v3.Clerk;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.XRegistration;
import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessInfo;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.api_v3.ServiceInfo;
import org.uddi.sub_v3.SubscriptionResultsList;

/**
 * Used to factor out inquiry functionality as it is used in more than one spot.
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class XRegisterHelper {

	private static Log log = LogFactory.getLog(XRegisterHelper.class);

	public static void handle(Clerk fromClerk, Clerk toClerk, SubscriptionResultsList list) {

		UDDIClerk uddiToClerk = new UDDIClerk(toClerk);
		UDDIClerk uddiFromClerk = new UDDIClerk(fromClerk);
		//SERVICE LIST
		if (list.getServiceList()!=null) {
			log.info("Subscription result for ServiceList with subscription key=" + list.getSubscription().getSubscriptionKey());
			for (ServiceInfo serviceInfo : list.getServiceList().getServiceInfos().getServiceInfo() ) {
				
				BusinessEntity existingBusinessEntity = null;
				try {
					if (existingBusinessEntity==null) {
						existingBusinessEntity = uddiToClerk.findBusiness(serviceInfo.getBusinessKey(), toClerk.getNode());
					}
					if (existingBusinessEntity!=null) {
						log.debug("Found business with key " +  existingBusinessEntity.getBusinessKey() + ". No need to add it again");
					} else {
						log.info("Business was not found in the destination UDDI " + toClerk.getNode().getName() 
								+ ", going to add it in.");
						new XRegistration(serviceInfo.getBusinessKey(), new UDDIClerk(fromClerk), new UDDIClerk(toClerk)).xRegisterBusiness();
					}
					new XRegistration(serviceInfo.getServiceKey(), new UDDIClerk(fromClerk), new UDDIClerk(toClerk)).xRegisterServiceAndBindings();
				} catch (Exception e) {
					log.error(e.getMessage(),e);	
				}
			}
		}
		//SERVICE DETAIL
		if (list.getServiceDetail()!=null) {
			log.info("Subscription result for ServiceDetail with subscription key=" + list.getSubscription().getSubscriptionKey());
			ServiceDetail serviceDetail = list.getServiceDetail();
			if (serviceDetail.isTruncated()) {
				log.info("The serviceDetail is truncated, the maxEntries must have been hit. The number of services is " + serviceDetail.getBusinessService().size());
			}
			for (BusinessService service : serviceDetail.getBusinessService()) {
				BusinessEntity existingBusinessEntity = null;
				try {
					if (existingBusinessEntity==null) {
						existingBusinessEntity = uddiToClerk.findBusiness(service.getBusinessKey(), toClerk.getNode());
					}
					if (existingBusinessEntity!=null) {
						log.debug("Found business with key " +  existingBusinessEntity.getBusinessKey() + ". No need to add it again");
					} else {
						log.info("Business was not found in the destination UDDI " + toClerk.getNode().getName() 
								+ ", going to add it in.");
						new XRegistration(service.getBusinessKey(), new UDDIClerk(fromClerk), new UDDIClerk(toClerk)).xRegisterBusiness();
					}
					new XRegistration(service.getServiceKey(), new UDDIClerk(fromClerk), new UDDIClerk(toClerk)).xRegisterServiceAndBindings();
				} catch (Exception e) {
					log.error(e.getMessage(),e);	
				}
			}
		}
		
		//BUSINESS LIST
		if (list.getBusinessList()!=null) {
			log.info("Subscription result for BusinessList with subscription key=" + list.getSubscription().getSubscriptionKey());
			for (BusinessInfo businessInfo : list.getBusinessList().getBusinessInfos().getBusinessInfo()) {
				new XRegistration(businessInfo.getBusinessKey(), new UDDIClerk(fromClerk), new UDDIClerk(toClerk)).xRegisterBusinessAndServices();
			}
		}
		
		//BUSINESS DETAIL
		if (list.getBusinessDetail()!=null) {
			log.info("Subscription result for BusinessDetail with subscription key=" + list.getSubscription().getSubscriptionKey());
			BusinessDetail businessDetail = list.getBusinessDetail();
			if (businessDetail.isTruncated()) {
				log.info("The businessDetail is truncated, the maxEntries must have been hit. The number of businesses is " + businessDetail.getBusinessEntity().size());
			}
			for (BusinessEntity businessEntity : businessDetail.getBusinessEntity()) {
				new XRegistration(businessEntity.getBusinessKey(), new UDDIClerk(fromClerk), new UDDIClerk(toClerk)).xRegisterBusinessAndServices();
			}
		}
		
		//KEY BAG, NOT IMPLEMENTED
		if (list.getKeyBag()!=null) {
			log.info("Returning results when a 'brief' format is selected, please do not use 'brief' results when using the XRegistration functionality");
		}
		
		//BINDING DETAIL
		if (list.getBindingDetail()!=null) {
			log.info("Subscription result for BindingDetail with subscription key=" + list.getSubscription().getSubscriptionKey());
			BindingDetail bindingDetail = list.getBindingDetail();
			if (bindingDetail.isTruncated()) {
				log.info("The bindingDetail is truncated, the maxEntries must have been hit. The number of bindings is " + bindingDetail.getBindingTemplate().size());
			}
			for (BindingTemplate bindingTemplate : bindingDetail.getBindingTemplate()) {
				try {
					//check if the service exist
					BusinessService existingToService = uddiToClerk.findService(bindingTemplate.getServiceKey(), toClerk.getNode());
					if (existingToService!=null) {
						log.debug("Found service with key " +  existingToService.getServiceKey() + ". No need to add it again");
					} else {
						BusinessService fromService = uddiFromClerk.findService(bindingTemplate.getServiceKey(), fromClerk.getNode());
						fromService.getBusinessKey();
						//check if the business exist
						BusinessEntity existingBusinessEntity = uddiToClerk.findBusiness(fromService.getBusinessKey(), toClerk.getNode());
						if (existingBusinessEntity!=null) {
							log.debug("Found business with key " +  existingBusinessEntity.getBusinessKey() + ". No need to add it again");
						} else {
							log.info("Business was not found in the destination UDDI " + toClerk.getNode().getName() 
									+ ", going to add it in.");
							new XRegistration(fromService.getBusinessKey(), new UDDIClerk(fromClerk), new UDDIClerk(toClerk)).xRegisterBusiness();
						}
						log.info("Service was not found in the destination UDDI " + toClerk.getNode().getName() 
								+ ", going to add it in.");
						new XRegistration(fromService.getServiceKey(), new UDDIClerk(fromClerk), new UDDIClerk(toClerk)).xRegisterService();
					} 
					//now the service exists in the toNode and we can add this binding
					new XRegistration(bindingTemplate.getBindingKey(), new UDDIClerk(fromClerk), new UDDIClerk(toClerk)).xRegisterServiceBinding();
				} catch (Exception e) {
					log.error(e.getMessage(),e);	
				}
			}
		}
		
		//RELATED BUSINESSES
		if (list.getRelatedBusinessesList()!=null) {
			log.info("Subscription result for RelatedBusinesses with subscription key=" + list.getSubscription().getSubscriptionKey());
			log.info("The jUDDI Listener is not doing anything with this subscription at this moment");
		}
		
		//ASSERTION STATUS REPORT
		if (list.getAssertionStatusReport()!=null) {
			log.info("Subscription result for AssertionStatusReport with subscription key=" + list.getSubscription().getSubscriptionKey());
			log.info("The jUDDI Listener is not doing anything with this subscription at this moment");
		}
		
		//TMODELS
		if (list.getTModelList()!=null ){
			log.info("Subscription result for tModelList with subscription key=" + list.getSubscription().getSubscriptionKey());
			log.info("The jUDDI Listener is not doing anything with this subscription at this moment");
		}
	}

}
