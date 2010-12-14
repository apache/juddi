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
package org.apache.juddi.v3.tck;

import static junit.framework.Assert.assertEquals;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.jaxb.EntityCreator;
import org.junit.Assert;
import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.DeleteBinding;
import org.uddi.api_v3.GetBindingDetail;
import org.uddi.api_v3.SaveBinding;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class TckBindingTemplate 
{
	final static String JOE_BINDING_XML               = "uddi_data/joepublisher/bindingTemplate.xml";
    final static String JOE_BINDING_KEY               = "uddi:uddi.joepublisher.com:bindingtwo";
  
	private Log logger = LogFactory.getLog(this.getClass());
	private UDDIPublicationPortType publication = null;
    private UDDIInquiryPortType inquiry = null;
	
	public TckBindingTemplate(UDDIPublicationPortType publication, 
				  UDDIInquiryPortType inquiry) {
		super();
		this.publication = publication;
		this.inquiry = inquiry;
	}

	public void saveJoePublisherBinding(String authInfoJoe) {
		saveBinding(authInfoJoe, JOE_BINDING_XML, JOE_BINDING_KEY);
	}
	
	public void deleteJoePublisherBinding(String authInfoJoe) {
		deleteBinding(authInfoJoe, JOE_BINDING_KEY);
	}
	
	public void saveBinding(String authInfo, String bindingXML, String bindingKey) {
		try {
			// First save the entity
			SaveBinding sb = new SaveBinding();
			sb.setAuthInfo(authInfo);
			
			BindingTemplate btIn = (BindingTemplate)EntityCreator.buildFromDoc(bindingXML, "org.uddi.api_v3");
			sb.getBindingTemplate().add(btIn);
			publication.saveBinding(sb);
			
			// Now get the entity and check the values
			GetBindingDetail gb = new GetBindingDetail();
			gb.getBindingKey().add(bindingKey);
			BindingDetail bd = inquiry.getBindingDetail(gb);
			List<BindingTemplate> btOutList = bd.getBindingTemplate();
			BindingTemplate btOut = btOutList.get(0);

			assertEquals(btIn.getServiceKey(), btOut.getServiceKey());
			assertEquals(btIn.getBindingKey(), btOut.getBindingKey());
			
			TckValidator.checkDescriptions(btIn.getDescription(), btOut.getDescription());
			TckValidator.checkCategories(btIn.getCategoryBag(), btOut.getCategoryBag());
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown: " + e.getMessage());
		}
		
	}
	
	public void deleteBinding(String authInfo, String bindingKey) {
		try {
			// Delete the entity and make sure it is removed
			DeleteBinding db = new DeleteBinding();
			db.setAuthInfo(authInfo);
			
			db.getBindingKey().add(bindingKey);
			publication.deleteBinding(db);
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown.");
		}
		
	}
}