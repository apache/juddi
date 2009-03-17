package org.apache.juddi.portlets.client;

import java.util.Map;

public class InquiryResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	Map<String,String> response;
	
	public Map<String,String> getResponse() {
		return response;
	}
	public void setResponse(Map<String,String> response) {
		this.response = response;
	}

}
