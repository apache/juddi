package org.apache.juddi.v3.tck;

/*
 * Copyright 2001-2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;


/**
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
public class TckSubscriptionListenerRMI extends TckSubscriptionListener
{
	public final String LISTENER_SERVICE_XML_RMI   = "uddi_data/subscriptionnotifier/listenerServiceRMI.xml";
    public final String LISTENER_SERVICE_KEY_RMI   = "uddi:uddi.joepublisher.com:listeneronermi";

	/** note that the subscription1.xml contains the binding template for the UDDI server to call back into */
	public final static String SUBSCRIPTION_XML_RMI = "uddi_data/subscriptionnotifier/subscription1RMI.xml";
    public final static String SUBSCRIPTION_KEY_RMI = "uddi:uddi.joepublisher.com:subscriptiononermi";
    
    public TckSubscriptionListenerRMI(
			UDDISubscriptionPortType subscription,
			UDDIPublicationPortType publication) {
    	
		super(subscription, publication);
		this.LISTENER_SERVICE_KEY = LISTENER_SERVICE_KEY_RMI;
    	this.LISTENER_HTTP_SERVICE_XML = LISTENER_SERVICE_XML_RMI;
    	this.SUBSCRIPTION_KEY     = SUBSCRIPTION_KEY_RMI;
    	this.SUBSCRIPTION_XML     = SUBSCRIPTION_XML_RMI;
    	
	}
	
}