package org.apache.juddi.api_v3;

public enum AccessPointType {
	
	END_POINT ("endPoint"),
	BINDING_TEMPLATE("bindingTemplate"),
	HOSTING_REDIRECTOR("hostingDirector"),
	WSDL_DEPLOYMENT ("wsdlDeployment");
	
	final String type;

	private AccessPointType(String type) {
		this.type = type;
	}
	
 	public String toString() {
		return type;	
	}
	
}
