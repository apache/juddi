package org.apache.juddi.test;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.juddi.api.impl.UDDIPublicationImpl;
import org.apache.juddi.api.impl.UDDIInquiryImpl;
import org.apache.juddi.api.impl.UDDISubscriptionImpl;
import org.testng.Assert;
import org.testng.annotations.*;
import static junit.framework.Assert.assertEquals;

import org.apache.juddi.api.datatype.*;
import org.uddi.sub_v3.SaveSubscription;
import org.uddi.sub_v3.Subscription;
import org.uddi.v3_service.DispositionReportFaultMessage;

public class SubscriberSaveTest {
	
	private UDDISubscriptionImpl subscribe = new UDDISubscriptionImpl();
	private UDDIPublicationImpl publish = new UDDIPublicationImpl();
	private UDDIInquiryImpl inquiry = new UDDIInquiryImpl();
	
	@Parameters({ "sourceDir", "subscriptionFile" })
	@Test
	public void saveSubscriber(String sourceDir, String subscriptionFile) {
		try {
			String authInfo = UDDIApiTestHelper.getAuthToken(UDDIApiTestHelper.ROOT_PUBLISHER);
			System.out.println("AUTHINFO=" + authInfo);
			SaveSubscription ss = new SaveSubscription();
			ss.setAuthInfo(authInfo);

			System.out.println("FILE="+sourceDir+"/"+subscriptionFile);
			File newfile = new File(sourceDir + subscriptionFile);
			System.out.println("EXISTS: " + newfile.exists());
			Subscription subIn = (Subscription)UDDIApiTestHelper.buildEntityFromDoc(sourceDir + subscriptionFile, "org.uddi.sub_v3");
			ss.getSubscription().add(subIn);
			subscribe.saveSubscription(authInfo, ss);
		}
		catch(DispositionReportFaultMessage dr) {
			dr.printStackTrace();
			Assert.fail("No exception should be thrown", dr);
		} catch (JAXBException e) {
			e.printStackTrace();
			Assert.fail("No exception should be thrown", e);		
		}
	}

	@Parameters({ "subscriptionFile" })
	@Test
	public void deleteSubscriber(String subscriptionFile) {
		try {
			String authInfo = UDDIApiTestHelper.getAuthToken(UDDIApiTestHelper.ROOT_PUBLISHER);
			System.out.println("AUTHINFO=" + authInfo);
			
		}
		catch(DispositionReportFaultMessage dr) {
			Assert.fail("No exception should be thrown", dr);
		}
	}
}
