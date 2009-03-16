package org.apache.juddi.portlets.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;

public interface GetTokenServiceAsync extends RemoteService {
	public void get(String username, String password, AsyncCallback<String> callback);
}

