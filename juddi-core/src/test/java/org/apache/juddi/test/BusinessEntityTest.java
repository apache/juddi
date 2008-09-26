package org.apache.juddi.test;

import java.util.List;
import java.util.Iterator;
import javax.xml.bind.JAXBException;

import org.apache.juddi.api.impl.UDDIPublicationImpl;
import org.apache.juddi.api.impl.UDDIInquiryImpl;
import org.testng.annotations.*;
import static junit.framework.Assert.fail;
import static junit.framework.Assert.assertEquals;

import org.uddi.api_v3.*;
import org.uddi.v3_service.DispositionReportFaultMessage;

public class BusinessEntityTest {
	private UDDIPublicationImpl publish = new UDDIPublicationImpl();
	private UDDIInquiryImpl inquiry = new UDDIInquiryImpl();
	
	@Parameters({ "businessFile", "businessKey" })
	@Test
	public void saveBusiness(String businessFile, String businessKey) {
		try {
			SaveBusiness sb = new SaveBusiness();
			BusinessEntity beIn = (BusinessEntity)UDDIApiTestHelper.buildEntityFromDoc(businessFile);
			sb.getBusinessEntity().add(beIn);
			publish.saveBusiness(sb);
	
			// Now get the entity and check the values
			GetBusinessDetail gb = new GetBusinessDetail();
			gb.getBusinessKey().add(businessKey);
			BusinessDetail bd = inquiry.getBusinessDetail(gb);
			List<BusinessEntity> beOutList = bd.getBusinessEntity();
			BusinessEntity beOut = beOutList.get(0);

			assertEquals(beIn.getBusinessKey(), beOut.getBusinessKey());
			
			UDDIApiTestHelper.checkNames(beIn.getName(), beOut.getName());
			UDDIApiTestHelper.checkDescriptions(beIn.getDescription(), beOut.getDescription());
			UDDIApiTestHelper.checkDiscoveryUrls(beIn.getDiscoveryURLs(), beOut.getDiscoveryURLs());
			UDDIApiTestHelper.checkContacts(beIn.getContacts(), beOut.getContacts());
			UDDIApiTestHelper.checkCategories(beIn.getCategoryBag(), beOut.getCategoryBag());
		}
		catch(DispositionReportFaultMessage dr) {
			fail("No exception should be thrown");
		}
		catch(JAXBException je) {
			fail("No exception should be thrown");
		}

	}

	@Parameters({ "businessKey" })
	@Test
	public void deleteBusiness(String businessKey) {
		try {
			// Delete the entity and make sure it is removed
			DeleteBusiness db = new DeleteBusiness();
			db.getBusinessKey().add(businessKey);
			publish.deleteBusiness(db);
		}
		catch(DispositionReportFaultMessage dr) {
			
		}
		
	}

}
