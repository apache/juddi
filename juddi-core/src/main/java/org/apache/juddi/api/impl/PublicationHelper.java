/*
 * Copyright 2001-2008 The Apache Software Foundation.
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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.juddi.mapping.MappingModelToApi;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.query.FindBusinessByPublisherQuery;
import org.apache.juddi.query.FindPublisherAssertionByBusinessQuery;
import org.uddi.api_v3.AssertionStatusItem;
import org.uddi.api_v3.CompletionStatus;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * Used to factor out publication functionality that is used in more than one spot.
 * 
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class PublicationHelper {

	public static List<AssertionStatusItem> getAssertionStatusItemList(UddiEntityPublisher publisher, CompletionStatus completionStatus, EntityManager em) throws DispositionReportFaultMessage {
		List<org.uddi.api_v3.AssertionStatusItem> result = new ArrayList<org.uddi.api_v3.AssertionStatusItem>(0);

		List<?> businessKeysFound = null;
		businessKeysFound = FindBusinessByPublisherQuery.select(em, null, publisher, businessKeysFound);
		
		List<org.apache.juddi.model.PublisherAssertion> pubAssertionList = FindPublisherAssertionByBusinessQuery.select(em, businessKeysFound, completionStatus);
		for(org.apache.juddi.model.PublisherAssertion modelPubAssertion : pubAssertionList) {
			org.uddi.api_v3.AssertionStatusItem apiAssertionStatusItem = new org.uddi.api_v3.AssertionStatusItem();

			MappingModelToApi.mapAssertionStatusItem(modelPubAssertion, apiAssertionStatusItem, businessKeysFound);
			
			result.add(apiAssertionStatusItem);
		}
		
		return result;
	}

}
