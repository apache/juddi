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


import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.Property;

import org.apache.juddi.model.Subscription;
import org.apache.juddi.model.UddiEntity;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.UnsupportedException;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public abstract class ValidateUDDIApi {

	protected UddiEntityPublisher publisher;
   protected String nodeID=null;
		
   /**
    * This is used only during the install process to prevent infinite loops
    * @param publisher
    * @param nodeid 
    */
	public ValidateUDDIApi(UddiEntityPublisher publisher, String nodeid) {
		this.publisher = publisher;
      this.nodeID = nodeid;
	}
   
   public ValidateUDDIApi(UddiEntityPublisher publisher) {
		this.publisher = publisher;
      try {
         this.nodeID = AppConfig.getConfiguration().getString(Property.JUDDI_NODE_ID);
      } catch (ConfigurationException ex) {
         Logger.getLogger(ValidateUDDIApi.class.getName()).log(Level.SEVERE, "unable to get the current node id, this may cause access control problems"
                 + " and must be fixed. set " + Property.JUDDI_NODE_ID + " in juddiv3.xml", ex);
      }
	}

	public UddiEntityPublisher getPublisher() {
		return publisher;
	}

	public void setPublisher(UddiEntityPublisher publisher) {
		this.publisher = publisher;
	}
	
	public static void unsupportedAPICall() throws DispositionReportFaultMessage {
		throw new UnsupportedException(new ErrorMessage("errors.Unsupported"));
	}
	
	public static boolean isUniqueKey(EntityManager em, String entityKey) {
		Object obj = em.find(UddiEntity.class, entityKey);
		if (obj != null)
			return false;
		
		obj = em.find(Subscription.class, entityKey);
		if (obj != null)
			return false;
		
		return true;
	}
}
