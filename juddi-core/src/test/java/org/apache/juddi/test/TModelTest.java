package org.apache.juddi.test;

import java.util.List;
import javax.xml.bind.JAXBException;

import org.apache.juddi.api.impl.UDDIPublicationImpl;
import org.apache.juddi.api.impl.UDDIInquiryImpl;
import org.testng.Assert;
import org.testng.annotations.*;
import static junit.framework.Assert.assertEquals;

import org.uddi.api_v3.*;
import org.uddi.v3_service.DispositionReportFaultMessage;

public class TModelTest {
	private UDDIPublicationImpl publish = new UDDIPublicationImpl();
	private UDDIInquiryImpl inquiry = new UDDIInquiryImpl();
	
	@Parameters({ "sourceDir", "tmodelFile", "tmodelKey", "publisherId" })
	@Test
	public void saveTModel(String sourceDir, String tmodelFile, String tmodelKey, String publisherId) {
		try {
			String authInfo = UDDIApiTestHelper.getAuthToken(publisherId);
			
			SaveTModel st = new SaveTModel();
			st.setAuthInfo(authInfo);

			TModel tmIn = (TModel)UDDIApiTestHelper.buildEntityFromDoc(sourceDir + tmodelFile, "org.uddi.api_v3");
			st.getTModel().add(tmIn);
			publish.saveTModel(st);
	
			// Now get the entity and check the values
			GetTModelDetail gt = new GetTModelDetail();
			gt.getTModelKey().add(tmodelKey);
			TModelDetail td = inquiry.getTModelDetail(gt);
			List<TModel> tmOutList = td.getTModel();
			TModel tmOut = tmOutList.get(0);

			assertEquals(tmIn.getTModelKey(), tmOut.getTModelKey());

			assertEquals(tmIn.getName().getLang(), tmOut.getName().getLang());
			assertEquals(tmIn.getName().getValue(), tmOut.getName().getValue());
			UDDIApiTestHelper.checkDescriptions(tmIn.getDescription(), tmOut.getDescription());
			UDDIApiTestHelper.checkCategories(tmIn.getCategoryBag(), tmOut.getCategoryBag());
		}
		catch(DispositionReportFaultMessage dr) {
			Assert.fail("No exception should be thrown", dr);
		}
		catch(JAXBException je) {
			Assert.fail("No exception should be thrown", je);
		}

	}

	@Parameters({ "tmodelKey", "publisherId" })
	@Test
	public void deleteTModel(String tmodelKey, String publisherId) {
		try {
			String authInfo = UDDIApiTestHelper.getAuthToken(publisherId);
			
			// Delete the entity and make sure it is removed
			DeleteTModel dt = new DeleteTModel();
			dt.setAuthInfo(authInfo);
			
			dt.getTModelKey().add(tmodelKey);
			publish.deleteTModel(dt);
			
			// Above call will only do a lazy deletion.  Must remove outside of API to clean database.
			UDDIApiTestHelper.deleteEntity(org.apache.juddi.model.Tmodel.class, tmodelKey);
		}
		catch(DispositionReportFaultMessage dr) {
			Assert.fail("No exception should be thrown", dr);
		}
		
	}

}
