package org.apache.juddi.test;

import static junit.framework.Assert.fail;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;

import org.apache.juddi.api.impl.UDDIPublicationImpl;
import org.testng.annotations.*;
import org.uddi.api_v3.DeleteService;
import org.uddi.api_v3.DeleteService;
import org.uddi.api_v3.SaveService;
import org.uddi.v3_service.DispositionReportFaultMessage;

public class BusinessServiceTest {
	private UDDIPublicationImpl publish = new UDDIPublicationImpl();

	@Parameters({ "serviceFile" })
	@Test
	public void saveService(String serviceFile) {
		try {
			// First save the entity
			SaveService ss = new SaveService();
			org.uddi.api_v3.BusinessService bs = (org.uddi.api_v3.BusinessService)buildEntityFromDoc(serviceFile);
			ss.getBusinessService().add(bs);
			publish.saveService(ss);
			
			// Now get the entity and check the values
		}
		catch(DispositionReportFaultMessage dr) {
			
		}
		catch(JAXBException je) {
			
		}
		
	}
	
	@Parameters({ "serviceKey" })
	@Test
	public void deleteService(String serviceKey) {
		try {
			// Delete the entity and make sure it is removed
			DeleteService ds = new DeleteService();
			ds.getServiceKey().add(serviceKey);
			publish.deleteService(ds);
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
