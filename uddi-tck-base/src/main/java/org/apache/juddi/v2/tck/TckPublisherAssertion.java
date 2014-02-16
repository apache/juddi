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
package org.apache.juddi.v2.tck;

import static junit.framework.Assert.assertEquals;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.jaxb.EntityCreator;
import org.junit.Assert;
import org.uddi.api_v2.AddPublisherAssertions;
import org.uddi.api_v2.DeletePublisherAssertions;
import org.uddi.api_v2.GetPublisherAssertions;
import org.uddi.api_v2.KeyedReference;
import org.uddi.api_v2.PublisherAssertion;
import org.uddi.api_v2.PublisherAssertions;
import org.uddi.v2_service.Publish;
/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class TckPublisherAssertion 
{
	final static String JOE_ASSERT_XML    = "uddi_data_v2/joepublisher/publisherAssertion.xml";
	final static String MARY_ASSERT_XML    = "uddi_data_v2/marypublisher/publisherAssertion.xml";
	final static String JOE_ASSERT2_XML    = "uddi_data_v2/joepublisher/publisherAssertion2.xml";
	final static String SAM_ASSERT_XML    = "uddi_data_v2/samsyndicator/publisherAssertion.xml";
	
	private Log logger = LogFactory.getLog(this.getClass());
	private Publish publication = null;
 
	public TckPublisherAssertion(Publish publication) {
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
               ap.setGeneric("2.0");
			PublisherAssertion paIn = (PublisherAssertion)EntityCreator.buildFromDoc(pubassertXML, "org.uddi.api_v2");
			ap.getPublisherAssertion().add(paIn);
			publication.addPublisherAssertions(ap);
	
               GetPublisherAssertions body = new GetPublisherAssertions();
               body.setAuthInfo(authInfo);
               body.setGeneric("2.0");
               // Now get the entity and check the values
               PublisherAssertions publisherAssertions = publication.getPublisherAssertions(body);
			List<PublisherAssertion> paOutList =publisherAssertions.getPublisherAssertion();
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
			dp.setGeneric("2.0");
			PublisherAssertion paIn = (PublisherAssertion)EntityCreator.buildFromDoc(pubassertXML, "org.uddi.api_v2");
			dp.getPublisherAssertion().add(paIn);
			
			publication.deletePublisherAssertions(dp);
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail("No exception should be thrown");
		}
		
	}
}