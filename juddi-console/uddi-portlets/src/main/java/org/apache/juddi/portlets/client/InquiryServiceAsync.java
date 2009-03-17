package org.apache.juddi.portlets.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;

public interface InquiryServiceAsync extends RemoteService {
	public void getTModelDetail(String authToken, String tModelKey, AsyncCallback<InquiryResponse> callback);
}

