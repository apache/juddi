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

import org.apache.juddi.api_v3.Clerk;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.XRegistration;
import org.uddi.api_v3.ServiceInfo;
import org.uddi.sub_v3.SubscriptionResultsList;


/**
 * Used to factor out inquiry functionality as it is used in more than one spot.
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class XRegisterHelper {

	public static void handle(Clerk fromClerk, Clerk toClerk, SubscriptionResultsList list) {
		
		if (list.getServiceList()!=null) {
			for (ServiceInfo serviceInfo : list.getServiceList().getServiceInfos().getServiceInfo() ) {
				serviceInfo.getBusinessKey();
				new XRegistration(serviceInfo.getBusinessKey(), new UDDIClerk(fromClerk), new UDDIClerk(toClerk)).xRegisterBusiness();
				new XRegistration(serviceInfo.getServiceKey(), new UDDIClerk(fromClerk), new UDDIClerk(toClerk)).xRegisterService();
			}
		}
		
	}
	
	
}
