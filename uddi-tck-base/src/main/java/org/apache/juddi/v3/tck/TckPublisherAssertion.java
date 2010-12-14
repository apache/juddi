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
import org.uddi.api_v3.AddPublisherAssertions;
import org.uddi.api_v3.DeletePublisherAssertions;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.PublisherAssertion;
import org.uddi.v3_service.UDDIPublicationPortType;
/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class TckPublisherAssertion 
{
	final static String JOE_ASSERT_XML    = "uddi_data/joepublisher/publisherAssertion.xml";
	final static String MARY_ASSERT_XML    = "uddi_data/marypublisher/publisherAssertion.xml";
	final static String JOE_ASSERT2_XML    = "uddi_data/joepublisher/publisherAssertion2.xml";
	final static String SAM_ASSERT_XML    = "uddi_data/samsyndicator/publisherAssertion.xml";
	
	private Log logger = LogFactory.getLog(this.getClass());
	private UDDIPublicationPortType publication = null;
 
	public TckPublisherAssertion(UDDIPublicationPortType publication) {
		super();
		this.publication = publication;
	}
	
	public void saveJoePublisherPublisherAssertion(String authInfoJoe) {
		addPublisherAssertion(authInfoJoe, JOE_ASSERT_XML);
	}
	
	public void saveSamPublisherPublisherAssertion(String authInfoSam) {
		addPublisherAssertion(authInfoSam, SAM_ASSERT_XML);
	}
	
	public void saveMaryPublisherPublisherAssertion(String authInfoMary) {
		addPublisherAssertion(authInfoMary, MARY_ASSERT_XML);
	}
	
	public void saveJoePublisherPublisherAssertion2(String authInfoJoe) {
		addPublisherAssertion(authInfoJoe, JOE_ASSERT2_XML);
	}
	
	public void deleteJoePublisherPublisherAssertion(String authInfoJoe) {
		deletePublisherAssertion(authInfoJoe, JOE_ASSERT_XML);
	}
	
	public void deleteMaryPublisherPublisherAssertion(String authInfoMary) {
		deletePublisherAssertion(authInfoMary, MARY_ASSERT_XML);
	}
	
	public void deleteSamPublisherPublisherAssertion(String authInfoSam) {
		deletePublisherAssertion(authInfoSam, SAM_ASSERT_XML);
	}
	
	public void deleteJoePublisherPublisherAssertion2(String authInfoJoe) {
		deletePublisherAssertion(authInfoJoe, JOE_ASSERT2_XML);
	}

	
	public void addPublisherAssertion(String authInfo, String pubassertXML) {
		try {
			AddPublisherAssertions ap = new AddPublisherAssertions();
			ap.setAuthInfo(authInfo);

			PublisherAssertion paIn = (PublisherAssertion)EntityCreator.buildFromDoc(pubassertXML, "org.uddi.api_v3");
			ap.getPublisherAssertion().add(paIn);
			publication.addPublisherAssertions(ap);
	
			// Now get the entity and check the values
			List<PublisherAssertion> paOutList = publication.getPublisherAssertions(authInfo);
			if (paOutList.size()==1) {
				PublisherAssertion paOut = paOutList.get(0);
	
				assertEquals(paIn.getFromKey(), paOut.getFromKey());
				assertEquals(paIn.getToKey(), paOut.getToKey());
				
				KeyedReference keyRefIn = paIn.getKeyedReference();
				KeyedReference keyRefOut = paOut.getKeyedReference();
				
				assertEquals(keyRefIn.getTModelKey(), keyRefOut.getTModelKey());
				assertEquals(keyRefIn.getKeyName(), keyRefOut.getKeyName());
				assertEquals(keyRefIn.getKeyValue(), keyRefOut.getKeyValue());
			}
			
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown");
		}

	}

	public void deletePublisherAssertion(String authInfo, String pubassertXML) {
		try {
			// Delete the entity and make sure it is removed
			DeletePublisherAssertions dp = new DeletePublisherAssertions();
			dp.setAuthInfo(authInfo);
			
			PublisherAssertion paIn = (PublisherAssertion)EntityCreator.buildFromDoc(pubassertXML, "org.uddi.api_v3");
			dp.getPublisherAssertion().add(paIn);
			
			publication.deletePublisherAssertions(dp);
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown");
		}
		
	}
}