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

package org.apache.juddi.validation;

import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.juddi.api_v3.ClientSubscriptionInfo;
import org.apache.juddi.api_v3.DeleteClientSubscriptionInfo;
import org.apache.juddi.api_v3.GetAllClientSubscriptionInfoDetail;
import org.apache.juddi.api_v3.GetClientSubscriptionInfoDetail;
import org.apache.juddi.api_v3.SaveClientSubscriptionInfo;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.FatalErrorException;
import org.apache.juddi.v3.error.InvalidKeyPassedException;
import org.apache.juddi.v3.error.ValueNotAllowedException;
import org.uddi.v3_service.DispositionReportFaultMessage;


/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class ValidateClientSubscriptionInfo extends ValidateUDDIApi {

	public ValidateClientSubscriptionInfo(UddiEntityPublisher publisher) {
		super(publisher);
	}

	
	
	/*-------------------------------------------------------------------
	 ClientSubscriptionInf functions are specific to jUDDI.
	 --------------------------------------------------------------------*/
	
	public void validateDeleteClientSubscriptionInfo(EntityManager em, DeleteClientSubscriptionInfo body) throws DispositionReportFaultMessage {

		// No null input
		if (body == null)
			throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
		
		// No null or empty list
		List<String> entityKeyList = body.getSubscriptionKey();
		if (entityKeyList == null || entityKeyList.size() == 0)
			throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.NoKeys"));
		
		HashSet<String> dupCheck = new HashSet<String>();
		for (String entityKey : entityKeyList) {
			boolean inserted = dupCheck.add(entityKey);
			if (!inserted)
				throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.DuplicateKey", entityKey));
			
			Object obj = em.find(org.apache.juddi.model.ClientSubscriptionInfo.class, entityKey);
			if (obj == null)
				throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.SubscriptionKeyNotFound", entityKey));
			
		}
	}

	public void validateSaveClientSubscriptionInfo(EntityManager em, SaveClientSubscriptionInfo body) throws DispositionReportFaultMessage {

		// No null input
		if (body == null)
			throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
		
		// No null or empty list
		List<ClientSubscriptionInfo> clientSubscriptionInfos = body.getClientSubscriptionInfo();
		if (clientSubscriptionInfos == null)
			throw new ValueNotAllowedException(new ErrorMessage("errors.saveclientsubscriptioninfo.NoInput"));
		
		for (ClientSubscriptionInfo clientSubscriptionInfo : body.getClientSubscriptionInfo()) {
			if (clientSubscriptionInfo.getSubscriptionKey()==null || clientSubscriptionInfo.getSubscriptionKey().equals("")) {
				throw new ValueNotAllowedException(new ErrorMessage("errors.saveclientsubscriptionKey.NoInput"));
			}
			validateClerk(em, clientSubscriptionInfo.getFromClerk());
		}
	}

	public void validateClerk(EntityManager em, org.apache.juddi.api_v3.Clerk clerk) throws DispositionReportFaultMessage {

		// No null input
		if (clerk == null)
			throw new ValueNotAllowedException(new ErrorMessage("errors.clerk.NullInput"));
		
		String name = clerk.getName();
		if (name == null || name.length() == 0)
			throw new ValueNotAllowedException(new ErrorMessage("errors.clerk.NoName"));
		
		//make sure clerk exists
		Object obj = em.find(org.apache.juddi.model.Clerk.class, name);
		if (obj == null)
			throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.ClerkNotFound", name));
	}
	
	public void validateGetClientSubscriptionInfoDetail(GetClientSubscriptionInfoDetail body) throws DispositionReportFaultMessage {

		// No null input
		if (body == null)
			throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
		
		// No null or empty list
		List<String> clientSubscriptionKeyList = body.getClientSubscriptionKey();
		if (clientSubscriptionKeyList == null || clientSubscriptionKeyList.size() == 0)
			throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.NoKeys"));

		HashSet<String> dupCheck = new HashSet<String>();
		for (String clientSubscriptionKey : clientSubscriptionKeyList) {
			boolean inserted = dupCheck.add(clientSubscriptionKey);
			if (!inserted)
				throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.DuplicateKey", clientSubscriptionKey));
		}
	}
	
	public void validateGetAllClientSubscriptionDetail(GetAllClientSubscriptionInfoDetail body) throws DispositionReportFaultMessage {

		// No null input
		if (body == null)
			throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
		
	}
	
	
}
