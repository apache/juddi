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
package org.apache.juddi.portlets.server.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;

import org.apache.juddi.api_v3.ClientSubscriptionInfo;
import org.apache.juddi.api_v3.DeleteClientSubscriptionInfo;
import org.apache.juddi.api_v3.SaveClerk;
import org.apache.juddi.api_v3.SaveClientSubscriptionInfo;
import org.apache.juddi.api_v3.SaveNode;
import org.apache.juddi.jaxb.JAXBMarshaller;
import org.apache.juddi.portlets.client.model.Node;
import org.apache.juddi.portlets.client.model.Subscription;
import org.apache.juddi.portlets.client.service.SubscriptionResponse;
import org.apache.juddi.portlets.client.service.SubscriptionService;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClerkManager;
import org.apache.juddi.v3.client.config.UDDINode;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.uddi.sub_v3.DeleteSubscription;
import org.uddi.sub_v3.GetSubscriptionResults;
import org.uddi.sub_v3.ObjectFactory;
import org.uddi.sub_v3.SubscriptionFilter;
import org.uddi.v3_service.UDDISubscriptionPortType;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
/**
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 *
 */
public class SubscriptionServiceImpl extends RemoteServiceServlet implements SubscriptionService {

	private static final long serialVersionUID = 6366224282740095468L;
	private Logger logger = Logger.getLogger(this.getClass());
	private final static String UP = "Up";


	public SubscriptionResponse getSubscriptions() 
	{
		SubscriptionResponse response = new SubscriptionResponse();
		response.setSuccess(false);
		HttpServletRequest request = getThreadLocalRequest();
		HttpSession session = request.getSession();
		String publisher = (String) session.getAttribute("UserName");
		Principal user = request.getUserPrincipal();
		if (publisher==null && user!=null) {
			logger.debug("UserPrincipal " + user);
			publisher = user.getName();
		}
		logger.debug("Publisher " + publisher + " sending getSubscription request..");
		try {
			boolean isMatchingClerk=false;
			Map<String, UDDIClerk> clerks = UDDIClerkManager.getClientConfig().getUDDIClerks();
			for (UDDIClerk clerk : clerks.values()) {
				if (publisher.equals(clerk.getPublisher())) {
					isMatchingClerk = true;
					Node modelNode = getSubscriptions(session, clerk);
					if (UP.equals(modelNode.getStatus())) {
						response.setSuccess(true);
					}
					modelNode.setClerkName(clerk.getName());
					response.getNodes().add(modelNode);
				}
			}
			if (! isMatchingClerk) {
				response.setMessage("This user is  not setup to own subscriptions");
			}
		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage(e.getMessage());
			response.setErrorCode("102");
		}
		return response;
	}

	private Node getSubscriptions(HttpSession session, UDDIClerk clerk) {

		Node modelNode = new Node();
		UDDINode node = clerk.getUDDINode();
		modelNode.setName(node.getName());
		modelNode.setClerkName(clerk.getName());
		modelNode.setDescription(node.getDescription());
		try {
			String clazz = UDDIClerkManager.getClientConfig().getUDDINodes().get(clerk.getUDDINode().getName()).getProxyTransport();
			Class<?> transportClass = Loader.loadClass(clazz);
			Transport transport = (Transport) transportClass.getConstructor(String.class).newInstance(clerk.getUDDINode().getName());  
			String authToken = (String) session.getAttribute("token-" + clerk.getName());

			UDDISubscriptionPortType subscriptionService = transport.getUDDISubscriptionService();
			List<org.uddi.sub_v3.Subscription> subscriptions = subscriptionService.getSubscriptions(authToken);
			for (org.uddi.sub_v3.Subscription subscription : subscriptions) {
				String expiresAfter = null;
				if (subscription.getExpiresAfter()!=null) expiresAfter = subscription.getExpiresAfter().toString();
				String rawFilter = JAXBMarshaller.marshallToString(new ObjectFactory().createSubscriptionFilter(subscription.getSubscriptionFilter()), "org.uddi.sub_v3");
				Subscription modelSubscription = new Subscription(
						subscription.getBindingKey(),
						subscription.isBrief(),
						expiresAfter,
						subscription.getMaxEntities(),
						subscription.getNotificationInterval().toString(),
						rawFilter,
						subscription.getSubscriptionKey());
				modelSubscription.setNode(modelNode);
				modelNode.getSubscriptions().add(modelSubscription);
			}
			modelNode.setStatus(UP);
		} catch (Exception e) {
			logger.error("Could not obtain subscription due to " + e.getMessage(), e);
			modelNode.setStatus("Down, communication problem: " + e.getMessage());
		} 
		return modelNode;
	}

	public SubscriptionResponse saveSubscription(String userAuthToken, Subscription modelSubscription) {

		HttpServletRequest request = getThreadLocalRequest();
		HttpSession session = request.getSession();
		SubscriptionResponse response = new SubscriptionResponse();

		logger.info("Sending saveSubscriptions request..");
		try {
			//before sending this we need to ready the listener node 
			UDDIClerk clerk = UDDIClerkManager.getClientConfig().getUDDIClerks().get(modelSubscription.getClerkName());
			
			logger.info("Updating default UDDI server..");
			String defaultClazz = UDDIClerkManager.getClientConfig().getUDDINodes().get(Constants.NODE_NAME).getProxyTransport();
			Class<?> defaultTransportClass = Loader.loadClass(defaultClazz); 
			Transport defaultTransport = (Transport) defaultTransportClass.getConstructor(String.class).newInstance(Constants.NODE_NAME); 
			JUDDIApiPortType juddiApiService = defaultTransport.getJUDDIApiService();
			//making sure our node info is there and up to date.
			SaveNode saveNode = new SaveNode();
			saveNode.setAuthInfo(userAuthToken);
			saveNode.getNode().add(clerk.getApiClerk().getNode());
			juddiApiService.saveNode(saveNode);
			//making sure our clerk info is there and up to date
			SaveClerk saveClerk = new SaveClerk();
			saveClerk.setAuthInfo(userAuthToken);
			saveClerk.getClerk().add(clerk.getApiClerk());
			juddiApiService.saveClerk(saveClerk);
			logger.debug("Updating default UDDI server completed.");
			
			SaveClientSubscriptionInfo saveClientSubscriptionInfo = new SaveClientSubscriptionInfo();
			saveClientSubscriptionInfo.setAuthInfo(userAuthToken);
			ClientSubscriptionInfo clientSubscriptionInfo = new ClientSubscriptionInfo();
			clientSubscriptionInfo.setSubscriptionKey(modelSubscription.getSubscriptionKey());
			clientSubscriptionInfo.setClerk(clerk.getApiClerk());
			saveClientSubscriptionInfo.getClientSubscriptionInfo().add(clientSubscriptionInfo);
			//save clientSubscription to the listening UDDI default server.
			juddiApiService.saveClientSubscriptionInfo(saveClientSubscriptionInfo);
			logger.debug("Saved ClientSubscriptionInfo to the default UDDI server");
			
			//the listening server is ready; now add the subscription
			String clazz = UDDIClerkManager.getClientConfig().getUDDINodes().get(clerk.getUDDINode().getName()).getProxyTransport();
			Class<?> transportClass = Loader.loadClass(clazz); 
			Transport transport = (Transport) transportClass.getConstructor(String.class).newInstance(clerk.getUDDINode().getName()); 
			UDDISubscriptionPortType subscriptionService = transport.getUDDISubscriptionService();
			List<org.uddi.sub_v3.Subscription> subscriptionList = new ArrayList<org.uddi.sub_v3.Subscription>();
			org.uddi.sub_v3.Subscription subscription = new org.uddi.sub_v3.Subscription();
			subscription.setBindingKey(modelSubscription.getBindingKey());
			subscription.setBrief(modelSubscription.getBrief());
			if (!"".equals(modelSubscription.getExpiresAfter())) {
				XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(modelSubscription.getExpiresAfter());
				subscription.setExpiresAfter(calendar);
			}
			subscription.setMaxEntities(modelSubscription.getMaxEntities());
			if (!"".equals(modelSubscription.getNotificationInterval())) {
				Duration duration = DatatypeFactory.newInstance().newDuration(modelSubscription.getNotificationInterval());
				subscription.setNotificationInterval(duration);
			}
			if (!"".equals(modelSubscription.getSubscriptionFilter())) {
				SubscriptionFilter subscriptionFilter = (SubscriptionFilter)JAXBMarshaller.unmarshallFromString(modelSubscription.getSubscriptionFilter(), JAXBMarshaller.PACKAGE_SUBSCRIPTION);
				subscription.setSubscriptionFilter(subscriptionFilter);
			}
			subscription.setSubscriptionKey(modelSubscription.getSubscriptionKey());
			subscriptionList.add(subscription);
			Holder<List<org.uddi.sub_v3.Subscription>> subscriptionHolder = new Holder<List<org.uddi.sub_v3.Subscription>>();
			subscriptionHolder.value = subscriptionList;
			String authToken = (String) session.getAttribute("token-" + clerk.getName());
			subscriptionService.saveSubscription(authToken, subscriptionHolder);
			subscription = subscriptionHolder.value.get(0);
			String expiresAfter = null;
			if (subscription.getExpiresAfter()!=null) expiresAfter = subscription.getExpiresAfter().toString();
			String rawFilter = JAXBMarshaller.marshallToString(new ObjectFactory().createSubscriptionFilter(subscription.getSubscriptionFilter()), "org.uddi.sub_v3");
			Subscription savedModelSubscription = new Subscription(
					subscription.getBindingKey(),
					subscription.isBrief(),
					expiresAfter,
					subscription.getMaxEntities(),
					subscription.getNotificationInterval().toString(),
					rawFilter,
					subscription.getSubscriptionKey());
			savedModelSubscription.setNode(modelSubscription.getNode());
			response.setSubscription(savedModelSubscription);

			response.setSuccess(true);
		} catch (Exception e) {
			logger.error("Could not save subscription. " + e.getMessage(), e);
			response.setSuccess(false);
			response.setMessage(e.getMessage());
			response.setErrorCode("102");
		} catch (Throwable t) {
			logger.error("Could not save subscription. " + t.getMessage(), t);
			response.setSuccess(false);
			response.setMessage(t.getMessage());
			response.setErrorCode("102");
		} 

		return response;
	}

	public SubscriptionResponse deleteSubscription(String userAuthToken, Subscription modelSubscription) {

		HttpServletRequest request = getThreadLocalRequest();
		HttpSession session = request.getSession();
		SubscriptionResponse response = new SubscriptionResponse();

		logger.info("Sending deleteSubscriptions request for subscriptionKey=" 
				+ modelSubscription.getSubscriptionKey());
		try {
			UDDIClerk clerk = UDDIClerkManager.getClientConfig().getUDDIClerks().get(modelSubscription.getClerkName());
			String clazz = UDDIClerkManager.getClientConfig().getUDDINodes().get(clerk.getUDDINode().getName()).getProxyTransport();
			Class<?> transportClass = Loader.loadClass(clazz);
			Transport transport = (Transport) transportClass.getConstructor(String.class).newInstance(clerk.getUDDINode().getName()); 
			UDDISubscriptionPortType subscriptionService = transport.getUDDISubscriptionService();
			DeleteSubscription deleteSubscription = new DeleteSubscription();
			String authToken = (String) session.getAttribute("token-" + clerk.getName());
			deleteSubscription.setAuthInfo(authToken);
			deleteSubscription.getSubscriptionKey().add(modelSubscription.getSubscriptionKey());
			subscriptionService.deleteSubscription(deleteSubscription);
			
			//now remove it from the listener UDDI server too
			String defaultClazz = UDDIClerkManager.getClientConfig().getUDDINodes().get(Constants.NODE_NAME).getProxyTransport();
			Class<?> defaultTransportClass = Loader.loadClass(defaultClazz); 
			Transport defaultTransport = (Transport) defaultTransportClass.getConstructor(String.class).newInstance(Constants.NODE_NAME); 
			JUDDIApiPortType juddiApiService = defaultTransport.getJUDDIApiService();
			
			DeleteClientSubscriptionInfo deleteClientSubscriptionInfo = new DeleteClientSubscriptionInfo();
			deleteClientSubscriptionInfo.setAuthInfo(userAuthToken);
			deleteClientSubscriptionInfo.getSubscriptionKey().add(modelSubscription.getSubscriptionKey());
			//remove the clientSubscription to the listening UDDI default server.
			juddiApiService.deleteClientSubscriptionInfo(deleteClientSubscriptionInfo);
			logger.debug("Deleted ClientSubscriptionInfo to the default UDDI server");
			
			response.setSuccess(true);
		} catch (Exception e) {
			logger.error("Could not delete subscription. " + e.getMessage(), e);
			response.setSuccess(false);
			response.setMessage(e.getMessage());
			response.setErrorCode("102");
		} catch (Throwable t) {
			logger.error("Could not delete subscription. " + t.getMessage(), t);
			response.setSuccess(false);
			response.setMessage(t.getMessage());
			response.setErrorCode("102");
		} 

		return response;
	}
	
	public SubscriptionResponse invokeSyncSubscription(String userAuthToken, Subscription modelSubscription) {

		HttpServletRequest request = getThreadLocalRequest();
		HttpSession session = request.getSession();
		SubscriptionResponse response = new SubscriptionResponse();
	
		//Send a request to the listener serverNode
		GetSubscriptionResults getSubscriptionResults = new GetSubscriptionResults();
		getSubscriptionResults.setAuthInfo(userAuthToken);
		getSubscriptionResults.setSubscriptionKey(modelSubscription.getSubscriptionKey());
		//getSubscriptionResults.set
		
		return response;
	}

}
