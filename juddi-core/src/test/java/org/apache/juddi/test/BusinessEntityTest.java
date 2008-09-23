package org.apache.juddi.test;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;

import org.apache.juddi.api.impl.UDDIPublicationImpl;
import org.testng.annotations.*;
import static junit.framework.Assert.fail;

import org.uddi.api_v3.DeleteBusiness;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.v3_service.DispositionReportFaultMessage;

public class BusinessEntityTest {
	private UDDIPublicationImpl publish = new UDDIPublicationImpl();
	
	@Parameters({ "businessFile" })
	@Test
	public void saveBusiness(String businessFile) {
		try {
			SaveBusiness sb = new SaveBusiness();
			org.uddi.api_v3.BusinessEntity be = (org.uddi.api_v3.BusinessEntity)buildEntityFromDoc(businessFile);
			sb.getBusinessEntity().add(be);
			publish.saveBusiness(sb);
	
			// Now get the entity and check the values
		}
		catch(DispositionReportFaultMessage dr) {
			
		}
		catch(JAXBException je) {
			
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

	public static Object buildEntityFromDoc(String fileName) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance("org.uddi.api_v3");
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		Object obj = ((JAXBElement)unmarshaller.unmarshal(new File(fileName))).getValue();
		return obj;
	}

}
