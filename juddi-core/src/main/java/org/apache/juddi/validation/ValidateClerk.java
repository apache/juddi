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

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.juddi.api_v3.Clerk;
import org.apache.juddi.api_v3.Node;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.FatalErrorException;
import org.apache.juddi.v3.error.InvalidKeyPassedException;
import org.apache.juddi.v3.error.ValueNotAllowedException;
import org.uddi.v3_service.DispositionReportFaultMessage;


/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class ValidateClerk extends ValidateUDDIApi {

	public ValidateClerk(UddiEntityPublisher publisher) {
		super(publisher);
	}

	
	
	/*-------------------------------------------------------------------
	 ClientSubscriptionInf functions are specific to jUDDI.
	 --------------------------------------------------------------------*/


	public void validateSaveClerk(EntityManager em, org.apache.juddi.api_v3.SaveClerk body) throws DispositionReportFaultMessage {

		if (body == null)
			throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
		
		// No null or empty list
		List<Clerk> clerks = body.getClerk();
		if (clerks == null)
			throw new ValueNotAllowedException(new ErrorMessage("errors.saveClerk.NoInput"));
		
		for (Clerk clerk : body.getClerk()) {
			validateClerk(em, clerk);
		}

	}
	
	public void validateClerk(EntityManager em, org.apache.juddi.api_v3.Clerk clerk) throws DispositionReportFaultMessage {

		// No null input
		if (clerk == null)
			throw new ValueNotAllowedException(new ErrorMessage("errors.clerk.NullInput"));
		
		String name = clerk.getName();
		if (name == null || name.length() == 0)
			throw new ValueNotAllowedException(new ErrorMessage("errors.clerk.NoName"));
	
		String publisherName = clerk.getPublisher();
		if (publisherName == null || publisherName.length() == 0)
			throw new ValueNotAllowedException(new ErrorMessage("errors.clerk.NoPublisherName"));
		
		Node node = clerk.getNode();
		if (node == null) 
			throw new ValueNotAllowedException(new ErrorMessage("errors.clerk.NullNodeInput"));
		
		String nodeName = node.getName();
		if (nodeName == null || nodeName.length() == 0)
			throw new ValueNotAllowedException(new ErrorMessage("errors.node.NoName"));
		
		//make sure node exists
		Object obj = em.find(org.apache.juddi.model.Node.class, nodeName);
		if (obj == null)
			throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.NodeNotFound", nodeName));

	}
	
	
	
	
}
