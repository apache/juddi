package org.apache.juddi.portlets.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("token")
public interface GetTokenService extends RemoteService {
	public String get(String username, String password);
}
