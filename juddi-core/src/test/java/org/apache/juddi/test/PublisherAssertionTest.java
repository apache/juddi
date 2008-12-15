package org.apache.juddi.test;

import java.util.List;
import javax.xml.bind.JAXBException;

import org.apache.juddi.api.impl.UDDIPublicationImpl;
import org.testng.Assert;
import org.testng.annotations.*;
import static junit.framework.Assert.assertEquals;

import org.uddi.api_v3.*;
import org.uddi.v3_service.DispositionReportFaultMessage;

public class PublisherAssertionTest {
	private UDDIPublicationImpl publish = new UDDIPublicationImpl();
	
	@Parameters({ "sourceDir", "pubassertFile", "publisherId" })
	@Test
	public void addPublisherAssertion(String sourceDir, String pubassertFile, String publisherId) {
		try {
			String authInfo = UDDIApiTestHelper.getAuthToken(publisherId);
			
			AddPublisherAssertions ap = new AddPublisherAssertions();
			ap.setAuthInfo(authInfo);

			PublisherAssertion paIn = (PublisherAssertion)UDDIApiTestHelper.buildEntityFromDoc(sourceDir + pubassertFile, "org.uddi.api_v3");
			ap.getPublisherAssertion().add(paIn);
			publish.addPublisherAssertions(ap);
	
			// Now get the entity and check the values
			List<PublisherAssertion> paOutList = publish.getPublisherAssertions(authInfo);
			PublisherAssertion paOut = paOutList.get(0);

			assertEquals(paIn.getFromKey(), paOut.getFromKey());
			assertEquals(paIn.getToKey(), paOut.getToKey());
			
			KeyedReference keyRefIn = paIn.getKeyedReference();
			KeyedReference keyRefOut = paOut.getKeyedReference();
			
			assertEquals(keyRefIn.getTModelKey(), keyRefOut.getTModelKey());
			assertEquals(keyRefIn.getKeyName(), keyRefOut.getKeyName());
			assertEquals(keyRefIn.getKeyValue(), keyRefOut.getKeyValue());
			
		}
		catch(DispositionReportFaultMessage dr) {
			Assert.fail("No exception should be thrown", dr);
		}
		catch(Exception je) {
			Assert.fail("No exception should be thrown", je);
		}

	}

	@Parameters({ "sourceDir", "pubassertFile", "publisherId" })
	@Test
	public void deletePublisherAssertion(String sourceDir, String pubassertFile, String publisherId) {
		try {
			String authInfo = UDDIApiTestHelper.getAuthToken(publisherId);
			
			// Delete the entity and make sure it is removed
			DeletePublisherAssertions dp = new DeletePublisherAssertions();
			dp.setAuthInfo(authInfo);
			
			PublisherAssertion paIn = (PublisherAssertion)UDDIApiTestHelper.buildEntityFromDoc(sourceDir + pubassertFile, "org.uddi.api_v3");
			dp.getPublisherAssertion().add(paIn);
			
			publish.deletePublisherAssertions(dp);
		}
		catch(DispositionReportFaultMessage dr) {
			Assert.fail("No exception should be thrown", dr);
		}
		catch(Exception je) {
			Assert.fail("No exception should be thrown", je);
		}
		
	}

}
