package org.apache.juddi.portlets.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("security")
public interface SecurityService extends RemoteService {
	public SecurityResponse get(String username, String password);
}
