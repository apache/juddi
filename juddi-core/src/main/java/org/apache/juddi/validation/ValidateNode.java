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

import org.apache.juddi.api_v3.Node;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.FatalErrorException;
import org.apache.juddi.v3.error.ValueNotAllowedException;
import org.uddi.v3_service.DispositionReportFaultMessage;


/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class ValidateNode extends ValidateUDDIApi {

	public ValidateNode(UddiEntityPublisher publisher) {
		super(publisher);
	}

	
	
	/*-------------------------------------------------------------------
	 ClientSubscriptionInf functions are specific to jUDDI.
	 --------------------------------------------------------------------*/


	public void validateSaveNode(EntityManager em, org.apache.juddi.api_v3.SaveNode body) throws DispositionReportFaultMessage {

		if (body == null)
			throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
		
		// No null or empty list
		List<Node> nodes = body.getNode();
		if (nodes == null)
			throw new ValueNotAllowedException(new ErrorMessage("errors.saveNodes.NoInput"));
		
		for (Node clerk : body.getNode()) {
			validateNode(clerk);
		}

	}
	
	public void validateNode(org.apache.juddi.api_v3.Node node) throws DispositionReportFaultMessage {

		// No null input
		if (node == null)
			throw new ValueNotAllowedException(new ErrorMessage("errors.node.NullInput"));
		
		String name = node.getName();
		if (name == null || name.length() == 0)
			throw new ValueNotAllowedException(new ErrorMessage("errors.node.NoName"));
	
		//TODO could check we have all the urls
	
	}
	
	
	
	
}
