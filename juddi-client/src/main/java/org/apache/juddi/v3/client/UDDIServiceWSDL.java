package org.apache.juddi.v3.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class UDDIServiceWSDL {
	
	/** The WSDLEnpoint Types as defined in the UDDI v3 specification. */
	public static enum WSDLEndPointType {SECURITY, INQUIRY, PUBLISH, SUBSCRIPTION, SUBSCRIPTION_LISTENER, CUSTODY_TRANSFER, 
		REPLICATION, VALUESETVALIDATION, VALUESETCACHING};
	/** The default file name of the uddi_v3_service.wsdl file. The file will be located and read of the classpath. */
	private String uddiV3ServiceWSDL = "uddi_v3_service.wsdl";
	/** The default soap:address location values of the WSDLEndPointTypes as defined in the UDDI v3 Client Specification. */
	private final static Map<WSDLEndPointType, String> specEndPoints = new HashMap<WSDLEndPointType, String>();
	/** the wsdl imports */
	private final static String[] imports = {
		"uddi_api_v3_binding.wsdl",
		"uddi_api_v3_portType.wsdl",
		"uddi_custody_v3_binding.wsdl",
		"uddi_custody_v3_portType.wsdl",
		"uddi_repl_v3_binding.wsdl",
		"uddi_repl_v3_portType.wsdl",
		"uddi_sub_v3_binding.wsdl",
		"uddi_sub_v3_portType.wsdl",
		"uddi_subr_v3_binding.wsdl",
		"uddi_subr_v3_portType.wsdl",
		"uddi_v3.xsd",
		"uddi_v3_service.wsdl",
		"uddi_v3custody.xsd",
		"uddi_v3policy.xsd",
		"uddi_v3policy_instanceParms.xsd",
		"uddi_v3replication.xsd",
		"uddi_v3subscription.xsd",
		"uddi_v3subscriptionListener.xsd",
		"uddi_v3valueset.xsd",
		"uddi_v3valuesetcaching.xsd",
		"uddi_vs_v3_binding.wsdl",
		"uddi_vs_v3_portType.wsdl",
		"uddi_vscache_v3_binding.wsdl",
		"uddi_vscache_v3_portType.wsdl"
	};
	
	static {
		specEndPoints.put(WSDLEndPointType.SECURITY             , "http://localhost/uddi/security/");
		specEndPoints.put(WSDLEndPointType.INQUIRY              , "http://localhost/uddi/inquire/");
		specEndPoints.put(WSDLEndPointType.PUBLISH              , "http://localhost/uddi/publish/");
		specEndPoints.put(WSDLEndPointType.SUBSCRIPTION         , "http://localhost/uddi/subscription/");
		specEndPoints.put(WSDLEndPointType.SUBSCRIPTION_LISTENER, "http://localhost/uddi/subscriptionlistener/");
		specEndPoints.put(WSDLEndPointType.CUSTODY_TRANSFER     , "http://localhost/uddi/custody/");
		specEndPoints.put(WSDLEndPointType.REPLICATION          , "http://localhost/uddi/replication/");
		specEndPoints.put(WSDLEndPointType.VALUESETVALIDATION   , "http://localhost/uddi/valuesetvalidation/");
		specEndPoints.put(WSDLEndPointType.VALUESETCACHING      , "http://localhost/uddi/valuesetcaching/");
	}
	
	/**
	 * Returns the path to a temporary uddi_v3_service.wsdl file, where the soap:address location
	 * of the given endPointType has been updated with the value specified in the soapAddressLocation.
	 * 
	 * @param endpointType
	 * @param soapAddressLocation
	 * @return WSDL File Path
	 * @throws IOException
	 */
	public URL getWSDLFilePath(WSDLEndPointType endpointType, String soapAddressLocation) throws IOException 
	{
		String wsdlString = getServiceWSDLContent();
	    String specEndPoint = specEndPoints.get(endpointType);
	    wsdlString = wsdlString.replace(specEndPoint, soapAddressLocation);
	    File tmpWSDLFile = File.createTempFile("uddi_v3_service", ".wsdl");
	    Writer out = new OutputStreamWriter(new FileOutputStream(tmpWSDLFile));
	    try {
	      out.write(wsdlString);
	    } finally {
	      out.close();
	    }
	    copyImportFiles();
	    URL url = new URL("file:" + tmpWSDLFile.getAbsolutePath());
	    return url;
	}
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	protected String getServiceWSDLContent() throws IOException 
	{
		URL serviceWSDLURL = ClassUtil.getResource(getUddiV3ServiceWSDL(), this.getClass());
		if (serviceWSDLURL==null) throw new IOException("Could not locate resource " + getUddiV3ServiceWSDL());
	    return read(serviceWSDLURL);
	}
	
	private void copyImportFiles() throws IOException
	{
		URL serviceWSDLURL = ClassUtil.getResource(getUddiV3ServiceWSDL(),this.getClass());
		if (serviceWSDLURL==null) throw new IOException("Could not locate resource " + getUddiV3ServiceWSDL());
		int endIndex = 0;
    	if (getUddiV3ServiceWSDL().contains(File.separator)) {
    		endIndex = getUddiV3ServiceWSDL().lastIndexOf(File.separator);
    	}
		String srcDir  = getUddiV3ServiceWSDL().substring(0,endIndex);
		String destDir = System.getProperty("java.io.tmpdir");
		for (String importFileName : imports) {
			URL url = ClassUtil.getResource(srcDir + importFileName, this.getClass());
			String content = read(url);
			File importFile = new File(destDir + File.separator + importFileName);
		    Writer out = new OutputStreamWriter(new FileOutputStream(importFile));
		    try {
		      out.write(content);
		    } finally {
		      out.close();
		    }
		}
	}
	
	private String read(URL url) throws IOException {
		InputStream resourceStream = url.openStream();
		StringBuilder wsdl = new StringBuilder();
	    byte[] b = new byte[4096];
	    for (int n; (n = resourceStream.read(b)) != -1;) {
	    	wsdl.append(new String(b, 0, n));
	    }
	    return wsdl.toString();
	}

	public String getUddiV3ServiceWSDL() {
		return uddiV3ServiceWSDL;
	}

	public void setUddiV3ServiceWSDL(String uddiV3ServiceWSDL) {
		this.uddiV3ServiceWSDL = uddiV3ServiceWSDL;
	}
	
}
