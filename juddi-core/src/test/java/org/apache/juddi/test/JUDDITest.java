package org.apache.juddi.test;

import javax.xml.bind.JAXBException;

import org.testng.Assert;
import org.testng.annotations.*;
import org.uddi.v3_service.DispositionReportFaultMessage;


/*
 * This test is used to initialize and clean up after other tests.
 */
public class JUDDITest {
	
	
	@Parameters({ "sourceDir" })
	@Test
	public void initializeTests(String sourceDir) {
		try {
			// The root publisher with admin rights must exist to publish another publisher.
			UDDIApiTestHelper.installRootPublisher(sourceDir);
			// Install the root Key Generator
			UDDIApiTestHelper.installRootPublisherKeyGen(sourceDir);

		}
		catch(DispositionReportFaultMessage dr) {
			Assert.fail("No exception should be thrown", dr);
		}
		catch(JAXBException je) {
			Assert.fail("No exception should be thrown", je);
		}

	}

	@Test
	public void cleanupTests() {
		try {
			
			UDDIApiTestHelper.removeRootPublisherKeyGen();
			UDDIApiTestHelper.removeRootPublisher();
			UDDIApiTestHelper.removeAuthTokens();
		}
		catch(Exception e) {
			Assert.fail("No exception should be thrown", e);
		}
		
	}

	
	
}
