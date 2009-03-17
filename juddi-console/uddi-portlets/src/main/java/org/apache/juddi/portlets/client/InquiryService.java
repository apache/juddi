package org.apache.juddi.portlets.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("inquiry")
public interface InquiryService extends RemoteService {
	
	public InquiryResponse getTModelDetail(String authToken, String tModelKey);
}
