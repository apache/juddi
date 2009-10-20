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

import org.apache.juddi.api_v3.DeletePublisher;
import org.apache.juddi.api_v3.GetAllPublisherDetail;
import org.apache.juddi.api_v3.GetPublisherDetail;
import org.apache.juddi.api_v3.SavePublisher;
import org.apache.juddi.model.Publisher;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.FatalErrorException;
import org.apache.juddi.v3.error.InvalidKeyPassedException;
import org.apache.juddi.v3.error.UserMismatchException;
import org.apache.juddi.v3.error.ValueNotAllowedException;
import org.uddi.v3_service.DispositionReportFaultMessage;


/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class ValidatePublisher extends ValidateUDDIApi {

	public ValidatePublisher(UddiEntityPublisher publisher) {
		super(publisher);
	}

	
	
	/*-------------------------------------------------------------------
	 Publisher functions are specific to jUDDI.
	 --------------------------------------------------------------------*/
	
	public void validateDeletePublisher(EntityManager em, DeletePublisher body) throws DispositionReportFaultMessage {

		// No null input
		if (body == null)
			throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
		
		// No null or empty list
		List<String> entityKeyList = body.getPublisherId();
		if (entityKeyList == null || entityKeyList.size() == 0)
			throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.NoKeys"));
		
		if (!((Publisher)publisher).isAdmin())
			throw new UserMismatchException(new ErrorMessage("errors.deletepublisher.AdminReqd"));

		HashSet<String> dupCheck = new HashSet<String>();
		for (String entityKey : entityKeyList) {
			boolean inserted = dupCheck.add(entityKey);
			if (!inserted)
				throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.DuplicateKey", entityKey));
			
			Object obj = em.find(org.apache.juddi.model.Publisher.class, entityKey);
			if (obj == null)
				throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.PublisherNotFound", entityKey));
			
		}
	}

	public void validateSavePublisher(EntityManager em, SavePublisher body) throws DispositionReportFaultMessage {

		// No null input
		if (body == null)
			throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
		
		// No null or empty list
		List<org.apache.juddi.api_v3.Publisher> entityList = body.getPublisher();
		if (entityList == null || entityList.size() == 0)
			throw new ValueNotAllowedException(new ErrorMessage("errors.savepublisher.NoInput"));
		
		if (!((Publisher)publisher).isAdmin())
			throw new UserMismatchException(new ErrorMessage("errors.savepublisher.AdminReqd"));
		
		for (org.apache.juddi.api_v3.Publisher entity : entityList) {
			validatePublisher(em, entity);
		}
	}

	public void validatePublisher(EntityManager em, org.apache.juddi.api_v3.Publisher publisher) throws DispositionReportFaultMessage {

		// No null input
		if (publisher == null)
			throw new ValueNotAllowedException(new ErrorMessage("errors.publisher.NullInput"));
		
		String authorizedName = publisher.getAuthorizedName();
		if (authorizedName == null || authorizedName.length() == 0)
			throw new ValueNotAllowedException(new ErrorMessage("errors.publisher.NoAuthorizedName"));
	
		String publisherName = publisher.getPublisherName();
		if (publisherName == null || publisherName.length() == 0)
			throw new ValueNotAllowedException(new ErrorMessage("errors.publisher.NoPublisherName"));

	}
	
	public void validateGetPublisherDetail(GetPublisherDetail body) throws DispositionReportFaultMessage {

		// No null input
		if (body == null)
			throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
		
		// No null or empty list
		List<String> publisherIdList = body.getPublisherId();
		if (publisherIdList == null || publisherIdList.size() == 0)
			throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.NoKeys"));

		HashSet<String> dupCheck = new HashSet<String>();
		for (String publisherId : publisherIdList) {
			boolean inserted = dupCheck.add(publisherId);
			if (!inserted)
				throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.DuplicateKey", publisherId));
		}
	}
	
	public void validateGetAllPublisherDetail(GetAllPublisherDetail body) throws DispositionReportFaultMessage {

		// No null input
		if (body == null)
			throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
		
	}
	
	
}
