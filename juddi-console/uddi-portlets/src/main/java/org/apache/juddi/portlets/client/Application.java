package org.apache.juddi.portlets.client;

public interface Application {

	public ApplicationPanel getApplicationPanel();
	public LoginPanel getLoginPanel();
	public void setToken(String token);
	public String getToken();
	public String getUsername();
	public void setUsername(String username);
}
