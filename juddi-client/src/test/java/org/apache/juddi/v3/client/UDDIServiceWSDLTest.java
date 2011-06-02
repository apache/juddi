package org.apache.juddi.v3.client;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.juddi.v3.client.config.Property;
import org.junit.Assert;
import org.junit.Test;


public class UDDIServiceWSDLTest {
	
	@Test()
	public void canFindSpecWSDL() throws IOException {
		UDDIServiceWSDL uddiServiceWSDL = new UDDIServiceWSDL();
		String wsdlContent = uddiServiceWSDL.getServiceWSDLContent();
		assertNotNull(wsdlContent);
		//The unaltered WSDL should contain the following string
		assertTrue(wsdlContent.contains("http://localhost/uddi/inquire/"));
	}
	
	@Test(expected=IOException.class)
	public void cannotFindSpecWSDL() throws IOException {
		UDDIServiceWSDL uddiServiceWSDL = new UDDIServiceWSDL();
		uddiServiceWSDL.setUddiV3ServiceWSDL("nonExisting.wsdl");
		uddiServiceWSDL.getServiceWSDLContent();
	}
	
	@Test()
	public void getUpdatedWSDL() {
		UDDIServiceWSDL uddiServiceWSDL = new UDDIServiceWSDL();
		URL wsdlPath;
		try {
			wsdlPath = uddiServiceWSDL.getWSDLFilePath(UDDIServiceWSDL.WSDLEndPointType.SECURITY, "securityeurl");
			System.out.println("WSDL Path=" + wsdlPath);
			assertNotNull(wsdlPath);
			
		} catch (IOException e) {
			String tmpDir = Property.getTempDir();
			Assert.fail("Could not create file in dir " + tmpDir);
			e.printStackTrace();
		}
	}
	
}
